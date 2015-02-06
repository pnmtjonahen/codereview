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
package nl.tjonahen.java.codereview.matching;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import nl.tjonahen.java.codereview.javaparsing.ExtractEntryPoints;
import nl.tjonahen.java.codereview.javaparsing.ExtractExitPoints;
import nl.tjonahen.java.codereview.javaparsing.visitor.EntryPoint;
import nl.tjonahen.java.codereview.javaparsing.visitor.ExitPoint;
import nl.tjonahen.java.codereview.javaparsing.visitor.TypeHierarchy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ExitPointMatchingTest {

    /**
     * Test of match method, of class ExitPointMatching.
     */
    @Test
    public void testMatch() throws ParseException {
        final TypeHierarchyMatching typeHierarchyMatching = new TypeHierarchyMatching();
        final TypeHierarchy typeHierarchy = new TypeHierarchy("java.util.ArrayList");
        typeHierarchy.addIsAType("java.util.Collection");
        typeHierarchyMatching.add(typeHierarchy);
        ExitPointMatching epm = new ExitPointMatching(typeHierarchyMatching);

        epm.addAll(getEntryPoints());
        final List<ExitPoint> exitPoints = getExitPoints();
        assertEquals(6, exitPoints.size());

        assertNotNull(epm.match(exitPoints.get(0)).getEntryPoint());

        assertEquals("No such method..", epm.match(exitPoints.get(1)).getReason());
        assertTrue(epm.match(exitPoints.get(1)).getPosibleMethods().isEmpty());
        assertEquals("No such method..", epm.match(exitPoints.get(2)).getReason());
        assertFalse(epm.match(exitPoints.get(2)).getPosibleMethods().isEmpty());
        assertEquals(1, epm.match(exitPoints.get(2)).getPosibleMethods().size());

        assertEquals("Type Not Found..", epm.match(exitPoints.get(3)).getReason());
        assertTrue(epm.match(exitPoints.get(3)).getPosibleMethods().isEmpty());
        assertEquals("No such method..", epm.match(exitPoints.get(4)).getReason());
        assertTrue(epm.match(exitPoints.get(4)).getPosibleMethods().isEmpty());

        assertNotNull(epm.match(exitPoints.get(5)).getEntryPoint());

    }

    private List<EntryPoint> getEntryPoints() throws ParseException {
        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "package nl.tjonahen;"
                + "import nl.tjonahen.TestB;"
                + "import java.util.Collection;"
                + "public class TestA { "
                + " public String ibmA(String a, TestB b) { "
                + " }"
                + " public String ibmA(TestB b) { "
                + " }"
                + " public String ibmA(Collection<String> b) { "
                + " }"
                + "}"));

        final List<EntryPoint> extract = new ExtractEntryPoints().extract(cu);
        return extract;
    }

    private List<ExitPoint> getExitPoints() throws ParseException {
        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "package nl.tjonahen;"
                + "import nl.tjonahen.TestA;"
                + "import nl.tjonahen.TestC;"
                + "import java.util.ArrayList;"
                + "public class TestB { "
                + " private String ibmB(TestA p) { "
                + "     return p.ibmA(this); "
                + "  }"
                + " private String ibmB2(TestA p) { "
                + "     return p.ibmA(); "
                + "  }"
                + " private String ibmB3(TestA p) { "
                + "     return p.ibmA(this, \"a String\"); "
                + "  }"
                + " private String ibmB4(TestC p) { "
                + "     return p.ibmC(this, \"a String\"); "
                + "  }"
                + " private String ibmB(TestA p) { "
                + "     return p.ibmNot(this); "
                + "  }"
                + " private String ibmB(TestA p) { "
                + "     return p.ibmA(new ArrayList<String>()); "
                + "  }"
                + "}"));

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu, new ExitPointMatching(new TypeHierarchyMatching()));
        return extract;
    }

    private InputStream getSource(String source) {
        return new ByteArrayInputStream(source.getBytes());
    }
}
