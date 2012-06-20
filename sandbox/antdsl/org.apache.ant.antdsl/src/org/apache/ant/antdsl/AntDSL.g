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
grammar AntDSL;

@lexer::header {
package org.apache.ant.antdsl.antlr;
}

@parser::header {
package org.apache.ant.antdsl.antlr;

import org.apache.ant.antdsl.*;
import org.apache.ant.antdsl.AbstractAntDslProjectHelper.InnerElement;
import org.apache.ant.antdsl.IfTask.ConditionnalSequential;
import org.apache.ant.antdsl.Target;
import org.apache.ant.antdsl.ExtensionPoint;
import org.apache.ant.antdsl.expr.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.taskdefs.MacroDef.Attribute;
import org.apache.tools.ant.taskdefs.MacroDef.NestedSequential;
import org.apache.tools.ant.taskdefs.MacroDef.TemplateElement;
import org.apache.tools.ant.taskdefs.MacroDef.Text;
import org.apache.tools.ant.taskdefs.condition.*;
import java.util.LinkedHashMap;
}

@parser::members {
    private Project project;

    private AntDslContext context;

    private AntDslAntlrProjectHelper projectHelper;

    public void setProject(Project project) {
        this.project = project;
    }

    public void setContext(AntDslContext context) {
        this.context = context;
    }

    public void setProjectHelper(AntDslAntlrProjectHelper projectHelper) {
        this.projectHelper = projectHelper;
    }
}

project:
    (
        ('name'    ':' name=NAME)?
        ('default' ':' def=NAME)?
        ('basedir' ':' basedir=STRING)?
    )
    { projectHelper.setupProject(project, context, $name.text, projectHelper.readString($basedir.text), $def.text); }
    ( 'namespaces' '{' ( ns=namespace { context.addNamespace(ns.first, ns.second); } )* '}')?
    tl=taskList?
    { for (Task t : tl) { context.getImplicitTarget().addTask(t); } }
    (   target
      | extensionPoint
      | macrodef
    )*;

namespace returns [Pair<String, String> ns = new Pair<String, String>()]:
    NAME { ns.first = $NAME.text; } ':' STRING { ns.second = projectHelper.readString($STRING.text); };

extensionPoint returns [ExtensionPoint ep = new ExtensionPoint()]:
    { context.setCurrentTarget(ep); }
    desc=DOC?
    'extension-point' n=NAME
    ('depends' d=targetList)?
    ('extensionOf' eo=targetList ('onMiss' onMiss=STRING )? )?
    ('if' if_=boolExpr { ep.setIf(if_); } )?
    ('unless' unless=boolExpr { ep.setUnless(unless); } )?
    { projectHelper.mapCommonTarget(ep, project, context, $n.text, projectHelper.readDoc($desc.text), d, eo, $onMiss.text); }
    { context.setCurrentTarget(context.getImplicitTarget()); }
    ;

target returns [Target t = new Target()]:
    { context.setCurrentTarget(t); }
    desc=DOC?
    'target' n=NAME
    ('depends' d=targetList)?
    ('extensionOf' eo=targetList ('onMiss' onMiss=STRING)? )?
    ('if' if_=boolExpr { t.setIf(if_); } )?
    ('unless' unless=boolExpr { t.setUnless(unless); } )?
    { projectHelper.mapCommonTarget(t, project, context, $n.text, projectHelper.readDoc($desc.text), d, eo, $onMiss.text); }
    tl=taskList?
    { for (Task task : tl) { t.addTask(task); } }
    { context.setCurrentTarget(context.getImplicitTarget()); }
    ;

taskList returns [List<Task> tl = new ArrayList<Task>()]:
    '{' (t=task { tl.add(t); } )* '}';

targetList returns [List<String> tl = new ArrayList<String>()]:
    n=NAME { tl.add($n.text); }
    (',' n=NAME { tl.add($n.text); } )*;

task returns [Task t = null]:
      a=assignment {t=a;}
    | ie=innerElement {t=projectHelper.mapUnknown(project, context, ie, false);}
    | b=branch {t=b;}
    ;

nsName returns [Pair<String, String> ns = new Pair<String, String>()]:
    (n=NAME { ns.first = $n.text; } ':')? n=NAME { ns.second = $n.text; } ;

