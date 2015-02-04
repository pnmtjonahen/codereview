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

import java.util.List;

/**
 * A public method of a type.
 * 
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class EntryPoint {
    private final boolean internal;
    private final String packageName;
    private final String type;
    private final String returnType;
    private final String name;
    private final List<String> params;

    /**
     * 
     * @param internal indicator if the method is private or not
     * @param packageName packageName of the containing class
     * @param type typeName aka class name
     * @param returnType return type name
     * @param name name of the method
     * @param params list of parameter types
     */
    public EntryPoint(final boolean internal, 
                        final String packageName, 
                        final String type, 
                        final String returnType,
                        final String name, 
                        final List<String> params) {
        this.internal = internal;
        this.packageName = packageName;
        this.type = type;
        this.returnType = returnType;
        this.name = name;
        this.params = params;
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isInternal() {
        return internal;
    }

    public List<String> getParams() {
        return params;
    }

    public String getReturnType() {
        return returnType;
    }
    
    
}
