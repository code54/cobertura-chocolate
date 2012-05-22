package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;

public class SourceFileEntry implements Comparable{
    @Attribute
    private String className;
    @Attribute
    private String methodName;
    @Attribute
    private long lineNumber;
    @Attribute
    private String codeLine;

    /* We keep this since we need it for xml serialization */
    public SourceFileEntry(){}

    public SourceFileEntry(String className, String methodName,
                           long lineNumber, String codeLine){
        this.className = className;
        this.methodName = methodName;
        this.lineNumber = lineNumber;
        this.codeLine = codeLine;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public String getCodeLine() {
        return codeLine;
    }

    @Override
    public int compareTo(Object o) {
        return (int)(getLineNumber()-((SourceFileEntry) o).getLineNumber());
    }
}
