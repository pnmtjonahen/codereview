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

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ScopeTypeVisitor extends VoidVisitorAdapter<CallScopeType> {
    
    private final Stack<ScopeVariable> scopeStack;
    private final FQCMap fqc;
    private final ArrayList<MethodCall> methods = new ArrayList<>();    
    private String name;

    public ScopeTypeVisitor(final FQCMap fqc, final Stack<ScopeVariable> scopeStack) {
        this.scopeStack = scopeStack;
        this.fqc = fqc;
    }

    public ArrayList<MethodCall> getMethods() {
        return methods;
    }

    
    
    public String getName() {
        return name;
    }
    
    
    
    @Override
    public void visit(ObjectCreationExpr n, CallScopeType arg) {
        name = n.getType().getName();
    }

    @Override
    public void visit(NameExpr n, CallScopeType arg) {
        name = n.getName();
    }

    @Override
    public void visit(MethodCallExpr n, CallScopeType arg) {
        MethodBodyVisitor methodBodyVisitor = new MethodBodyVisitor(fqc, scopeStack);
        
//        n.accept(methodBodyVisitor, arg);
        methodBodyVisitor.visit(n, arg);
        
        methods.addAll(methodBodyVisitor.getMethods());    }
    
    
}
