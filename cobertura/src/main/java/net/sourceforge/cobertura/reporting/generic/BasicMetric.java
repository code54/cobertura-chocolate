package net.sourceforge.cobertura.reporting.generic;

public class BasicMetric implements IMetric{
    private String name;
    private String description;
    private double value;

    public BasicMetric(String name, String description, double value){
        this.name = name;
        this.description = description;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public String getMetricDescription() {
        return description;
    }
}
