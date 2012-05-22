package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;

/**
 * Represents a threshold for a specific metric,
 * and applies to a level (project/package/etc)
 * and criteria (all or a project/package/class/method name)
 */
public class Threshold {
    @Attribute
    private String metricName;
    @Attribute
    private String level;
    @Attribute
    private String criteria;//all or a level name (ex. some method name)
    @Attribute
    private double threshold;

    private Threshold(){};

    public Threshold(String metricName, String level, String criteria, double threshold){
        this.metricName = metricName;
        this.level = level;
        this.criteria = criteria;
        this.threshold = threshold;
    }

    public boolean isBelowThreshold(double value){
        return value<threshold;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getLevel() {
        return level;
    }

    public String getCriteria() {
        return criteria;
    }
}
