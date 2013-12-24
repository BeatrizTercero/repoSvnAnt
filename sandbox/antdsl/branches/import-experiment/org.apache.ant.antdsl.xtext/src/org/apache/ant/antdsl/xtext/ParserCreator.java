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

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.apache.ant.antdsl.xtext.parser.antlr.AntDSLAntlrTokenFileProvider;
import org.apache.ant.antdsl.xtext.parser.antlr.AntDSLParser;
import org.apache.ant.antdsl.xtext.parser.antlr.internal.InternalAntDSLLexer;
import org.apache.ant.antdsl.xtext.services.AntDSLGrammarAccess;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.common.services.TerminalsGrammarAccess;
import org.eclipse.xtext.conversion.impl.DefaultTerminalConverter;
import org.eclipse.xtext.conversion.impl.DefaultTerminalConverter.Factory;
import org.eclipse.xtext.conversion.impl.IDValueConverter;
import org.eclipse.xtext.conversion.impl.INTValueConverter;
import org.eclipse.xtext.conversion.impl.STRINGValueConverter;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.eclipse.xtext.parser.DefaultEcoreElementFactory;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.parser.antlr.AntlrTokenDefProvider;
import org.eclipse.xtext.parser.antlr.AntlrTokenToStringConverter;
import org.eclipse.xtext.parser.antlr.IReferableElementsUnloader;
import org.eclipse.xtext.parser.antlr.IReferableElementsUnloader.NullUnloader;
import org.eclipse.xtext.parser.antlr.IUnorderedGroupHelper;
import org.eclipse.xtext.parser.antlr.Lexer;
import org.eclipse.xtext.parser.antlr.SyntaxErrorMessageProvider;
import org.eclipse.xtext.parser.antlr.UnorderedGroupHelper;
import org.eclipse.xtext.parser.impl.PartialParsingHelper;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.SynchronizedXtextResourceSet;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceFactory;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.resource.impl.DefaultResourceServiceProvider;
import org.eclipse.xtext.service.GrammarProvider;

import com.google.inject.Injector;
import com.google.inject.Provider;

public class ParserCreator {

    public static IParser createWithGuice() {
        Injector guiceInjector = new AntDSLStandaloneSetup().createInjectorAndDoEMFRegistration();
        IParser parser = guiceInjector.getInstance(IParser.class);
        return parser;
    }

