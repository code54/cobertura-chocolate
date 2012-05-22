package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.*;

public class SourceFile {

    @Attribute
    private String sourceFileName;

    @ElementList(inline=true)
    private Set<SourceFileEntry> entries;

    public SourceFile(){
        entries = new HashSet<SourceFileEntry>();
    }

    public SourceFile(String sourceFileName){
        entries = new HashSet<SourceFileEntry>();
        this.sourceFileName = sourceFileName;
    }

    public void addEntry(SourceFileEntry entry){
        entries.add(entry);
    }

    public Set<SourceFileEntry>getEntries(){
        return Collections.unmodifiableSet(entries);
    }
}
