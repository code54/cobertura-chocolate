package net.sourceforge.cobertura.reporting.generic;

public interface ICustomMetric {
    public static final String level_project = GenericReportEntry.level_project;
    public static final String level_package = GenericReportEntry.level_package;
    public static final String level_class = GenericReportEntry.level_class;
    public static final String level_method = GenericReportEntry.level_method;

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

    /**
     * Returns the level to which the metric applies.
     * @return
     */
    String getApplicableLevel();

}
