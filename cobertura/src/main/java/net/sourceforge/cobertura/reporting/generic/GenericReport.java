package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

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

/**
 * This class groups data to be reported,
 * so that can be easily accessed when building reports.
 */
public class GenericReport {

    //TODO refactor GenericReport into a Node with a custom payload
    //which holds additional data: date and thresholds

    @Attribute
    private Date created;

    @ElementList(inline=true)
    private Set<Threshold>thresholds;
    //create a thresholds lookup object; not persisted, loaded on initialization
    private ThresholdsLookup thresholdsLookup;

    @ElementList(inline=true)
    private List<GenericReportEntry> entries;

    @ElementList(inline=true)
    private Set<SourceFile> sourceFiles;

    private SourceFilesLookup sourceFilesLookup;

    public GenericReport(){
        init();
    }

    public GenericReport(List<GenericReportEntry> entries, Set<SourceFile> sourceFiles){
        init();
        created = new Date();
        this.entries = Collections.unmodifiableList(entries);
        this.sourceFiles = Collections.unmodifiableSet(sourceFiles);
        sourceFilesLookup = new SourceFilesLookup(this.sourceFiles);
    }

    public void addThreshold(Threshold threshold){
        thresholds.add(threshold);
        thresholdsLookup.add(threshold);
    }

    @Deprecated
    public List<GenericReportEntry>getEntriesForLevel(String level){
        List<GenericReportEntry>entries = new ArrayList<GenericReportEntry>();
        for(GenericReportEntry entry : this.entries){
//            entry.getEntriesForLevel(entries, level);//TODO replace this method for aux
        }
        return entries;
    }

    public Set<Threshold> getThresholds(GenericReportEntry entry){
        return thresholdsLookup.getThresholds(entry.getType(), entry.getName());
    }

    public Set<SourceFileEntry> getSourceLinesByClass(String className){
        return sourceFilesLookup.getSourceLinesByClass(className);
    }

    public Set<SourceFileEntry> getSourceLinesByMethod(String methodName, String method){
        return sourceFilesLookup.getSourceLinesByMethod(methodName, method);
    }

    /*   Aux init method   */
    private void init(){
        entries = new ArrayList<GenericReportEntry>();
        sourceFiles = new HashSet<SourceFile>();
        thresholds = new HashSet<Threshold>();
        thresholdsLookup = new ThresholdsLookup(new MetricsLoader().getMetrics());
    }

    public void export(IReportFormatStrategy reportFormat){
        reportFormat.save(this);
    }
}
