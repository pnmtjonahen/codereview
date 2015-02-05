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
import nl.tjonahen.java.codereview.javaparsing.visitor.EntryPoint;
import nl.tjonahen.java.codereview.javaparsing.visitor.ExitPoint;
import nl.tjonahen.java.codereview.matching.ExitPointMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ExtractExitPointsTest {

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

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(104, extract.size());

    }

    /**
     * Test of extract method, of class ExtractMethodCalls.
     */
    @Test
    public void testExtractSimple() throws ParseException {

        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "public class Test { "
                + " public Test() {}"
                + " public String ibm(String p) { "
                + "     return p.toUpperCase(); "
                + " }"
                + "}"));

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(1, extract.size());
        assertEquals("String", extract.get(0).getType());
        assertEquals("toUpperCase", extract.get(0).getName());
        assertEquals("default", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(0).getCallScopeType().getMethodName());
    }

    /**
     * Test of extract method, of class ExtractMethodCalls.
     */
    @Test
    public void testExtractPublicCallAssignment() throws ParseException {

        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "public class Test { "
                + " public Test() {}"
                + " public void ibm(String p) { "
                + "     String value = p.toUpperCase(); "
                + " }"
                + "}"));

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(1, extract.size());
        assertEquals("String", extract.get(0).getType());
        assertEquals("toUpperCase", extract.get(0).getName());
        assertEquals("default", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(0).getCallScopeType().getMethodName());
    }

    @Test
    public void testExtractWithGenericsInMethod() throws ParseException {

        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "import java.util.List;"
                + "public class Test { "
                + " public Test() {}"
                + " public List<String> ibm(List<String> p) { "
                + "     if (p != null && !p.isEmpty()) { "
                + "     List<String> param = new ArrayList<String>();"
                + "     return param.toUpperCase(); "
                + "     }"
                + " }"
                + "}"));

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(2, extract.size());
        assertEquals("java.util.List", extract.get(0).getType());
        assertEquals("isEmpty", extract.get(0).getName());
        assertEquals("java.util.List", extract.get(1).getType());
        assertEquals("toUpperCase", extract.get(1).getName());

        assertEquals("default", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(0).getCallScopeType().getMethodName());
    }
    @Test
    public void testExtractWithGenericsInConstructor() throws ParseException {

        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "import java.util.List;"
                + "public class Test { "
                + " private List<String> param = new ArrayList<String>();"
                + " public Test(List<String> p) { "
                + "     if (p != null && !p.isEmpty()) { "
                + "         this.param.toUpperCase(); "
                + "     }"
                + " }"
                + "}"));

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(2, extract.size());
        assertEquals("java.util.List", extract.get(0).getType());
        assertEquals("isEmpty", extract.get(0).getName());
        assertEquals("java.util.List", extract.get(1).getType());
        assertEquals("toUpperCase", extract.get(1).getName());

        assertEquals("default", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("Test", extract.get(0).getCallScopeType().getMethodName());
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

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(3, extract.size());
        assertEquals("nl.tjonahen.dummy.IBM", extract.get(0).getType());
        assertEquals("calculate", extract.get(0).getName());
        assertEquals("nl.tjonahen.sample.test", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("Test", extract.get(0).getCallScopeType().getMethodName());

        assertEquals("", extract.get(1).getType());
        assertEquals("run", extract.get(1).getName());
        assertEquals("nl.tjonahen.sample.test", extract.get(1).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(1).getCallScopeType().getTypeName());
        assertEquals("Test", extract.get(1).getCallScopeType().getMethodName());

        assertEquals("nl.tjonahen.dummy.IBM", extract.get(2).getType());
        assertEquals("process", extract.get(2).getName());
        assertEquals(3, extract.get(2).getParams().size());
        assertEquals("nl.tjonahen.dummy.IBM", extract.get(2).getParams().get(0));
        assertEquals("String", extract.get(2).getParams().get(1));
        assertEquals("int", extract.get(2).getParams().get(2));

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

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(7, extract.size());
        assertEquals("nl.tjonahen.dummy.IBM", extract.get(0).getType());
        assertEquals("process", extract.get(0).getName());
        assertEquals("String", extract.get(0).getParams().get(0));
        assertEquals("nl.tjonahen.sample.test", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(0).getCallScopeType().getMethodName());
        assertEquals("process", extract.get(1).getName());
        assertEquals("Integer", extract.get(1).getParams().get(0));
        assertEquals("process", extract.get(2).getName());
        assertEquals("Double", extract.get(2).getParams().get(0));
        assertEquals("process", extract.get(3).getName());
        assertEquals("Long", extract.get(3).getParams().get(0));
        assertEquals("process", extract.get(4).getName());
        assertEquals("Boolean", extract.get(4).getParams().get(0));
        assertEquals("process", extract.get(5).getName());
        assertEquals("Char", extract.get(5).getParams().get(0));
        assertEquals("process", extract.get(6).getName());
        assertEquals("Object", extract.get(6).getParams().get(0));
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

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(1, extract.size());
        assertEquals("String", extract.get(0).getType());
        assertEquals("format", extract.get(0).getName());
        assertEquals("String", extract.get(0).getParams().get(0));
        assertEquals("String", extract.get(0).getParams().get(1));
        assertEquals("nl.tjonahen.sample.test", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(0).getCallScopeType().getMethodName());
    }

    @Test
    public void testExtractWithNested() throws ParseException {

        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "package nl.tjonahen.sample.test; "
                + "import nl.tjonahen.dummy.IBM; "
                + "import nl.tjonahen.dummy.Header; "
                + "import java.text.SimpleDateFormat;"
                + "import java.util.Date;"
                + "public class Test { "
                + " public Test() {}"
                + " public void ibm(IBM p) { "
                + "     Header header = new Header();"
                + "     SimpleDateFormat sdf = new SimpleDateFormat();"
                + "     String servicePrefix = \"prefix\";"
                + "     header.setMessageId(new StringBuffer(servicePrefix).append(sdf.format(new Date())).toString());"
                + " }"
                + "}"));

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(4, extract.size());
        assertEquals("nl.tjonahen.sample.test", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(0).getCallScopeType().getMethodName());

        assertEquals("java.text.SimpleDateFormat", extract.get(0).getType());
        assertEquals("format", extract.get(0).getName());
        assertEquals("Date", extract.get(0).getParams().get(0));

        assertEquals("StringBuffer", extract.get(1).getType());
        assertEquals("append", extract.get(1).getName());

        assertNull(extract.get(2).getType());
        assertEquals("toString", extract.get(2).getName());

        assertEquals("nl.tjonahen.dummy.Header", extract.get(3).getType());
        assertEquals("setMessageId", extract.get(3).getName());
    }

    @Test
    public void testExtractTrainWreck() throws ParseException {

        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "package nl.tjonahen.sample.test; "
                + "import nl.tjonahen.dummy.IBM; "
                + "import nl.tjonahen.dummy.Header; "
                + "import java.util.ArrayList;"
                + "import java.util.List;"
                + ""
                + "import nl.tjonahen.dummy.view.Step;"
                + "import nl.tjonahen.dummy.view.View;"
                + ""
                + "import java.util.Date;"
                + "public class Test { "
                + " public Test() {}"
                + "    public Step determineMenuState(boolean accessible, boolean validated) {"
                + "            final Step step ="
                + "                new Step.Builder(new View(\"firstView\"))"
                + "            		.setVisible(true)"
                + "                    .setAccessible(accessible)"
                + "                    .setVisited(validated)"
                + "                    .setValidated(validated)"
                + "                    .build();"
                + "            new Step().calculate();"
                + "    }"
                + ""
                + "}"));

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(6, extract.size());
        assertEquals("nl.tjonahen.dummy.view.Step.Builder", extract.get(0).getType());
        assertEquals("setVisible", extract.get(0).getName());
        assertFalse(extract.get(0).getParams().isEmpty());

        assertEquals("nl.tjonahen.dummy.view.Step", extract.get(5).getType());
        assertEquals("calculate", extract.get(5).getName());
        assertTrue(extract.get(5).getParams().isEmpty());

    }

    @Test
    public void testExtractWithStaticImport() throws ParseException {
        // creates an input stream for the file to be parsed

        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "import static java.util.List.isEmpty;"
                + "import nl.tjonahen.sample.IBM;"
                + "public class Test { "
                + " public List<IBM> ibm(final List<IBM> p) { "
                + "     return isEmpty(); "
                + " }"
                + "}"));

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(1, extract.size());
        assertEquals("java.util.List", extract.get(0).getType());
        assertEquals("isEmpty", extract.get(0).getName());
        assertTrue(extract.get(0).getParams().isEmpty());

    }

    @Test
    public void testInlineDeclaredVar() throws ParseException {
        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "package nl.tjonahen.dummy;"
                + "import nl.tjonahen.dummy.CreditCardArrangement;"
                + "import nl.tjonahen.dummy.CreditCard;"
                + "import nl.tjonahen.dummy.FundingInfo;"
                + "import nl.tjonahen.dummy.Arrangement;"
                + "import nl.tjonahen.dummy.Identifier;"
                + "import nl.tjonahen.dummy.ContainerOrderServiceClient;"
                + "import nl.tjonahen.dummy.FundingInfoServiceClient;"
                + "import nl.tjonahen.dummy.PaymentAccountArrangementServiceClient;"
                + "import nl.tjonahen.dummy.RetrieveArrangementDetailsCreditCardServiceClient;"
                + ""
                + "import java.util.ArrayList;"
                + "import java.util.List;"
                + ""
                + "public class Test { "
                + "    private List<Arrangement> getCreditCardArrangementsOfCurrentAccount(String currentAgreementNumber,"
                + "                                                                        List<Arrangement> paymentAccountArrangementList)"
                + "    {"
                + "        List<Arrangement> creditCardArrangements = new ArrayList<Arrangement>();"
                + ""
                + "        for (Arrangement arrangement : paymentAccountArrangementList) {"
                + "            if (thisIsTheSameArrangement(arrangement, currentAgreementNumber)) {"
                + "                for (Arrangement subArrangement : arrangement.getSubArrangements()) {"
                + "                    if (isCreditCardArrangement(subArrangement)) {"
                + "                        creditCardArrangements.add(subArrangement);"
                + "                    }"
                + "                }"
                + "            }"
                + "        }"
                + "        return creditCardArrangements;"
                + "    }"
                + "}"));
        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(4, extract.size());
        assertEquals("", extract.get(0).getType());
        assertEquals("thisIsTheSameArrangement", extract.get(0).getName());
        assertEquals("nl.tjonahen.dummy.Arrangement", extract.get(0).getParams().get(0));
    }

    @Test
    public void testExtendsImplements() throws ParseException {
        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "package nl.tjonahen.dummy;"
                + ""
                + "public class Test extends AbstractTest { "
                + " public Test(String code) {"
                + "     this(code);"
                + " }"
                + " public List<IBM> ibm(final List<IBM> p) { "
                + "     return super.ibm(p);"
                + " }"
                + "}"));
        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(1, extract.size());
        assertEquals("super", extract.get(0).getType());
        assertEquals("ibm", extract.get(0).getName());
        assertEquals("List", extract.get(0).getParams().get(0));

    }

    /**
     * Test of extract method, of class ExtractMethodCalls.
     */
    @Test
    public void testExtractThisCall() throws ParseException {

        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "package nl.tjonahen.sample.test; "
                + "import nl.tjonahen.dummy.IBM; "
                + "public class Test { "
                + " public Test() {}"
                + " public void ibm(IBM p) { "
                + "     this.format(\"%s\", this); "
                + " }"
                + "}"));

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching());
        assertEquals(1, extract.size());
        assertEquals("this", extract.get(0).getType());
        assertEquals("format", extract.get(0).getName());
        assertEquals("String", extract.get(0).getParams().get(0));
        assertEquals("nl.tjonahen.sample.test.Test", extract.get(0).getParams().get(1));
        assertEquals("nl.tjonahen.sample.test", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("Test", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(0).getCallScopeType().getMethodName());
    }
    /**
     * Test of extract method, of class ExtractMethodCalls.
     */
    @Test
    public void testExtractFindReturnType() throws ParseException {

        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "package nl.tjonahen.sample.test; "
                + "import nl.tjonahen.sample.test.TestB; "
                + "public class TestA { "
                + " public void ibm(TestB testB) { "
                + "     this.format(testB.ibm()); "
                + " }"
                + "}"));
        
        List<EntryPoint> points = new ArrayList<>();
        points.add(new EntryPoint(true, "nl.tjonahen.sample.test", "TestB", "String", "ibm", new ArrayList<>()));
        final ExitPointMatching exitPointMatching = new ExitPointMatching();
        exitPointMatching.addAll(points);
        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, exitPointMatching);
        assertEquals(2, extract.size());
        assertEquals("this", extract.get(1).getType());
        assertEquals("format", extract.get(1).getName());
        assertEquals("String", extract.get(1).getParams().get(0));
        assertEquals("nl.tjonahen.sample.test", extract.get(0).getCallScopeType().getPackageName());
        assertEquals("TestA", extract.get(0).getCallScopeType().getTypeName());
        assertEquals("ibm", extract.get(0).getCallScopeType().getMethodName());
    }

    private InputStream getSource(String source) {
        return new ByteArrayInputStream(source.getBytes());
    }
}
