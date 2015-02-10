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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
        if (type != null && typeHierarchyMapping.containsKey(type)) {
            Set<String> types = new TreeSet<>(typeHierarchyMapping.get(type));
            Set<String> collected = new TreeSet<>();
            types.stream().forEach(t -> collected.addAll(getSubstitutions(types, t)) );
            return new ArrayList<>(collected);
        }
        return new ArrayList<>();
    }
    
    private Set<String> getSubstitutions(Set<String> collected, final String type) {
        Set<String> current = new TreeSet<>(collected);
        if (typeHierarchyMapping.containsKey(type)) {
            for (String t: typeHierarchyMapping.get(type)) {
                if (!current.contains(t)) {
                    current.add(t);
                    current = getSubstitutions(current,t);
                }
            }
        }    
        return current;
    }
    
}
