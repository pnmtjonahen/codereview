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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
        ExitPointMatching epm = new ExitPointMatching();

        epm.addAll(getEntryPoints());
        final List<ExitPoint> exitPoints = getExitPoints();
        assertEquals(4, exitPoints.size());
        
        assertNotNull(epm.match(exitPoints.get(0)));
        assertNull(epm.match(exitPoints.get(1)));
        assertNull(epm.match(exitPoints.get(2)));
        assertNull(epm.match(exitPoints.get(3)));

    }

    private List<EntryPoint> getEntryPoints() throws ParseException {
        final CompilationUnit cu = JavaParser.parse(getSource(""
                + "package nl.tjonahen;"
                + "import nl.tjonahen.TestB;"
                + "public class TestA { "
                + " public String ibmA(String a, TestB b) { "
                + " }"
                + " public String ibmA(TestB b) { "
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
                + "}"));

        final List<ExitPoint> extract = new ExtractExitPoints().extract(cu);
        return extract;
    }

    private InputStream getSource(String source) {
        return new ByteArrayInputStream(source.getBytes());
    }
}