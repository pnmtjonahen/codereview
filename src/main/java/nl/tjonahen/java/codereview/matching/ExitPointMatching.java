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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tjonahen.java.codereview.javaparsing.visitor.EntryPoint;
import nl.tjonahen.java.codereview.javaparsing.visitor.ExitPoint;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ExitPointMatching {

    private final Map<String, List<EntryPoint>> methodNameMapping = new HashMap<>();

    private void add(EntryPoint p) {
        final String key = p.getPackageName() + "." + p.getType();
        if (methodNameMapping.containsKey(key)) {
            methodNameMapping.get(key).add(p);
        } else {
            final List<EntryPoint> value = new ArrayList<>();
            value.add(p);
            methodNameMapping.put(key, value);
        }
    }

    public void addAll(Collection<EntryPoint> eps) {
        eps.forEach(this::add);
    }

    
    private boolean filter(List<String> entryParams, List<String> exitParams) {
        if (entryParams.size() != exitParams.size()) {
            return false;
        }
        for (int i = 0; i < entryParams.size(); i++) {
            if (!entryParams.get(i).equals(exitParams.get(i))) {
                return false;
            }
        }
        return true;
    }
    /**
     * matches a exit point to an entry point. (aka connecting the dots)
     *
     * @param ep ExitPoint
     * @return EntryPoint or null if none found.
     */
    public EntryPoint match(final ExitPoint ep) {
        if (!methodNameMapping.containsKey(ep.getType())) {
            return null;
        }
        return methodNameMapping.get(ep.getType())
                .stream()
                .filter(p -> filter(p.getParams(), ep.getParams()))
                .findFirst()
                    .orElse(null);
    }
}
