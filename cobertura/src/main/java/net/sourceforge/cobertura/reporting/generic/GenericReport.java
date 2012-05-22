package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.coveragedata.*;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

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

    public List<GenericReportEntry>getEntriesForLevel(String level){
        List<GenericReportEntry>entries = new ArrayList<GenericReportEntry>();
        for(GenericReportEntry entry : this.entries){
            entry.getEntriesForLevel(entries, level);
        }
        return entries;
    }

    public Set<Threshold> getThresholds(GenericReportEntry entry){
        return thresholdsLookup.getThresholds(entry.getEntryLevel(), entry.getName());
    }

    public Set<SourceFileEntry> getSourceLinesByClass(String className){
        return sourceFilesLookup.getSourceLinesByClass(className);
    }

    public Set<SourceFileEntry> getSourceLinesByMethod(String className, String method){
        return sourceFilesLookup.getSourceLinesByMethod(className, method);
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
