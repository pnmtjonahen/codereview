package nl.tjonahen.java.samples;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.TypeDeclarationStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class MethodPrinter {

    public static void main(String[] args) throws Exception {
        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream("src/test/resources/test.java");

        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }

        // visit and print the methods names
        final String packageName = (cu.getPackage() == null ? "" : cu.getPackage().getName().toString());
//        new TypeDeclarationVisitor().visit(cu, null);
        new ImportDeclarationVisitor().visit(cu, null);
//        new FieldDeclarationVisitor().visit(cu, null);
        cu.getTypes().stream().forEach((td) -> {
            td.accept(new MethodVisitor(), new ScopeType(packageName));
        });
//            new MethodVisitor().visit(cu, null);
    }
    private static String determineFqc(String name) {
        if (fqc.containsKey(name)) {
            return fqc.get(name);
        }
        return name;
    }

    private static class ScopeVar {

        private final String type;
        private final String name;

        public ScopeVar(final String type, final String name) {
            this.type = determineFqc(type);
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

    }
    
    private static class ScopeType {
        private final String packageName;
        private final String typeName;
        private final String methodName;

        public ScopeType(final String packageName, final String typeName, final String methodName) {
            this.packageName = packageName;
            this.typeName = typeName;
            this.methodName = methodName;
        }

        public ScopeType(final String packageName) {
            this(packageName, "", "");
        }

        public ScopeType(final String packageName, final String typeName) {
            this(packageName, typeName, "");
        }

         
        public String getPackageName() {
            return packageName;
        }

        public String getTypeName() {
            return typeName;
        }

        public String getMethodName() {
            return methodName;
        }

        @Override
        public String toString() {
            return "ScopeType{" + "packageName=" + packageName + ", typeName=" + typeName + ", methodName=" + methodName + '}';
        }
        
        
    }
    
//    private static class TypeDeclarationVisitor extends VoidVisitorAdapter<String> {
//
//
//        @Override
//        public void visit(ClassExpr n, String arg) {
////            System.out.println("TYPENAME " + n.getType().toString());
//        }
//
//        @Override
//        public void visit(TypeDeclarationStmt n, String arg) {
////            System.out.println("TYPEDECL " + n.getTypeDeclaration().getName());
//        }
//        
//        
//
//        @Override
//        public void visit(PackageDeclaration n, String arg) {
////            System.out.println("CONTAIN " + n.getName());
//        }
//        
//    }

    private static final Stack<ScopeVar> scopeStack = new Stack<>();
    private static Map<String, String> fqc = new HashMap<>();
    
    private static class ImportDeclarationVisitor extends VoidVisitorAdapter<List<String>> {

        @Override
        public void visit(ImportDeclaration n, final List<String> arg) {
            final ArrayList<String> arrayList = new ArrayList<>();
            n.getName().accept(this, arrayList);
            String importStmt = arrayList.stream().reduce("", (s, v) -> s + v);
//            System.out.println("USE " + importStmt);
            
            fqc.put(arrayList.get(arrayList.size()-1), importStmt);
        }

        @Override
        public void visit(final NameExpr n, final List<String> arg) {
            if (arg == null) {
                return ;
            }
            arg.add(n.getName());
        }

        @Override
        public void visit(QualifiedNameExpr n, final List<String> arg) {
            if (arg == null) {
                return ;
            }
            n.getQualifier().accept(this, arg);
            arg.add(".");
            arg.add(n.getName());

        }

    }
    

//    private static class FieldDeclarationVisitor extends VoidVisitorAdapter<String> {
//
//        @Override
//        public void visit(final FieldDeclaration n, String arg) {
//            String vars = n.getVariables().stream().map(v -> v.getId().getName()).reduce("", (s, v) -> (s.equals("") ? "" : ",") + v);
//            List<ScopeVar> scopeVars = n.getVariables().stream().map(v -> new ScopeVar(n.getType().toString(), v.getId().getName())).collect(Collectors.toList());
////            System.out.println("GLOBAL " + scopeVars.stream().map(v -> v.getType() + " " + v.getName()).reduce("", (s, v) -> s + v));
//
//            scopeVars.forEach(sv -> scopeStack.push(sv));
//
//        }
//
//    }

    private static ScopeVar map(final Parameter p) {
        
        final Type type = p.getType();
        String paramType = type.toString();
        if (type instanceof ReferenceType) {
            ReferenceType refType = (ReferenceType) type;
            if (refType.getType() instanceof ClassOrInterfaceType) {
                ClassOrInterfaceType coiType = (ClassOrInterfaceType) refType.getType();
                paramType = coiType.getName();
            }
        }
        return new ScopeVar(paramType, p.getId().getName());
    }
    private static class MethodVisitor extends VoidVisitorAdapter<ScopeType> {


        @Override
        public void visit(ClassOrInterfaceDeclaration n, ScopeType arg) {
//            System.out.println("CLASSORINTERFACE DECL " + n.getName());
            super.visit(n, new ScopeType(arg.getPackageName(), n.getName())); 
            
        }

        @Override
        public void visit(final FieldDeclaration n, ScopeType arg) {
            String vars = n.getVariables().stream().map(v -> v.getId().getName()).reduce("", (s, v) -> (s.equals("") ? "" : ",") + v);
            List<ScopeVar> scopeVars = n.getVariables().stream().map(v -> new ScopeVar(n.getType().toString(), v.getId().getName())).collect(Collectors.toList());
//            System.out.println("GLOBAL " + scopeVars.stream().map(v -> v.getType() + " " + v.getName()).reduce("", (s, v) -> s + v));

            scopeVars.forEach(sv -> scopeStack.push(sv));

        }
        
        

        
        @Override
        public void visit(MethodDeclaration n, ScopeType arg) {

            final String params = n.getParameters() == null ? "" : n.getParameters().stream().map(p -> p.getType().toString() + " " + p.getId().getName()).reduce("", (s, p) -> s + (s.equals("") ? "" : ",") + p);
            final List<ScopeVar> scopeVar = n.getParameters() == null ? new ArrayList<>() 
                    : n.getParameters()
                            .stream()
                            .map(MethodPrinter::map)
                            .collect(Collectors.toList());
            final String marker = arg.getPackageName()+ "." +arg.getTypeName() + ".FUNCTION " + n.getType() + " " + n.getName() + "(" + params + ")";

//            System.out.println(marker);
//            System.out.println("BEGIN");

            scopeStack.push(new ScopeVar(marker, marker));
            scopeVar.forEach(v -> {
                scopeStack.push(v);
            });

            new MethodBodyVisitor().visit(n, new ScopeType(arg.getPackageName(), arg.typeName, n.getName()));

            ScopeVar el = scopeStack.pop();
            while (!el.getType().equals(marker)) {
                el = scopeStack.pop();
            }
//            System.out.println("END");
        }

        @Override
        public void visit(ConstructorDeclaration n, ScopeType arg) {
            String params = n.getParameters() == null ? "" : n.getParameters().stream().map(p -> p.getType().toString() + " " + p.getId().getName()).reduce("", (s, p) -> s + (s.equals("") ? "" : ",") + p);
            final List<ScopeVar> scopeVar = n.getParameters() == null ? new ArrayList<>() : n.getParameters().stream().map(p -> new ScopeVar(p.getType().toString(), p.getId().getName())).collect(Collectors.toList());
            final String marker = arg.getPackageName() + "." + arg.getTypeName() + ".DECLARE " + n.getName() + "(" + params + ")";

//            System.out.println(marker);
//            System.out.println("BEGIN");

            scopeStack.push(new ScopeVar(marker, marker));
            scopeVar.forEach(v -> {
                scopeStack.push(v);
            });

            new MethodBodyVisitor().visit(n, new ScopeType(arg.getPackageName(), arg.typeName, n.getName()));

            ScopeVar el = scopeStack.pop();
            while (!el.getType().equals(marker)) {
                el = scopeStack.pop();
            }

//            System.out.println("END");
        }
    }

    private static ScopeVar findType(String name) {
        return scopeStack.stream().filter(v -> v.getName().equals(name)).findFirst().orElse(null);
    }

    private static class MethodBodyVisitor extends VoidVisitorAdapter<ScopeType> {

        @Override
        public void visit(MethodCallExpr n, ScopeType arg) {
            final Expression scope = n.getScope();
            String param = "this";
            ScopeVar paramType = null;
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
                System.out.print("STATIC " + arg + "::" +  param + "." + n.getName() + "(");
                printNameExpr(n.getArgs());
                System.out.println(" )");
            } else {
                System.out.print("CALL " + arg + "::" + paramType.getType() + "." + n.getName() + "(");
                printNameExpr(n.getArgs());
                System.out.println(" )");

            }

        }

        @Override
        public void visit(VariableDeclarationExpr n, ScopeType arg) {
            n.getVars().forEach(v -> scopeStack.push(new ScopeVar(n.getType().toString(), v.getId().getName())));
        }

    }

    private static void printNameExpr(List<Expression> expList) {
        if (expList == null) {
            return;
        }
        for (Expression expression : expList) {
            if (expression instanceof NameExpr) {
                NameExpr name = (NameExpr) expression;
                System.out.print(" " + name.getName());

            }
        }
    }

}
