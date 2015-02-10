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

import nl.tjonahen.java.codereview.javaparsing.visitor.EntryPoint;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import nl.tjonahen.java.codereview.CompilationUnitFactory;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ExtractEntryPointsTest {

    public ExtractEntryPointsTest() {
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

        final List<EntryPoint> extract = new ExtractEntryPoints().extract(cu);
        assertEquals(25, extract.size());

        assertEquals(1, extract.stream().filter(p -> p.isInternal()).count());
        assertEquals(24, extract.stream().filter(p -> !p.isInternal()).count());

        extract.stream().forEach(p -> System.out.println(p.getType() + "::" + p.getName()));

    }

    /**
     * Test of extract method, of class ExtractPublicMethods.
     *
     */
    @Test
    public void testExtractPublic() throws ParseException {
        // creates an input stream for the file to be parsed

        final CompilationUnit cu = new CompilationUnitFactory().get(""
                + "public class Test { "
                + " public String ibm(String p) { "
                + "     return p.toUpperCase(); "
                + "  }"
                + " protected String ibm2(String p) { "
                + "     return p.toUpperCase(); "
                + "  }"
                + " public static String ibmS(String p) { "
                + "     return p.toUpperCase(); "
                + "  }"
                + " String ibmPL(String p) { "
                + "     return p.toUpperCase(); "
                + "  }"
                + "}");

        final List<EntryPoint> extract = new ExtractEntryPoints().extract(cu);
        assertEquals(4, extract.size());

        extract.stream().forEach(p -> System.out.println(p.getType() + "::" + p.getName()));

    }

    /**
     * Test of extract method, of class ExtractPublicMethods.
     *
     */
    @Test
    public void testExtractNoPublic() throws ParseException {
        // creates an input stream for the file to be parsed

        final CompilationUnit cu = new CompilationUnitFactory().get(""
                + "public class Test { "
                + " private Test() {}"
                + " private String ibm(String p) { "
                + "     return p.toUpperCase(); "
                + "  }"
                + "}");

        final List<EntryPoint> extract = new ExtractEntryPoints().extract(cu);
        assertEquals(2, extract.size());

        assertEquals(2, extract.stream().filter(p -> p.isInternal()).count());
        assertEquals(0, extract.stream().filter(p -> !p.isInternal()).count());

    }

    /**
     * Test of extract method, of class ExtractPublicMethods.
     */
    @Test
    public void testExtractSimplePublic() throws ParseException {
        // creates an input stream for the file to be parsed

        final CompilationUnit cu = new CompilationUnitFactory().get(""
                + "public class Test { "
                + " public Test() {}"
                + " public String ibm(String p) { "
                + "     return p.toUpperCase(); "
                + " }"
                + "}");

        final List<EntryPoint> extract = new ExtractEntryPoints().extract(cu);
        assertEquals(2, extract.size());
        assertEquals("java.lang.String", extract.get(1).getReturnType());
        assertEquals("ibm", extract.get(1).getName());
        assertEquals("java.lang.String", extract.get(1).getParams().get(0));

    }

    /**
     * Test of extract method, of class ExtractPublicMethods.
     */
    @Test
    public void testExtractWithTypeInfo() throws ParseException {
        // creates an input stream for the file to be parsed

        final CompilationUnit cu = new CompilationUnitFactory().get(""
                + "import nl.tjonahen.sample.IBM;"
                + "public class Test { "
                + " public String ibm(IBM p) { "
                + "     return p.toUpperCase(); "
                + " }"
                + "}");

        final List<EntryPoint> extract = new ExtractEntryPoints().extract(cu);
        assertEquals(1, extract.size());
        assertEquals("java.lang.String", extract.get(0).getReturnType());
        assertEquals("ibm", extract.get(0).getName());
        assertEquals("nl.tjonahen.sample.IBM", extract.get(0).getParams().get(0));

    }

    /**
     * Test of extract method, of class ExtractPublicMethods.
     */
    @Test
    public void testExtractWithGenericType() throws ParseException {
        // creates an input stream for the file to be parsed

        final CompilationUnit cu = new CompilationUnitFactory().get(""
                + "import java.util.List;"
                + "import nl.tjonahen.sample.IBM;"
                + "public class Test { "
                + " public Boolean ibm(final List<IBM> p) { "
                + "     return p.isEmpty(); "
                + " }"
                + "}");

        final List<EntryPoint> extract = new ExtractEntryPoints().extract(cu);
        assertEquals(1, extract.size());
        assertEquals("java.lang.Boolean", extract.get(0).getReturnType());
        assertEquals("ibm", extract.get(0).getName());
        assertEquals("java.util.List", extract.get(0).getParams().get(0));

    }

    /**
     * Test of extract method, of class ExtractPublicMethods.
     */
    @Test
    public void testExtractWithGenericTypeAndGenericReturnType() throws ParseException {
        // creates an input stream for the file to be parsed

        final CompilationUnit cu = new CompilationUnitFactory().get("" + "import java.util.List;"
                + "import nl.tjonahen.sample.IBM;"
                + "public class Test { "
                + " public List<IBM> ibm(final List<IBM> p) { "
                + "     return p.isEmpty(); "
                + " }"
                + "}");

        final List<EntryPoint> extract = new ExtractEntryPoints().extract(cu);
        assertEquals(1, extract.size());
        assertEquals("java.util.List", extract.get(0).getReturnType());
        assertEquals("ibm", extract.get(0).getName());
        assertEquals("java.util.List", extract.get(0).getParams().get(0));

    }

    /**
     * Test of extract method, of class ExtractPublicMethods.
     */
    @Test
    public void testExtractWithTypes() throws ParseException {
        // creates an input stream for the file to be parsed

        final CompilationUnit cu = new CompilationUnitFactory().get(""
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
                + " public void callNested(TestNested tn) {"
                + "     tn.ibm(new List<IBM>());"
                + " }"
                + "}"
                + "public class TestTwo { "
                + " public Boolean ibm(final List<IBM> p) { "
                + "     return p.isEmpty(); "
                + " }"
                + "}"
        );

        final List<EntryPoint> extract = new ExtractEntryPoints().extract(cu);
        assertEquals(4, extract.size());
        assertEquals("ibm", extract.get(0).getName());
        assertEquals("nl.tjonahen.sampleapp", extract.get(0).getPackageName());
        assertEquals("Test", extract.get(0).getType());
        assertEquals("ibm", extract.get(1).getName());
        assertEquals("nl.tjonahen.sampleapp.Test", extract.get(1).getPackageName());
        assertEquals("TestNested", extract.get(1).getType());
        
        assertEquals("callNested", extract.get(2).getName());
        assertEquals("nl.tjonahen.sampleapp", extract.get(2).getPackageName());
        assertEquals("Test", extract.get(2).getType());

        assertEquals("ibm", extract.get(3).getName());
        assertEquals("nl.tjonahen.sampleapp", extract.get(3).getPackageName());
        assertEquals("TestTwo", extract.get(3).getType());

    }

    /**
     * Test of extract method, of class ExtractPublicMethods.
     */
    @Test
    public void testExtractLambda() throws ParseException {
        // creates an input stream for the file to be parsed

        final CompilationUnit cu = new CompilationUnitFactory().get("" + "public class Test { "
                + " public Test() {}"
                + " public String ibm(String p) { "
                + " ExtractPublicMethods extractPublicMethods = new ExtractPublicMethods();"
                + " extractPublicMethods.extract(cu)"
                + "             .stream()"
                + "             .map(p -> \"ENTRYPOINT \" + p.getPackageName()+\".\"+p.getTypeName()+\"::\"+p.getSignature())"
                + "             .forEach(System.out::println);"
                + " }"
                + "}");

        final List<EntryPoint> extract = new ExtractEntryPoints().extract(cu);
        assertEquals(2, extract.size());
        assertEquals("java.lang.String", extract.get(1).getReturnType());
        assertEquals("ibm", extract.get(1).getName());
        assertEquals("java.lang.String", extract.get(1).getParams().get(0));

    }

    /**
     * Test of extract method, of class ExtractPublicMethods.
     */
    @Test
    public void testExtractWithStaticImport() throws ParseException {
        // creates an input stream for the file to be parsed

        final CompilationUnit cu = new CompilationUnitFactory().get(""
                + "import static java.util.List.isEmpty;"
                + "import nl.tjonahen.sample.IBM;"
                + "public class Test { "
                + " public List<IBM> ibm(final List<IBM> p) { "
                + "     return isEmpty(); "
                + " }"
                + "}");

        final List<EntryPoint> extract = new ExtractEntryPoints().extract(cu);
        assertEquals(1, extract.size());
        assertEquals("java.util.List", extract.get(0).getReturnType());
        assertEquals("ibm", extract.get(0).getName());
        assertEquals("java.util.List", extract.get(0).getParams().get(0));

    }

}
