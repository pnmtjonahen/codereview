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

import nl.tjonahen.java.codereview.javaparsing.visitor.TypeHierarchy;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class TypeHierarchyMatchingTest {
    

    @Test
    public void testMatch() {
        TypeHierarchyMatching matching = new TypeHierarchyMatching();
        final TypeHierarchy typeHierarchy = new TypeHierarchy("test");
        typeHierarchy.addIsAType("test1");
        typeHierarchy.addIsAType("test2");
        matching.add(typeHierarchy);
        final TypeHierarchy typeHierarchy1 = new TypeHierarchy("test2");
        typeHierarchy1.addIsAType("object");
        matching.add(typeHierarchy1);
        
        assertEquals(3, matching.getSubstitutions("test").size());
        
    }
}
