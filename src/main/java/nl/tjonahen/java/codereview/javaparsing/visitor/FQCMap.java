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
package nl.tjonahen.java.codereview.javaparsing.visitor;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping between a simple type and the fully qualified type.
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class FQCMap {

    private final Map<String, String> fqc = new HashMap<>();

    public void put(String key, String value) {
        fqc.put(key, value);
    }

    /**
     * Determine the fully qualified name of a type. (as imported by this class)
     *
     * @param key Type Name as it is specified by the source code
     * @return the FQC
     */
    public String determineFqc(final String key) {
        if (fqc.containsKey(key)) {
            return fqc.get(key);
        }
        return key;
    }

    /**
     * Determine the fully qualified name of a type. (as imported by this class)
     *
     * @param type the AST type
     * @return the FQC
     */
    public String determineFqc(final Type type) {
        String paramType = type.toString();
        if (type instanceof ReferenceType) {
            ReferenceType refType = (ReferenceType) type;
            if (refType.getType() instanceof ClassOrInterfaceType) {
                ClassOrInterfaceType coiType = (ClassOrInterfaceType) refType.getType();
                paramType = coiType.getName();
            }
        }
        return determineFqc(paramType);
    }
}
