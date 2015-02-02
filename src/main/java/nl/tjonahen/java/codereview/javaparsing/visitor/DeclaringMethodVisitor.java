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
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Visitor to extract public methods, aka public constructors, public, protected or package local methods.
 * 
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class DeclaringMethodVisitor extends VoidVisitorAdapter<ScopeType> {

    private final ArrayList<EntryPoint> methods = new ArrayList<>();
    private final FQCMap fqc;

    public ArrayList<EntryPoint> getMethods() {
        return methods;
    }

    public DeclaringMethodVisitor(final FQCMap fqc) {
        this.fqc = fqc;
    }
    
    @Override
    public void visit(ClassOrInterfaceDeclaration n, ScopeType type) {
        super.visit(n, new ScopeType(type.getPackageName() 
                                + (type.getTypeName() == null ? "" : "." + type.getTypeName())
                        , n.getName()));
    }
    

    @Override
    public void visit(MethodDeclaration n, ScopeType type) {
        boolean internal = isNotPublic(n.getModifiers());
        
        final String params = params(n.getParameters());
        final String signature = "" + determineTypeName(n.getType()) + " " + n.getName() + "(" + params + ")";

        methods.add(new EntryPoint(internal, type.getPackageName(), type.getTypeName(), signature));

    }

    private static boolean isNotPublic(final int modifiers) {
        return !ModifierSet.isPublic(modifiers)
                && !ModifierSet.isProtected(modifiers)
                && !ModifierSet.hasPackageLevelAccess(modifiers);
    }

    @Override
    public void visit(ConstructorDeclaration n, ScopeType type) {
        final boolean internal = isNotPublic(n.getModifiers());
        
        final String params = params(n.getParameters());
        final String signature = "" + n.getName() + "(" + params + ")";

        methods.add(new EntryPoint(internal, type.getPackageName(), type.getTypeName(), signature));

    }

    private String map(final Parameter p) {

        final Type type = p.getType();
        return determineTypeName(type);
    }

    private String determineTypeName(final Type type) {
        String paramType = type.toString();
        if (type instanceof ReferenceType) {
            ReferenceType refType = (ReferenceType) type;
            if (refType.getType() instanceof ClassOrInterfaceType) {
                ClassOrInterfaceType coiType = (ClassOrInterfaceType) refType.getType();
                paramType = coiType.getName();
            }
        }
        return fqc.determineFqc(paramType);
    }

    private String params(final List<Parameter> params) {
        return params == null ? "" : params.stream().map(this::map)
                .reduce("", (s, p) -> s + (s.equals("") ? "" : ",") + p);
    }
}
