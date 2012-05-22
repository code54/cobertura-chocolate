package net.sourceforge.cobertura.reporting.generic;

public class CrapMetric implements ICustomMetric {

    private double crapMetric;

    public CrapMetric(){}

    @Override
    public String getName() {
        return "CRAP";
    }

    @Override
    public double getValue() {
        return crapMetric;
    }

    @Override
    public String getMetricDescription() {
        return "Calculated as: CRAP1(m) = comp(m)^2 * (1 – cov(m)/100)^3 + comp(m)\n" +
                "See http://googletesting.blogspot.com.ar/2011/02/this-code-is-crap.html";
    }

    @Override
    public String getApplicableLevel() {
        return level_method;
    }

    @Override
    public void setBasicMetricData(BasicMetricData data) {
        crapMetric = Math.pow(data.getCyclomaticCodeComplexity(),2) *
                Math.pow((1-(data.getLineCoverage().getCoverageRate())),3)+
                data.getCyclomaticCodeComplexity();
    }
}
