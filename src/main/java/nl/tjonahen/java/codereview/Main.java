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

import nl.tjonahen.java.codereview.javaparsing.ExtractEntryPoints;
import nl.tjonahen.java.codereview.javaparsing.ExtractExitPoints;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.logging.Logger;
import nl.tjonahen.java.codereview.files.Find;
import nl.tjonahen.java.codereview.javaparsing.ExtractTypeHierarchy;
import nl.tjonahen.java.codereview.matching.ExitPointMatching;
import nl.tjonahen.java.codereview.matching.TypeHierarchyMatching;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class Main {

    private static final int WORKING_FOLDER_IDX = 0;
    private static final int FILTER_IDX = 1;
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String... aArgs) throws FileNotFoundException, ParseException {
        
        final Main main = new Main();

        main.check(aArgs);
    }

    
    private void check(String... aArgs) throws FileNotFoundException, ParseException {
        final Find find = new Find(new File(aArgs[WORKING_FOLDER_IDX]));

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
                    .filter(c -> c.getType().startsWith(aArgs[FILTER_IDX]))
                    .filter(c -> exitPointMatching.match(c).getEntryPoint() == null)
                    .map(c -> "EXITPOINT " +  c.getType() + "::"
                            + c.getName() + "(" + printParams(c.getParams()) + ")" + c.getSourceLocation())
                    .forEach(LOGGER::info);

        }

    }

    private String printParams(List<String> params) {
        return params.stream().reduce("", (p, s) -> p + ("".equals(p) ? "" : ",") + s);
    }
}
