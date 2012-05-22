package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import java.util.*;

/**
 * This class groups data to be reported,
 * so that can be easily accessed when building reports.
 */
public class GenericReport {

    @Attribute
    private Date created;

    @ElementList(inline=true)
    private Set<Threshold>thresholds;
    //create a thresholds lookup object; not persisted, loaded on initialization
    private ThresholdsLookup thresholdsLookup;


    @ElementList(inline=true)
    private List<GenericReportEntry> entries;

    public GenericReport(){
        init();
    }

    public GenericReport(List<GenericReportEntry> entries){
        init();
        created = new Date();
        this.entries = Collections.unmodifiableList(entries);
    }

    public void addThreshold(Threshold threshold){
        thresholds.add(threshold);
        thresholdsLookup.add(threshold);
    }

    //TODO this should be removed
    public List<GenericReportEntry>getProjectsReport(){
        return entries;
    }

    public Set<Threshold> getThresholds(GenericReportEntry entry){
        return thresholdsLookup.getThresholds(entry.getEntryLevel(), entry.getName());
    }

    /*   Aux init method   */
    private void init(){
        entries = new ArrayList<GenericReportEntry>();
        thresholds = new HashSet<Threshold>();
        thresholdsLookup = new ThresholdsLookup(new MetricsLoader().getMetrics());
    }

    public void export(IReportFormatStrategy reportFormat){
        reportFormat.save(this);
    }
}
