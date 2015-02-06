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
import nl.tjonahen.java.codereview.javaparsing.visitor.EntryPoint;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class MatchPoint {
    private final EntryPoint entryPoint;
    private final String reason;
    private final List<EntryPoint> posibleMethods;
    
    public MatchPoint(final EntryPoint entryPoint) {
        this.entryPoint = entryPoint;
        this.reason = "";
        this.posibleMethods = new ArrayList<>();
    }

    public MatchPoint(final String reason) {
        this.entryPoint = null;
        this.reason = reason;
        this.posibleMethods = new ArrayList<>();
    }
    public MatchPoint(final String reason, final List<EntryPoint> posibleMethods) {
        this.entryPoint = null;
        this.reason = reason;
        this.posibleMethods = posibleMethods;
    }

    public EntryPoint getEntryPoint() {
        return entryPoint;
    }

    public String getReason() {
        return reason;
    }

    public List<EntryPoint> getPosibleMethods() {
        return posibleMethods;
    }
    
}
