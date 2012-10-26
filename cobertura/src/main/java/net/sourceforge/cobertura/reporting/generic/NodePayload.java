package net.sourceforge.cobertura.reporting.generic;

import com.sun.istack.internal.NotNull;
import org.simpleframework.xml.Element;

/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2012 Jose M. Rozanec
 *
 * Cobertura is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * Cobertura is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cobertura; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
public class NodePayload implements Payload{

    @Element(required = false)
    private Object content;
    @Element
    private MetricRegistry metrics;

    public NodePayload(CoverageData branchCoverage, CoverageData lineCoverage,
                       double cyclomaticCodeComplexity, long hits){
        metrics = new MetricRegistry();

        IMetric.BasicMetricsEnum metric = IMetric.BasicMetricsEnum.hits;
        putMetric(new BasicMetric(metric.toString(), metric.desc(), hits));

        metric = IMetric.BasicMetricsEnum.ccn;
        putMetric(new BasicMetric(metric.toString(), metric.desc(), cyclomaticCodeComplexity));

        buildBranchMetrics(branchCoverage);
        buildLineMetrics(lineCoverage);
    }

    @Override
    public Object getContent() {
        return content;
    }

    @Override
    public void setContent(Object content) {
        this.content = content;
    }

    @Override
    public IMetric getMetric(String name) {
        return metrics.get(name);
    }

    @Override
    public void putMetric(@NotNull IMetric metric) {
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
