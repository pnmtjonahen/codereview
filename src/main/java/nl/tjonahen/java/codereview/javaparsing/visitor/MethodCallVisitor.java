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

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class MethodCallVisitor extends VoidVisitorAdapter<CallContext> {

    private final List<ExitPoint> methods = new ArrayList<>();
    private final FQCMap fqc;
    private final Deque<ScopeVariable> scopeStack = new ArrayDeque<>();

    public MethodCallVisitor(final FQCMap fqc) {
        this.fqc = fqc;
    }

    public List<ExitPoint> getMethods() {
        return methods;
    }

    private ScopeVariable map(final Parameter p) {
        
        final Type type = p.getType();
        String paramType = type.toString();
        if (type instanceof ReferenceType) {
            ReferenceType refType = (ReferenceType) type;
            if (refType.getType() instanceof ClassOrInterfaceType) {
                ClassOrInterfaceType coiType = (ClassOrInterfaceType) refType.getType();
                paramType = coiType.getName();
            }
        }
        return new ScopeVariable(fqc.determineFqc(paramType), p.getId().getName());
    }  
    
    private ScopeVariable mapFieldDeclaration(final FieldDeclaration n, final VariableDeclarator v) {
        final Type type = n.getType();
        String paramType = type.toString();
        if (type instanceof ReferenceType) {
            ReferenceType refType = (ReferenceType) type;
            if (refType.getType() instanceof ClassOrInterfaceType) {
                ClassOrInterfaceType coiType = (ClassOrInterfaceType) refType.getType();
                paramType = coiType.getName();
            }
        }
        return new ScopeVariable(fqc.determineFqc(paramType), v.getId().getName());
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, CallContext arg) {
        final String name;
        if (arg.getTypeName() != null && !arg.getTypeName().isEmpty()) {
            name = arg.getPackageName() + "." + arg.getTypeName();
        } else {
            name = arg.getPackageName();
        }
        super.visit(n, new CallContext(arg.getExitPointMatching(), arg.getSource(), name, n.getName(), ""));

    }

    @Override
    public void visit(final FieldDeclaration n, CallContext arg) {
        n.getVariables()
                .stream()
                .map(v -> mapFieldDeclaration(n, v))
                .collect(Collectors.toList())
                    .forEach(sv -> scopeStack.push(sv));
    }
    
    @Override
    public void visit(MethodDeclaration n, CallContext arg) {

        final String params = n.getParameters() == null ? "" : 
                                n.getParameters()
                                        .stream()
                                        .map(p -> p.getType().toString() + " " + p.getId().getName())
                                        .reduce("", (s, p) -> s + ("".equals(s) ? "" : ",") + p);
        final List<ScopeVariable> scopeVar = n.getParameters() == null ? new ArrayList<>()
                : n.getParameters()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
        final String marker = arg.getPackageName() 
                                + "." + arg.getTypeName() 
                                + "." + n.getType() 
                                + " " + n.getName() 
                                + "(" + params + ")";

        scopeStack.push(new ScopeVariable(marker, marker));
        scopeVar.forEach(v -> scopeStack.push(v));
        final MethodBodyVisitor methodBodyVisitor = new MethodBodyVisitor(fqc, scopeStack);

        methodBodyVisitor
                    .visit(n, new CallContext(arg.getExitPointMatching(), 
                                            arg.getSource(), 
                                            arg.getPackageName(), arg.getTypeName()
                                            , n.getName()));

        
        methods.addAll(methodBodyVisitor.getMethods());
        ScopeVariable el = scopeStack.pop();
        while (!el.getType().equals(marker)) {
            el = scopeStack.pop();
        }
        
    }


    @Override
    public void visit(ConstructorDeclaration n, CallContext arg) {
        String params = n.getParameters() == null ? "" : 
                                n.getParameters()
                                        .stream()
                                        .map(p -> p.getType().toString() + " " + p.getId().getName())
                                        .reduce("", (s, p) -> s + ("".equals(s) ? "" : ",") + p);
        final List<ScopeVariable> scopeVar = n.getParameters() == null ? new ArrayList<>() : 
                                n.getParameters()
                                        .stream()
                                        .map(this::map)
                                        .collect(Collectors.toList());
        final String marker = arg.getPackageName() + "." + arg.getTypeName() + "." + n.getName() + "(" + params + ")";

        scopeStack.push(new ScopeVariable(marker, marker));
        scopeVar.forEach(v -> scopeStack.push(v));
        final MethodBodyVisitor methodBodyVisitor = new MethodBodyVisitor(fqc, scopeStack);

        methodBodyVisitor
                .visit(n, new CallContext(arg.getExitPointMatching(), 
                                                arg.getSource(), 
                                                arg.getPackageName(), arg.getTypeName(), arg.getTypeName()));
        methods.addAll(methodBodyVisitor.getMethods());

        ScopeVariable el = scopeStack.pop();
        while (!el.getType().equals(marker)) {
            el = scopeStack.pop();
        }
    }

}
