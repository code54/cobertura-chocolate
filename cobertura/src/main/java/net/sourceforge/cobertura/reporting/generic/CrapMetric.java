package net.sourceforge.cobertura.reporting.generic;

public class CrapMetric implements ICustomMetric {

    private double crapMetric;
    private MetricRegistry registry;

    public CrapMetric(){}

    @Override
    public String getName() {
        return "CRAP";
    }

    @Override
    public double getValue() {
        //TODO perform calculation retrieving required data from registry
        return crapMetric;
    }

    @Override
    public String getMetricDescription() {
        return "Calculated as: CRAP1(m) = comp(m)^2 * (1 – cov(m)/100)^3 + comp(m)\n" +
                "See http://googletesting.blogspot.com.ar/2011/02/this-code-is-crap.html";
    }

    @Override
    public NodeType getApplicableType() {
        return NodeType.METHOD;
    }

    @Override
    public void setMetricRegistry(MetricRegistry registry) {
        this.registry = registry;
    }

//    @Override
    public void setBasicMetricData(BasicMetricData data) {
        crapMetric = Math.pow(data.getCyclomaticCodeComplexity(),2) *
                Math.pow((1-(data.getLineCoverage().getCoverageRate())),3)+
                data.getCyclomaticCodeComplexity();
    }
}
