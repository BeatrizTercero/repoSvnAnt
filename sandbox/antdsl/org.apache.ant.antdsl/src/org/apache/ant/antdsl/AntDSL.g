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
        ('name'    ':' name=identifier)?
        ('default' ':' def=identifier)?
        ('basedir' ':' basedir=stringLiteral)?
    )
    { projectHelper.setupProject(project, context, name, basedir, def); }
    ( 'namespaces' '{' ( ns=namespace { context.addNamespace(ns.first, ns.second); } )* '}')?
    tl=taskList?
    { for (Task t : tl) { context.getImplicitTarget().addTask(t); } }
    (   target
      | extensionPoint
      | macrodef
    )*
;

namespace returns [Pair<String, String> ns = new Pair<String, String>()]:
    n=identifier { ns.first = n; }
    ':' uri=stringLiteral { ns.second = uri; }
;

extensionPoint returns [ExtensionPoint ep = new ExtensionPoint()]:
    { context.setCurrentTarget(ep); }
    desc=doc?
    'extension-point' n=identifier
    ('depends' d=targetList)?
    ('extensionOf' eo=targetList ('onMiss' onMiss=stringLiteral )? )?
    ('if' if_=expr { ep.setIf(projectHelper.expression2Condition(if_)); } )?
    ('unless' unless=expr { ep.setUnless(projectHelper.expression2Condition(unless)); } )?
    { projectHelper.mapCommonTarget(ep, project, context, n, desc, d, eo, onMiss); }
    { context.setCurrentTarget(context.getImplicitTarget()); }
;

target returns [Target t = new Target()]:
    { context.setCurrentTarget(t); }
    desc=doc?
    'target' n=identifier
    ('depends' d=targetList)?
    ('extensionOf' eo=targetList ('onMiss' onMiss=stringLiteral)? )?
    ('if' if_=expr { t.setIf(projectHelper.expression2Condition(if_)); } )?
    ('unless' unless=expr { t.setUnless(projectHelper.expression2Condition(unless)); } )?
    { projectHelper.mapCommonTarget(t, project, context, n, desc, d, eo, onMiss); }
    tl=taskList?
    { for (Task task : tl) { t.addTask(task); } }
    { context.setCurrentTarget(context.getImplicitTarget()); }
;

taskList returns [List<Task> tl = new ArrayList<Task>()]:
    '{' (t=task { tl.add(t); } )* '}'
;

targetList returns [List<String> tl = new ArrayList<String>()]:
    n=identifier { tl.add(n); }
    (',' n=identifier { tl.add(n); } )*
;

task returns [Task t = null]:
      a=assignment {t=a;}
    | ie=innerElement {t=projectHelper.mapUnknown(project, context, ie, false);}
    | b=branch {t=b;}
;

nsName returns [Pair<String, String> ns = new Pair<String, String>()]:
    (n=identifier { ns.first = n; } ':')? n=identifier { ns.second = n; }
;

arguments returns [LinkedHashMap<String, String> args = new LinkedHashMap<String, String>();]:
    arg=argument { args.put(arg.first, arg.second); }
    (',' arg=argument { args.put(arg.first, arg.second); } )*
;

argument returns [Pair<String, String> arg = new Pair<String, String>()]:
    n=identifier { arg.first = n; } '=' v=stringLiteral { arg.second = v; }
;

innerElements returns [List<InnerElement> ies = new ArrayList<InnerElement>()]:
    '{' (ie=innerElement { ies.add(ie); } )+ '}'
;

innerElement returns [InnerElement ie = new InnerElement()]:
    ns=nsName {ie.ns = ns.first; ie.name = ns.second;}
    '(' args=arguments? { ie.attributes = args; } 
    (','? ies=innerElements)? { ie.children = ies; } ')'
;

assignment returns [Task assign]:
      p=propertyAssignment { assign = p; }
    | r=refAssignment { assign = r; }
    | l=localAssignment { assign = l; }
;

propertyAssignment returns [AssignPropertyTask p = new AssignPropertyTask()]:
    'prop'
    { projectHelper.mapCommonTask(project, context, p); }
    n=identifier { p.setName(n); } '=' e=expr { p.setValue(e); }
