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

import nl.tjonahen.java.codereview.visitor.PublicMethod;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ExtractPublicMethodsTest {
    
    public ExtractPublicMethodsTest() {
    }
    /**
     * Test of extract method, of class ExtractPublicMethods.
     */
    @Test
    public void testExtract() throws FileNotFoundException, ParseException, IOException {
        // creates an input stream for the file to be parsed

        FileInputStream in = new FileInputStream("src/test/resources/test.java");

        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }
        
        final List<PublicMethod> extract = new ExtractPublicMethods().extract(cu);
        assertEquals(24, extract.size());
        
        extract.stream().forEach( p ->  System.out.println(p.getTypeName() + "::" + p.getSignature()));
        
    }

    /**
     * Test of extract method, of class ExtractPublicMethods.
     * 
     */
    @Test
    public void testExtractPublic() throws ParseException {
        // creates an input stream for the file to be parsed

        final CompilationUnit cu = JavaParser.parse(getSource(""
                                + "public class Test { "
                                + " public String ibm(String p) { "
                                + "     return p.toUpperCase(); "
                                +"  }"
                                + " protected String ibm2(String p) { "
                                + "     return p.toUpperCase(); "
                                +"  }"
                                + " public static String ibmS(String p) { "
                                + "     return p.toUpperCase(); "
                                +"  }"
                                + " String ibmPL(String p) { "
                                + "     return p.toUpperCase(); "
                                +"  }"
                                + "}"));
        
        final List<PublicMethod> extract = new ExtractPublicMethods().extract(cu);
        assertEquals(4, extract.size());
        
        extract.stream().forEach( p ->  System.out.println(p.getTypeName() + "::" + p.getSignature()));
        
    }
    /**
     * Test of extract method, of class ExtractPublicMethods.
     * 
     */
    @Test
    public void testExtractNoPublic() throws ParseException {
        // creates an input stream for the file to be parsed

        final CompilationUnit cu = JavaParser.parse(getSource(""
                                + "public class Test { "
                                + " private Test() {}"
                                + " private String ibm(String p) { "
                                + "     return p.toUpperCase(); "
                                +"  }"
                                + "}"));
        
        final List<PublicMethod> extract = new ExtractPublicMethods().extract(cu);
        assertEquals(0, extract.size());
        
    }
    /**
     * Test of extract method, of class ExtractPublicMethods.
     */
    @Test
    public void testExtractSimplePublic() throws ParseException  {
        // creates an input stream for the file to be parsed


        final CompilationUnit cu = JavaParser.parse(getSource(""
                                + "public class Test { "
                                + " public Test() {}"
                                + " public String ibm(String p) { "
                                + "     return p.toUpperCase(); "
                                + " }"
                                + "}"));
        
        final List<PublicMethod> extract = new ExtractPublicMethods().extract(cu);
        assertEquals(2, extract.size());
        assertEquals("String ibm(String)", extract.get(1).getSignature());
        
    }
    /**
     * Test of extract method, of class ExtractPublicMethods.
     */
    @Test
    public void testExtractWithTypeInfo() throws ParseException  {
        // creates an input stream for the file to be parsed


        final CompilationUnit cu = JavaParser.parse(getSource(""
                                + "import nl.tjonahen.sample.IBM;"
                                + "public class Test { "
                                + " public String ibm(IBM p) { "
                                + "     return p.toUpperCase(); "
                                + " }"
                                + "}"));
        
        final List<PublicMethod> extract = new ExtractPublicMethods().extract(cu);
        assertEquals(1, extract.size());
        assertEquals("String ibm(nl.tjonahen.sample.IBM)", extract.get(0).getSignature());
        
    }
    /**
     * Test of extract method, of class ExtractPublicMethods.
     */
    @Test
    public void testExtractWithGenericType() throws ParseException  {
        // creates an input stream for the file to be parsed


        final CompilationUnit cu = JavaParser.parse(getSource(""
                                + "import java.util.List;"
                                + "import nl.tjonahen.sample.IBM;"
                                + "public class Test { "
                                + " public Boolean ibm(final List<IBM> p) { "
                                + "     return p.isEmpty(); "
                                + " }"
                                + "}"));
        
        final List<PublicMethod> extract = new ExtractPublicMethods().extract(cu);
        assertEquals(1, extract.size());
        assertEquals("Boolean ibm(java.util.List)", extract.get(0).getSignature());
        
    }
    /**
     * Test of extract method, of class ExtractPublicMethods.
     */
    @Test
    public void testExtractWithTypes() throws ParseException  {
        // creates an input stream for the file to be parsed


        final CompilationUnit cu = JavaParser.parse(getSource(""
                                + "package nl.tjonahen.sampleapp;"
                                + "import java.util.List;"
                                + "import nl.tjonahen.sample.IBM;"
                                + "public class Test { "
                                + " public Boolean ibm(final List<IBM> p) { "
                                + "     return p.isEmpty(); "
                                + " }"
                                + " public static class TestNested { "
                                + "     public Boolean ibm(final List<IBM> p) { "
                                + "         return p.isEmpty(); "
                                + "     }"
                                + " }"
                                + "}"
                                + "public class TestTwo { "
                                + " public Boolean ibm(final List<IBM> p) { "
                                + "     return p.isEmpty(); "
                                + " }"
                                + "}"
        ));
        
        final List<PublicMethod> extract = new ExtractPublicMethods().extract(cu);
        assertEquals(3, extract.size());
        assertEquals("Boolean ibm(java.util.List)", extract.get(0).getSignature());
        assertEquals("nl.tjonahen.sampleapp", extract.get(0).getPackageName());
        assertEquals("Test", extract.get(0).getTypeName());
        assertEquals("Boolean ibm(java.util.List)", extract.get(1).getSignature());
        assertEquals("nl.tjonahen.sampleapp.Test", extract.get(1).getPackageName());
        assertEquals("TestNested", extract.get(1).getTypeName());
        assertEquals("Boolean ibm(java.util.List)", extract.get(2).getSignature());
        assertEquals("nl.tjonahen.sampleapp", extract.get(2).getPackageName());
        assertEquals("TestTwo", extract.get(2).getTypeName());
        
    }
    
    private InputStream getSource(String source) {
        return new ByteArrayInputStream(source.getBytes());
    }
}
