/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.LineComment;

/**
 *
 * @author apple
 */
public class ParseJavaFile {

    private File inputFile; 
    private File outputFile; 
    private boolean ifGeneral;
    private Map<String, Boolean> libraryTypeCondition;

    public ParseJavaFile(File inputFile, File outputFile, boolean ifGeneral, Map<String, Boolean> libraryTypeCondition) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.ifGeneral = ifGeneral;
        this.libraryTypeCondition = libraryTypeCondition;
    }

    public void extractComments() {
        try {
            String converted = readFileToString(inputFile.getPath());
            parse(converted);

        } catch (IOException ex) {
            Logger.getLogger(ParseJavaFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // use ASTParse to parse string
    private void parse(final String str) {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(str.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        StringBuffer allComments = new StringBuffer();

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        for (Comment comment : (List<Comment>) cu.getCommentList()) {
            CommentVisitor commentVisitor = new CommentVisitor(cu, str);
            comment.accept(commentVisitor);
//            System.out.println(commentVisitor.getAllComments().toString());
            allComments.append(commentVisitor.getAllComments().toString());
        }

        ParseWords parseWords = new ParseWords(allComments, ifGeneral, libraryTypeCondition);
        allComments = parseWords.parseAllWords();
        writeToFile(allComments.toString());
    }

    // read file content into a string
    private String readFileToString(String filePath) throws IOException {

        StringBuilder fileData = new StringBuilder(1000);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            char[] buf = new char[10];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }

            reader.close();
            return fileData.toString();
        }

    }

    private void writeToFile(String words) {
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.getPath()))) {
                writer.write(words);
            }

        } catch (IOException ex) {
            Logger.getLogger(ParseJavaFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

//comment visitor
class CommentVisitor extends ASTVisitor {

    CompilationUnit cu;
    String source;
    StringBuffer allComments = new StringBuffer();

    public CommentVisitor(CompilationUnit cu, String source) {
        super();
        this.cu = cu;
        this.source = source;

    }

    public boolean visit(LineComment node) {
        int start = node.getStartPosition();
        int end = start + node.getLength();
        String comment = source.substring(start, end);
        allComments.append(comment);
        allComments.append("\r\n");
        return true;
    }

    public boolean visit(BlockComment node) {
        int start = node.getStartPosition();
        int end = start + node.getLength();
        String comment = source.substring(start, end);

        if (!comment.contains("Copyright")) {
            allComments.append(comment);
            allComments.append("\r\n");
            return true;
        } else {
            return false;
        }
    }

    public StringBuffer getAllComments() {
        return allComments;
    }

}
