/*
 * Copyright (C) 2015 Philippe Tjon - A - Hen, philippe@tjonahen.nl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.tjonahen.java.codereview.javaparsing;

import nl.tjonahen.java.codereview.javaparsing.visitor.ExtractMethodCalls;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import nl.tjonahen.java.codereview.javaparsing.visitor.MethodCall;
import nl.tjonahen.java.codereview.javaparsing.visitor.PublicMethod;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ExtractMethodCallsTest {
    

    @Test
    public void testExtractTest() throws FileNotFoundException, ParseException, IOException {
        FileInputStream in = new FileInputStream("src/test/resources/test.java");

        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }
        
        final List<MethodCall> extract = new ExtractMethodCalls().extract(cu);
        assertEquals(53, extract.size());
        

    }
    /**
     * Test of extract method, of class ExtractMethodCalls.
     */
    @Test
    public void testExtract() throws ParseException {
        
        final CompilationUnit cu = JavaParser.parse(getSource(""
                                + "public class Test { "
                                + " public Test() {}"
                                + " public String ibm(String p) { "
                                + "     return p.toUpperCase(); "
                                + " }"
                                + "}"));
        
        final List<MethodCall> extract = new ExtractMethodCalls().extract(cu);
        assertEquals(1, extract.size());
        assertEquals("String", extract.get(0).getType());
        assertEquals("toUpperCase()", extract.get(0).getSignature());
        assertEquals("default", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(0).getCallScopeType().getMethodName());
    }
    /**
     * Test of extract method, of class ExtractMethodCalls.
     */
    @Test
    public void testExtractWithParams() throws ParseException {
        
        final CompilationUnit cu = JavaParser.parse(getSource(""
                                + "package nl.tjonahen.sample.test; "
                                + "import nl.tjonahen.dummy.IBM; "
                                + "public class Test { "
                                + " private int intValue;"
                                + " public Test(IBM p) {"
                                + "     intValue = p.calculate();"
                                + "     run();"
                                + " }"
                                + " public String ibm(IBM p) { "
                                + "     String var = null;"    
                                + "     return p.process(p, var, intValue); "
                                + " }"
                                + "}"));
        
        final List<MethodCall> extract = new ExtractMethodCalls().extract(cu);
        assertEquals(3, extract.size());
        assertEquals("nl.tjonahen.dummy.IBM", extract.get(0).getType());
        assertEquals("calculate()", extract.get(0).getSignature());
        assertEquals("nl.tjonahen.sample.test", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("Test", extract.get(0).getCallScopeType().getMethodName());

        assertEquals("this", extract.get(1).getType());
        assertEquals("run()", extract.get(1).getSignature());
        assertEquals("nl.tjonahen.sample.test", extract.get(1).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(1).getCallScopeType().getTypeName());
        assertEquals("Test", extract.get(1).getCallScopeType().getMethodName());
        
        assertEquals("nl.tjonahen.dummy.IBM", extract.get(2).getType());
        assertEquals("process(nl.tjonahen.dummy.IBM, String, int)", extract.get(2).getSignature());
        assertEquals("nl.tjonahen.sample.test", extract.get(2).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(2).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(2).getCallScopeType().getMethodName());
        
    }
    
    /**
     * Test of extract method, of class ExtractMethodCalls.
     */
    @Test
    public void testExtractWithLiterals() throws ParseException {
        
        final CompilationUnit cu = JavaParser.parse(getSource(""
                                + "package nl.tjonahen.sample.test; "
                                + "import nl.tjonahen.dummy.IBM; "
                                + "public class Test { "
                                + " public Test() {}"
                                + " public void ibm(IBM p) { "
                                + "     p.process(\"dummy\"); "
                                + "     p.process(1); "
                                + "     p.process(0.1); "
                                + "     p.process(1L); "
                                + "     p.process(true); "
                                + "     p.process('a'); "
                                + "     p.process(null); "
                                + " }"
                                + "}"));
        
        final List<MethodCall> extract = new ExtractMethodCalls().extract(cu);
        assertEquals(7, extract.size());
        assertEquals("nl.tjonahen.dummy.IBM", extract.get(0).getType());
        assertEquals("process(String)", extract.get(0).getSignature());
        assertEquals("nl.tjonahen.sample.test", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(0).getCallScopeType().getMethodName());
        assertEquals("process(Integer)", extract.get(1).getSignature());
        assertEquals("process(Double)", extract.get(2).getSignature());
        assertEquals("process(Long)", extract.get(3).getSignature());
        assertEquals("process(Boolean)", extract.get(4).getSignature());
        assertEquals("process(Char)", extract.get(5).getSignature());
        assertEquals("process(Object)", extract.get(6).getSignature());
    }
    /**
     * Test of extract method, of class ExtractMethodCalls.
     */
    @Test
    public void testExtractWithStaticCall() throws ParseException {
        
        final CompilationUnit cu = JavaParser.parse(getSource(""
                                + "package nl.tjonahen.sample.test; "
                                + "import nl.tjonahen.dummy.IBM; "
                                + "public class Test { "
                                + " public Test() {}"
                                + " public void ibm(IBM p) { "
                                + "     String.format(\"%s\", \"dummy\"); "
                                + " }"
                                + "}"));
        
        final List<MethodCall> extract = new ExtractMethodCalls().extract(cu);
        assertEquals(1, extract.size());
        assertEquals("String", extract.get(0).getType());
        assertEquals("format(String, String)", extract.get(0).getSignature());
        assertEquals("nl.tjonahen.sample.test", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(0).getCallScopeType().getMethodName());
    }
    
    private InputStream getSource(String source) {
        return new ByteArrayInputStream(source.getBytes());
    }    
}
