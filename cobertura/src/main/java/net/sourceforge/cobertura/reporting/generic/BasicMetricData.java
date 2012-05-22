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
    @Element
    private long hits;

    public BasicMetricData(){}

    public BasicMetricData(
            CoverageData branchCoverage, CoverageData lineCoverage,
            double cyclomaticCodeComplexity, long hits){
        this.branchCoverage = branchCoverage;
        this.lineCoverage = lineCoverage;
        this.cyclomaticCodeComplexity = cyclomaticCodeComplexity;
        this.hits = hits;
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

    public long getHits(){
        return hits;
    }
}
