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
import nl.tjonahen.java.codereview.files.Find;
import nl.tjonahen.java.codereview.matching.ExitPointMatching;



/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class Main {
    
    private static final int WORKING_FOLDER_IDX = 0;
    private static final int FILTER_IDX = 1;
    
    public static void main(String... aArgs) throws FileNotFoundException, ParseException {
        final Main main = new Main();
        
        main.check(aArgs);
    }
    
    private void check(String... aArgs)  throws FileNotFoundException, ParseException {
        final Find find = new Find(new File(aArgs[WORKING_FOLDER_IDX]));
        final ExitPointMatching exitPointMatching = new ExitPointMatching();
        for (File file : find.find()) {
            final CompilationUnit cu = JavaParser.parse(new FileInputStream(file));
            
            ExtractEntryPoints extractPublicMethods = new ExtractEntryPoints();
            exitPointMatching.addAll(extractPublicMethods.extract(cu));
                        
            
        }
        for (File file : find.find()) {
            final CompilationUnit cu = JavaParser.parse(new FileInputStream(file));
            
                        
            ExtractExitPoints extractMethodCalls = new  ExtractExitPoints();
            extractMethodCalls.extract(cu)
                    .stream()
                    .filter(c -> c.getType() != null)
                    .filter(c -> c.getType().startsWith(aArgs[FILTER_IDX]))
                    .filter(c -> exitPointMatching.match(c) == null)
                    .map(c -> "EXITPOINT " + c.getCallScopeType().getTypeName() + " => " + c.getType()+"::"+c.getName() + "(" + printParams(c.getParams()) + ")")
                    .forEach(System.out::println);
            
            
        }
        
        
    }
    
    private String printParams(List<String> params) {
        return params.stream().reduce("", (p, s) -> p + (p.equals("") ? "" : ",") + s );
    }
}
