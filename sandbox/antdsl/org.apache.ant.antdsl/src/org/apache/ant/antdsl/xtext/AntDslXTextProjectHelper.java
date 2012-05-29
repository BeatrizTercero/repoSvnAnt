package org.apache.ant.antdsl.xtext;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ant.antdsl.AbstractAntDslProjectHelper;
import org.apache.ant.antdsl.AntDslContext;
import org.apache.ant.antdsl.IfTask;
import org.apache.ant.antdsl.IfTask.ConditionnalSequential;
import org.apache.ant.antdsl.xtext.antdsl.EArgument;
import org.apache.ant.antdsl.xtext.antdsl.EArguments;
import org.apache.ant.antdsl.xtext.antdsl.EBranch;
import org.apache.ant.antdsl.xtext.antdsl.EConditionedTasks;
import org.apache.ant.antdsl.xtext.antdsl.EExtensionPoint;
import org.apache.ant.antdsl.xtext.antdsl.EInnerElement;
import org.apache.ant.antdsl.xtext.antdsl.EInnerElements;
import org.apache.ant.antdsl.xtext.antdsl.EMacrodef;
import org.apache.ant.antdsl.xtext.antdsl.ENamespace;
import org.apache.ant.antdsl.xtext.antdsl.EProject;
import org.apache.ant.antdsl.xtext.antdsl.EPropertyAssignment;
import org.apache.ant.antdsl.xtext.antdsl.ETarget;
import org.apache.ant.antdsl.xtext.antdsl.ETargetList;
import org.apache.ant.antdsl.xtext.antdsl.ETask;
import org.apache.ant.antdsl.xtext.antdsl.ETaskLists;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExtensionPoint;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;

public class AntDslXTextProjectHelper extends AbstractAntDslProjectHelper {

    private IParser parser;

    public AntDslXTextProjectHelper() {
        parser = ParserCreator.createWithGuice();
    }

