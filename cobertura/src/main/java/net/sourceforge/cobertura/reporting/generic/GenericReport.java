package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.reporting.generic.filter.Relation;
import net.sourceforge.cobertura.reporting.generic.filter.TypeFilter;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.EqCriteria;
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

    //TODO refactor GenericReport
    //holds date, code graph and a thresholds tree
    //provide means to check thresholds against metrics

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
        List<GenericReportEntry>entries = new ArrayList<GenericReportEntry>();
        for(Node entry : this.entries){
            Collections.addAll((Collection)entries,
                    (entry.getAllNodes(true, new TypeFilter(new EqCriteria(getType(level))))).toArray());
        }
        return entries;
    }

    public Set<Threshold> getThresholds(GenericReportEntry entry){
        return new HashSet<Threshold>();
    }

    @Deprecated
    public Set<SourceFileEntry> getSourceLinesByClass(String className){
        return new HashSet<SourceFileEntry>();
    }

    public void export(IReportFormatStrategy reportFormat){
        reportFormat.save(this);
    }

    @Deprecated//kept for refactoring
    private NodeType getType(String level){
        if(ReportConstants.level_project.equals(level)){
            return NodeType.PROJECT;
        }
        if(ReportConstants.level_package.equals(level)){
            return NodeType.PACKAGE;
        }
        if(ReportConstants.level_sourcefile.equals(level)){
            return NodeType.SOURCE;
        }
        if(ReportConstants.level_class.equals(level)){
            return NodeType.CLASS;
        }
        if(ReportConstants.level_method.equals(level)){
            return NodeType.METHOD;
        }
        if(ReportConstants.level_line.equals(level)){
            return NodeType.LINE;
        }
            return NodeType.ALL;
    }
}
