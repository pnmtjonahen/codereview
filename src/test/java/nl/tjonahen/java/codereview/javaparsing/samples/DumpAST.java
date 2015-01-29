package nl.tjonahen.java.codereview.javaparsing.samples;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class DumpAST {

    public static void main(String[] args) throws Exception {
        // creates an input stream for the file to be parsed
//        FileInputStream in = new FileInputStream("src/test/resources/test.java");
//
//        CompilationUnit cu;
//        try {
//            // parse the file
//            cu = JavaParser.parse(in);
//        } finally {
//            in.close();
//        }
         final CompilationUnit cu = JavaParser.parse(getSource(""
                                + "public class Test { "
                                + " public Test() {}"
                                + " public void ibm(String p) { "
                                + "     String value = p.toUpperCase(); "
                                + " }"
                                + "}"));
       final DumpASTVisitor dumpASTVisitor = new DumpASTVisitor();
        dumpASTVisitor.visit(cu, null);
        System.out.println(dumpASTVisitor.getSource());
    }
    private static InputStream getSource(String source) {
        return new ByteArrayInputStream(source.getBytes());
    }
}
