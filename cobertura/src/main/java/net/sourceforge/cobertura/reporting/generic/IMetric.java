package net.sourceforge.cobertura.reporting.generic;

public interface IMetric {
    /**
     * Metrics name;
     * @return
     */
    String getName();

    /**
     * Metric value;
     * @return
     */
    double getValue();

    /**
     * Provide a metric description;
     * @return
     */
    String getMetricDescription();
}
