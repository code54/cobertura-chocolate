package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;

/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2011 Jose M. Rozanec
 *
 * Cobertura is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * Cobertura is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cobertura; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
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