;

refAssignment returns [AssignReferenceTask r = new AssignReferenceTask()]:
    'ref'
    { projectHelper.mapCommonTask(project, context, r); }
    n=identifier { r.setName(n); } '=' e=expr { r.setValue(e); }
;

localAssignment returns [AssignLocalTask l = new AssignLocalTask()]:
    'local'
    { projectHelper.mapCommonTask(project, context, l); }
    n=identifier { l.setName(n); } '=' e=expr { l.setValue(e); }
;

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
    )?
;

conditionedTasks returns [ConditionnalSequential seq = new ConditionnalSequential()]:
    { projectHelper.mapCommonTask(project, context, seq); }
    'if' '(' e=expr { seq.setCondition(projectHelper.expression2Condition(e)); } ')'
    tl=taskList { for (Task t : tl) { seq.addTask(t); } }
;

macrodef returns [MacroDef macroDef = new MacroDef()]:
    ( d=doc { macroDef.setDescription(d); } )?
    'macrodef' n=identifier { macroDef.setName(n); }
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
    (',' att=attribute { atts.add(att); } )*
;

attribute returns [Object att]:
      aatt=argAttribute { att = aatt; }
    | tatt=textAttribute { att = tatt; }
    | eatt=elementAttribute  { att = eatt; }
;

argAttribute returns [Attribute att = new Attribute()]:
    'arg'
    n=identifier { att.setName(n); }
    ('=' d=stringLiteral { att.setDefault(d); } )?
;

textAttribute returns [Text text = new Text()]:
    ('optional' { text.setOptional(true); } )?
    ('trimmed' { text.setTrim(true); } )?
    'text'
    n=identifier { text.setName(n); }
;

elementAttribute returns [TemplateElement element = new TemplateElement()]:
    ('optional' { element.setOptional(true); } )?
    ('implicit' { element.setImplicit(true); } )?
    'element'
    n=identifier { element.setName(n); }
;

////////////////
// Expression
////////////////

// an expression is actually a lot like a java one
// highly inspired by http://www.antlr.org/grammar/1152141644268/Java.g
expr returns [AntExpression e]:
    te=ternaryExpr { e = te; }
;

ternaryExpr returns [AntExpression e]:
    cond=conditionnalInclusiveOrExpr { e = cond; }
    ( '?' onTrue=expr ':' onFalse=expr
        {
          TernaryAntExpression te = new TernaryAntExpression();
          te.setProject(project);
          te.setCondition(projectHelper.expression2Condition(e));
          te.setOnTrue(onTrue);
          te.setOnFalse(onFalse);
          e = te;
        }
    )?
;

conditionnalInclusiveOrExpr returns [AntExpression e]:
    left=conditionnalExclusiveOrExpr { e = left; }
    ( '||' right=conditionnalExclusiveOrExpr
      {
        Or or = new Or();
        or.setProject(project);
        or.add(projectHelper.expression2Condition(e));
        or.add(projectHelper.expression2Condition(right));
        e = projectHelper.condition2Expression(or);
      }
    )*
;

conditionnalExclusiveOrExpr returns [AntExpression e]:
    // not a real java operator, but nice to have
    left=conditionnalAndExpr { e = left; }
    ( '^^' right=conditionnalAndExpr
      { 
        Xor xor = new Xor();
        xor.setProject(project);
        xor.add(projectHelper.expression2Condition(e));
        xor.add(projectHelper.expression2Condition(right));
        e = projectHelper.condition2Expression(xor);
      }
    )*
;

conditionnalAndExpr returns [AntExpression e]:
    left=inclusiveOrExpr { e = left; }
    ( '&&' right=inclusiveOrExpr
      {
        And and = new And();
        and.setProject(project);
        and.add(projectHelper.expression2Condition(e));
        and.add(projectHelper.expression2Condition(right));
        e = projectHelper.condition2Expression(and);
      }
    )*
;

inclusiveOrExpr returns [AntExpression e]:
    left=exclusiveOrExpr { e = left; }
    ( '|' right=exclusiveOrExpr
      {
        InclusiveOrAntExpression or = new InclusiveOrAntExpression();
        or.setProject(project);
        or.add(e);
        or.add(left);
        e = or;
      }
    )*