arguments returns [LinkedHashMap<String, String> args = new LinkedHashMap<String, String>();]:
    arg=argument { args.put(arg.first, arg.second); }
    (',' arg=argument { args.put(arg.first, arg.second); } )*;

argument returns [Pair<String, String> arg = new Pair<String, String>()]:
    NAME { arg.first = $NAME.text; } '=' STRING { arg.second = projectHelper.readString($STRING.text); } ;

innerElements returns [List<InnerElement> ies = new ArrayList<InnerElement>()]:
    '{' (ie=innerElement { ies.add(ie); } )+ '}';

innerElement returns [InnerElement ie = new InnerElement()]:
    ns=nsName {ie.ns = ns.first; ie.name = ns.second;}
    '(' args=arguments? { ie.attributes = args; } 
    (','? ies=innerElements)? { ie.children = ies; } ')';

assignment returns [Task assign]:
      p=propertyAssignment { assign = p; }
    | r=refAssignment { assign = r; }
    | l=localAssignment { assign = l; };

propertyAssignment returns [AssignPropertyTask p = new AssignPropertyTask()]:
    'prop'
    { projectHelper.mapCommonTask(project, context, p); }
    NAME { p.setName($NAME.text); } '=' e=expr { p.setValue(e); } ;

refAssignment returns [AssignReferenceTask r = new AssignReferenceTask()]:
    'ref'
    { projectHelper.mapCommonTask(project, context, r); }
    NAME { r.setName($NAME.text); } '=' e=expr { r.setValue(e); } ;

localAssignment returns [AssignLocalTask l = new AssignLocalTask()]:
    'local'
    { projectHelper.mapCommonTask(project, context, l); }
    NAME { l.setName($NAME.text); } '=' e=expr { l.setValue(e); } ;

branch returns [IfTask if_ = new IfTask()]:
    { projectHelper.mapCommonTask(project, context, if_); }
    main=conditionedTasks { if_.setMain(main); }
    ('else' elseif=conditionedTasks { if_.addElseIf(elseif); } )*
    ('else' tl=taskList
        { Sequential seq = new Sequential();
          projectHelper.mapCommonTask(project, context, seq);
          for (Task t : tl) { seq.addTask(t); }
          if_.setElse(seq);
        }
    )?;

conditionedTasks returns [ConditionnalSequential seq = new ConditionnalSequential()]:
    { projectHelper.mapCommonTask(project, context, seq); }
    'if' '(' ie=boolExpr { seq.setCondition(ie); } ')'
    tl=taskList { for (Task t : tl) { seq.addTask(t); } }
    ;

boolExpr returns [Condition c]:
    be=boolXorExpr { c = be; } ;

boolXorExpr returns [Condition c]:
    be=boolOrExpr { c = be; }
    ( '^^' right=boolOrExpr
      { Xor xor = new Xor();
        xor.setProject(project);
        xor.add(c);
        xor.add(right);
        c = xor;
      }
    )*;

boolOrExpr returns [Condition c]:
    be=boolAndExpr { c = be; }
    ( '||' right=boolAndExpr
      { Or or = new Or();
        or.setProject(project);
        or.add(c);
        or.add(right);
        c = or;
      }
    )*;

boolAndExpr returns [Condition c]:
    be=boolPrimaryExpr { c = be; }
    ( '&&' right=boolPrimaryExpr
      { And and = new And();
        and.setProject(project);
        and.add(c);
        and.add(right);
        c = and;
      }
    )*;

boolPrimaryExpr returns [Condition c]:
     ie=innerElement { c = projectHelper.mapExpectedUnknown(project, context, ie, Condition.class); }
   | '(' be=boolExpr { c = be; } ')'
   | be=boolNotExpr { c = be; } ;

boolNotExpr returns [Condition c]:
    '!' be=boolExpr
    { Not not = new Not();
      not.setProject(project);
      not.add(be);
      c = not;
    };

expr returns [AntExpression e]:
    me=multExpr { e = me; } ;

multExpr returns [AntExpression e]:
    ae=addExpr { e = ae; }
    ( '*' right=addExpr
      {  MultiplicationAntExpression me = new MultiplicationAntExpression();
         me.setProject(project);
         me.add(e);
         me.add(right);
         e = me;
      }
    )*;

