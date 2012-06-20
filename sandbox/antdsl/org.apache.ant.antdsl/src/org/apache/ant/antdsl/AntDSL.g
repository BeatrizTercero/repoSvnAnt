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

    private String readString(String s) {
        if (s == null) {
            return null;
        }
        return s.substring(1, s.length() - 1);
    }
}

project:
    (
        ('name'    ':' name=NAME)?
        ('default' ':' def=NAME)?
        ('basedir' ':' basedir=STRING)?
    )
    { projectHelper.setupProject(project, context, $name.text, readString($basedir.text), $def.text); }
    ( 'namespaces' '{' ( ns=namespace { context.addNamespace(ns.first, ns.second); } )* '}')?
    tl=taskLists?
    { for (Task t : tl) { context.getImplicitTarget().addTask(t); } }
    (   target
      | extensionPoint
      | macrodef
    )*;

namespace returns [Pair<String, String> ns = new Pair<String, String>()]:
    NAME { ns.first = $NAME.text; } ':' STRING { ns.second = readString($STRING.text); };

extensionPoint returns [Target t = new Target()]:
    { context.setCurrentTarget(t); }
    desc=DOC?
    'extension-point' n=NAME
    ('depends' d=targetList)?
    ('extensionOf' eo=targetList ('onMiss' onMiss=STRING )? )?
    { projectHelper.mapCommonTarget(t, project, context, $n.text, $desc.text, d, eo, $onMiss.text); }
    { context.setCurrentTarget(context.getImplicitTarget()); }
    ;

target returns [Target t = new Target()]:
    { context.setCurrentTarget(t); }
    desc=DOC?
    'target' n=NAME
    ('depends' d=targetList)?
    ('extensionOf' eo=targetList ('onMiss' onMiss=STRING)? )?
    { projectHelper.mapCommonTarget(t, project, context, $n.text, $desc.text, d, eo, $onMiss.text); }
    tl=taskLists?
    { for (Task task : tl) { t.addTask(task); } }
    { context.setCurrentTarget(context.getImplicitTarget()); }
    ;

taskLists returns [List<Task> tl = new ArrayList<Task>()]:
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
    NAME { arg.first = $NAME.text; } '=' STRING { arg.second = readString($STRING.text); } ;

innerElements returns [List<InnerElement> ies = new ArrayList<InnerElement>()]:
    '{' (ie=innerElement { ies.add(ie); } )+ '}';

innerElement returns [InnerElement ie = new InnerElement()]:
    ns=nsName {ie.ns = ns.first; ie.name = ns.second;}
    '(' args=arguments? { ie.attributes = args; } ')'
    ies=innerElements? { ie.children = ies; };

assignment returns [Task assign]:
    p=propertyAssignment { assign = p; } | r=refAssignment { assign = r; } ;

propertyAssignment returns [Property p = new Property()]:
    'prop'
    { projectHelper.mapCommonTask(project, context, p); }
    NAME { p.setName($NAME.text); } '=' STRING { p.setValue(readString($STRING.text)); } ;

refAssignment returns [RefTask r = new RefTask()]:
    'ref'
    { projectHelper.mapCommonTask(project, context, r); }
    NAME { r.setName($NAME.text); } '=' STRING { r.setValue(readString($STRING.text)); } ;

branch returns [IfTask if_ = new IfTask()]:
    { projectHelper.mapCommonTask(project, context, if_); }
    main=conditionedTasks { if_.setMain(main); }
    ('else' elseif=conditionedTasks { if_.addElseIf(elseif); } )*
    ('else' tl=taskLists
        { Sequential seq = new Sequential();
          projectHelper.mapCommonTask(project, context, seq);
          for (Task t : tl) { seq.addTask(t); }
          if_.setElse(seq);
        }
    )?;

conditionedTasks returns [ConditionnalSequential seq = new ConditionnalSequential()]:
    { projectHelper.mapCommonTask(project, context, seq); }
    'if' '(' ie=innerElement { seq.setCondition(projectHelper.mapExpectedUnknown(project, context, ie, Condition.class)); } ')'
    tl=taskLists { for (Task t : tl) { seq.addTask(t); } }
    ;

macrodef returns [MacroDef macroDef = new MacroDef()]:
    ( DOC { macroDef.setDescription($DOC.text); } )?  
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
    tl=taskLists
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
    '%' ~('\n'|'\r')* '\r'? '\n'
;

PROPERTY:
    '$' NAME
;

NAME:
    ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-'|'.')*
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