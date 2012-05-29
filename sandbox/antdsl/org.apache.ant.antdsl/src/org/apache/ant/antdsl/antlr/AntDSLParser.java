// $ANTLR 3.4 /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g 2012-05-28 23:52:12

package org.apache.ant.antdsl.antlr;

import org.apache.ant.antdsl.*;
import org.apache.ant.antdsl.AbstractAntDslProjectHelper.InnerElement;
import org.apache.ant.antdsl.IfTask.ConditionnalSequential;
import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.taskdefs.condition.*;
import java.util.LinkedHashMap;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class AntDSLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "COMMENT", "DOC", "NAME", "PROPERTY", "STRING", "WS", "'('", "')'", "','", "':'", "'='", "'basedir'", "'default'", "'depends'", "'element'", "'else'", "'extension-point'", "'extensionOf'", "'if'", "'implicit'", "'macrodef'", "'name'", "'namespaces'", "'optional'", "'target'", "'text'", "'trimmed'", "'{'", "'}'"
    };

    public static final int EOF=-1;
    public static final int T__10=10;
    public static final int T__11=11;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int T__15=15;
    public static final int T__16=16;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int T__32=32;
    public static final int COMMENT=4;
    public static final int DOC=5;
    public static final int NAME=6;
    public static final int PROPERTY=7;
    public static final int STRING=8;
    public static final int WS=9;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public AntDSLParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public AntDSLParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return AntDSLParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g"; }


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



    // $ANTLR start "project"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:46:1: project : ( ( 'name' ':' name= NAME )? ( 'default' ':' def= NAME )? ( 'basedir' ':' basedir= STRING )? ) ( 'namespaces' '{' (ns= namespace )* '}' )? (tl= taskLists )? ( target | extensionPoint | macrodef )* ;
    public final void project() throws RecognitionException {
        Token name=null;
        Token def=null;
        Token basedir=null;
        Pair<String, String> ns =null;

        List<Task> tl =null;


        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:46:8: ( ( ( 'name' ':' name= NAME )? ( 'default' ':' def= NAME )? ( 'basedir' ':' basedir= STRING )? ) ( 'namespaces' '{' (ns= namespace )* '}' )? (tl= taskLists )? ( target | extensionPoint | macrodef )* )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:47:5: ( ( 'name' ':' name= NAME )? ( 'default' ':' def= NAME )? ( 'basedir' ':' basedir= STRING )? ) ( 'namespaces' '{' (ns= namespace )* '}' )? (tl= taskLists )? ( target | extensionPoint | macrodef )*
            {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:47:5: ( ( 'name' ':' name= NAME )? ( 'default' ':' def= NAME )? ( 'basedir' ':' basedir= STRING )? )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:48:9: ( 'name' ':' name= NAME )? ( 'default' ':' def= NAME )? ( 'basedir' ':' basedir= STRING )?
            {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:48:9: ( 'name' ':' name= NAME )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==25) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:48:10: 'name' ':' name= NAME
                    {
                    match(input,25,FOLLOW_25_in_project51); 

                    match(input,13,FOLLOW_13_in_project56); 

                    name=(Token)match(input,NAME,FOLLOW_NAME_in_project60); 

                    }
                    break;

            }


            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:49:9: ( 'default' ':' def= NAME )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==16) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:49:10: 'default' ':' def= NAME
                    {
                    match(input,16,FOLLOW_16_in_project73); 

                    match(input,13,FOLLOW_13_in_project75); 

                    def=(Token)match(input,NAME,FOLLOW_NAME_in_project79); 

                    }
                    break;

            }


            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:50:9: ( 'basedir' ':' basedir= STRING )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==15) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:50:10: 'basedir' ':' basedir= STRING
                    {
                    match(input,15,FOLLOW_15_in_project92); 

                    match(input,13,FOLLOW_13_in_project94); 

                    basedir=(Token)match(input,STRING,FOLLOW_STRING_in_project98); 

                    }
                    break;

            }


            }


             projectHelper.setupProject(project, context, (name!=null?name.getText():null), readString((basedir!=null?basedir.getText():null)), (def!=null?def.getText():null)); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:53:5: ( 'namespaces' '{' (ns= namespace )* '}' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==26) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:53:7: 'namespaces' '{' (ns= namespace )* '}'
                    {
                    match(input,26,FOLLOW_26_in_project120); 

                    match(input,31,FOLLOW_31_in_project122); 

                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:53:24: (ns= namespace )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==NAME) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:53:26: ns= namespace
                    	    {
                    	    pushFollow(FOLLOW_namespace_in_project128);
                    	    ns=namespace();

                    	    state._fsp--;


                    	     context.addNamespace(ns.first, ns.second); 

                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);


                    match(input,32,FOLLOW_32_in_project135); 

                    }
                    break;

            }


            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:54:7: (tl= taskLists )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==31) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:54:7: tl= taskLists
                    {
                    pushFollow(FOLLOW_taskLists_in_project145);
                    tl=taskLists();

                    state._fsp--;


                    }
                    break;

            }


             for (Task t : tl) { context.getImplicitTarget().addTask(t); } 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:56:5: ( target | extensionPoint | macrodef )*
            loop7:
            do {
                int alt7=4;
                switch ( input.LA(1) ) {
                case DOC:
                    {
                    int LA7_2 = input.LA(2);

                    if ( (LA7_2==28) ) {
                        alt7=1;
                    }
                    else if ( (LA7_2==20) ) {
                        alt7=2;
                    }


                    }
                    break;
                case 28:
                    {
                    alt7=1;
                    }
                    break;
                case 20:
                    {
                    alt7=2;
                    }
                    break;
                case 24:
                    {
                    alt7=3;
                    }
                    break;

                }

                switch (alt7) {
            	case 1 :
            	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:56:9: target
            	    {
            	    pushFollow(FOLLOW_target_in_project162);
            	    target();

            	    state._fsp--;


            	    }
            	    break;
            	case 2 :
            	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:57:9: extensionPoint
            	    {
            	    pushFollow(FOLLOW_extensionPoint_in_project172);
            	    extensionPoint();

            	    state._fsp--;


            	    }
            	    break;
            	case 3 :
            	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:58:9: macrodef
            	    {
            	    pushFollow(FOLLOW_macrodef_in_project182);
            	    macrodef();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "project"



    // $ANTLR start "namespace"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:61:1: namespace returns [Pair<String, String> ns = new Pair<String, String>()] : NAME ':' STRING ;
    public final Pair<String, String> namespace() throws RecognitionException {
        Pair<String, String> ns =  new Pair<String, String>();


        Token NAME1=null;
        Token STRING2=null;

        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:61:73: ( NAME ':' STRING )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:62:5: NAME ':' STRING
            {
            NAME1=(Token)match(input,NAME,FOLLOW_NAME_in_namespace204); 

             ns.first = (NAME1!=null?NAME1.getText():null); 

            match(input,13,FOLLOW_13_in_namespace208); 

            STRING2=(Token)match(input,STRING,FOLLOW_STRING_in_namespace210); 

             ns.second = readString((STRING2!=null?STRING2.getText():null)); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ns;
    }
    // $ANTLR end "namespace"



    // $ANTLR start "extensionPoint"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:64:1: extensionPoint returns [Target t = new Target()] : (desc= DOC )? 'extension-point' n= NAME ( 'extensionOf' eo= targetList )? ( 'depends' d= targetList )? ;
    public final Target extensionPoint() throws RecognitionException {
        Target t =  new Target();


        Token desc=null;
        Token n=null;
        List<String> eo =null;

        List<String> d =null;


        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:64:49: ( (desc= DOC )? 'extension-point' n= NAME ( 'extensionOf' eo= targetList )? ( 'depends' d= targetList )? )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:65:5: (desc= DOC )? 'extension-point' n= NAME ( 'extensionOf' eo= targetList )? ( 'depends' d= targetList )?
            {
             context.setCurrentTarget(t); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:66:9: (desc= DOC )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==DOC) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:66:9: desc= DOC
                    {
                    desc=(Token)match(input,DOC,FOLLOW_DOC_in_extensionPoint235); 

                    }
                    break;

            }


            match(input,20,FOLLOW_20_in_extensionPoint242); 

            n=(Token)match(input,NAME,FOLLOW_NAME_in_extensionPoint246); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:68:5: ( 'extensionOf' eo= targetList )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==21) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:68:6: 'extensionOf' eo= targetList
                    {
                    match(input,21,FOLLOW_21_in_extensionPoint253); 

                    pushFollow(FOLLOW_targetList_in_extensionPoint257);
                    eo=targetList();

                    state._fsp--;


                    }
                    break;

            }


            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:69:5: ( 'depends' d= targetList )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==17) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:69:6: 'depends' d= targetList
                    {
                    match(input,17,FOLLOW_17_in_extensionPoint266); 

                    pushFollow(FOLLOW_targetList_in_extensionPoint270);
                    d=targetList();

                    state._fsp--;


                    }
                    break;

            }


             projectHelper.mapCommonTarget(t, project, context, (n!=null?n.getText():null), (desc!=null?desc.getText():null), d, eo); 

             context.setCurrentTarget(context.getImplicitTarget()); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return t;
    }
    // $ANTLR end "extensionPoint"



    // $ANTLR start "target"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:74:1: target returns [Target t = new Target()] : (desc= DOC )? 'target' n= NAME ( 'extensionOf' eo= targetList )? ( 'depends' d= targetList )? (tl= taskLists )? ;
    public final Target target() throws RecognitionException {
        Target t =  new Target();


        Token desc=null;
        Token n=null;
        List<String> eo =null;

        List<String> d =null;

        List<Task> tl =null;


        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:74:41: ( (desc= DOC )? 'target' n= NAME ( 'extensionOf' eo= targetList )? ( 'depends' d= targetList )? (tl= taskLists )? )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:75:5: (desc= DOC )? 'target' n= NAME ( 'extensionOf' eo= targetList )? ( 'depends' d= targetList )? (tl= taskLists )?
            {
             context.setCurrentTarget(t); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:76:9: (desc= DOC )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==DOC) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:76:9: desc= DOC
                    {
                    desc=(Token)match(input,DOC,FOLLOW_DOC_in_target312); 

                    }
                    break;

            }


            match(input,28,FOLLOW_28_in_target319); 

            n=(Token)match(input,NAME,FOLLOW_NAME_in_target323); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:78:5: ( 'extensionOf' eo= targetList )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==21) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:78:6: 'extensionOf' eo= targetList
                    {
                    match(input,21,FOLLOW_21_in_target330); 

                    pushFollow(FOLLOW_targetList_in_target334);
                    eo=targetList();

                    state._fsp--;


                    }
                    break;

            }


            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:79:5: ( 'depends' d= targetList )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==17) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:79:6: 'depends' d= targetList
                    {
                    match(input,17,FOLLOW_17_in_target343); 

                    pushFollow(FOLLOW_targetList_in_target347);
                    d=targetList();

                    state._fsp--;


                    }
                    break;

            }


             projectHelper.mapCommonTarget(t, project, context, (n!=null?n.getText():null), (desc!=null?desc.getText():null), d, eo); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:81:7: (tl= taskLists )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==31) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:81:7: tl= taskLists
                    {
                    pushFollow(FOLLOW_taskLists_in_target363);
                    tl=taskLists();

                    state._fsp--;


                    }
                    break;

            }


             for (Task task : tl) { t.addTask(task); } 

             context.setCurrentTarget(context.getImplicitTarget()); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return t;
    }
    // $ANTLR end "target"



    // $ANTLR start "taskLists"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:86:1: taskLists returns [List<Task> tl = new ArrayList<Task>()] : '{' (t= task )+ '}' ;
    public final List<Task> taskLists() throws RecognitionException {
        List<Task> tl =  new ArrayList<Task>();


        Task t =null;


        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:86:58: ( '{' (t= task )+ '}' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:87:5: '{' (t= task )+ '}'
            {
            match(input,31,FOLLOW_31_in_taskLists396); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:87:9: (t= task )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==NAME||LA15_0==22) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:87:10: t= task
            	    {
            	    pushFollow(FOLLOW_task_in_taskLists401);
            	    t=task();

            	    state._fsp--;


            	     tl.add(t); 

            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);


            match(input,32,FOLLOW_32_in_taskLists408); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return tl;
    }
    // $ANTLR end "taskLists"



    // $ANTLR start "targetList"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:89:1: targetList returns [List<String> tl = new ArrayList<String>()] : n= NAME ( ',' n= NAME )* ;
    public final List<String> targetList() throws RecognitionException {
        List<String> tl =  new ArrayList<String>();


        Token n=null;

        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:89:63: (n= NAME ( ',' n= NAME )* )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:90:5: n= NAME ( ',' n= NAME )*
            {
            n=(Token)match(input,NAME,FOLLOW_NAME_in_targetList425); 

             tl.add((n!=null?n.getText():null)); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:91:5: ( ',' n= NAME )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==12) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:91:6: ',' n= NAME
            	    {
            	    match(input,12,FOLLOW_12_in_targetList434); 

            	    n=(Token)match(input,NAME,FOLLOW_NAME_in_targetList438); 

            	     tl.add((n!=null?n.getText():null)); 

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return tl;
    }
    // $ANTLR end "targetList"



    // $ANTLR start "task"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:93:1: task returns [Task t = null] : (pa= propertyAssignment |ie= innerElement |b= branch );
    public final Task task() throws RecognitionException {
        Task t =  null;


        Property pa =null;

        InnerElement ie =null;

        IfTask b =null;


        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:93:29: (pa= propertyAssignment |ie= innerElement |b= branch )
            int alt17=3;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==NAME) ) {
                int LA17_1 = input.LA(2);

                if ( (LA17_1==14) ) {
                    alt17=1;
                }
                else if ( (LA17_1==10||LA17_1==13) ) {
                    alt17=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 17, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA17_0==22) ) {
                alt17=3;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;

            }
            switch (alt17) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:94:7: pa= propertyAssignment
                    {
                    pushFollow(FOLLOW_propertyAssignment_in_task462);
                    pa=propertyAssignment();

                    state._fsp--;


                    t=pa;

                    }
                    break;
                case 2 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:95:7: ie= innerElement
                    {
                    pushFollow(FOLLOW_innerElement_in_task474);
                    ie=innerElement();

                    state._fsp--;


                    t=projectHelper.mapUnknown(project, context, ie, false);

                    }
                    break;
                case 3 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:96:7: b= branch
                    {
                    pushFollow(FOLLOW_branch_in_task486);
                    b=branch();

                    state._fsp--;


                    t=b;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return t;
    }
    // $ANTLR end "task"



    // $ANTLR start "nsName"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:99:1: nsName returns [Pair<String, String> ns = new Pair<String, String>()] : (n= NAME ':' )? n= NAME ;
    public final Pair<String, String> nsName() throws RecognitionException {
        Pair<String, String> ns =  new Pair<String, String>();


        Token n=null;

        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:99:70: ( (n= NAME ':' )? n= NAME )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:100:5: (n= NAME ':' )? n= NAME
            {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:100:5: (n= NAME ':' )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0==NAME) ) {
                int LA18_1 = input.LA(2);

                if ( (LA18_1==13) ) {
                    alt18=1;
                }
            }
            switch (alt18) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:100:6: n= NAME ':'
                    {
                    n=(Token)match(input,NAME,FOLLOW_NAME_in_nsName511); 

                     ns.first = (n!=null?n.getText():null); 

                    match(input,13,FOLLOW_13_in_nsName515); 

                    }
                    break;

            }


            n=(Token)match(input,NAME,FOLLOW_NAME_in_nsName521); 

             ns.second = (n!=null?n.getText():null); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ns;
    }
    // $ANTLR end "nsName"



    // $ANTLR start "arguments"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:102:1: arguments returns [LinkedHashMap<String, String> args = new LinkedHashMap<String, String>();] : arg= argument ( ',' arg= argument )* ;
    public final LinkedHashMap<String, String> arguments() throws RecognitionException {
        LinkedHashMap<String, String> args =  new LinkedHashMap<String, String>();;


        Pair<String, String> arg =null;


        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:102:94: (arg= argument ( ',' arg= argument )* )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:103:5: arg= argument ( ',' arg= argument )*
            {
            pushFollow(FOLLOW_argument_in_arguments541);
            arg=argument();

            state._fsp--;


             args.put(arg.first, arg.second); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:104:5: ( ',' arg= argument )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==12) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:104:6: ',' arg= argument
            	    {
            	    match(input,12,FOLLOW_12_in_arguments550); 

            	    pushFollow(FOLLOW_argument_in_arguments554);
            	    arg=argument();

            	    state._fsp--;


            	     args.put(arg.first, arg.second); 

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return args;
    }
    // $ANTLR end "arguments"



    // $ANTLR start "argument"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:106:1: argument returns [Pair<String, String> arg = new Pair<String, String>()] : NAME '=' STRING ;
    public final Pair<String, String> argument() throws RecognitionException {
        Pair<String, String> arg =  new Pair<String, String>();


        Token NAME3=null;
        Token STRING4=null;

        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:106:73: ( NAME '=' STRING )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:107:5: NAME '=' STRING
            {
            NAME3=(Token)match(input,NAME,FOLLOW_NAME_in_argument574); 

             arg.first = (NAME3!=null?NAME3.getText():null); 

            match(input,14,FOLLOW_14_in_argument578); 

            STRING4=(Token)match(input,STRING,FOLLOW_STRING_in_argument580); 

             arg.second = readString((STRING4!=null?STRING4.getText():null)); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return arg;
    }
    // $ANTLR end "argument"



    // $ANTLR start "innerElements"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:109:1: innerElements returns [List<InnerElement> ies = new ArrayList<InnerElement>()] : '{' (ie= innerElement )+ '}' ;
    public final List<InnerElement> innerElements() throws RecognitionException {
        List<InnerElement> ies =  new ArrayList<InnerElement>();


        InnerElement ie =null;


        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:109:79: ( '{' (ie= innerElement )+ '}' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:110:5: '{' (ie= innerElement )+ '}'
            {
            match(input,31,FOLLOW_31_in_innerElements598); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:110:9: (ie= innerElement )+
            int cnt20=0;
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==NAME) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:110:10: ie= innerElement
            	    {
            	    pushFollow(FOLLOW_innerElement_in_innerElements603);
            	    ie=innerElement();

            	    state._fsp--;


            	     ies.add(ie); 

            	    }
            	    break;

            	default :
            	    if ( cnt20 >= 1 ) break loop20;
                        EarlyExitException eee =
                            new EarlyExitException(20, input);
                        throw eee;
                }
                cnt20++;
            } while (true);


            match(input,32,FOLLOW_32_in_innerElements610); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ies;
    }
    // $ANTLR end "innerElements"



    // $ANTLR start "innerElement"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:112:1: innerElement returns [InnerElement ie = new InnerElement()] : ns= nsName '(' (args= arguments )? ')' (ies= innerElements )? ;
    public final InnerElement innerElement() throws RecognitionException {
        InnerElement ie =  new InnerElement();


        Pair<String, String> ns =null;

        LinkedHashMap<String, String> args =null;

        List<InnerElement> ies =null;


        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:112:60: (ns= nsName '(' (args= arguments )? ')' (ies= innerElements )? )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:113:5: ns= nsName '(' (args= arguments )? ')' (ies= innerElements )?
            {
            pushFollow(FOLLOW_nsName_in_innerElement627);
            ns=nsName();

            state._fsp--;


            ie.ns = ns.first; ie.name = ns.second;

            match(input,10,FOLLOW_10_in_innerElement635); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:114:13: (args= arguments )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==NAME) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:114:13: args= arguments
                    {
                    pushFollow(FOLLOW_arguments_in_innerElement639);
                    args=arguments();

                    state._fsp--;


                    }
                    break;

            }


             ie.attributes = args; 

            match(input,11,FOLLOW_11_in_innerElement644); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:115:8: (ies= innerElements )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==31) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:115:8: ies= innerElements
                    {
                    pushFollow(FOLLOW_innerElements_in_innerElement652);
                    ies=innerElements();

                    state._fsp--;


                    }
                    break;

            }


             ie.children = ies; 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ie;
    }
    // $ANTLR end "innerElement"



    // $ANTLR start "propertyAssignment"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:117:1: propertyAssignment returns [Property p = new Property()] : NAME '=' STRING ;
    public final Property propertyAssignment() throws RecognitionException {
        Property p =  new Property();


        Token NAME5=null;
        Token STRING6=null;

        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:117:57: ( NAME '=' STRING )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:118:5: NAME '=' STRING
            {
             projectHelper.mapCommonTask(project, context, p); 

            NAME5=(Token)match(input,NAME,FOLLOW_NAME_in_propertyAssignment676); 

             p.setName((NAME5!=null?NAME5.getText():null)); 

            match(input,14,FOLLOW_14_in_propertyAssignment680); 

            STRING6=(Token)match(input,STRING,FOLLOW_STRING_in_propertyAssignment682); 

             p.setValue(readString((STRING6!=null?STRING6.getText():null))); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return p;
    }
    // $ANTLR end "propertyAssignment"



    // $ANTLR start "branch"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:121:1: branch returns [IfTask if_ = new IfTask()] :main= conditionedTasks ( 'else' elseif= conditionedTasks )* ( 'else' tl= taskLists )? ;
    public final IfTask branch() throws RecognitionException {
        IfTask if_ =  new IfTask();


        ConditionnalSequential main =null;

        ConditionnalSequential elseif =null;

        List<Task> tl =null;


        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:121:43: (main= conditionedTasks ( 'else' elseif= conditionedTasks )* ( 'else' tl= taskLists )? )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:122:5: main= conditionedTasks ( 'else' elseif= conditionedTasks )* ( 'else' tl= taskLists )?
            {
             projectHelper.mapCommonTask(project, context, if_); 

            pushFollow(FOLLOW_conditionedTasks_in_branch708);
            main=conditionedTasks();

            state._fsp--;


             if_.setMain(main); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:124:5: ( 'else' elseif= conditionedTasks )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==19) ) {
                    int LA23_1 = input.LA(2);

                    if ( (LA23_1==22) ) {
                        alt23=1;
                    }


                }


                switch (alt23) {
            	case 1 :
            	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:124:6: 'else' elseif= conditionedTasks
            	    {
            	    match(input,19,FOLLOW_19_in_branch717); 

            	    pushFollow(FOLLOW_conditionedTasks_in_branch721);
            	    elseif=conditionedTasks();

            	    state._fsp--;


            	     if_.addElseIf(elseif); 

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);


            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:125:5: ( 'else' tl= taskLists )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==19) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:125:6: 'else' tl= taskLists
                    {
                    match(input,19,FOLLOW_19_in_branch733); 

                    pushFollow(FOLLOW_taskLists_in_branch737);
                    tl=taskLists();

                    state._fsp--;


                     Sequential seq = new Sequential();
                              projectHelper.mapCommonTask(project, context, seq);
                              for (Task t : tl) { seq.addTask(t); }
                              if_.setElse(seq);
                            

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return if_;
    }
    // $ANTLR end "branch"



    // $ANTLR start "conditionedTasks"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:133:1: conditionedTasks returns [ConditionnalSequential seq = new ConditionnalSequential()] : 'if' '(' ie= innerElement ')' tl= taskLists ;
    public final ConditionnalSequential conditionedTasks() throws RecognitionException {
        ConditionnalSequential seq =  new ConditionnalSequential();


        InnerElement ie =null;

        List<Task> tl =null;


        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:133:85: ( 'if' '(' ie= innerElement ')' tl= taskLists )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:134:5: 'if' '(' ie= innerElement ')' tl= taskLists
            {
             projectHelper.mapCommonTask(project, context, seq); 

            match(input,22,FOLLOW_22_in_conditionedTasks775); 

            match(input,10,FOLLOW_10_in_conditionedTasks777); 

            pushFollow(FOLLOW_innerElement_in_conditionedTasks781);
            ie=innerElement();

            state._fsp--;


             seq.setCondition(projectHelper.mapExpectedUnknown(project, context, ie, Condition.class)); 

            match(input,11,FOLLOW_11_in_conditionedTasks785); 

            pushFollow(FOLLOW_taskLists_in_conditionedTasks793);
            tl=taskLists();

            state._fsp--;


             for (Task t : tl) { seq.addTask(t); } 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return seq;
    }
    // $ANTLR end "conditionedTasks"



    // $ANTLR start "macrodef"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:139:1: macrodef : 'macrodef' NAME '(' ( attributes )? ')' taskLists ;
    public final void macrodef() throws RecognitionException {
        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:139:9: ( 'macrodef' NAME '(' ( attributes )? ')' taskLists )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:140:5: 'macrodef' NAME '(' ( attributes )? ')' taskLists
            {
            match(input,24,FOLLOW_24_in_macrodef811); 

            match(input,NAME,FOLLOW_NAME_in_macrodef813); 

            match(input,10,FOLLOW_10_in_macrodef815); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:140:25: ( attributes )?
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==NAME||LA25_0==18||LA25_0==29) ) {
                alt25=1;
            }
            switch (alt25) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:140:25: attributes
                    {
                    pushFollow(FOLLOW_attributes_in_macrodef817);
                    attributes();

                    state._fsp--;


                    }
                    break;

            }


            match(input,11,FOLLOW_11_in_macrodef820); 

            pushFollow(FOLLOW_taskLists_in_macrodef822);
            taskLists();

            state._fsp--;


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "macrodef"



    // $ANTLR start "attributes"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:142:1: attributes : attribute ( ',' attribute )* ;
    public final void attributes() throws RecognitionException {
        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:142:11: ( attribute ( ',' attribute )* )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:143:5: attribute ( ',' attribute )*
            {
            pushFollow(FOLLOW_attribute_in_attributes833);
            attribute();

            state._fsp--;


            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:143:15: ( ',' attribute )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==12) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:143:16: ',' attribute
            	    {
            	    match(input,12,FOLLOW_12_in_attributes836); 

            	    pushFollow(FOLLOW_attribute_in_attributes838);
            	    attribute();

            	    state._fsp--;


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "attributes"



    // $ANTLR start "attribute"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:145:1: attribute : ( simpleAttribute | textAttribute | elementAttribute );
    public final void attribute() throws RecognitionException {
        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:145:10: ( simpleAttribute | textAttribute | elementAttribute )
            int alt27=3;
            switch ( input.LA(1) ) {
            case NAME:
                {
                alt27=1;
                }
                break;
            case 29:
                {
                alt27=2;
                }
                break;
            case 18:
                {
                alt27=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;

            }

            switch (alt27) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:146:5: simpleAttribute
                    {
                    pushFollow(FOLLOW_simpleAttribute_in_attribute851);
                    simpleAttribute();

                    state._fsp--;


                    }
                    break;
                case 2 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:146:23: textAttribute
                    {
                    pushFollow(FOLLOW_textAttribute_in_attribute855);
                    textAttribute();

                    state._fsp--;


                    }
                    break;
                case 3 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:146:39: elementAttribute
                    {
                    pushFollow(FOLLOW_elementAttribute_in_attribute859);
                    elementAttribute();

                    state._fsp--;


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "attribute"



    // $ANTLR start "simpleAttribute"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:148:1: simpleAttribute : NAME ( '=' STRING )? ;
    public final void simpleAttribute() throws RecognitionException {
        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:148:16: ( NAME ( '=' STRING )? )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:149:5: NAME ( '=' STRING )?
            {
            match(input,NAME,FOLLOW_NAME_in_simpleAttribute870); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:149:10: ( '=' STRING )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==14) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:149:11: '=' STRING
                    {
                    match(input,14,FOLLOW_14_in_simpleAttribute873); 

                    match(input,STRING,FOLLOW_STRING_in_simpleAttribute875); 

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "simpleAttribute"



    // $ANTLR start "textAttribute"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:151:1: textAttribute : 'text' ( 'optional' )? ( 'trimmed' )? NAME ;
    public final void textAttribute() throws RecognitionException {
        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:151:14: ( 'text' ( 'optional' )? ( 'trimmed' )? NAME )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:152:5: 'text' ( 'optional' )? ( 'trimmed' )? NAME
            {
            match(input,29,FOLLOW_29_in_textAttribute888); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:152:12: ( 'optional' )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==27) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:152:12: 'optional'
                    {
                    match(input,27,FOLLOW_27_in_textAttribute890); 

                    }
                    break;

            }


            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:152:24: ( 'trimmed' )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==30) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:152:24: 'trimmed'
                    {
                    match(input,30,FOLLOW_30_in_textAttribute893); 

                    }
                    break;

            }


            match(input,NAME,FOLLOW_NAME_in_textAttribute896); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "textAttribute"



    // $ANTLR start "elementAttribute"
    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:154:1: elementAttribute : 'element' ( 'optional' )? ( 'implicit' )? NAME ;
    public final void elementAttribute() throws RecognitionException {
        try {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:154:17: ( 'element' ( 'optional' )? ( 'implicit' )? NAME )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:155:5: 'element' ( 'optional' )? ( 'implicit' )? NAME
            {
            match(input,18,FOLLOW_18_in_elementAttribute907); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:155:15: ( 'optional' )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==27) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:155:15: 'optional'
                    {
                    match(input,27,FOLLOW_27_in_elementAttribute909); 

                    }
                    break;

            }


            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:155:27: ( 'implicit' )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==23) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:155:27: 'implicit'
                    {
                    match(input,23,FOLLOW_23_in_elementAttribute912); 

                    }
                    break;

            }


            match(input,NAME,FOLLOW_NAME_in_elementAttribute915); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "elementAttribute"

    // Delegated rules


 

    public static final BitSet FOLLOW_25_in_project51 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_project56 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_project60 = new BitSet(new long[]{0x0000000095118022L});
    public static final BitSet FOLLOW_16_in_project73 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_project75 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_project79 = new BitSet(new long[]{0x0000000095108022L});
    public static final BitSet FOLLOW_15_in_project92 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_project94 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_project98 = new BitSet(new long[]{0x0000000095100022L});
    public static final BitSet FOLLOW_26_in_project120 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_project122 = new BitSet(new long[]{0x0000000100000040L});
    public static final BitSet FOLLOW_namespace_in_project128 = new BitSet(new long[]{0x0000000100000040L});
    public static final BitSet FOLLOW_32_in_project135 = new BitSet(new long[]{0x0000000091100022L});
    public static final BitSet FOLLOW_taskLists_in_project145 = new BitSet(new long[]{0x0000000011100022L});
    public static final BitSet FOLLOW_target_in_project162 = new BitSet(new long[]{0x0000000011100022L});
    public static final BitSet FOLLOW_extensionPoint_in_project172 = new BitSet(new long[]{0x0000000011100022L});
    public static final BitSet FOLLOW_macrodef_in_project182 = new BitSet(new long[]{0x0000000011100022L});
    public static final BitSet FOLLOW_NAME_in_namespace204 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_namespace208 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_namespace210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOC_in_extensionPoint235 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_extensionPoint242 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_extensionPoint246 = new BitSet(new long[]{0x0000000000220002L});
    public static final BitSet FOLLOW_21_in_extensionPoint253 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_targetList_in_extensionPoint257 = new BitSet(new long[]{0x0000000000020002L});
    public static final BitSet FOLLOW_17_in_extensionPoint266 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_targetList_in_extensionPoint270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOC_in_target312 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_target319 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_target323 = new BitSet(new long[]{0x0000000080220002L});
    public static final BitSet FOLLOW_21_in_target330 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_targetList_in_target334 = new BitSet(new long[]{0x0000000080020002L});
    public static final BitSet FOLLOW_17_in_target343 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_targetList_in_target347 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_taskLists_in_target363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_taskLists396 = new BitSet(new long[]{0x0000000000400040L});
    public static final BitSet FOLLOW_task_in_taskLists401 = new BitSet(new long[]{0x0000000100400040L});
    public static final BitSet FOLLOW_32_in_taskLists408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_targetList425 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_12_in_targetList434 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_targetList438 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_propertyAssignment_in_task462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_innerElement_in_task474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_branch_in_task486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_nsName511 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_nsName515 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_nsName521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_argument_in_arguments541 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_12_in_arguments550 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_argument_in_arguments554 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_NAME_in_argument574 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_argument578 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_argument580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_innerElements598 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_innerElement_in_innerElements603 = new BitSet(new long[]{0x0000000100000040L});
    public static final BitSet FOLLOW_32_in_innerElements610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nsName_in_innerElement627 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_innerElement635 = new BitSet(new long[]{0x0000000000000840L});
    public static final BitSet FOLLOW_arguments_in_innerElement639 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_innerElement644 = new BitSet(new long[]{0x0000000080000002L});
    public static final BitSet FOLLOW_innerElements_in_innerElement652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_propertyAssignment676 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_14_in_propertyAssignment680 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_propertyAssignment682 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionedTasks_in_branch708 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_19_in_branch717 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_conditionedTasks_in_branch721 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_19_in_branch733 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_taskLists_in_branch737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_conditionedTasks775 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_conditionedTasks777 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_innerElement_in_conditionedTasks781 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_conditionedTasks785 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_taskLists_in_conditionedTasks793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_macrodef811 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_macrodef813 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_10_in_macrodef815 = new BitSet(new long[]{0x0000000020040840L});
    public static final BitSet FOLLOW_attributes_in_macrodef817 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_11_in_macrodef820 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_taskLists_in_macrodef822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_attributes833 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_12_in_attributes836 = new BitSet(new long[]{0x0000000020040040L});
    public static final BitSet FOLLOW_attribute_in_attributes838 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_simpleAttribute_in_attribute851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_textAttribute_in_attribute855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_elementAttribute_in_attribute859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_simpleAttribute870 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_14_in_simpleAttribute873 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_simpleAttribute875 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_textAttribute888 = new BitSet(new long[]{0x0000000048000040L});
    public static final BitSet FOLLOW_27_in_textAttribute890 = new BitSet(new long[]{0x0000000040000040L});
    public static final BitSet FOLLOW_30_in_textAttribute893 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_textAttribute896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_elementAttribute907 = new BitSet(new long[]{0x0000000008800040L});
    public static final BitSet FOLLOW_27_in_elementAttribute909 = new BitSet(new long[]{0x0000000000800040L});
    public static final BitSet FOLLOW_23_in_elementAttribute912 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_elementAttribute915 = new BitSet(new long[]{0x0000000000000002L});

}