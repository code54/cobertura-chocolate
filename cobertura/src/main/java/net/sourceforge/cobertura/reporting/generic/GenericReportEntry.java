package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.HashSet;
import java.util.Set;

public class GenericReportEntry {

    public static final String level_project = "project";
    public static final String level_package = "package";
    public static final String level_sourcefile = "sourcefile";
    public static final String level_class = "class";
    public static final String level_method = "method";
    public static final String level_all = "all";

    @Attribute
    private String entryLevel;
    @Attribute
    private String name;

    @Element
    private CoverageData branchCoverage;
    @Element
    private CoverageData lineCoverage;
    @Attribute
    private double cyclomaticCodeComplexity;
    @Attribute
    private int numberOfClasses;
    @Attribute
    private int numberOfSouceFiles;

    private Set<ICustomMetric>customMetrics;

    @ElementList(inline=true)
    private Set<GenericReportEntry>childs;

    public GenericReportEntry(){}

    public GenericReportEntry(String entryLevel, String name,
                              CoverageData branchCoverage, CoverageData lineCoverage,
                              double cyclomaticCodeComplexity, int numberOfClasses,
                              int numberOfSourceFiles){
        this.entryLevel = entryLevel;
        this.name = name;
        this.branchCoverage = branchCoverage;
        this.lineCoverage = lineCoverage;
        this.cyclomaticCodeComplexity = cyclomaticCodeComplexity;
        this.numberOfClasses = numberOfClasses;
        this.numberOfSouceFiles = numberOfSourceFiles;

        customMetrics = new HashSet<ICustomMetric>();
        childs = new HashSet<GenericReportEntry>();
    }

    /**
     * Allows to add a custom metric.
     * Will be accepted only if applies to this level.
     * @param customMetric
     */
    public void addCustomMetric(ICustomMetric customMetric){
        if(customMetric.getApplicableLevel().equals(getEntryLevel())||
                level_all.equals(getEntryLevel())){
            customMetrics.add(customMetric);
        }
    }

    public void addChild(GenericReportEntry entry){
        childs.add(entry);
    }

    /**
     * Returns level to which this information applies.
     * @return
     */
    public String getEntryLevel(){
        return entryLevel;
    }

    public String getName(){
        return name;
    }

    public CoverageData getBranchCoverageData(){
        return branchCoverage;
    }

    public CoverageData getLineCoverage(){
        return lineCoverage;
    }

    public double getCyclomaticCodeComplexity(){
        return cyclomaticCodeComplexity;
    }

    public int getNumberOfClasses(){
        return numberOfClasses;
    }

    public int getNumberOfSouceFiles(){
        return numberOfSouceFiles;
    }
}