;

exclusiveOrExpr returns [AntExpression e]:
    left=andExpr { e = left; }
    ( '^' right=andExpr
      {
        ExclusiveOrAntExpression or = new ExclusiveOrAntExpression();
        or.setProject(project);
        or.add(e);
        or.add(right);
        e = or;
      }
    )*
;

andExpr returns [AntExpression e]:
    left=equalityExpr { e = left; }
    ( '&' right=equalityExpr
      {
        AndAntExpression and = new AndAntExpression();
        and.setProject(project);
        and.add(e);
        and.add(right);
        e = and;
      }
    )*
;

equalityExpr returns [AntExpression e]:
    left=instanceOfExpr { e = left; }
    ( '==' right=instanceOfExpr
      {
        EqualityCondition equ = new EqualityCondition();
        equ.setProject(project);
        equ.add(e);
        equ.add(right);
        e = equ;
      }
    )*
;

instanceOfExpr returns [AntExpression e]:
    left=relationalExpr { e = left; }
    ( 'instanceof' right=relationalExpr
      {
        InstanceofAntExpression instof = new InstanceofAntExpression();
        instof.setProject(project);
        instof.add(e);
        instof.add(right);
        e = instof;
      }
    )*
;

relationalExpr returns [AntExpression e]:
    left=shiftExpr { e = left; }
    ( op=relationalOp right=shiftExpr
      {
        BinaryAntExpression bae;
        if (op.equals("<=")) {
            bae = new LEAntExpression();
        } else if (op.equals("<")) {
            bae = new LTAntExpression();
        } else if (op.equals(">=")) {
            bae = new GEAntExpression();
        } else if (op.equals(">")) {
            bae = new GTAntExpression();
        } else {
            throw new IllegalStateException("Unsupported relational operator " + op);
        }
        bae.setProject(project);
        bae.add(e);
        bae.add(right);
        e = bae;
      }
    )*
;

relationalOp returns [String s]:
// java spec authorize '<=' and '>=' to be split by spaces or tabs
// for sake of simplicity, we won't here
    op=('<=' | '>=' | '<' | '>') { s = $op.text; }
;

shiftExpr returns [AntExpression e]:
    left=additiveExpr { e = left; }
    ( op=shiftOp right=additiveExpr
      {
        BinaryAntExpression bae;
        if (op.equals("<<")) {
            bae = new LeftShiftAntExpression();
        } else if (op.equals(">>")) {
            bae = new RightShiftAntExpression();
        } else if (op.equals(">>>")) {
            bae = new LogicalRightShiftAntExpression();
        } else {
            throw new IllegalStateException("Unsupported shift operator " + op);
        }
        bae.setProject(project);
        bae.add(e);
        bae.add(right);
        e = bae;
      }
    )*
;

shiftOp returns [String s]:
// idem as relationalOp, no authorized space 
    op=('<<' | '>>>' | '>>') { s = $op.text; }
;

additiveExpr returns [AntExpression e]:
    left=multiplicativeExpr { e = left; }
    ( op=('+' | '-') right=multiplicativeExpr
      {
        BinaryAntExpression bae;
        if ($op.text.equals("+")) {
            bae = new AddAntExpression();
        } else if ($op.text.equals("-")) {
            bae = new MinusAntExpression();
        } else {
            throw new IllegalStateException("Unsupported additive operator " + $op.text);
        }
        bae.setProject(project);
        bae.add(e);
        bae.add(right);
        e = bae;
      }
    )*
;

multiplicativeExpr returns [AntExpression e]:
    left=unaryExpr { e = left; }
    ( op=('*' | '/' | '%') right=unaryExpr
      {
        BinaryAntExpression bae;
        if ($op.text.equals("*")) {
            bae = new MultiplicationAntExpression();
        } else if ($op.text.equals("/")) {
            bae = new DivisionAntExpression();
        } else if ($op.text.equals("\%")) {
            bae = new ModuloAntExpression();
        } else {
            throw new IllegalStateException("Unsupported multiplicative operator " + $op.text);
        }
        bae.setProject(project);
        bae.add(e);
        bae.add(right);
        e = bae;
      }
    )*
