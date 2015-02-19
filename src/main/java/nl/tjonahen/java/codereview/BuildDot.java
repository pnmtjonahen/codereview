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
package nl.tjonahen.java.codereview;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import nl.tjonahen.java.codereview.files.Find;
import nl.tjonahen.java.codereview.javaparsing.ExtractEntryPoints;
import nl.tjonahen.java.codereview.javaparsing.ExtractExitPoints;
import nl.tjonahen.java.codereview.javaparsing.ExtractTypeHierarchy;
import nl.tjonahen.java.codereview.javaparsing.visitor.ExitPoint;
import nl.tjonahen.java.codereview.matching.ExitPointMatching;
import nl.tjonahen.java.codereview.matching.TypeHierarchyMatching;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class BuildDot {


    private PrintWriter pw;
    private final Map<String, List<ExitPoint>> grouping;
    private final String output;
    private final String inputFolder;
    private final String filter;

    public BuildDot(final String[] args) {
        this.grouping = new TreeMap<>();
        this.output = args[0];
        this.inputFolder = args[1];
        this.filter = args[2];
    }
    
    public static final void main(String[] args) throws FileNotFoundException, ParseException {
        new BuildDot(args).process();
    }
    
    
    public void process() throws FileNotFoundException, ParseException {
        pw = new PrintWriter(new File(output));
        pw.println("digraph calls {");
        
        final Find find = new Find(new File(inputFolder));

        final TypeHierarchyMatching hierarchyMatching = new TypeHierarchyMatching();
        final ExitPointMatching exitPointMatching = new ExitPointMatching(hierarchyMatching);
        final ExtractEntryPoints extractPublicMethods = new ExtractEntryPoints();
        final ExtractTypeHierarchy extractTypeHierarchy = new ExtractTypeHierarchy();

        for (File file : find.find()) {
            final CompilationUnit cu = JavaParser.parse(new FileInputStream(file));

            exitPointMatching.addAll(extractPublicMethods.extract(file.getAbsolutePath(), cu));
            hierarchyMatching.addAll(extractTypeHierarchy.extract(cu));
        }

        final ExtractExitPoints extractMethodCalls = new ExtractExitPoints();
        for (File file : find.find()) {
            final CompilationUnit cu = JavaParser.parse(new FileInputStream(file));

            extractMethodCalls.extract(file.getAbsolutePath(), cu, exitPointMatching)
                    .stream()
                    .filter(c -> c.getType() != null)
                    .filter(c -> c.getType().startsWith(filter))
                    .distinct()
                    .forEach(this::addToGroup);

        }
        
        grouping.entrySet().stream().forEach(this::addToDb);
        pw.println("}");
        pw.flush();
        pw.close();
    }

    private void addToGroup(ExitPoint ep) {
        final String[] packageName = ep.getSourceLocation().getPackageName().substring(filter.length()+1).split("\\.");
        String group;
        if ("access".equals(packageName[0])) {
            group = filter + "." + packageName[0] + "." + packageName[1];
        } else {
            group = filter + "." + packageName[0];
        }
        
        if (grouping.containsKey(group)) {
            grouping.get(group).add(ep);
        } else {
            List<ExitPoint> list = new ArrayList<>();
            list.add(ep);
            grouping.put(group, list);
        }
        
    }
    
    private void addToDb(Entry<String, List<ExitPoint>> e) {
        pw.println(String.format("subgraph \"%s\" {", e.getKey()));
        e.getValue().stream().forEach(this::addToDb);
        pw.println("}");
        
    }
    private void addToDb(ExitPoint ep) {
        pw.println(String.format("\"%s\" -> \"%s\" [label=\"%s\"]", 
                    ep.getSourceLocation().getPackageName() + "." + ep.getSourceLocation().getTypeName(), 
                    ep.getType(), 
                    ep.getName()));
    }

}
