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
import java.sql.SQLException;
import java.util.Map.Entry;
import javax.persistence.EntityManager;
import nl.tjonahen.java.codereview.files.Find;
import nl.tjonahen.java.codereview.javaparsing.ExtractEntryPoints;
import nl.tjonahen.java.codereview.javaparsing.ExtractExitPoints;
import nl.tjonahen.java.codereview.javaparsing.ExtractTypeHierarchy;
import nl.tjonahen.java.codereview.javaparsing.visitor.ExitPoint;
import nl.tjonahen.java.codereview.javaparsing.visitor.TypeDefiningVisitor;
import nl.tjonahen.java.codereview.jpa.JpaNode;
import nl.tjonahen.java.codereview.jpa.PersistenceManager;
import nl.tjonahen.java.codereview.matching.ExitPointMatching;
import nl.tjonahen.java.codereview.matching.TypeHierarchyMatching;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class BuildDerby {


    private final EntityManager em;

    public BuildDerby(EntityManager em) {
        this.em = em;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException, FileNotFoundException, ParseException {
        EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
        new BuildDerby(em).process(args);
        em.close();
        PersistenceManager.INSTANCE.close();
    }
    
    
    public void process(String[] args) throws FileNotFoundException, ParseException {

        final Find find = new Find(new File(args[1]));

        final TypeHierarchyMatching hierarchyMatching = new TypeHierarchyMatching();
        final ExitPointMatching exitPointMatching = new ExitPointMatching(hierarchyMatching);
        final ExtractEntryPoints extractPublicMethods = new ExtractEntryPoints();
        final ExtractTypeHierarchy extractTypeHierarchy = new ExtractTypeHierarchy();

        for (File file : find.find()) {
            final CompilationUnit cu = JavaParser.parse(new FileInputStream(file));

            final String packageName = cu.getPackage() == null ? "default" : cu.getPackage().getName().toString();
            final TypeDefiningVisitor typeDefiningVisitor = new TypeDefiningVisitor(packageName);
            typeDefiningVisitor.visit(cu, null);
            typeDefiningVisitor.getFqc()
                    .stream()
                    .filter(e -> e.getValue().startsWith(args[2]))
                    .forEach(this::addToDb);

        }
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
                    .filter(c -> c.getType().startsWith(args[2]))
                    .distinct()
                    .forEach(this::addToDb);

        }
    }


    private void addToDb(Entry<String, String> e) {
        em.getTransaction()
                .begin();

        JpaNode node = new JpaNode();
        node.setId(e.getValue());
        em.persist(node);
        
        em.getTransaction()
                .commit();

    }

    private void addToDb(ExitPoint ep) {
        
    }
}
