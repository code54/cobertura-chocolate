package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;

public class CoverageData {

    public CoverageData(){}

    public CoverageData(double total,
                        double covered,
                        double coverageRate,
                        double coverageRateThreshold){
        this.total = total;
        this.covered=covered;
        this.coverageRate=coverageRate;
        this.coverageRateThreshold=coverageRateThreshold;
    }
    @Attribute
    private double total;
    @Attribute
    private double covered;

    /*   Requested thresholds   */
    @Attribute
    private double coverageRateThreshold;

    /*   Percentage we have   */
    @Attribute
    private double coverageRate;

    public double getTotal() {
        return total;
    }

    public double getCovered() {
        return covered;
    }

    public double getCoverageRateThreshold() {
        return coverageRateThreshold;
    }

    public double getCoverageRate() {
        return coverageRate;
    }
}