    @Override
    protected void doParse(InputStream in, String buildFileName, Project project, AntDslContext context) {
        IParseResult result;
        try {
            result = parser.parse(new InputStreamReader(in));
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // don't care
            }
        }
        if (result.hasSyntaxErrors()) {
            project.log("Syntax error while parsing the build file " + buildFileName, Project.MSG_ERR);
            for (INode error : result.getSyntaxErrors()) {
                project.log(error.getText(), Project.MSG_ERR);
                project.log(error.getSyntaxErrorMessage().getMessage(), Project.MSG_ERR);
            }
            throw new BuildException("Syntax error while parsing the build file " + buildFileName);
        }
        EProject eProject = (EProject) result.getRootASTElement();
        mapProject(project, context, eProject);
    }

    private void mapProject(Project project, AntDslContext context, EProject eProject) {
        String name = eProject.getName();
        String basedir = eProject.getBasedir();
        String def = eProject.getDefault();

        setupProject(project, context, name, basedir, def);

        EList<ENamespace> namespaces = eProject.getNamespaces();
        if (namespaces != null) {
            for (ENamespace namespace : namespaces) {
                context.addNamespace(namespace.getName(), namespace.getUri());
            }
        }

        EList<EMacrodef> macros = eProject.getMacrodDefs();
        if (macros != null) {
            for (EMacrodef macro : macros) {
                mapMacro(project, context, macro);
            }
        }
        ETaskLists tasks = eProject.getTasks();
        if (tasks != null) {
            for (ETask eTask : tasks.getTasks()) {
                context.getImplicitTarget().addTask(mapTask(project, context, eTask));
            }
        }
        for (ETarget eTarget : eProject.getTargets()) {
            mapTarget(project, context, eTarget);
        }
        for (EExtensionPoint eExtensionPoint : eProject.getExtensionPoints()) {
            mapExtensionPoint(project, context, eExtensionPoint);
        }
    }

    private void mapMacro(Project project, AntDslContext context, EMacrodef macro) {
        // TODO
    }

    private Target mapTarget(Project project, AntDslContext context, ETarget eTarget) {
        Target target = new Target();
        context.setCurrentTarget(target);
        mapCommonTarget(target, project, context, eTarget.getName(), eTarget.getDescription(), mapTargetList(eTarget.getDepends()),
                mapTargetList(eTarget.getExtensionsOf()));
        ETaskLists tasks = eTarget.getTasks();
        if (tasks != null && tasks.getTasks() != null) {
            for (ETask eTask : tasks.getTasks()) {
                target.addTask(mapTask(project, context, eTask));
            }
        }
        context.setCurrentTarget(context.getImplicitTarget());
        return target;
    }

    private List<String> mapTargetList(ETargetList targetList) {
        if (targetList == null) {
            return null;
        }
        return targetList.getNames();
    }

    private ExtensionPoint mapExtensionPoint(Project project, AntDslContext context, EExtensionPoint eExtensionPoint) {
        ExtensionPoint extensionPoint = new ExtensionPoint();
        mapCommonTarget(extensionPoint, project, context, eExtensionPoint.getName(), eExtensionPoint.getDescription(),
                mapTargetList(eExtensionPoint.getDepends()), mapTargetList(eExtensionPoint.getExtensionsOf()));
        context.setCurrentTarget(context.getImplicitTarget());
        return extensionPoint;
    }

    private Task mapTask(Project project, AntDslContext context, ETask eTask) {
        if (eTask instanceof EPropertyAssignment) {
            EPropertyAssignment ePropertyAssignment = (EPropertyAssignment) eTask;
            Property property = new Property();
            mapCommonTask(project, context, property);
            property.setName(ePropertyAssignment.getName());
            property.setValue(ePropertyAssignment.getValue());
            return property;
        }
        if (eTask instanceof EInnerElement) {
            EInnerElement eInnerElement = (EInnerElement) eTask;
            return mapUnknown(project, context, mapInnerElement(eInnerElement), false);
        }
        if (eTask instanceof EBranch) {
            EBranch eBranch = (EBranch) eTask;
            IfTask ifTask = new IfTask();
            mapCommonTask(project, context, ifTask);
            EConditionedTasks if_ = eBranch.getIf();
            if (if_ != null) {
                ConditionnalSequential main = new ConditionnalSequential();
                mapCommonTask(project, context, main);
                main.setCondition(mapExpectedUnknown(project, context, mapInnerElement(if_.getCondition()), Condition.class));
                for (ETask t : if_.getTasks().getTasks()) {
                    main.addTask(mapTask(project, context, t));
                }
            }
            EList<EConditionedTasks> elseifs = eBranch.getElseif();
            if (elseifs != null) {
                for (EConditionedTasks elseif : elseifs) {
                    ConditionnalSequential ei = new ConditionnalSequential();
                    mapCommonTask(project, context, ei);
                    ei.setCondition(mapExpectedUnknown(project, context, mapInnerElement(elseif.getCondition()), Condition.class));
                    for (ETask t : elseif.getTasks().getTasks()) {
                        ei.addTask(mapTask(project, context, t));
                    }
                }
            }
            ETaskLists else_ = eBranch.getElse();
            if (else_ != null) {
                Sequential e = new Sequential();
                mapCommonTask(project, context, e);
                for (ETask t : else_.getTasks()) {
                    e.addTask(mapTask(project, context, t));
                }
                ifTask.setElse(e);
            }
            return ifTask;
        }
        throw new IllegalStateException("Unknown task type " + eTask.getClass().getName());
    }

    private InnerElement mapInnerElement(EInnerElement eInnerElement) {
        if (eInnerElement == null) {
            return null;
        }
        InnerElement innerElement = new InnerElement();
        innerElement.ns = eInnerElement.getName().getName();
        innerElement.name = eInnerElement.getName().getName();

        EArguments arguments = eInnerElement.getArguments();
        if (arguments != null) {
            innerElement.attributes = new LinkedHashMap<String, String>();
            for (EArgument argument : arguments.getArguments()) {
                innerElement.attributes.put(argument.getName(), argument.getValue());
            }
        }

        EInnerElements inners = eInnerElement.getInners();
        if (inners != null) {
            innerElement.children = new ArrayList<InnerElement>();
            for (EInnerElement inner : inners.getElements()) {
                innerElement.children.add(mapInnerElement(inner));
            }
        }

        return innerElement;
    }
}
