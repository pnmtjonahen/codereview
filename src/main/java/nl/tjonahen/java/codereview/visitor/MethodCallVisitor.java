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

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class MethodCallVisitor extends VoidVisitorAdapter<CallScopeType> {

    private final ArrayList<MethodCall> methods = new ArrayList<>();
    private final FQCMap fqc;
    private final Stack<ScopeVariable> scopeStack = new Stack<>();

    public ArrayList<MethodCall> getMethods() {
        return methods;
    }

    public MethodCallVisitor(final FQCMap fqc) {
        this.fqc = fqc;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, CallScopeType arg) {
        super.visit(n, new CallScopeType(arg.getPackageName(), n.getName()));

    }

    @Override
    public void visit(final FieldDeclaration n, CallScopeType arg) {
        n.getVariables()
                .stream()
                .map(v -> new ScopeVariable(fqc.determineFqc(n.getType().toString()), v.getId().getName()))
                .collect(Collectors.toList())
                    .forEach(sv -> scopeStack.push(sv));
    }

    @Override
    public void visit(MethodDeclaration n, CallScopeType arg) {

        final String params = n.getParameters() == null ? "" : 
                                n.getParameters()
                                        .stream()
                                        .map(p -> p.getType().toString() + " " + p.getId().getName())
                                        .reduce("", (s, p) -> s + (s.equals("") ? "" : ",") + p);
        final List<ScopeVariable> scopeVar = n.getParameters() == null ? new ArrayList<>()
                : n.getParameters()
                .stream()
                .map(ScopeVariable::map)
                .collect(Collectors.toList());
        final String marker = arg.getPackageName() 
                                + "." + arg.getTypeName() 
                                + "." + n.getType() 
                                + " " + n.getName() 
                                + "(" + params + ")";

        scopeStack.push(new ScopeVariable(marker, marker));
        scopeVar.forEach(v -> {
            scopeStack.push(v);
        });
        final MethodBodyVisitor methodBodyVisitor = new MethodBodyVisitor(scopeStack);

        methodBodyVisitor
                    .visit(n, new CallScopeType(arg.getPackageName(), arg.getTypeName(), n.getName()));

        methods.addAll(methodBodyVisitor.getMethods());
        ScopeVariable el = scopeStack.pop();
        while (!el.getType().equals(marker)) {
            el = scopeStack.pop();
        }
    }

    @Override
    public void visit(ConstructorDeclaration n, CallScopeType arg) {
        String params = n.getParameters() == null ? "" : 
                                n.getParameters()
                                        .stream()
                                        .map(p -> p.getType().toString() + " " + p.getId().getName())
                                        .reduce("", (s, p) -> s + (s.equals("") ? "" : ",") + p);
        final List<ScopeVariable> scopeVar = n.getParameters() == null ? new ArrayList<>() : 
                                n.getParameters()
                                        .stream()
                                        .map(p -> new ScopeVariable(fqc.determineFqc(p.getType().toString()), p.getId().getName()))
                                        .collect(Collectors.toList());
        final String marker = arg.getPackageName() + "." + arg.getTypeName() + "." + n.getName() + "(" + params + ")";

        scopeStack.push(new ScopeVariable(marker, marker));
        scopeVar.forEach(v -> {
            scopeStack.push(v);
        });
        final MethodBodyVisitor methodBodyVisitor = new MethodBodyVisitor(scopeStack);

        methodBodyVisitor
                .visit(n, new CallScopeType(arg.getPackageName(), arg.getTypeName(), n.getName()));
        methods.addAll(methodBodyVisitor.getMethods());

        ScopeVariable el = scopeStack.pop();
        while (!el.getType().equals(marker)) {
            el = scopeStack.pop();
        }
    }

}
