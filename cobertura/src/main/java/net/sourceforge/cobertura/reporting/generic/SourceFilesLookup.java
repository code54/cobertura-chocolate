package net.sourceforge.cobertura.reporting.generic;

import java.util.*;

public class SourceFilesLookup {
    private Map<String, Set<SourceFileEntry>> sourcesByClass;

    private SourceFilesLookup(){}

    public SourceFilesLookup(Set<SourceFile>sourceFiles){
        sourcesByClass = new HashMap<String, Set<SourceFileEntry>>();
        Iterator<SourceFile>iterator = sourceFiles.iterator();
            SourceFile entry;
            while (iterator.hasNext()){
                entry = iterator.next();
                addEntry(sourcesByClass, entry);
            }
    }

    public Set<SourceFileEntry> getSourceLinesByClass(String className){
        return Collections.unmodifiableSet(sourcesByClass.get(className));
    }

    public Set<SourceFileEntry> getSourceLinesByMethod(String className, String method){
        Set<SourceFileEntry>result = new HashSet<SourceFileEntry>();
        Iterator<SourceFileEntry> entries = getSourceLinesByClass(className).iterator();
        SourceFileEntry entry;
        while(entries.hasNext()){
            entry = entries.next();
            if(entry.getMethodName().equals(method)){
                result.add(entry);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    private void addEntry(Map map, SourceFile entry){
        Iterator<SourceFileEntry> entries = entry.getEntries().iterator();
        while(entries.hasNext()){
            SourceFileEntry fileEntry = entries.next();
            if(map.get(fileEntry.getClassName())==null){
                map.put(fileEntry.getClassName(), new HashSet<SourceFileEntry>());
            }
            ((Set)map.get(fileEntry.getClassName())).add(entry);
        }
    }
}
