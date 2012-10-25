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

    //we should be able to add thresholds: this is a separate graph
    //provide an utility to check thresholds against metrics

    @Attribute
    private Date created;

    @ElementList(inline=true)
    private Set<Threshold>thresholds;

    @ElementList(inline=true)
    private Set<Node> entries;

    public GenericReport(){
        thresholds = new HashSet<Threshold>();
        entries = new HashSet<Node>();
    }

    public GenericReport(Set<Node> entries){
        created = new Date();
        thresholds = new HashSet<Threshold>();
        this.entries = Collections.unmodifiableSet(entries);
    }

    public void addThreshold(Threshold threshold){
        //TODO build a thresholds graph: each threshold is encapsulated in a Node with a ThresholdPayload
        thresholds.add(threshold);
    }

    @Deprecated
    public List<GenericReportEntry>getEntriesForLevel(String level){
//        List<GenericReportEntry>entries = new ArrayList<GenericReportEntry>();
//        for(GenericReportEntry entry : this.entries){
////            entry.getEntriesForLevel(entries, level);//TODO replace this method for aux
//        }
//        return entries;
        throw new RuntimeException("Deprecated!");
    }

    @Deprecated
    public Set<Threshold> getThresholds(GenericReportEntry entry){
//        return thresholdsLookup.getThresholds(entry.getType(), entry.getName());
        throw new RuntimeException("Deprecated!");
    }

    @Deprecated
    public Set<SourceFileEntry> getSourceLinesByClass(String className){
//        return sourceFilesLookup.getSourceLinesByClass(className);
        throw new RuntimeException("Deprecated!");
    }

    public void export(IReportFormatStrategy reportFormat){
        reportFormat.save(this);
    }
}
