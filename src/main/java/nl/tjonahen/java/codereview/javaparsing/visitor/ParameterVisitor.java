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

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ParameterVisitor extends VoidVisitorAdapter<CallScopeType> {

    private final Stack<ScopeVariable> scopeStack;
    private final FQCMap fqc;
    private final ArrayList<ExitPoint> methods = new ArrayList<>();


    private final List<String> params;

    public ParameterVisitor(final FQCMap fqc, final Stack<ScopeVariable> scopeStack) {
        this.scopeStack = scopeStack;
        this.fqc = fqc;
        this.params = new ArrayList<>();
    }

    public ArrayList<ExitPoint> getMethods() {
        return methods;
    }

    
    
    public List<String> getParams() {
        return params;
    }

    private void add(String type) {
        params.add(type);
    }

    @Override
    public void visit(StringLiteralExpr n, CallScopeType arg) {
        add("String");
    }

    @Override
    public void visit(NullLiteralExpr n, CallScopeType arg) {
        add("Object");
    }

    @Override
    public void visit(NameExpr n, CallScopeType arg) {
        add(scopeStack
                .stream()
                .filter(v -> v.getName().equals(n.getName()))
                .map(v -> v.getType())
                .findFirst().orElse(""));
    }


    @Override
    public void visit(LongLiteralExpr n, CallScopeType arg) {
        add("Long");
    }

    @Override
    public void visit(IntegerLiteralExpr n, CallScopeType arg) {
        add("Integer");
    }

    @Override
    public void visit(DoubleLiteralExpr n, CallScopeType arg) {
        add("Double");
    }

    @Override
    public void visit(CharLiteralExpr n, CallScopeType arg) {
        add("Char");
    }

    @Override
    public void visit(BooleanLiteralExpr n, CallScopeType arg) {
        add("Boolean");
    }

    @Override
    public void visit(ObjectCreationExpr n, CallScopeType arg) {
        add(n.getType().getName());
    }

    @Override
    public void visit(MethodCallExpr n, CallScopeType arg) {
        MethodBodyVisitor methodBodyVisitor = new MethodBodyVisitor(fqc, scopeStack);
        
        methodBodyVisitor.visit(n, arg);
        
        methods.addAll(methodBodyVisitor.getMethods());
        
        final ExitPoint ep = methodBodyVisitor.getMethods().get(0);
        
        final EntryPoint entryP = arg.getExitPointMatching().match(ep).getEntryPoint();
        if (entryP != null) {
            add(entryP.getReturnType());
        }
    }

    @Override
    public void visit(ThisExpr n, CallScopeType arg) {
        add(arg.getPackageName() + "." + arg.getTypeName());
    }
    
    

}
