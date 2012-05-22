package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="metric")
public class CustomMetricWrapper {

    @Attribute
    private String name;
    @Attribute
    private double value;
    @Attribute
    private String metricDescription;
    @Attribute
    private String applicableLevel;

    public CustomMetricWrapper(){}
    public CustomMetricWrapper(ICustomMetric metric){
        this.name = metric.getName();
        this.value = metric.getValue();
        this.metricDescription = metric.getMetricDescription();
        this.applicableLevel = metric.getApplicableLevel();
    }

    public String getName() {
        return name;
    }


    public double getValue() {
        return value;
    }


    public String getMetricDescription() {
        return metricDescription;
    }

    public String getApplicableLevel() {
        return applicableLevel;
    }
}
