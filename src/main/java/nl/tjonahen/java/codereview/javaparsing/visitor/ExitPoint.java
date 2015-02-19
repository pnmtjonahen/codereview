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
import java.util.Objects;

/**
 * Represents outgoing method calls.
 * 
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ExitPoint {
    private final SourceLocation sourceLocation;
    private final String type;
    private final String name;
    private final List<String> params;

    public ExitPoint(final SourceLocation sourceLocation, final String type, 
            final String name, final List<String> params) {
        this.sourceLocation = sourceLocation;
        this.type = type;
        this.name = name;
        this.params = params;
    }

    public SourceLocation getSourceLocation() {
        return sourceLocation;
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

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExitPoint other = (ExitPoint) obj;
        if (!Objects.equals(this.sourceLocation, other.sourceLocation)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
}
