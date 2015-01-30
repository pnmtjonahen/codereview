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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class MethodBodyVisitor extends VoidVisitorAdapter<CallScopeType> {

    private final ArrayList<MethodCall> methods = new ArrayList<>();

    private final Stack<ScopeVariable> scopeStack;

    public MethodBodyVisitor(Stack<ScopeVariable> scopeStack) {
        this.scopeStack = scopeStack;
    }

    public ArrayList<MethodCall> getMethods() {
        return methods;
    }

    private ScopeVariable findType(String name) {
        return scopeStack.stream().filter(v -> v.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void visit(MethodCallExpr n, CallScopeType arg) {
        final Expression scope = n.getScope();
        String param = "this";
        ScopeVariable paramType = null;
        if (scope != null) {
            final ScopeTypeVisitor scopeTypeVisitor = new ScopeTypeVisitor(scopeStack);
            
            scope.accept(scopeTypeVisitor, arg);
            methods.addAll(scopeTypeVisitor.getMethods());
            param = scopeTypeVisitor.getName();
            paramType = findType(param);
            
        }
        if (paramType == null) {
            // static method call ??
            final ParameterVisitor parameterVisitor = new ParameterVisitor(scopeStack);
            if (n.getArgs() != null) {
                n.getArgs().forEach(p -> p.accept(parameterVisitor, arg));
                methods.addAll(parameterVisitor.getMethods());
            }
            methods.add(new MethodCall(arg, param, n.getName(), parameterVisitor.getParams()));
        } else {
            ParameterVisitor parameterVisitor = new ParameterVisitor(scopeStack);
            if (n.getArgs() != null) {
                n.getArgs().forEach(p -> p.accept(parameterVisitor, arg));
                methods.addAll(parameterVisitor.getMethods());
            }
            methods.add(new MethodCall(arg, paramType.getType(), n.getName(), parameterVisitor.getParams()));
        }

    }

    @Override
    public void visit(VariableDeclarationExpr n, CallScopeType arg) {
        n.getVars().forEach(v -> scopeStack.push(new ScopeVariable(n.getType().toString(), v.getId().getName())));
        super.visit(n, arg);
    }

}
