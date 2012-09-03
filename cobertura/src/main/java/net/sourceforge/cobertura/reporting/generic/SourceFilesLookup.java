package net.sourceforge.cobertura.reporting.generic;

import java.util.*;

/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2012 Jose M. Rozanec
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
        if(sourcesByClass.get(className)!=null){
            return Collections.unmodifiableSet(sourcesByClass.get(className));
        }else{
            return Collections.unmodifiableSet(new HashSet<SourceFileEntry>());
        }
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
            ((Set)map.get(fileEntry.getClassName())).add(fileEntry);
        }
    }
}
