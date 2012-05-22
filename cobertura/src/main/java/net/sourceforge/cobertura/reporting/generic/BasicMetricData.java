package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class BasicMetricData {

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

    public BasicMetricData(){}

    public BasicMetricData(
            CoverageData branchCoverage, CoverageData lineCoverage,
            double cyclomaticCodeComplexity, int numberOfClasses,
            int numberOfSourceFiles){
        this.branchCoverage = branchCoverage;
        this.lineCoverage = lineCoverage;
        this.cyclomaticCodeComplexity = cyclomaticCodeComplexity;
        this.numberOfClasses = numberOfClasses;
        this.numberOfSouceFiles = numberOfSourceFiles;
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
