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

import nl.tjonahen.java.codereview.visitor.PublicMethod;
import com.github.javaparser.ast.CompilationUnit;
import java.util.List;
import nl.tjonahen.java.codereview.visitor.ImportDeclarationVisitor;
import nl.tjonahen.java.codereview.visitor.PublicMethodVisitor;
import nl.tjonahen.java.codereview.visitor.ScopeType;

/**
 * extract all public methods from the compilation unit
 * 
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ExtractPublicMethods {

    public List<PublicMethod> extract(final CompilationUnit cu) {
        final ImportDeclarationVisitor importDeclarationVisitor = new ImportDeclarationVisitor();
        importDeclarationVisitor.visit(cu, null);
        
        final PublicMethodVisitor publicMethodVisitor = new PublicMethodVisitor(importDeclarationVisitor.getFqc());
        final String packageName = (cu.getPackage() == null ? "default" : cu.getPackage().getName().toString());
        cu.getTypes().stream().forEach((td) -> {
            td.accept(publicMethodVisitor, new ScopeType(packageName, null));
        });
        
        return publicMethodVisitor.getMethods();
    }

}