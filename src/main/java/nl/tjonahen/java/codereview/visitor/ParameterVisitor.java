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
package nl.tjonahen.java.codereview.visitor;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralMinValueExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralMinValueExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.Stack;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ParameterVisitor extends VoidVisitorAdapter<String> {

    private final Stack<ScopeVariable> scopeStack;

    private String params = "";

    public ParameterVisitor(Stack<ScopeVariable> scopeStack) {
        this.scopeStack = scopeStack;
    }

    
    public String getParams() {
        return params;
    }

    private String add(String params, String type) {
        if (!params.equals("")) {
            return params + ", " + type;
        }
        return type;

    }

    @Override
    public void visit(StringLiteralExpr n, String arg) {
        params = add(params, "String");
    }

    @Override
    public void visit(NullLiteralExpr n, String arg) {
        params = add(params, "Object");
    }

    @Override
    public void visit(NameExpr n, String arg) {
        params = add(params, scopeStack
                .stream()
                .filter(v -> v.getName().equals(n.getName()))
                .map(v -> v.getType())
                .findFirst().orElse(""));
    }

    @Override
    public void visit(LongLiteralMinValueExpr n, String arg) {
        params = add(params, "Long");
    }

    @Override
    public void visit(LongLiteralExpr n, String arg) {
        params = add(params, "Long");
    }

    @Override
    public void visit(IntegerLiteralMinValueExpr n, String arg) {
        params = add(params, "Integer");
    }

    @Override
    public void visit(IntegerLiteralExpr n, String arg) {
        params = add(params, "Integer");
    }

    @Override
    public void visit(DoubleLiteralExpr n, String arg) {
        params = add(params, "Double");
    }

    @Override
    public void visit(CharLiteralExpr n, String arg) {
        params = add(params, "Char");
    }

    @Override
    public void visit(BooleanLiteralExpr n, String arg) {
        params = add(params, "Boolean");
    }

}