;

unaryExpr returns [AntExpression e]:
      '+' ue=unaryExpr
      {
        PositiveAntExpression pae = new PositiveAntExpression();
        pae.setProject(project);
        pae.setExpr(ue);
        e = pae;
      }
    | '-' ue=unaryExpr
      {
        NegativeAntExpression nae = new NegativeAntExpression();
        nae.setProject(project);
        nae.setExpr(ue);
        e = nae;
      }
//    | op='++' expr=unaryExpr
//    | op='--' expr=unaryExpr
    | uenpe=unaryExprNotPlusMinus { e = uenpe; }
;

unaryExprNotPlusMinus returns [AntExpression e]:
      '~' ue=unaryExpr
      {
        NotBitwiseAntExpression not = new NotBitwiseAntExpression();
        not.setProject(project);
        not.setExpr(ue);
        e = not;
      }
    | '!' ue=unaryExpr
      {
        Not not = new Not();
        not.setProject(project);
        not.add(projectHelper.expression2Condition(ue));
        e = projectHelper.condition2Expression(not);
      }
// no need for cast, we are are not staticly typed
//    | castExpr 
//    | primaryExpr selector* ('++'|'--')?
    | pe=primaryExpr { e = pe; }
;

primaryExpr returns [AntExpression e]:
      '(' ex=expr ')' { e = ex; }
    | ve=variableExpr { e = ve; }
    | fe=funcExpr { e = fe; }
    | ie=innerElement { e = projectHelper.mapCallAntExpression(project, context, ie); }
    | le=literalExpr { e = le; }
;

funcExpr returns [FuncAntExpression func = new FuncAntExpression()]:
    { func.setProject(project); }
    n=identifier { func.setName(n); }
    '(' arg=expr { func.addArgument(arg); }
        (',' arg2=expr  { func.addArgument(arg2); } )*
    ')'
;

variableExpr returns [VariableAntExpression var = new VariableAntExpression()]:
    { var.setProject(project); }
    v=variable { var.setName(v); }
;

literalExpr returns [PrimaryAntExpression e]:
      hle=hexLiteralExpr { e = hle; }
    | ole=octalLiteralExpr { e = ole; }
    | dle=decimalLiteralExpr { e = dle; }
    | fple=floatingPointLiteralExpr { e = fple; }
    | cle=characterLiteralExpr { e = cle; }
    | sle=stringLiteralExpr { e = sle; }
    | ble=booleanLiteralExpr { e = ble; }
    | ne=nullExpr { e = ne; }
;

nullExpr returns [PrimaryAntExpression primary = new PrimaryAntExpression()]:
    { primary.setProject(project); }
    'null'
    { primary.setValue(null); }
;

hexLiteralExpr returns [PrimaryAntExpression primary = new PrimaryAntExpression()]:
    { primary.setProject(project); }
    value=hexLiteral
    { primary.setValue(value); }
;

octalLiteralExpr returns [PrimaryAntExpression primary = new PrimaryAntExpression()]:
    { primary.setProject(project); }
    value=octalLiteral
    { primary.setValue(value); }
;

decimalLiteralExpr returns [PrimaryAntExpression primary = new PrimaryAntExpression()]:
    { primary.setProject(project); }
    value=decimalLiteral
    { primary.setValue(value); }
;

floatingPointLiteralExpr returns [PrimaryAntExpression primary = new PrimaryAntExpression()]:
    { primary.setProject(project); }
    value=floatingPointLiteral
    { primary.setValue(value); }
;

characterLiteralExpr returns [PrimaryAntExpression primary = new PrimaryAntExpression()]:
    { primary.setProject(project); }
    value=characterLiteral
    { primary.setValue(value); }
;

stringLiteralExpr returns [PrimaryAntExpression primary = new PrimaryAntExpression()]:
    { primary.setProject(project); }
    value=stringLiteral
    { primary.setValue(value); }
;

booleanLiteralExpr returns [PrimaryAntExpression primary = new PrimaryAntExpression()]:
    { primary.setProject(project); }
      value=('true' | 'false')
    { primary.setValue(Boolean.parseBoolean($value.text)); }
;

