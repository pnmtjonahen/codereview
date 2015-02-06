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

import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class TypeHierarchyVisitor extends VoidVisitorAdapter<String>{
    private final FQCMap fqc;

    private final List<TypeHierarchy> typeHierarchy;
    
    private TypeHierarchy currentType;
    
    public TypeHierarchyVisitor(final FQCMap fqc) {
        this.fqc = fqc;
        this.typeHierarchy = new ArrayList<>();
        
    }

    public List<TypeHierarchy> getTypeHierarchy() {
        return typeHierarchy;
    }
    
    

    @Override
    public void visit(ClassOrInterfaceDeclaration n, String arg) {
        this.currentType = new TypeHierarchy(arg + "." + n.getName());
        this.typeHierarchy.add(currentType);
        super.visit(n, arg+"."+n.getName()); 
    }

    @Override
    public void visit(EnumDeclaration n, String arg) {
        this.currentType = new TypeHierarchy(arg + "." + n.getName());
        this.typeHierarchy.add(currentType);
        super.visit(n, arg+"."+n.getName()); 
    }

    @Override
    public void visit(AnnotationDeclaration n, String arg) {
        this.currentType = new TypeHierarchy(arg + "." + n.getName());
        this.typeHierarchy.add(currentType);
        super.visit(n, arg+"."+n.getName()); 
    }

    
    
    @Override
    public void visit(ClassOrInterfaceType n, String arg) {
        currentType.addIsAType(fqc.determineFqc(n.getName()));
        super.visit(n, arg); 
    }
    
    
}
