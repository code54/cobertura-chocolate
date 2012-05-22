package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "entry")
public class ThresholdReportEntry {

    @Attribute
    private String metricName;
    @Attribute
    private String level;
    @Attribute
    private double thresholdValue;
    @Attribute
    private double metricValue;

    public ThresholdReportEntry(
            String metricName, String level,
            double thresholdValue, double metricValue){
        this.metricName = metricName;
        this.level = level;
        this.thresholdValue = thresholdValue;
        this.metricValue = metricValue;
    }
}
