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
package nl.tjonahen.java.codereview.javaparsing;

import com.github.javaparser.ast.CompilationUnit;
import java.util.List;
import nl.tjonahen.java.codereview.javaparsing.visitor.CallScopeType;
import nl.tjonahen.java.codereview.javaparsing.visitor.ImportDeclarationVisitor;
import nl.tjonahen.java.codereview.javaparsing.visitor.ExitPoint;
import nl.tjonahen.java.codereview.javaparsing.visitor.MethodCallVisitor;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ExtractExitPoints {

    public List<ExitPoint> extract(final CompilationUnit cu) {
        final ImportDeclarationVisitor importDeclarationVisitor = new ImportDeclarationVisitor();
        importDeclarationVisitor.visit(cu, null);

        final MethodCallVisitor methodCallVisitor = new MethodCallVisitor(importDeclarationVisitor.getFqc());
        final String packageName = (cu.getPackage() == null ? "default" : cu.getPackage().getName().toString());
        if (cu.getTypes() != null) {
            cu.getTypes().stream().forEach((td) -> {
                td.accept(methodCallVisitor, new CallScopeType(packageName));
            });
        }
        return methodCallVisitor.getMethods();

    }

}
