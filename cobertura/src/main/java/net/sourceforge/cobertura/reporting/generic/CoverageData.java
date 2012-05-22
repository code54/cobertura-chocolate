package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;

public class CoverageData {

    public CoverageData(){}

    public CoverageData(double total,
                        double covered,
                        double coverageRate){
        this.total = total;
        this.covered=covered;
        this.coverageRate=coverageRate;
    }
    @Attribute
    private double total;
    @Attribute
    private double covered;

    /*   Percentage we have   */
    @Attribute
    private double coverageRate;

    public double getTotal() {
        return total;
    }

    public double getCovered() {
        return covered;
    }

    public double getCoverageRate() {
        return coverageRate;
    }
}
