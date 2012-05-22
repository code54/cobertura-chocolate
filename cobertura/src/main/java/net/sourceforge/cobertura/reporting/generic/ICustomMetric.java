package net.sourceforge.cobertura.reporting.generic;

public interface ICustomMetric {
    public static final String level_project = ReportConstants.level_project;
    public static final String level_package = ReportConstants.level_package;
    public static final String level_class = ReportConstants.level_class;
    public static final String level_method = ReportConstants.level_method;

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

    /**
     * Sets data associated to the GenericReportEntry to which the
     * ICustomMetricInstance applies. Used to perform calculations
     * to get metrics value.
     * @param data
     */
    void setBasicMetricData(BasicMetricData data);
}
