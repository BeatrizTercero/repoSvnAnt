// $ANTLR 3.4 /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g 2012-05-28 23:52:12

package org.apache.ant.antdsl.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class AntDSLLexer extends Lexer {
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
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public AntDSLLexer() {} 
    public AntDSLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public AntDSLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "/Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g"; }

    // $ANTLR start "T__10"
    public final void mT__10() throws RecognitionException {
        try {
            int _type = T__10;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:6:7: ( '(' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:6:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__10"

    // $ANTLR start "T__11"
    public final void mT__11() throws RecognitionException {
        try {
            int _type = T__11;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:7:7: ( ')' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:7:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__11"

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:8:7: ( ',' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:8:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:9:7: ( ':' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:9:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:10:7: ( '=' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:10:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "T__15"
    public final void mT__15() throws RecognitionException {
        try {
            int _type = T__15;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:11:7: ( 'basedir' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:11:9: 'basedir'
            {
            match("basedir"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__15"

    // $ANTLR start "T__16"
    public final void mT__16() throws RecognitionException {
        try {
            int _type = T__16;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:12:7: ( 'default' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:12:9: 'default'
            {
            match("default"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__16"

    // $ANTLR start "T__17"
    public final void mT__17() throws RecognitionException {
        try {
            int _type = T__17;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:13:7: ( 'depends' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:13:9: 'depends'
            {
            match("depends"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__17"

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:14:7: ( 'element' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:14:9: 'element'
            {
            match("element"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:15:7: ( 'else' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:15:9: 'else'
            {
            match("else"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:16:7: ( 'extension-point' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:16:9: 'extension-point'
            {
            match("extension-point"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:17:7: ( 'extensionOf' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:17:9: 'extensionOf'
            {
            match("extensionOf"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:18:7: ( 'if' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:18:9: 'if'
            {
            match("if"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:19:7: ( 'implicit' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:19:9: 'implicit'
            {
            match("implicit"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:20:7: ( 'macrodef' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:20:9: 'macrodef'
            {
            match("macrodef"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:21:7: ( 'name' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:21:9: 'name'
            {
            match("name"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:22:7: ( 'namespaces' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:22:9: 'namespaces'
            {
            match("namespaces"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:23:7: ( 'optional' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:23:9: 'optional'
            {
            match("optional"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:24:7: ( 'target' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:24:9: 'target'
            {
            match("target"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:25:7: ( 'text' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:25:9: 'text'
            {
            match("text"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:26:7: ( 'trimmed' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:26:9: 'trimmed'
            {
            match("trimmed"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:27:7: ( '{' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:27:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "T__32"
    public final void mT__32() throws RecognitionException {
        try {
            int _type = T__32;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:28:7: ( '}' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:28:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__32"

    // $ANTLR start "DOC"
    public final void mDOC() throws RecognitionException {
        try {
            int _type = DOC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:157:4: ( '%' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:158:5: '%' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match('%'); 

            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:158:9: (~ ( '\\n' | '\\r' ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0 >= '\u0000' && LA1_0 <= '\t')||(LA1_0 >= '\u000B' && LA1_0 <= '\f')||(LA1_0 >= '\u000E' && LA1_0 <= '\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:158:23: ( '\\r' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\r') ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:158:23: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }


            match('\n'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DOC"

    // $ANTLR start "PROPERTY"
    public final void mPROPERTY() throws RecognitionException {
        try {
            int _type = PROPERTY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:161:9: ( '$' NAME )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:162:5: '$' NAME
            {
            match('$'); 

            mNAME(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "PROPERTY"

    // $ANTLR start "NAME"
    public final void mNAME() throws RecognitionException {
        try {
            int _type = NAME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:165:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' | '.' )* )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:166:5: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' | '.' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:166:29: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' | '.' )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0 >= '-' && LA3_0 <= '.')||(LA3_0 >= '0' && LA3_0 <= '9')||(LA3_0 >= 'A' && LA3_0 <= 'Z')||LA3_0=='_'||(LA3_0 >= 'a' && LA3_0 <= 'z')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:
            	    {
            	    if ( (input.LA(1) >= '-' && input.LA(1) <= '.')||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NAME"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:169:8: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' | '/*' ( options {greedy=false; } : . )* '*/' )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='/') ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1=='/') ) {
                    alt7=1;
                }
                else if ( (LA7_1=='*') ) {
                    alt7=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 7, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }
            switch (alt7) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:170:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
                    {
                    match("//"); 



                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:170:14: (~ ( '\\n' | '\\r' ) )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0 >= '\u0000' && LA4_0 <= '\t')||(LA4_0 >= '\u000B' && LA4_0 <= '\f')||(LA4_0 >= '\u000E' && LA4_0 <= '\uFFFF')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);


                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:170:28: ( '\\r' )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0=='\r') ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:170:28: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }


                    match('\n'); 

                    skip();

                    }
                    break;
                case 2 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:171:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 



                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:171:14: ( options {greedy=false; } : . )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0=='*') ) {
                            int LA6_1 = input.LA(2);

                            if ( (LA6_1=='/') ) {
                                alt6=2;
                            }
                            else if ( ((LA6_1 >= '\u0000' && LA6_1 <= '.')||(LA6_1 >= '0' && LA6_1 <= '\uFFFF')) ) {
                                alt6=1;
                            }


                        }
                        else if ( ((LA6_0 >= '\u0000' && LA6_0 <= ')')||(LA6_0 >= '+' && LA6_0 <= '\uFFFF')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:171:42: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    match("*/"); 



                    skip();

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:174:3: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:175:5: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:182:7: ( ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) |~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) |~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:183:5: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) |~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) |~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            {
            // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:183:5: ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) |~ ( ( '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) |~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0=='\"') ) {
                alt10=1;
            }
            else if ( (LA10_0=='\'') ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }
            switch (alt10) {
                case 1 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:183:9: '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) |~ ( ( '\\\\' | '\"' ) ) )* '\"'
                    {
                    match('\"'); 

                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:183:14: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) |~ ( ( '\\\\' | '\"' ) ) )*
                    loop8:
                    do {
                        int alt8=3;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0=='\\') ) {
                            alt8=1;
                        }
                        else if ( ((LA8_0 >= '\u0000' && LA8_0 <= '!')||(LA8_0 >= '#' && LA8_0 <= '[')||(LA8_0 >= ']' && LA8_0 <= '\uFFFF')) ) {
                            alt8=2;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:183:16: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' )
                    	    {
                    	    match('\\'); 

                    	    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||(input.LA(1) >= 't' && input.LA(1) <= 'u') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:183:63: ~ ( ( '\\\\' | '\"' ) )
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);


                    match('\"'); 

                    }
                    break;
                case 2 :
                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:184:9: '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) |~ ( ( '\\\\' | '\\'' ) ) )* '\\''
                    {
                    match('\''); 

                    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:184:14: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' ) |~ ( ( '\\\\' | '\\'' ) ) )*
                    loop9:
                    do {
                        int alt9=3;
                        int LA9_0 = input.LA(1);

                        if ( (LA9_0=='\\') ) {
                            alt9=1;
                        }
                        else if ( ((LA9_0 >= '\u0000' && LA9_0 <= '&')||(LA9_0 >= '(' && LA9_0 <= '[')||(LA9_0 >= ']' && LA9_0 <= '\uFFFF')) ) {
                            alt9=2;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:184:16: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\'' | '\\\\' )
                    	    {
                    	    match('\\'); 

                    	    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||(input.LA(1) >= 't' && input.LA(1) <= 'u') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:184:63: ~ ( ( '\\\\' | '\\'' ) )
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    match('\''); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    public void mTokens() throws RecognitionException {
        // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:8: ( T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | DOC | PROPERTY | NAME | COMMENT | WS | STRING )
        int alt11=29;
        alt11 = dfa11.predict(input);
        switch (alt11) {
            case 1 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:10: T__10
                {
                mT__10(); 


                }
                break;
            case 2 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:16: T__11
                {
                mT__11(); 


                }
                break;
            case 3 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:22: T__12
                {
                mT__12(); 


                }
                break;
            case 4 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:28: T__13
                {
                mT__13(); 


                }
                break;
            case 5 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:34: T__14
                {
                mT__14(); 


                }
                break;
            case 6 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:40: T__15
                {
                mT__15(); 


                }
                break;
            case 7 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:46: T__16
                {
                mT__16(); 


                }
                break;
            case 8 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:52: T__17
                {
                mT__17(); 


                }
                break;
            case 9 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:58: T__18
                {
                mT__18(); 


                }
                break;
            case 10 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:64: T__19
                {
                mT__19(); 


                }
                break;
            case 11 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:70: T__20
                {
                mT__20(); 


                }
                break;
            case 12 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:76: T__21
                {
                mT__21(); 


                }
                break;
            case 13 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:82: T__22
                {
                mT__22(); 


                }
                break;
            case 14 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:88: T__23
                {
                mT__23(); 


                }
                break;
            case 15 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:94: T__24
                {
                mT__24(); 


                }
                break;
            case 16 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:100: T__25
                {
                mT__25(); 


                }
                break;
            case 17 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:106: T__26
                {
                mT__26(); 


                }
                break;
            case 18 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:112: T__27
                {
                mT__27(); 


                }
                break;
            case 19 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:118: T__28
                {
                mT__28(); 


                }
                break;
            case 20 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:124: T__29
                {
                mT__29(); 


                }
                break;
            case 21 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:130: T__30
                {
                mT__30(); 


                }
                break;
            case 22 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:136: T__31
                {
                mT__31(); 


                }
                break;
            case 23 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:142: T__32
                {
                mT__32(); 


                }
                break;
            case 24 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:148: DOC
                {
                mDOC(); 


                }
                break;
            case 25 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:152: PROPERTY
                {
                mPROPERTY(); 


                }
                break;
            case 26 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:161: NAME
                {
                mNAME(); 


                }
                break;
            case 27 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:166: COMMENT
                {
                mCOMMENT(); 


                }
                break;
            case 28 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:174: WS
                {
                mWS(); 


                }
                break;
            case 29 :
                // /Users/hibou/dev/ant/svn/sandbox/antdsl/org.apache.ant.antdsl/src/org/apache/ant/antdsl/AntDSL.g:1:177: STRING
                {
                mSTRING(); 


                }
                break;

        }

    }


    protected DFA11 dfa11 = new DFA11(this);
    static final String DFA11_eotS =
        "\6\uffff\10\22\10\uffff\4\22\1\50\15\22\1\uffff\13\22\1\101\3\22"+
        "\1\106\2\22\1\111\5\22\1\uffff\4\22\1\uffff\2\22\1\uffff\12\22\1"+
        "\137\1\22\1\141\1\142\1\143\1\144\5\22\1\uffff\1\152\4\uffff\1\22"+
        "\1\154\1\155\1\22\1\157\1\uffff\1\22\2\uffff\1\22\1\uffff\2\22\1"+
        "\165\1\22\1\167\1\uffff\1\22\1\uffff\2\22\1\173\1\uffff";
    static final String DFA11_eofS =
        "\174\uffff";
    static final String DFA11_minS =
        "\1\11\5\uffff\1\141\1\145\1\154\1\146\2\141\1\160\1\141\10\uffff"+
        "\1\163\1\146\1\145\1\164\1\55\1\160\1\143\1\155\1\164\1\162\1\170"+
        "\1\151\1\145\1\141\1\145\1\155\2\145\1\uffff\1\154\1\162\1\145\1"+
        "\151\1\147\1\164\1\155\1\144\1\165\1\156\1\145\1\55\1\156\1\151"+
        "\1\157\1\55\1\157\1\145\1\55\1\155\1\151\1\154\1\144\1\156\1\uffff"+
        "\1\163\1\143\1\144\1\160\1\uffff\1\156\1\164\1\uffff\1\145\1\162"+
        "\1\164\1\163\1\164\2\151\1\145\2\141\1\55\1\144\4\55\1\157\1\164"+
        "\1\146\1\143\1\154\1\uffff\1\55\4\uffff\1\156\2\55\1\145\1\55\1"+
        "\uffff\1\55\2\uffff\1\163\1\uffff\1\160\1\146\1\55\1\157\1\55\1"+
        "\uffff\1\151\1\uffff\1\156\1\164\1\55\1\uffff";
    static final String DFA11_maxS =
        "\1\175\5\uffff\1\141\1\145\1\170\1\155\2\141\1\160\1\162\10\uffff"+
        "\1\163\1\160\1\163\1\164\1\172\1\160\1\143\1\155\1\164\1\162\1\170"+
        "\1\151\1\145\1\141\1\145\1\155\2\145\1\uffff\1\154\1\162\1\145\1"+
        "\151\1\147\1\164\1\155\1\144\1\165\1\156\1\145\1\172\1\156\1\151"+
        "\1\157\1\172\1\157\1\145\1\172\1\155\1\151\1\154\1\144\1\156\1\uffff"+
        "\1\163\1\143\1\144\1\160\1\uffff\1\156\1\164\1\uffff\1\145\1\162"+
        "\1\164\1\163\1\164\2\151\1\145\2\141\1\172\1\144\4\172\1\157\1\164"+
        "\1\146\1\143\1\154\1\uffff\1\172\4\uffff\1\156\2\172\1\145\1\172"+
        "\1\uffff\1\117\2\uffff\1\163\1\uffff\1\160\1\146\1\172\1\157\1\172"+
        "\1\uffff\1\151\1\uffff\1\156\1\164\1\172\1\uffff";
    static final String DFA11_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\10\uffff\1\26\1\27\1\30\1\31\1\32\1"+
        "\33\1\34\1\35\22\uffff\1\15\30\uffff\1\12\4\uffff\1\20\2\uffff\1"+
        "\24\25\uffff\1\23\1\uffff\1\6\1\7\1\10\1\11\5\uffff\1\25\1\uffff"+
        "\1\16\1\17\1\uffff\1\22\5\uffff\1\21\1\uffff\1\14\3\uffff\1\13";
    static final String DFA11_specialS =
        "\174\uffff}>";
    static final String[] DFA11_transitionS = {
            "\2\24\2\uffff\1\24\22\uffff\1\24\1\uffff\1\25\1\uffff\1\21\1"+
            "\20\1\uffff\1\25\1\1\1\2\2\uffff\1\3\2\uffff\1\23\12\uffff\1"+
            "\4\2\uffff\1\5\3\uffff\32\22\4\uffff\1\22\1\uffff\1\22\1\6\1"+
            "\22\1\7\1\10\3\22\1\11\3\22\1\12\1\13\1\14\4\22\1\15\6\22\1"+
            "\16\1\uffff\1\17",
            "",
            "",
            "",
            "",
            "",
            "\1\26",
            "\1\27",
            "\1\30\13\uffff\1\31",
            "\1\32\6\uffff\1\33",
            "\1\34",
            "\1\35",
            "\1\36",
            "\1\37\3\uffff\1\40\14\uffff\1\41",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\42",
            "\1\43\11\uffff\1\44",
            "\1\45\15\uffff\1\46",
            "\1\47",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54",
            "\1\55",
            "\1\56",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\64",
            "\1\65",
            "",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\73",
            "\1\74",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\100",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\102",
            "\1\103",
            "\1\104",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\22\22"+
            "\1\105\7\22",
            "\1\107",
            "\1\110",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\112",
            "\1\113",
            "\1\114",
            "\1\115",
            "\1\116",
            "",
            "\1\117",
            "\1\120",
            "\1\121",
            "\1\122",
            "",
            "\1\123",
            "\1\124",
            "",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\136",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\140",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "",
            "",
            "",
            "",
            "\1\153",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\156",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "",
            "\1\160\41\uffff\1\161",
            "",
            "",
            "\1\162",
            "",
            "\1\163",
            "\1\164",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "\1\166",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            "",
            "\1\170",
            "",
            "\1\171",
            "\1\172",
            "\2\22\1\uffff\12\22\7\uffff\32\22\4\uffff\1\22\1\uffff\32\22",
            ""
    };

    static final short[] DFA11_eot = DFA.unpackEncodedString(DFA11_eotS);
    static final short[] DFA11_eof = DFA.unpackEncodedString(DFA11_eofS);
    static final char[] DFA11_min = DFA.unpackEncodedStringToUnsignedChars(DFA11_minS);
    static final char[] DFA11_max = DFA.unpackEncodedStringToUnsignedChars(DFA11_maxS);
    static final short[] DFA11_accept = DFA.unpackEncodedString(DFA11_acceptS);
    static final short[] DFA11_special = DFA.unpackEncodedString(DFA11_specialS);
    static final short[][] DFA11_transition;

    static {
        int numStates = DFA11_transitionS.length;
        DFA11_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA11_transition[i] = DFA.unpackEncodedString(DFA11_transitionS[i]);
        }
    }

    class DFA11 extends DFA {

        public DFA11(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 11;
            this.eot = DFA11_eot;
            this.eof = DFA11_eof;
            this.min = DFA11_min;
            this.max = DFA11_max;
            this.accept = DFA11_accept;
            this.special = DFA11_special;
            this.transition = DFA11_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__10 | T__11 | T__12 | T__13 | T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | DOC | PROPERTY | NAME | COMMENT | WS | STRING );";
        }
    }
 

}