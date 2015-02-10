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
 * Represents outgoing method calls.
 * 
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ExitPoint {
    private final CallContext callScopeType;
    private final String type;
    private final String name;
    private final List<String> params;

    public ExitPoint(final CallContext callScopeType, final String type, 
            final String name, final List<String> params) {
        this.callScopeType = callScopeType;
        this.type = type;
        this.name = name;
        this.params = params;
    }

    public CallContext getCallScopeType() {
        return callScopeType;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<String> getParams() {
        return params;
    }
    
}
