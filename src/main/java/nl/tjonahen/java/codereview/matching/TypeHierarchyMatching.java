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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import nl.tjonahen.java.codereview.javaparsing.visitor.TypeHierarchy;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class TypeHierarchyMatching {
    private final Map<String, List<String>> typeHierarchyMapping = new TreeMap<>();
    
    public void add(final TypeHierarchy typeHierarchy) {
        typeHierarchyMapping.put(typeHierarchy.getType(), typeHierarchy.getIsAType());
    }
    public void addAll(final List<TypeHierarchy> list) {
        list.forEach(this::add);
    }
    public List<String> getSubstitutions(final String type) {
        if (typeHierarchyMapping.containsKey(type)) {
            return typeHierarchyMapping.get(type);
        }
        final List<String> val = new ArrayList<>();
        val.add("Object");
        return val;
    }
    
    
}