    /**
     * Expermimental parser creator which mimic the guice boostrap, to try to avoid the guice cpu overhead  
     */
    public static IParser createWithNew() {

        // register default ePackages
        if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("ecore")) {
            Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl());
        }
        if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("xmi")) {
            Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl());
        }
        if (!EPackage.Registry.INSTANCE.containsKey(org.eclipse.xtext.XtextPackage.eNS_URI)) {
            EPackage.Registry.INSTANCE.put(org.eclipse.xtext.XtextPackage.eNS_URI, org.eclipse.xtext.XtextPackage.eINSTANCE);
        }
        if (!EPackage.Registry.INSTANCE.containsKey("http://www.apache.org/ant/AntDSL")) {
            EPackage.Registry.INSTANCE.put("http://www.apache.org/ant/AntDSL", org.apache.ant.antdsl.xtext.antdsl.AntdslPackage.eINSTANCE);
        }

        IResourceFactory resourceFactory = new XtextResourceFactory(makeProvider(new XtextResource()));
        IResourceServiceProvider serviceProvider = new DefaultResourceServiceProvider();
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ant", resourceFactory);
        IResourceServiceProvider.Registry.INSTANCE.getExtensionToFactoryMap().put("ant", serviceProvider);

        SynchronizedXtextResourceSet xtextResourceSet = new SynchronizedXtextResourceSet();
        Provider<XtextResourceSet> resourceSetProvider = makeProvider((XtextResourceSet) xtextResourceSet);

        GrammarProvider grammarProvider = new GrammarProvider("org.apache.ant.antdsl.AntDSL", resourceSetProvider);

        AntDSLGrammarAccess antDSLGrammarAccess = new AntDSLGrammarAccess(grammarProvider);

        SyntaxErrorMessageProvider syntaxErrorProvider = new SyntaxErrorMessageProvider();

        NullUnloader unloader = new IReferableElementsUnloader.NullUnloader();

        PartialParsingHelper partialParser = new PartialParsingHelper();
        partialParser.setUnloader(unloader);

        InternalAntDSLLexer lexer = new InternalAntDSLLexer();
        Provider<Lexer> lexerProvider = makeProvider((Lexer) lexer);

        AntDSLAntlrTokenFileProvider antlrTokenFileProvider = new AntDSLAntlrTokenFileProvider();

        AntlrTokenDefProvider tokenDefProvider = new AntlrTokenDefProvider();
        tokenDefProvider.setAntlrTokenFileProvider(antlrTokenFileProvider);

        AntlrTokenToStringConverter tokenConverter = new AntlrTokenToStringConverter();

        IDValueConverter idValueConverter = new IDValueConverter();
        idValueConverter.setGrammarAccess(antDSLGrammarAccess);
        idValueConverter.setTokenDefProvider(tokenDefProvider);
        idValueConverter.setLexerProvider(lexerProvider);

        INTValueConverter intValueConverter = new INTValueConverter();
        intValueConverter.setTokenDefProvider(tokenDefProvider);
        intValueConverter.setLexerProvider(lexerProvider);

        STRINGValueConverter stringValueConverter = new STRINGValueConverter();
        stringValueConverter.setTokenDefProvider(tokenDefProvider);
        stringValueConverter.setLexerProvider(lexerProvider);

        DefaultTerminalConverter defaultTerminalConverter = createPrivate(DefaultTerminalConverter.class);
        defaultTerminalConverter.setLexerProvider(lexerProvider);
        defaultTerminalConverter.setTokenDefProvider(tokenDefProvider);
        Provider<DefaultTerminalConverter> defaultTerminalConverterProvider = makeProvider(defaultTerminalConverter);
        Factory defaultTerminalConverterFactory = new DefaultTerminalConverter.Factory();
        setPrivate(defaultTerminalConverterFactory, "provider", defaultTerminalConverterProvider);

        DefaultTerminalConverters converterService = new DefaultTerminalConverters();
        setPrivate(converterService, "idValueConverter", idValueConverter);
        setPrivate(converterService, "intValueConverter", intValueConverter);
        setPrivate(converterService, "stringValueConverter", stringValueConverter);
        converterService.setGrammar(antDSLGrammarAccess);
        converterService.setDefaultTerminalConverterFactory(defaultTerminalConverterFactory);

        DefaultEcoreElementFactory elementFactory = new DefaultEcoreElementFactory();
        setPrivate(elementFactory, "tokenConverter", tokenConverter);
        elementFactory.setConverterService(converterService);

        NodeModelBuilder nodeModelBuilder = new NodeModelBuilder();
        Provider<NodeModelBuilder> nodeModelBuilderProvider = makeProvider(nodeModelBuilder);

        UnorderedGroupHelper unorderedGroupHelper = new UnorderedGroupHelper(new UnorderedGroupHelper.Collector(antDSLGrammarAccess));
        Provider<IUnorderedGroupHelper> unorderedGroupHelperProvider = makeProvider((IUnorderedGroupHelper) unorderedGroupHelper);

        AntDSLParser parser = new AntDSLParser();
        parser.setGrammarAccess(antDSLGrammarAccess);
        parser.setSyntaxErrorProvider(syntaxErrorProvider);
        parser.setPartialParser(partialParser);
        parser.setTokenDefProvider(tokenDefProvider);
        parser.setLexerProvider(lexerProvider);
        setPrivate(parser, "nodeModelBuilder", nodeModelBuilderProvider);
        parser.setUnorderedGroupHelper(unorderedGroupHelperProvider);
        parser.setElementFactory(elementFactory);

        return parser;
    }

    private static <T> Provider<T> makeProvider(final T provided) {
        return new Provider<T>() {
            public T get() {
                return provided;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> T createPrivate(Class<T> cl) {
        try {
            Constructor< ? >[] constructors = cl.getDeclaredConstructors();
            for (Constructor< ? > c : constructors) {
                if (c.getParameterTypes().length == 0) {
                    c.setAccessible(true);
                    return (T) c.newInstance();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("Missing default constructor");
    }

    private static void setPrivate(Object object, String field, Object fieldObject) {
        try {
            Field f = null;
            Class< ? > cl = object.getClass();
            while (f == null && cl != null) {
                try {
                    f = cl.getDeclaredField(field);
                } catch (NoSuchFieldException e) {
                    cl = cl.getSuperclass();
                }
            }
            f.setAccessible(true);
            f.set(object, fieldObject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        long t;
        t = System.currentTimeMillis();
        IParser parser1 = createWithNew();
        System.out.println(System.currentTimeMillis() - t);
    }
}
