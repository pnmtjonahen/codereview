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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import nl.tjonahen.java.codereview.files.Find;
import nl.tjonahen.java.codereview.javaparsing.ExtractEntryPoints;
import nl.tjonahen.java.codereview.javaparsing.ExtractExitPoints;
import nl.tjonahen.java.codereview.javaparsing.ExtractTypeHierarchy;
import nl.tjonahen.java.codereview.javaparsing.visitor.ExitPoint;
import nl.tjonahen.java.codereview.javaparsing.visitor.TypeDefiningVisitor;
import nl.tjonahen.java.codereview.matching.ExitPointMatching;
import nl.tjonahen.java.codereview.matching.TypeHierarchyMatching;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class BuildNeo4J {

    final GraphDatabaseService graphDb;
    final ExecutionEngine engine;
    final Label typeLabel;

    public BuildNeo4J(String dbName) {
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbName);
        registerShutdownHook(graphDb);

        try (Transaction tx = graphDb.beginTx()) {
            typeLabel = DynamicLabel.label("Type");
            graphDb.schema()
                    .constraintFor(typeLabel)
                    .assertPropertyIsUnique("typename")
                    .create();
            tx.success();
        }
        engine = new ExecutionEngine(graphDb);
    }

    public static void main(String[] args) throws FileNotFoundException, ParseException {
        new BuildNeo4J(args[0]).process(args);
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
                    .forEach(this::addToDb);

        }
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

    private void addToDb(Entry<String, String> e) {

        
        try (Transaction tx = graphDb.beginTx()) {
            String queryString = "MERGE (n:Type {typename: {typename}}) RETURN n";
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("typename", e.getValue());
            engine.execute(queryString, parameters).columnAs("n").next();
            tx.success();
        }
    }

    private static enum RelTypes implements RelationshipType {
        CALLS
    }

    private void addToDb(ExitPoint ep) {
        try (Transaction tx = graphDb.beginTx()) {

            String queryString = "MATCH (n:Type { typename: {typename} }) RETURN n";

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("typename", ep.getType());
            ResourceIterator<Node> resultIterator = engine.execute(queryString, parameters).columnAs("n");
            if (resultIterator.hasNext()) {
                Node secondNode = resultIterator.next();

                parameters = new HashMap<>();
                parameters.put("typename", ep.getSourceLocation().getPackageName() + "." + ep.getSourceLocation().getTypeName());
                resultIterator = engine.execute(queryString, parameters).columnAs("n");
                if (resultIterator.hasNext()) {
                    Node firstNode = resultIterator.next();

                    Relationship relationship = firstNode.createRelationshipTo(secondNode, RelTypes.CALLS);
                    relationship.setProperty("m", ep.getName());
                }
            }
            tx.success();

        }
    }
}
