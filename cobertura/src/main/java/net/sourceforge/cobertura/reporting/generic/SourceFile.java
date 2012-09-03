package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.io.File;
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
public class SourceFile {

    @Attribute
    private String sourceFileName;

    @ElementList(inline=true)
    private Set<SourceFileEntry> entries;

    private SourceFile(){
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
