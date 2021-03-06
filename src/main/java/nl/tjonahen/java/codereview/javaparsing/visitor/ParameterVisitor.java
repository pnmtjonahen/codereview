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

import com.github.javaparser.ast.expr.BinaryExpr;
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
import java.util.Deque;
import java.util.List;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class ParameterVisitor extends VoidVisitorAdapter<CallContext> {

    private final Deque<ScopeVariable> scopeStack;
    private final FQCMap fqc;
    private final List<ExitPoint> methods = new ArrayList<>();


    private final List<String> params;

    public ParameterVisitor(final FQCMap fqc, final Deque<ScopeVariable> scopeStack) {
        this.scopeStack = scopeStack;
        this.fqc = fqc;
        this.params = new ArrayList<>();
    }

    public List<ExitPoint> getMethods() {
        return methods;
    }

    
    
    public List<String> getParams() {
        return params;
    }

    private void add(String type) {
        params.add(type);
    }

    @Override
    public void visit(StringLiteralExpr n, CallContext arg) {
        add("java.lang.String");
    }

    @Override
    public void visit(BinaryExpr n, CallContext arg) {
        if (n.getLeft() instanceof StringLiteralExpr || n.getRight() instanceof StringLiteralExpr) {
            add("java.lang.String");
        } else {
            super.visit(n, arg);
        }
    }

    
    @Override
    public void visit(NullLiteralExpr n, CallContext arg) {
        add("java.lang.Object");
    }

    @Override
    public void visit(NameExpr n, CallContext arg) {
        add(scopeStack
                .stream()
                .filter(v -> v.getName().equals(n.getName()))
                .map(v -> v.getType())
                .findFirst().orElse(""));
    }


    @Override
    public void visit(LongLiteralExpr n, CallContext arg) {
        add("java.lang.Long");
    }

    @Override
    public void visit(IntegerLiteralExpr n, CallContext arg) {
        add("java.lang.Integer");
    }

    @Override
    public void visit(DoubleLiteralExpr n, CallContext arg) {
        add("java.lang.Double");
    }

    @Override
    public void visit(CharLiteralExpr n, CallContext arg) {
        add("java.lang.Char");
    }

    @Override
    public void visit(BooleanLiteralExpr n, CallContext arg) {
        add("java.lang.Boolean");
    }

    @Override
    public void visit(ObjectCreationExpr n, CallContext arg) {
        final String determineFqc = fqc.determineFqc(n.getType().getName());
        add(determineFqc);
    }

    @Override
    public void visit(MethodCallExpr n, CallContext arg) {
        MethodBodyVisitor methodBodyVisitor = new MethodBodyVisitor(fqc, scopeStack);
        
        methodBodyVisitor.visit(n, arg);
        
        methods.addAll(methodBodyVisitor.getMethods());
        
        final ExitPoint ep = methodBodyVisitor.getMethods().get(0);
        
        final EntryPoint entryP = arg.getExitPointMatching().match(ep).getEntryPoint();
        if (entryP != null) {
            add(entryP.getReturnType());
        } else {
            // no entrypoint found so the method call is outside our current source scope (aka a librarie) 
            // lets assume it is an Object.
            add("java.lang.Object");
        }
    }

    @Override
    public void visit(ThisExpr n, CallContext arg) {
        add(arg.getPackageName() + "." + arg.getTypeName());
    }
    
    

}
