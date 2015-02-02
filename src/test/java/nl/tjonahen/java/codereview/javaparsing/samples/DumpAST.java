package nl.tjonahen.java.codereview.javaparsing.samples;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 *
 * @author Philippe Tjon - A - Hen, philippe@tjonahen.nl
 */
public class DumpAST {

    public static void main(String[] args) throws Exception {
        CompilationUnit cu = getTrainWreck();
        final DumpASTVisitor dumpASTVisitor = new DumpASTVisitor();
        dumpASTVisitor.visit(cu, null);
        System.out.println(dumpASTVisitor.getSource());
    }

    private static CompilationUnit getTrainWreck() throws ParseException {
        return JavaParser.parse(getSource(""
                + "package nl.tjonahen.sample.test; "
                + "import nl.tjonahen.dummy.IBM; "
                + "import nl.tjonahen.dummy.Header; "
                + "import java.util.ArrayList;"
                + "import java.util.List;"
                + ""
                + "import nl.rabobank.gict.mcv.business.module.common.BusinessModule;"
                + "import nl.rabobank.gict.mcv.business.module.common.ProcessManager;"
                + "import nl.rabobank.gict.mcv.business.module.common.ValidationResult;"
                + "import nl.rabobank.gict.mcv.presentation.menustate.service.MenuService;"
                + "import nl.rabobank.gict.mcv.presentation.menustate.state.PageState;"
                + "import nl.rabobank.gict.mcv.presentation.menustate.view.ProcessType;"
                + "import nl.rabobank.gict.mcv.presentation.menustate.view.ViewName;"
                + ""
                + "import java.util.Date;"
                + "public class Test { "
                + " public Test() {}"
                + "    public List<PageState> determineMenuState() {"
                + "        final List<PageState> menuList = new ArrayList<PageState>();"
                + "        boolean accessible = true;"
                + "        for (final BusinessModule businessModule: processManager.getBusinessModulesForCurrentProcess()) {"
                + "        	boolean validated = isValid(businessModule);"
                + "            final PageState step ="
                + "                new PageState.Builder(new ViewName(businessModule.getRenderParam()))"
                + "            		.setVisible(true)"
                + "                    .setAccessible(accessible)"
                + "                    .setVisited(validated)"
                + "                    .setValidated(validated)"
                + "                    .build();"
                + "            if (!validated) {"
                + "            	accessible = false;"
                + "            }"
                + "            menuList.add(step);"
                + "        }"
                + ""
                + "        return menuList;"
                + "    }"
                + ""
                + "}"));

    }

    private static CompilationUnit getStaticmport() throws ParseException {
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
//         final CompilationUnit cu = JavaParser.parse(getSource(""
//                                + "public class Test { "
//                                + " public Test() {}"
//                                + " public void ibm(String p) { "
//                                + "     String value = p.toUpperCase(); "
//                                + " }"
//                                + "}"));

//       final CompilationUnit cu = JavaParser.parse(getSource(""
//                                + "package nl.tjonahen.sample.test; "
//                                + "import nl.tjonahen.dummy.IBM; "
//                                + "import nl.tjonahen.dummy.Header; "
//                                + "import java.text.SimpleDateFormat;"
//                                + "import java.util.Date;"                
//                                + "public class Test { "
//                                + " public Test() {}"
//                                + " public void ibm(IBM p) { "
//                                + "     int size = 1024;"
//                                + "     Header header = new Header();"
//                                + "     SimpleDateFormat sdf = new SimpleDateFormat();"
//                                + "     String servicePrefix = \"prefix\";"
//                                + "     header.setMessageId(new StringBuffer(servicePrefix).append(sdf.format(new Date())).toString());"
//                                + " }"
//                                + "}"));         
        return JavaParser.parse(getSource(""
                + "import static java.util.List.isEmpty;"
                + "import nl.tjonahen.sample.IBM;"
                + "public class Test { "
                + " public List<IBM> ibm(final List<IBM> p) { "
                + "     return isEmpty(); "
                + " }"
                + "}"));
    }

    private static InputStream getSource(String source) {
        return new ByteArrayInputStream(source.getBytes());
    }
}
