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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ant.antdsl.AbstractAntDslProjectHelper;
import org.apache.ant.antdsl.AntDslContext;
import org.apache.ant.antdsl.AssignLocalTask;
import org.apache.ant.antdsl.AssignPropertyTask;
import org.apache.ant.antdsl.AssignReferenceTask;
import org.apache.ant.antdsl.FunctionDef;
import org.apache.ant.antdsl.FunctionDef.LocalProperty;
import org.apache.ant.antdsl.FunctionDef.NestedSequential;
import org.apache.ant.antdsl.FunctionDef.TemplateElement;
import org.apache.ant.antdsl.IfTask;
import org.apache.ant.antdsl.IfTask.ConditionnalSequential;
import org.apache.ant.antdsl.expr.AddAntExpression;
import org.apache.ant.antdsl.expr.AndAntExpression;
import org.apache.ant.antdsl.expr.AntExpression;
import org.apache.ant.antdsl.expr.BinaryAntExpression;
import org.apache.ant.antdsl.expr.DivisionAntExpression;
import org.apache.ant.antdsl.expr.EqualityCondition;
import org.apache.ant.antdsl.expr.ExclusiveOrAntExpression;
import org.apache.ant.antdsl.expr.FuncAntExpression;
import org.apache.ant.antdsl.expr.GEAntExpression;
import org.apache.ant.antdsl.expr.GTAntExpression;
import org.apache.ant.antdsl.expr.InclusiveOrAntExpression;
import org.apache.ant.antdsl.expr.InstanceofAntExpression;
import org.apache.ant.antdsl.expr.LEAntExpression;
import org.apache.ant.antdsl.expr.LTAntExpression;
import org.apache.ant.antdsl.expr.LeftShiftAntExpression;
import org.apache.ant.antdsl.expr.LogicalRightShiftAntExpression;
import org.apache.ant.antdsl.expr.MinusAntExpression;
import org.apache.ant.antdsl.expr.ModuloAntExpression;
import org.apache.ant.antdsl.expr.MultiplicationAntExpression;
import org.apache.ant.antdsl.expr.NegativeAntExpression;
import org.apache.ant.antdsl.expr.NotBitwiseAntExpression;
import org.apache.ant.antdsl.expr.PositiveAntExpression;
import org.apache.ant.antdsl.expr.PrimaryAntExpression;
import org.apache.ant.antdsl.expr.RightShiftAntExpression;
import org.apache.ant.antdsl.expr.TernaryAntExpression;
import org.apache.ant.antdsl.expr.VariableAntExpression;
import org.apache.ant.antdsl.xtext.antdsl.EAdditiveExpr;
import org.apache.ant.antdsl.xtext.antdsl.EAndExpr;
import org.apache.ant.antdsl.xtext.antdsl.EAntlibImport;
import org.apache.ant.antdsl.xtext.antdsl.EArgument;
import org.apache.ant.antdsl.xtext.antdsl.EArguments;
import org.apache.ant.antdsl.xtext.antdsl.EBooleanLiteralExpr;
import org.apache.ant.antdsl.xtext.antdsl.EBranch;
import org.apache.ant.antdsl.xtext.antdsl.EBuildImport;
import org.apache.ant.antdsl.xtext.antdsl.ECharacterLiteralExpr;
import org.apache.ant.antdsl.xtext.antdsl.EConditionedTasks;
import org.apache.ant.antdsl.xtext.antdsl.EConditionnalAndExpr;
import org.apache.ant.antdsl.xtext.antdsl.EConditionnalExclusiveOrExpr;
import org.apache.ant.antdsl.xtext.antdsl.EConditionnalInclusiveOrExpr;
import org.apache.ant.antdsl.xtext.antdsl.EDecimalLiteralExpr;
import org.apache.ant.antdsl.xtext.antdsl.EElementFuncArgument;
import org.apache.ant.antdsl.xtext.antdsl.EEqualityExpr;
import org.apache.ant.antdsl.xtext.antdsl.EExclusiveOrExpr;
import org.apache.ant.antdsl.xtext.antdsl.EExpr;
import org.apache.ant.antdsl.xtext.antdsl.EExtensionPoint;
import org.apache.ant.antdsl.xtext.antdsl.EFloatingPointLiteralExpr;
import org.apache.ant.antdsl.xtext.antdsl.EFuncArgument;
import org.apache.ant.antdsl.xtext.antdsl.EFuncArguments;
import org.apache.ant.antdsl.xtext.antdsl.EFuncExpr;
import org.apache.ant.antdsl.xtext.antdsl.EFunctionDef;
import org.apache.ant.antdsl.xtext.antdsl.EHexLiteralExpr;
import org.apache.ant.antdsl.xtext.antdsl.EImport;
import org.apache.ant.antdsl.xtext.antdsl.EInclusiveOrExpr;
import org.apache.ant.antdsl.xtext.antdsl.EInnerElement;
import org.apache.ant.antdsl.xtext.antdsl.EInnerElements;
import org.apache.ant.antdsl.xtext.antdsl.EInstanceOfExpr;
import org.apache.ant.antdsl.xtext.antdsl.ELocalAssignment;
import org.apache.ant.antdsl.xtext.antdsl.ELocalPropertyFuncArgument;
import org.apache.ant.antdsl.xtext.antdsl.EMultiplicativeExpr;
import org.apache.ant.antdsl.xtext.antdsl.ENullExpr;
import org.apache.ant.antdsl.xtext.antdsl.EOctalLiteralExpr;
import org.apache.ant.antdsl.xtext.antdsl.EProject;
import org.apache.ant.antdsl.xtext.antdsl.EPropertyAssignment;
import org.apache.ant.antdsl.xtext.antdsl.EReferenceAssignment;
import org.apache.ant.antdsl.xtext.antdsl.ERelationalExpr;
import org.apache.ant.antdsl.xtext.antdsl.EShiftExpr;
import org.apache.ant.antdsl.xtext.antdsl.EStringLiteralExpr;
import org.apache.ant.antdsl.xtext.antdsl.ETarget;
import org.apache.ant.antdsl.xtext.antdsl.ETargetList;
import org.apache.ant.antdsl.xtext.antdsl.ETask;
import org.apache.ant.antdsl.xtext.antdsl.ETaskLists;
import org.apache.ant.antdsl.xtext.antdsl.ETernaryExpr;
import org.apache.ant.antdsl.xtext.antdsl.EUnaryExpr;
import org.apache.ant.antdsl.xtext.antdsl.EVariableExpr;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExtensionPoint;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.condition.And;
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
        String name = readIdentifier(eProject.getName());
        String basedir = readString(eProject.getBasedir());
        String def = readIdentifier(eProject.getDefault());

        setupProject(project, context, name, basedir, def);

        if (!context.isIgnoringProjectTag()) {
            EList<EInnerElement> eAntpathElements = eProject.getAntpath();
            List<InnerElement> antpathElements = new ArrayList<InnerElement>();
            if (eAntpathElements != null) {
                for (EInnerElement eAntpathElement : eAntpathElements) {
                    antpathElements.add(mapInnerElement(project, context, eAntpathElement));
                }
            }
            setupAntpath(project, context, antpathElements);
        }

        EList<EImport> eImports = eProject.getImports();
        if (eImports != null) {
            for (EImport eImport : eImports) {
                if (eImport instanceof EAntlibImport) {
                    EAntlibImport eAntlibImport = (EAntlibImport) eImport;
                    importAntlib(project, context, readIdentifier(eAntlibImport.getName()), readString(eAntlibImport.getResource()));
                } else if (eImport instanceof EBuildImport) {
                    EBuildImport eBuildImport = (EBuildImport) eImport;
                    importBuildModule(project, context, readString(eBuildImport.getFile()));
                } else {
                    throw new IllegalArgumentException("Unsupported import " + eImport.getClass().getName());
                }
            }
        }

        EList<EFunctionDef> efuncs = eProject.getFunctionDefs();
        if (efuncs != null) {
            for (EFunctionDef efunc : efuncs) {
                mapFunction(project, context, efunc);
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

    private void mapFunction(Project project, AntDslContext context, EFunctionDef efunc) {
        FunctionDef funcDef = new FunctionDef();
        funcDef.setDescription(efunc.getDescription());
        funcDef.setName(readIdentifier(efunc.getName()));
        EFuncArguments eargs = efunc.getArguments();
        if (eargs != null) {
            for (EFuncArgument earg : eargs.getArguments()) {
                if (earg instanceof ELocalPropertyFuncArgument) {
                    ELocalPropertyFuncArgument elocalpropatt = (ELocalPropertyFuncArgument) earg;
                    LocalProperty localProp = new LocalProperty();
                    localProp.setName(readIdentifier(elocalpropatt.getName()));
                    localProp.setDefault(mapExpr(project, context, elocalpropatt.getDefault()));
                    funcDef.addConfiguredLocalProperty(localProp);
                } else if (earg instanceof EElementFuncArgument) {
                    EElementFuncArgument eelemarg = (EElementFuncArgument) earg;
                    TemplateElement element = new TemplateElement();
                    element.setImplicit(eelemarg.isImplicit());
                    element.setOptional(eelemarg.isOptional());
                    element.setName(readIdentifier(eelemarg.getName()));
                    funcDef.addConfiguredElement(element);
                } else {
                    throw new IllegalArgumentException("Unsupported function argument " + earg.getClass().getName());
                }
            }
        }
        ETaskLists etasks = efunc.getTasks();
        if (etasks != null) {
            NestedSequential seq = funcDef.createSequential();
            for (ETask etask : etasks.getTasks()) {
                seq.addTask(mapTask(project, context, etask));
            }
        }
        funcDef.setProject(project);
        funcDef.execute();
    }

    private Target mapTarget(Project project, AntDslContext context, ETarget eTarget) {
        Target target = new Target();
        context.setCurrentTarget(target);
        target.setIf(expression2Condition(mapExpr(project, context, eTarget.getIf())));
        target.setUnless(expression2Condition(mapExpr(project, context, eTarget.getUnless())));
        mapCommonTarget(target, project, context, readIdentifier(eTarget.getName()), eTarget.getDescription(), mapTargetList(eTarget.getDepends()),
                mapTargetList(eTarget.getExtensionsOf()), readString(eTarget.getOnMissingExtensionPoint()));
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
        List<String> names = new ArrayList<String>();
        for (String name : targetList.getNames()) {
            names.add(readIdentifier(name));
        }
        return names;
    }

    private ExtensionPoint mapExtensionPoint(Project project, AntDslContext context, EExtensionPoint eExtensionPoint) {
        ExtensionPoint extensionPoint = new ExtensionPoint();
        extensionPoint.setIf(expression2Condition(mapExpr(project, context, eExtensionPoint.getIf())));
        extensionPoint.setUnless(expression2Condition(mapExpr(project, context, eExtensionPoint.getUnless())));
        mapCommonTarget(extensionPoint, project, context, readIdentifier(eExtensionPoint.getName()), eExtensionPoint.getDescription(),
                mapTargetList(eExtensionPoint.getDepends()), mapTargetList(eExtensionPoint.getExtensionsOf()),
                readString(eExtensionPoint.getOnMissingExtensionPoint()));
        context.setCurrentTarget(context.getImplicitTarget());
        return extensionPoint;
    }

    private Task mapTask(Project project, AntDslContext context, ETask eTask) {
        if (eTask instanceof EPropertyAssignment) {
            EPropertyAssignment ePropertyAssignment = (EPropertyAssignment) eTask;
            AssignPropertyTask property = new AssignPropertyTask();
            mapCommonTask(project, context, property);
            property.setName(readIdentifier(ePropertyAssignment.getName()));
            property.setValue(mapExpr(project, context, ePropertyAssignment.getValue()));
            return property;
        }
        if (eTask instanceof EReferenceAssignment) {
            EReferenceAssignment eReferenceAssignment = (EReferenceAssignment) eTask;
            AssignReferenceTask ref = new AssignReferenceTask();
            mapCommonTask(project, context, ref);
            ref.setName(readIdentifier(eReferenceAssignment.getName()));
            ref.setValue(mapExpr(project, context, eReferenceAssignment.getValue()));
            return ref;
        }
        if (eTask instanceof ELocalAssignment) {
            ELocalAssignment eLocalAssignment = (ELocalAssignment) eTask;
            AssignLocalTask local = new AssignLocalTask();
            mapCommonTask(project, context, local);
            local.setName(readIdentifier(eLocalAssignment.getName()));
            local.setValue(mapExpr(project, context, eLocalAssignment.getValue()));
            return local;
        }
        if (eTask instanceof EInnerElement) {
            EInnerElement eInnerElement = (EInnerElement) eTask;
            return mapUnknown(project, context, mapInnerElement(project, context, eInnerElement), false);
        }
        if (eTask instanceof EBranch) {
            EBranch eBranch = (EBranch) eTask;
            IfTask ifTask = new IfTask();
            mapCommonTask(project, context, ifTask);
            EConditionedTasks if_ = eBranch.getIf();
            if (if_ != null) {
                ConditionnalSequential main = new ConditionnalSequential();
                mapCommonTask(project, context, main);
                main.setCondition(expression2Condition(mapExpr(project, context, if_.getCondition())));
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
                    ei.setCondition(expression2Condition(mapExpr(project, context, elseif.getCondition())));
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

    private InnerElement mapInnerElement(Project project, AntDslContext context, EInnerElement eInnerElement) {
        if (eInnerElement == null) {
            return null;
        }
        InnerElement innerElement = new InnerElement();
        innerElement.ns = readIdentifier(eInnerElement.getName().getNamespace());
        innerElement.name = readIdentifier(eInnerElement.getName().getName());

        EArguments arguments = eInnerElement.getArguments();
        if (arguments != null) {
            innerElement.attributes = new LinkedHashMap<String, AntExpression>();
            for (EArgument argument : arguments.getArguments()) {
                innerElement.attributes.put(readIdentifier(argument.getName()), mapExpr(project, context, argument.getValue()));
            }
        }

        EInnerElements inners = eInnerElement.getInners();
        if (inners != null) {
            innerElement.children = new ArrayList<InnerElement>();
            for (EInnerElement inner : inners.getElements()) {
                innerElement.children.add(mapInnerElement(project, context, inner));
            }
        }

        return innerElement;
    }

    private AntExpression mapExpr(Project project, AntDslContext context, EExpr eexpr) {
        if (eexpr == null) {
            return null;
        }
        if (eexpr instanceof ETernaryExpr) {
            ETernaryExpr etern = (ETernaryExpr) eexpr;
            TernaryAntExpression tern = new TernaryAntExpression();
            tern.setProject(project);
            tern.setCondition(expression2Condition(mapExpr(project, context, etern.getCondition())));
            tern.setOnTrue(mapExpr(project, context, etern.getOnTrue()));
            tern.setOnFalse(mapExpr(project, context, etern.getOnFalse()));
            return tern;
        }
        if (eexpr instanceof EConditionnalInclusiveOrExpr) {
            EConditionnalInclusiveOrExpr orExpr = (EConditionnalInclusiveOrExpr) eexpr;
            Or or = new Or();
            or.setProject(project);
            for (EExpr child : orExpr.getChildren()) {
                or.add(expression2Condition(mapExpr(project, context, child)));
            }
            return condition2Expression(or);
        }
        if (eexpr instanceof EConditionnalExclusiveOrExpr) {
            EConditionnalExclusiveOrExpr xorExpr = (EConditionnalExclusiveOrExpr) eexpr;
            Xor xor = new Xor();
            xor.setProject(project);
            for (EExpr child : xorExpr.getChildren()) {
                xor.add(expression2Condition(mapExpr(project, context, child)));
            }
            return condition2Expression(xor);
        }
        if (eexpr instanceof EConditionnalAndExpr) {
            EConditionnalAndExpr andExpr = (EConditionnalAndExpr) eexpr;
            And and = new And();
            and.setProject(project);
            for (EExpr child : andExpr.getChildren()) {
                and.add(expression2Condition(mapExpr(project, context, child)));
            }
            return condition2Expression(and);
        }
        if (eexpr instanceof EInclusiveOrExpr) {
            EInclusiveOrExpr eor = (EInclusiveOrExpr) eexpr;
            InclusiveOrAntExpression or = new InclusiveOrAntExpression();
            or.setProject(project);
            for (EExpr child : eor.getChildren()) {
                or.add(mapExpr(project, context, child));
            }
            return or;
        }
        if (eexpr instanceof EExclusiveOrExpr) {
            EExclusiveOrExpr eor = (EExclusiveOrExpr) eexpr;
            ExclusiveOrAntExpression or = new ExclusiveOrAntExpression();
            or.setProject(project);
            for (EExpr child : eor.getChildren()) {
                or.add(mapExpr(project, context, child));
            }
            return or;
        }
        if (eexpr instanceof EAndExpr) {
            EAndExpr eand = (EAndExpr) eexpr;
            AndAntExpression and = new AndAntExpression();
            and.setProject(project);
            for (EExpr child : eand.getChildren()) {
                and.add(mapExpr(project, context, child));
            }
            return and;
        }
        if (eexpr instanceof EEqualityExpr) {
            EEqualityExpr eequ = (EEqualityExpr) eexpr;
            EqualityCondition equ = new EqualityCondition();
            equ.setProject(project);
            for (EExpr child : eequ.getChildren()) {
                equ.add(mapExpr(project, context, child));
            }
            return equ;
        }
        if (eexpr instanceof EInstanceOfExpr) {
            EInstanceOfExpr einstof = (EInstanceOfExpr) eexpr;
            InstanceofAntExpression instof = new InstanceofAntExpression();
            instof.setProject(project);
            for (EExpr child : einstof.getChildren()) {
                instof.add(mapExpr(project, context, child));
            }
            return instof;
        }
        if (eexpr instanceof ERelationalExpr) {
            ERelationalExpr erel = (ERelationalExpr) eexpr;
            Iterator<EExpr> itChild = erel.getChildren().iterator();
            AntExpression expr = mapExpr(project, context, itChild.next());
            Iterator<String> itOp = erel.getOperators().iterator();
            while (itChild.hasNext()) {
                String op = itOp.next();
                BinaryAntExpression e;
                if (op.equals("<=")) {
                    e = new LEAntExpression();
                } else if (op.equals("<")) {
                    e = new LTAntExpression();
                } else if (op.equals(">=")) {
                    e = new GEAntExpression();
                } else if (op.equals(">")) {
                    e = new GTAntExpression();
                } else {
                    throw new IllegalStateException("Unsupported relational operator " + op);
                }
                e.setProject(project);
                e.add(expr);
                e.add(mapExpr(project, context, itChild.next()));
                expr = e;
            }
            return expr;
        }
        if (eexpr instanceof EShiftExpr) {
            EShiftExpr eshift = (EShiftExpr) eexpr;
            Iterator<EExpr> itChild = eshift.getChildren().iterator();
            AntExpression expr = mapExpr(project, context, itChild.next());
            Iterator<String> itOp = eshift.getOperators().iterator();
            while (itChild.hasNext()) {
                String op = itOp.next();
                BinaryAntExpression e;
                if (op.equals("<<")) {
                    e = new LeftShiftAntExpression();
                } else if (op.equals(">>")) {
                    e = new RightShiftAntExpression();
                } else if (op.equals(">>>")) {
                    e = new LogicalRightShiftAntExpression();
                } else {
                    throw new IllegalStateException("Unsupported shift operator " + op);
                }
                e.setProject(project);
                e.add(expr);
                e.add(mapExpr(project, context, itChild.next()));
                expr = e;
            }
            return expr;
        }
        if (eexpr instanceof EAdditiveExpr) {
            EAdditiveExpr eadd = (EAdditiveExpr) eexpr;
            Iterator<EExpr> itChild = eadd.getChildren().iterator();
            AntExpression expr = mapExpr(project, context, itChild.next());
            Iterator<String> itOp = eadd.getOperators().iterator();
            while (itChild.hasNext()) {
                String op = itOp.next();
                BinaryAntExpression e;
                if (op.equals("+")) {
                    e = new AddAntExpression();
                } else if (op.equals("-")) {
                    e = new MinusAntExpression();
                } else {
                    throw new IllegalStateException("Unsupported additive operator " + op);
                }
                e.setProject(project);
                e.add(expr);
                e.add(mapExpr(project, context, itChild.next()));
                expr = e;
            }
            return expr;
        }
        if (eexpr instanceof EMultiplicativeExpr) {
            EMultiplicativeExpr emult = (EMultiplicativeExpr) eexpr;
            Iterator<EExpr> itChild = emult.getChildren().iterator();
            AntExpression expr = mapExpr(project, context, itChild.next());
            Iterator<String> itOp = emult.getOperators().iterator();
            while (itChild.hasNext()) {
                String op = itOp.next();
                BinaryAntExpression e;
                if (op.equals("*")) {
                    e = new MultiplicationAntExpression();
                } else if (op.equals("/")) {
                    e = new DivisionAntExpression();
                } else if (op.equals("%")) {
                    e = new ModuloAntExpression();
                } else {
                    throw new IllegalStateException("Unsupported multiplicative operator " + op);
                }
                e.setProject(project);
                e.add(expr);
                e.add(mapExpr(project, context, itChild.next()));
                expr = e;
            }
            return expr;
        }
        if (eexpr instanceof EUnaryExpr) {
            EUnaryExpr eunary = (EUnaryExpr) eexpr;
            String op = eunary.getOp();
            if (op.equals("+")) {
                PositiveAntExpression expr = new PositiveAntExpression();
                expr.setProject(project);
                expr.setExpr(mapExpr(project, context, eunary.getExpr()));
                return expr;
            } else if (op.equals("-")) {
                NegativeAntExpression expr = new NegativeAntExpression();
                expr.setProject(project);
                expr.setExpr(mapExpr(project, context, eunary.getExpr()));
                return expr;
            } else if (op.equals("~")) {
                NotBitwiseAntExpression not = new NotBitwiseAntExpression();
                not.setProject(project);
                not.setExpr(mapExpr(project, context, eunary.getExpr()));
                return not;
            } else if (op.equals("!")) {
                Not not = new Not();
                not.setProject(project);
                not.add(expression2Condition(mapExpr(project, context, eunary.getExpr())));
                return condition2Expression(not);
            } else {
                throw new IllegalStateException("Unsupported unary operator " + op);
            }
        }
        if (eexpr instanceof EVariableExpr) {
            EVariableExpr evar = (EVariableExpr) eexpr;
            VariableAntExpression var = new VariableAntExpression();
            var.setProject(project);
            var.setName(readVariable(evar.getName()));
            return var;
        }
        if (eexpr instanceof EFuncExpr) {
            EFuncExpr efunc = (EFuncExpr) eexpr;
            FuncAntExpression func = new FuncAntExpression();
            func.setProject(project);
            func.setName(readIdentifier(efunc.getName()));
            for (EExpr arg : efunc.getArguments()) {
                func.addArgument(mapExpr(project, context, arg));
            }
            return func;
        }
        if (eexpr instanceof EInnerElement) {
            EInnerElement elemExpr = (EInnerElement) eexpr;
            return mapCallAntExpression(project, context, mapInnerElement(project, context, elemExpr));
        }
        if (eexpr instanceof EHexLiteralExpr) {
            EHexLiteralExpr eint = (EHexLiteralExpr) eexpr;
            PrimaryAntExpression primary = new PrimaryAntExpression();
            primary.setProject(project);
            primary.setValue(readHex(eint.getValue()));
            return primary;
        }
        if (eexpr instanceof EOctalLiteralExpr) {
            EOctalLiteralExpr eint = (EOctalLiteralExpr) eexpr;
            PrimaryAntExpression primary = new PrimaryAntExpression();
            primary.setProject(project);
            primary.setValue(readOctal(eint.getValue()));
            return primary;
        }
        if (eexpr instanceof EDecimalLiteralExpr) {
            EDecimalLiteralExpr eint = (EDecimalLiteralExpr) eexpr;
            PrimaryAntExpression primary = new PrimaryAntExpression();
            primary.setProject(project);
            primary.setValue(readDecimal(eint.getValue()));
            return primary;
        }
        if (eexpr instanceof EFloatingPointLiteralExpr) {
            EFloatingPointLiteralExpr efloat = (EFloatingPointLiteralExpr) eexpr;
            PrimaryAntExpression primary = new PrimaryAntExpression();
            primary.setProject(project);
            primary.setValue(readFloat(efloat.getValue()));
            return primary;
        }
        if (eexpr instanceof ECharacterLiteralExpr) {
            ECharacterLiteralExpr echar = (ECharacterLiteralExpr) eexpr;
            PrimaryAntExpression primary = new PrimaryAntExpression();
            primary.setProject(project);
            primary.setValue(readChar(echar.getValue()));
            return primary;
        }
        if (eexpr instanceof EStringLiteralExpr) {
            EStringLiteralExpr estring = (EStringLiteralExpr) eexpr;
            PrimaryAntExpression primary = new PrimaryAntExpression();
            primary.setProject(project);
            primary.setValue(readString(estring.getValue()));
            return primary;
        }
        if (eexpr instanceof EBooleanLiteralExpr) {
            EBooleanLiteralExpr ebool = (EBooleanLiteralExpr) eexpr;
            PrimaryAntExpression primary = new PrimaryAntExpression();
            primary.setProject(project);
            primary.setValue(Boolean.parseBoolean(ebool.getValue()));
            return primary;
        }
        if (eexpr instanceof ENullExpr) {
            PrimaryAntExpression primary = new PrimaryAntExpression();
            primary.setProject(project);
            primary.setValue(null);
            return primary;
        }
        throw new IllegalArgumentException("Unsupported expression " + eexpr.getClass().getName());
    }

}
