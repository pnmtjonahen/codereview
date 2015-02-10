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

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.util.List;
import nl.tjonahen.java.codereview.CompilationUnitFactory;
import nl.tjonahen.java.codereview.javaparsing.visitor.TypeHierarchy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ExtractTypeHierarchyTest {

    public ExtractTypeHierarchyTest() {
    }

    /**
     * Test of extract method, of class ExtractTypeHierarchy.
     */
    @Test
    public void testExtract() throws ParseException {
        final CompilationUnit cu = new CompilationUnitFactory().get("" 
                + "package nl.tjonahen.sample;"
                + "public class Test extends Test2 implements ITest { "
                + "}");
        final ExtractTypeHierarchy extractTypeHierarchy = new ExtractTypeHierarchy();
        
        final List<TypeHierarchy> hierarchys = extractTypeHierarchy.extract(cu);
        
        assertNotNull(hierarchys);
        assertEquals(1, hierarchys.size());
        assertEquals(2, hierarchys.get(0).getIsAType().size());
        assertEquals("nl.tjonahen.sample.Test", hierarchys.get(0).getType());
        assertEquals("ITest", hierarchys.get(0).getIsAType().get(0));
        assertEquals("Test2", hierarchys.get(0).getIsAType().get(1));
    }
    /**
     * Test of extract method, of class ExtractTypeHierarchy.
     */
    @Test
    public void testExtractTypeOnly() throws ParseException {
        final CompilationUnit cu = new CompilationUnitFactory().get("" 
                + "package nl.tjonahen.sample;"
                + "public class Test extends Test2 implements ITest { "
                + "  public void testMA() {"
                + "       method(new ArrayList());"
                + "  }"
                + ""
                + "}");
        final ExtractTypeHierarchy extractTypeHierarchy = new ExtractTypeHierarchy();
        
        final List<TypeHierarchy> hierarchys = extractTypeHierarchy.extract(cu);
        
        assertNotNull(hierarchys);
        assertEquals(1, hierarchys.size());
        assertEquals(2, hierarchys.get(0).getIsAType().size());
        assertEquals("nl.tjonahen.sample.Test", hierarchys.get(0).getType());
        assertEquals("ITest", hierarchys.get(0).getIsAType().get(0));
        assertEquals("Test2", hierarchys.get(0).getIsAType().get(1));
    }
    
    @Test
    public void testExtractEnum() throws ParseException {
        final CompilationUnit cu = new CompilationUnitFactory().get("" 
                + "public enum Currency {PENNY(1), NICKLE(5), DIME(10), QUARTER(25)} "
                + "");
        final ExtractTypeHierarchy extractTypeHierarchy = new ExtractTypeHierarchy();
        
        final List<TypeHierarchy> hierarchys = extractTypeHierarchy.extract(cu);
        
        assertNotNull(hierarchys);
        assertEquals(1, hierarchys.size());
        assertTrue(hierarchys.get(0).getIsAType().isEmpty());
    }
        @Test
    public void testExtractAnnotation() throws ParseException {
        final CompilationUnit cu = new CompilationUnitFactory().get("" 
                + "public @interface Test { "
                + "}");
        final ExtractTypeHierarchy extractTypeHierarchy = new ExtractTypeHierarchy();
        
        final List<TypeHierarchy> hierarchys = extractTypeHierarchy.extract(cu);
        
        assertNotNull(hierarchys);
        assertEquals(1, hierarchys.size());
        assertTrue(hierarchys.get(0).getIsAType().isEmpty());
    }

}