hexLiteral returns [Number n]:
    HexLiteral { n = projectHelper.readHex($HexLiteral.text); }
;

HexLiteral:
    '0' ('x'|'X') HexDigit+ IntegerTypeSuffix?
;

decimalLiteral returns [Number n]:
    DecimalLiteral { n = projectHelper.readDecimal($DecimalLiteral.text); }
;

DecimalLiteral:
    ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix?
;

octalLiteral returns [Number n]:
    OctalLiteral { n = projectHelper.readOctal($OctalLiteral.text); }
;

OctalLiteral:
    '0' ('0'..'7')+ IntegerTypeSuffix?
;

fragment HexDigit:
    ('0'..'9'|'a'..'f'|'A'..'F')
;

fragment IntegerTypeSuffix:
    ('l'|'L')
;

floatingPointLiteral returns [Number n]:
    FloatingPointLiteral { n = projectHelper.readFloat($FloatingPointLiteral.text); }
;

FloatingPointLiteral:
      ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    | '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    | ('0'..'9')+ Exponent FloatTypeSuffix?
    | ('0'..'9')+ FloatTypeSuffix
;

fragment Exponent:
    ('e'|'E') ('+'|'-')? ('0'..'9')+
;

fragment FloatTypeSuffix:
    ('f'|'F'|'d'|'D')
;

characterLiteral returns [char c]:
    CharacterLiteral { c = projectHelper.readChar($CharacterLiteral.text); }
;

CharacterLiteral:
    '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
;

stringLiteral returns [String s]:
    StringLiteral { s = projectHelper.readString($StringLiteral.text); }
;

StringLiteral:
    '"' ( EscapeSequence | ~('\\'|'"') )* '"'
;

fragment EscapeSequence:
      '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    | UnicodeEscape
    | OctalEscape
;

fragment OctalEscape:
      '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    | '\\' ('0'..'7') ('0'..'7')
    | '\\' ('0'..'7')
;

fragment UnicodeEscape:
    '\\' 'u' HexDigit HexDigit HexDigit HexDigit
;

identifier returns [String s]:
    Identifier { s = projectHelper.readIdentifier($Identifier.text); }
;

Identifier:
      Letter (Letter|JavaIDDigit)*
    | '`' ~('`')+ '`'
;

fragment Letter:
      '\u0024'
    | '\u0041'..'\u005a'
    | '\u005f'
    | '\u0061'..'\u007a'
    | '\u00c0'..'\u00d6'
    | '\u00d8'..'\u00f6'
    | '\u00f8'..'\u00ff'
    | '\u0100'..'\u1fff'
    | '\u3040'..'\u318f'
    | '\u3300'..'\u337f'
    | '\u3400'..'\u3d2d'
    | '\u4e00'..'\u9fff'
    | '\uf900'..'\ufaff'
;

fragment JavaIDDigit: 
      '\u0030'..'\u0039'
    | '\u0660'..'\u0669'
    | '\u06f0'..'\u06f9'
    | '\u0966'..'\u096f'
    | '\u09e6'..'\u09ef'
    | '\u0a66'..'\u0a6f'
    | '\u0ae6'..'\u0aef'
    | '\u0b66'..'\u0b6f'
    | '\u0be7'..'\u0bef'
    | '\u0c66'..'\u0c6f'
    | '\u0ce6'..'\u0cef'
    | '\u0d66'..'\u0d6f'
    | '\u0e50'..'\u0e59'
    | '\u0ed0'..'\u0ed9'
    | '\u1040'..'\u1049'
;

//// End of copy of Java grammar

variable returns [String s]:
    Variable { s = projectHelper.readVariable($Variable.text); }
;

Variable:
    '$' ( Identifier | '{' ~('}')+ '}' )
;

doc returns [String s]:
    Doc { s = projectHelper.readDoc($Doc.text); }
;

Doc:
    ( '#' ~('\n'|'\r')* '\r'? '\n' )+
;

ML_COMMENT:
    '/*' ( options {greedy=false;} : . )* '*/' {skip();}
;

SL_COMMENT:
    '//' ~('\n'|'\r')* '\r'? '\n' {skip();}
;

WS:
    (' ' | '\t' | '\r' | '\n') {skip();}
;
