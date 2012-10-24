package net.sourceforge.cobertura.reporting.generic;

public interface Payload {
    Object getContent();
    IMetric getMetric(String name);
    void putMetric(IMetric metric);
}