addExpr returns [AntExpression e]:
    pe=primaryExpr { e = pe; }
    ( '+' right=primaryExpr
      { AddAntExpression ae = new AddAntExpression();
        ae.setProject(project);
        ae.add(e);
        ae.add(right);
        e = ae;
      }
    )*;

primaryExpr returns [AntExpression e]:
      fe=funcExpr { e = fe; }
    | ne=numExpr { e = ne; }
    | se=stringExpr { e = se; }
    | ve=varExpr { e = ve; };

funcExpr returns [FuncAntExpression fe = new FuncAntExpression()]:
    { fe.setProject(project); }
    NAME { fe.setName($NAME.text); }
    '(' arg=expr { fe.addArgument(arg); }
        (',' arg=expr  { fe.addArgument(arg); } )*
    ')';

numExpr returns [PrimaryAntExpression pe = new PrimaryAntExpression()]:
    { pe.setProject(project); }
    INT { pe.setValue(Integer.parseInt($INT.text)); };

stringExpr returns [PrimaryAntExpression pe = new PrimaryAntExpression()]:
    { pe.setProject(project); }
    STRING { pe.setValue(projectHelper.readString($STRING.text)); };

varExpr returns [VariableExpression ve = new VariableExpression()]:
    { ve.setProject(project); }
    VARIABLE { ve.setName(projectHelper.readVariable($VARIABLE.text)); };

macrodef returns [MacroDef macroDef = new MacroDef()]:
    ( DOC { macroDef.setDescription(projectHelper.readDoc($DOC.text)); } )?
    'macrodef' NAME { macroDef.setName($NAME.text); }
    '(' ( atts=attributes
          {  for (Object att : atts) {
                if (att instanceof Attribute) {
                    macroDef.addConfiguredAttribute((Attribute) att);
                } else if (att instanceof Text) {
                    macroDef.addConfiguredText((Text) att);
                } else if (att instanceof TemplateElement) {
                    macroDef.addConfiguredElement((TemplateElement) att);
                } else {
                    throw new IllegalArgumentException("Unsupported macro attribute " + att.getClass().getName());
                }
             }
          } )? ')'
    tl=taskList
    {
        NestedSequential seq = macroDef.createSequential();
        for (Task t : tl) { seq.addTask(t); }
        macroDef.setProject(project);
        macroDef.execute();
    }
    ;

attributes returns [List atts = new ArrayList()]:
    att=attribute { atts.add(att); }
    (',' att=attribute { atts.add(att); } )*;

attribute returns [Object att]:
      aatt=argAttribute { att = aatt; }
    | tatt=textAttribute { att = tatt; }
    | eatt=elementAttribute  { att = eatt; };

argAttribute returns [Attribute att = new Attribute()]:
    'arg'
    NAME { att.setName($NAME.text); }
    ('=' STRING { att.setDefault($STRING.text); } )?;

textAttribute returns [Text text = new Text()]:
    ('optional' { text.setOptional(true); } )?
    ('trimmed' { text.setTrim(true); } )?
    'text'
    NAME { text.setName($NAME.text); };

elementAttribute returns [TemplateElement element = new TemplateElement()]:
    ('optional' { element.setOptional(true); } )?
    ('implicit' { element.setImplicit(true); } )?
    'element'
    NAME { element.setName($NAME.text); };

DOC:
    ( '%' ~('\n'|'\r')* '\r'? '\n' )+
;

NAME:
    ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-'|'.')*
;

PROPERTY:
    '$' NAME
;

INT:
    ('0'..'9')+
;

COMMENT:
        '//' ~('\n'|'\r')* '\r'? '\n' {skip();}
    |   '/*' ( options {greedy=false;} : . )* '*/' {skip();}
;

WS:
    ( ' '
    | '\t'
    | '\r'
    | '\n'
    ) {skip();}
;

STRING:
    (   '"'  ( '\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|'\''|'\\') | ~(('\\'|'"'))  )* '"'
      | '\'' ( '\\' ('b'|'t'|'n'|'f'|'r'|'u'|'"'|'\''|'\\') | ~(('\\'|'\'')) )* '\''
    )
;