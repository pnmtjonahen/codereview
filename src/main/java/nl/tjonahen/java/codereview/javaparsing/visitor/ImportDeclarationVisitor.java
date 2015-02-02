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
package nl.tjonahen.java.codereview.javaparsing.visitor;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Visitor to get the imported classes, creates the FQCMap.
 * 
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ImportDeclarationVisitor extends VoidVisitorAdapter<List<String>> {

    private final FQCMap fqc = new FQCMap();

    public FQCMap getFqc() {
        return fqc;
    }

    
    @Override
    public void visit(ImportDeclaration n, final List<String> arg) {
        final ArrayList<String> arrayList = new ArrayList<>();
        n.getName().accept(this, arrayList);
        String importStmt = arrayList.stream().reduce("", (s, v) -> s + v);
        if (n.isStatic()) {
// static method is last             
            final String method = arrayList.get(arrayList.size() - 1);
            final String type = importStmt.substring(0, importStmt.lastIndexOf("."));
            fqc.put(method, type);
            final String baseType = arrayList.get(arrayList.size() - 3);
            fqc.put(baseType, type);
            
//        } else if (n.isAsterisk()) {
//            //
        } else {
            final String type = arrayList.get(arrayList.size() - 1);
            fqc.put(type, importStmt);
        }
                
    }

    @Override
    public void visit(final NameExpr n, final List<String> arg) {
        if (arg == null) {
            return;
        }
        arg.add(n.getName());
    }

    @Override
    public void visit(QualifiedNameExpr n, final List<String> arg) {
        if (arg == null) {
            return;
        }
        n.getQualifier().accept(this, arg);
        arg.add(".");
        arg.add(n.getName());

    }

}
