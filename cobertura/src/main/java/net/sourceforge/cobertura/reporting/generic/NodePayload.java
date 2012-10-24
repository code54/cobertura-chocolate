package net.sourceforge.cobertura.reporting.generic;

import java.util.HashMap;
import java.util.Map;

public class NodePayload implements Payload{

    private Object content;
    private MetricRegistry metrics;

    public NodePayload(CoverageData branchCoverage, CoverageData lineCoverage,
                       double cyclomaticCodeComplexity, long hits){
        metrics = new MetricRegistry();

        IMetric.BasicMetricsEnum metric = IMetric.BasicMetricsEnum.hits;
        putMetric(new BasicMetric(metric.toString(), metric.desc(), hits));

        metric = IMetric.BasicMetricsEnum.cnn;
        putMetric(new BasicMetric(metric.toString(), metric.desc(), cyclomaticCodeComplexity));

        buildBranchMetrics(branchCoverage);
        buildLineMetrics(lineCoverage);
    }

    @Override
    public Object getContent() {
        return content;
    }

    @Override
    public IMetric getMetric(String name) {
        return metrics.get(name);
    }

    @Override
    public void putMetric(IMetric metric) {
        metrics.register(metric);
    }

    private void buildBranchMetrics(CoverageData branch){
        IMetric.BasicMetricsEnum metric = IMetric.BasicMetricsEnum.branch_coverage_rate;
        putMetric(new BasicMetric(metric.toString(), metric.desc(), branch.getCoverageRate()));

        metric = IMetric.BasicMetricsEnum.covered_branches;
        putMetric(new BasicMetric(metric.toString(), metric.desc(), branch.getCovered()));

        metric = IMetric.BasicMetricsEnum.total_branches;
        putMetric(new BasicMetric(metric.toString(), metric.desc(), branch.getTotal()));
    }

    private void buildLineMetrics(CoverageData line){
        IMetric.BasicMetricsEnum metric = IMetric.BasicMetricsEnum.line_coverage_rate;
        putMetric(new BasicMetric(metric.toString(), metric.desc(), line.getCoverageRate()));

        metric = IMetric.BasicMetricsEnum.covered_lines;
        putMetric(new BasicMetric(metric.toString(), metric.desc(), line.getCovered()));

        metric = IMetric.BasicMetricsEnum.total_lines;
        putMetric(new BasicMetric(metric.toString(), metric.desc(), line.getTotal()));
    }
}
