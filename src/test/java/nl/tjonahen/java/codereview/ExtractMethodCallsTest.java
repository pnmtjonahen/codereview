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
package nl.tjonahen.java.codereview;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import nl.tjonahen.java.codereview.visitor.MethodCall;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ExtractMethodCallsTest {
    

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
                                + "public class Test { "
                                + " public Test() {}"
                                + " public String ibm(IBM p) { "
                                + "     String var = null;"    
                                + "     return p.process(p, var); "
                                + " }"
                                + "}"));
        
        final List<MethodCall> extract = new ExtractMethodCalls().extract(cu);
        assertEquals(1, extract.size());
        assertEquals("IBM", extract.get(0).getType());
        assertEquals("process(IBM, String)", extract.get(0).getSignature());
        assertEquals("default", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(0).getCallScopeType().getMethodName());
    }
    private InputStream getSource(String source) {
        return new ByteArrayInputStream(source.getBytes());
    }    
}
