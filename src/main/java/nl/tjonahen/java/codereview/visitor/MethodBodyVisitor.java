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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayList;
import java.util.List;
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
            if (scope instanceof NameExpr) {
                NameExpr name = (NameExpr) scope;
                param = name.getName();
                paramType = findType(name.getName());
            }
        } else {
            // global var ?

        }
        if (paramType == null) {
            // static method call ??
            System.out.print("STATIC " + arg + "::" + param + "." + n.getName() + "(");
            System.out.println(printNameExpr(n.getArgs()));
            System.out.println(" )");
            methods.add(new MethodCall(arg, param, n.getName() + "(" + printNameExpr(n.getArgs()) + ")"));
        } else {
            System.out.print("CALL " + arg + "::" + paramType.getType() + "." + n.getName() + "(");
            System.out.println(printNameExpr(n.getArgs()));
            System.out.println(" )");
            methods.add(new MethodCall(arg, paramType.getType(),n.getName() + "(" + printNameExpr(n.getArgs()) + ")"));
        }

    }
    
    private String printNameExpr(List<Expression> expList) {
        if (expList == null) {
            return "";
        }
        String params = "";
        for (Expression expression : expList) {
            if (expression instanceof NameExpr) {
                NameExpr name = (NameExpr) expression;
                if (!params.equals("")) {
                    params += ", ";
                }
                params += findType(name.getName()).getType();

            }
        }
        return params;
    }    

    @Override
    public void visit(VariableDeclarationExpr n, CallScopeType arg) {
        n.getVars().forEach(v -> scopeStack.push(new ScopeVariable(n.getType().toString(), v.getId().getName())));
    }

}
