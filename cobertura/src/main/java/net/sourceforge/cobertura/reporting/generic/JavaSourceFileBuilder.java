package net.sourceforge.cobertura.reporting.generic;

import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import net.sourceforge.cobertura.util.FileFinder;
import net.sourceforge.cobertura.util.Source;

import java.io.*;
import java.util.*;

public class JavaSourceFileBuilder {
    private Set<Entry> methods;
    private Set<Entry> classes;
    private SourceFile sourceFile;
    private Map<String, String> methodsAndDescriptors;
    private String packageName;

    public SourceFile build(FileFinder fileFinder, String sourceFileName, String encoding,
                            Set<String> methodsAndDescriptors) throws Exception {
        methods = new HashSet<Entry>();
        classes = new HashSet<Entry>();
        sourceFile = new SourceFile(sourceFileName);
        buildMethodsAndDescriptorsMap(methodsAndDescriptors);
        parseFile(fileFinder.getFileForSource(sourceFileName).getAbsolutePath());
        addSourceFileLines(fileFinder, sourceFileName, encoding);
        return sourceFile;
    }

    private void buildMethodsAndDescriptorsMap(Set<String> methodsAndDescriptors) {
        this.methodsAndDescriptors = new HashMap<String, String>();
        Iterator<String> iterator = methodsAndDescriptors.iterator();
        while (iterator.hasNext()) {
            String value = iterator.next();
            this.methodsAndDescriptors.put(value.split("\\(")[0], value);
        }
    }

    private void addSourceFileLines(FileFinder fileFinder, String sourceFileName, String encoding) {
        Source source = fileFinder.getSource(sourceFileName);

        if (source == null) {
            throw new RuntimeException("Unable to locate " + sourceFileName);
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(source.getInputStream(), encoding));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        try {
            String lineStr;
            int lineNumber = 1;
            while ((lineStr = br.readLine()) != null) {
                //we want to retrieve all the lines...
                sourceFile.addEntry(
                        new SourceFileEntry(
                                getClass(lineNumber),
                                methodsAndDescriptors.get(getMethod(lineNumber)),
                                lineNumber,
                                lineStr));
                lineNumber++;
            }
        } catch (IOException e) {
            //TODO see how to deal with this. We wont blow up just because of a singe file...
        }
    }

    //TODO this should be optimized some way...
    private String getLevelName(Set<Entry> levels, int lineNumber) {
        Iterator<Entry> iterator = levels.iterator();
        Entry entry;
        while (iterator.hasNext()) {
            if ((entry = iterator.next()).getKey().contains(lineNumber)) {
                return entry.getValue();
            }
        }
        return "";
    }

    private String getClass(int lineNumber) {
        return getLevelName(classes, lineNumber);
    }

    private String getMethod(int lineNumber) {
        return getLevelName(methods, lineNumber);
    }


    /*
     * For information on the parser library see: http://code.google.com/p/javaparser/
     */

    private void parseFile(String filePath) throws Exception {
        // creates an input stream for the file to be parsed
        FileInputStream in = new FileInputStream(filePath);
        CompilationUnit cu;
        try {
            // parse the file
            cu = JavaParser.parse(in);
        } finally {
            in.close();
        }

        // visit and print the methods names

        new PackageVisitor().visit(cu, null);
        new ClassVisitor().visit(cu, null);
        new MethodVisitor().visit(cu, null);
    }


    /**
     * Simple visitor implementation for visiting PackageDeclaration nodes.
     */
    private class PackageVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(PackageDeclaration n, Object arg) {
            packageName = n.getName().getName();
        }
    }

    /**
     * Simple visitor implementation for visiting ClassOrInterfaceDeclaration nodes.
     */
    private class ClassVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            classes.add(new Entry(Ranges.closed(n.getBeginLine(), n.getEndLine()), packageName + "." + n.getName()));
        }
    }

    /**
     * Simple visitor implementation for visiting MethodDeclaration nodes.
     */
    private class MethodVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(MethodDeclaration n, Object arg) {
            methods.add(new Entry(Ranges.closed(n.getBeginLine(), n.getEndLine()), n.getName()));
        }
    }


    /*    ***      */

    private class Entry {
        private Range key;
        private String value;

        public Entry(Range key, String value) {
            this.key = key;
            this.value = value;
        }

        public Range getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

}
