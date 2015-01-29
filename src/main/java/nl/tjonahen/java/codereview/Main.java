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

import nl.tjonahen.java.codereview.javaparsing.visitor.ExtractPublicMethods;
import nl.tjonahen.java.codereview.javaparsing.visitor.ExtractMethodCalls;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.regex.Pattern;
import nl.tjonahen.java.codereview.files.Find;



/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class Main {
    
    private static final int WORKING_FOLDER_IDX = 0;
    private static final int FIND_PATTERN_IDX = 1;
    
    public static void main(String... aArgs) throws FileNotFoundException, ParseException {
        final Find find = new Find(new File(aArgs[WORKING_FOLDER_IDX]));
        
        
        List<File> files = find.find();
        for (File file : files) {
            System.out.println("Processing " + file.getAbsolutePath());
            final CompilationUnit cu = JavaParser.parse(new FileInputStream(file));
            
            ExtractPublicMethods extractPublicMethods = new ExtractPublicMethods();
            extractPublicMethods.extract(cu)
                        .stream()
                        .map(p -> "ENTRYPOINT " + p.getPackageName()+"."+p.getTypeName()+"::"+p.getSignature())
                        .forEach(System.out::println);
                        
            ExtractMethodCalls extractMethodCalls = new  ExtractMethodCalls();
            extractMethodCalls.extract(cu)
                    .stream()
                    .map(c -> "EXITPOINT " + c.getType()+"::"+c.getSignature())
                    .forEach(System.out::println);
            
            
        }
    }
}
