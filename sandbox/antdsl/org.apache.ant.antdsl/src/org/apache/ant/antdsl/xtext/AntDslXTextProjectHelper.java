/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.ant.antdsl.xtext;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ant.antdsl.AbstractAntDslProjectHelper;
import org.apache.ant.antdsl.AntDslContext;
import org.apache.ant.antdsl.AssignLocalTask;
import org.apache.ant.antdsl.AssignPropertyTask;
import org.apache.ant.antdsl.AssignReferenceTask;
import org.apache.ant.antdsl.ExtensionPoint;
import org.apache.ant.antdsl.IfTask;
import org.apache.ant.antdsl.IfTask.ConditionnalSequential;
import org.apache.ant.antdsl.Target;
import org.apache.ant.antdsl.expr.AddAntExpression;
import org.apache.ant.antdsl.expr.AntExpression;
import org.apache.ant.antdsl.expr.FuncAntExpression;
import org.apache.ant.antdsl.expr.MultiplicationAntExpression;
import org.apache.ant.antdsl.expr.PrimaryAntExpression;
import org.apache.ant.antdsl.expr.PropExpression;
import org.apache.ant.antdsl.xtext.antdsl.EAddExpr;
import org.apache.ant.antdsl.xtext.antdsl.EArgAttribute;
import org.apache.ant.antdsl.xtext.antdsl.EArgument;
import org.apache.ant.antdsl.xtext.antdsl.EArguments;
import org.apache.ant.antdsl.xtext.antdsl.EAttribute;
import org.apache.ant.antdsl.xtext.antdsl.EAttributes;
import org.apache.ant.antdsl.xtext.antdsl.EBoolAndExpr;
import org.apache.ant.antdsl.xtext.antdsl.EBoolExpr;
import org.apache.ant.antdsl.xtext.antdsl.EBoolNotExpr;
import org.apache.ant.antdsl.xtext.antdsl.EBoolOrExpr;
import org.apache.ant.antdsl.xtext.antdsl.EBoolXorExpr;
import org.apache.ant.antdsl.xtext.antdsl.EBranch;
import org.apache.ant.antdsl.xtext.antdsl.EConditionedTasks;
import org.apache.ant.antdsl.xtext.antdsl.EElementAttribute;
import org.apache.ant.antdsl.xtext.antdsl.EExpr;
import org.apache.ant.antdsl.xtext.antdsl.EExtensionPoint;
import org.apache.ant.antdsl.xtext.antdsl.EFuncExpr;
import org.apache.ant.antdsl.xtext.antdsl.EInnerElement;
import org.apache.ant.antdsl.xtext.antdsl.EInnerElements;
import org.apache.ant.antdsl.xtext.antdsl.ELocalAssignment;
import org.apache.ant.antdsl.xtext.antdsl.EMacrodef;
import org.apache.ant.antdsl.xtext.antdsl.EMultExpr;
import org.apache.ant.antdsl.xtext.antdsl.ENamespace;
import org.apache.ant.antdsl.xtext.antdsl.ENumExpr;
import org.apache.ant.antdsl.xtext.antdsl.EProject;
import org.apache.ant.antdsl.xtext.antdsl.EPropExpr;
import org.apache.ant.antdsl.xtext.antdsl.EPropertyAssignment;
import org.apache.ant.antdsl.xtext.antdsl.EReferenceAssignment;
import org.apache.ant.antdsl.xtext.antdsl.EStringExpr;
import org.apache.ant.antdsl.xtext.antdsl.ETarget;
import org.apache.ant.antdsl.xtext.antdsl.ETargetList;
import org.apache.ant.antdsl.xtext.antdsl.ETask;
import org.apache.ant.antdsl.xtext.antdsl.ETaskLists;
import org.apache.ant.antdsl.xtext.antdsl.ETextAttribute;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.MacroDef;
import org.apache.tools.ant.taskdefs.MacroDef.Attribute;
import org.apache.tools.ant.taskdefs.MacroDef.NestedSequential;
import org.apache.tools.ant.taskdefs.MacroDef.TemplateElement;
import org.apache.tools.ant.taskdefs.MacroDef.Text;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.condition.And;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.Not;
import org.apache.tools.ant.taskdefs.condition.Or;
import org.apache.tools.ant.taskdefs.condition.Xor;
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

    private void mapMacro(Project project, AntDslContext context, EMacrodef emacro) {
        MacroDef macroDef = new MacroDef();
        macroDef.setDescription(emacro.getDescription());
        macroDef.setName(emacro.getName());
        EAttributes eatts = emacro.getAttributes();
        if (eatts != null) {
            for (EAttribute eatt : eatts.getAttributes()) {
                if (eatt instanceof EArgAttribute) {
                    EArgAttribute eargatt = (EArgAttribute) eatt;
                    Attribute att = new Attribute();
                    att.setName(eargatt.getName());
                    att.setDefault(eargatt.getDefault());
                    macroDef.addConfiguredAttribute(att);
                } else if (eatt instanceof ETextAttribute) {
                    ETextAttribute etextatt = (ETextAttribute) eatt;
                    Text text = new Text();
                    text.setName(etextatt.getName());
                    text.setTrim(etextatt.isTrimmed());
                    text.setOptional(etextatt.isOptional());
                    macroDef.addConfiguredText(text);
                } else if (eatt instanceof EElementAttribute) {
                    EElementAttribute eelematt = (EElementAttribute) eatt;
                    TemplateElement element = new TemplateElement();
                    element.setImplicit(eelematt.isImplicit());
                    element.setOptional(eelematt.isOptional());
                    element.setName(eelematt.getName());
                    macroDef.addConfiguredElement(element);
                } else {
                    throw new IllegalArgumentException("Unsupported macro attribute " + eatt.getClass().getName());
                }
            }
        }
        ETaskLists etasks = emacro.getTasks();
        if (etasks != null) {
            NestedSequential seq = macroDef.createSequential();
            for (ETask etask : etasks.getTasks()) {
                seq.addTask(mapTask(project, context, etask));
            }
        }
        macroDef.setProject(project);
        macroDef.execute();
    }

    private Target mapTarget(Project project, AntDslContext context, ETarget eTarget) {
        Target target = new Target();
        context.setCurrentTarget(target);
        target.setIf(mapCondition(project, context, eTarget.getIf()));
        target.setUnless(mapCondition(project, context, eTarget.getUnless()));
        mapCommonTarget(target, project, context, eTarget.getName(), eTarget.getDescription(), mapTargetList(eTarget.getDepends()),
                mapTargetList(eTarget.getExtensionsOf()), eTarget.getOnMissingExtensionPoint());
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
        extensionPoint.setIf(mapCondition(project, context, eExtensionPoint.getIf()));
        extensionPoint.setUnless(mapCondition(project, context, eExtensionPoint.getUnless()));
        mapCommonTarget(extensionPoint, project, context, eExtensionPoint.getName(), eExtensionPoint.getDescription(),
                mapTargetList(eExtensionPoint.getDepends()), mapTargetList(eExtensionPoint.getExtensionsOf()),
                eExtensionPoint.getOnMissingExtensionPoint());
        context.setCurrentTarget(context.getImplicitTarget());
        return extensionPoint;
    }

    private Task mapTask(Project project, AntDslContext context, ETask eTask) {
        if (eTask instanceof EPropertyAssignment) {
            EPropertyAssignment ePropertyAssignment = (EPropertyAssignment) eTask;
            AssignPropertyTask property = new AssignPropertyTask();
            mapCommonTask(project, context, property);
            property.setName(ePropertyAssignment.getName());
            property.setValue(mapExpr(project, context, ePropertyAssignment.getValue()));
            return property;
        }
        if (eTask instanceof EReferenceAssignment) {
            EReferenceAssignment eReferenceAssignment = (EReferenceAssignment) eTask;
            AssignReferenceTask ref = new AssignReferenceTask();
            mapCommonTask(project, context, ref);
            ref.setName(eReferenceAssignment.getName());
            ref.setValue(mapExpr(project, context, eReferenceAssignment.getValue()));
            return ref;
        }
        if (eTask instanceof ELocalAssignment) {
            ELocalAssignment eLocalAssignment = (ELocalAssignment) eTask;
            AssignLocalTask local = new AssignLocalTask();
            mapCommonTask(project, context, local);
            local.setName(eLocalAssignment.getName());
            local.setValue(mapExpr(project, context, eLocalAssignment.getValue()));
            return local;
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
                main.setCondition(mapCondition(project, context, if_.getCondition()));
                for (ETask t : if_.getTasks().getTasks()) {
                    main.addTask(mapTask(project, context, t));
                }
                ifTask.setMain(main);
            }
            EList<EConditionedTasks> elseifs = eBranch.getElseif();
            if (elseifs != null) {
                for (EConditionedTasks elseif : elseifs) {
                    ConditionnalSequential ei = new ConditionnalSequential();
                    mapCommonTask(project, context, ei);
                    ei.setCondition(mapCondition(project, context, elseif.getCondition()));
                    for (ETask t : elseif.getTasks().getTasks()) {
                        ei.addTask(mapTask(project, context, t));
                    }
                    ifTask.addElseIf(ei);
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

    private Condition mapCondition(Project project, AntDslContext context, EBoolExpr expr) {
        if (expr == null) {
            return null;
        }
        if (expr instanceof EInnerElement) {
            EInnerElement elemExpr = (EInnerElement) expr;
            return mapExpectedUnknown(project, context, mapInnerElement(elemExpr), Condition.class);
        }
        if (expr instanceof EBoolAndExpr) {
            EBoolAndExpr andExpr = (EBoolAndExpr) expr;
            And and = new And();
            and.setProject(project);
            and.add(mapCondition(project, context, andExpr.getLeft()));
            and.add(mapCondition(project, context, andExpr.getRight()));
            return and;
        }
        if (expr instanceof EBoolOrExpr) {
            EBoolOrExpr orExpr = (EBoolOrExpr) expr;
            Or or = new Or();
            or.setProject(project);
            or.add(mapCondition(project, context, orExpr.getLeft()));
            or.add(mapCondition(project, context, orExpr.getRight()));
            return or;
        }
        if (expr instanceof EBoolXorExpr) {
            EBoolXorExpr xorExpr = (EBoolXorExpr) expr;
            Xor xor = new Xor();
            xor.setProject(project);
            xor.add(mapCondition(project, context, xorExpr.getLeft()));
            xor.add(mapCondition(project, context, xorExpr.getRight()));
            return xor;
        }
        if (expr instanceof EBoolNotExpr) {
            EBoolNotExpr notExpr = (EBoolNotExpr) expr;
            Not not = new Not();
            not.setProject(project);
            not.add(mapCondition(project, context, notExpr.getExpr()));
            return not;
        }
        throw new IllegalArgumentException("Unsupported boolean expression " + expr.getClass().getName());
    }

    private InnerElement mapInnerElement(EInnerElement eInnerElement) {
        if (eInnerElement == null) {
            return null;
        }
        InnerElement innerElement = new InnerElement();
        innerElement.ns = eInnerElement.getName().getNamespace();
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

    private AntExpression mapExpr(Project project, AntDslContext context, EExpr eexpr) {
        if (eexpr instanceof EAddExpr) {
            EAddExpr eadd = (EAddExpr) eexpr;
            AddAntExpression add = new AddAntExpression();
            add.setProject(project);
            add.add(mapExpr(project, context, eadd.getLeft()));
            add.add(mapExpr(project, context, eadd.getRight()));
            return add;
        }
        if (eexpr instanceof EMultExpr) {
            EMultExpr emult = (EMultExpr) eexpr;
            MultiplicationAntExpression mult = new MultiplicationAntExpression();
            mult.setProject(project);
            mult.add(mapExpr(project, context, emult.getLeft()));
            mult.add(mapExpr(project, context, emult.getRight()));
            return mult;
        }
        if (eexpr instanceof EFuncExpr) {
            EFuncExpr efunc = (EFuncExpr) eexpr;
            FuncAntExpression func = new FuncAntExpression();
            func.setProject(project);
            func.setName(efunc.getName());
            for (EExpr arg : efunc.getArguments()) {
                func.addArgument(mapExpr(project, context, arg));
            }
            return func;
        }
        if (eexpr instanceof ENumExpr) {
            ENumExpr enumb = (ENumExpr) eexpr;
            PrimaryAntExpression primary = new PrimaryAntExpression();
            primary.setProject(project);
            primary.setValue(enumb.getValue());
            return primary;
        }
        if (eexpr instanceof EStringExpr) {
            EStringExpr estring = (EStringExpr) eexpr;
            PrimaryAntExpression primary = new PrimaryAntExpression();
            primary.setProject(project);
            primary.setValue(estring.getValue());
            return primary;
        }
        if (eexpr instanceof EPropExpr) {
            EPropExpr eprop = (EPropExpr) eexpr;
            PropExpression prop = new PropExpression();
            prop.setProject(project);
            prop.setProperty(eprop.getProperty().substring(1));
            return prop;
        }
        throw new IllegalArgumentException("Unsupported expression " + eexpr.getClass().getName());
    }
}
