package net.sourceforge.cobertura.reporting.generic;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import sun.net.www.content.text.Generic;

import java.util.*;

public class GenericReportEntry {

    private static final Logger log = Logger.getLogger(GenericReportEntry.class);

    @Attribute
    private String entryLevel;
    @Attribute
    private String name;

    //only used internally and for serialization purposes
    @Element
    private BasicMetricData basicMetricData;

    //only used internally and for serialization purposes
    @ElementList(inline = true)
    private Set<CustomMetricWrapper> customMetrics;

    //this map holds all IMetrics for this entry
    //exposes customMetric and basicMetric data
    private Map<String, IMetric>metrics;

    @ElementList(inline = true)
    private Set<GenericReportEntry> childs;

    public GenericReportEntry() {}

    public GenericReportEntry(String entryLevel, String name,
                              CoverageData branchCoverage, CoverageData lineCoverage,
                              double cyclomaticCodeComplexity, long hits) {
        metrics = new HashMap<String, IMetric>();
        childs = new HashSet<GenericReportEntry>();

        this.entryLevel = entryLevel;
        this.name = name;
        this.basicMetricData =
                new BasicMetricData(branchCoverage, lineCoverage,
                        cyclomaticCodeComplexity, hits);

        loadMetrics();
        buildMetricsMap();
    }

    public void addChild(GenericReportEntry entry) {
        childs.add(entry);
    }

    /**
     * Returns level to which this information applies.
     *
     * @return
     */
    public String getEntryLevel() {
        return entryLevel;
    }

    public String getName() {
        return name;
    }

    public void getEntriesForLevel(List<GenericReportEntry> entries, String level){
        if(ReportConstants.level_all.equals(level)){
            entries.add(this);
            for(GenericReportEntry entry: childs){
                    entry.getEntriesForLevel(entries, level);
            }
        }else{
            switch (new Levels().compare(entryLevel, level)){
                case 0:
                    entries.add(this);
                    break;
                case 1:
                    //my level is higher than the requested, stop here
                    break;
                case -1:
                    //my level is lower than the required, propagate to childs
                    for(GenericReportEntry entry: childs){
                        entry.getEntriesForLevel(entries, level);
                    }
                    break;
            }
        }
    }

    public IMetric getMetric(String name){
        return metrics.get(name);
    }

    /*   Aux methods   */
    private void loadMetrics() {
        customMetrics = new HashSet<CustomMetricWrapper>();

        Iterator<ICustomMetric> iterator =
                new MetricsLoader().getMetrics().iterator();

        while (iterator.hasNext()) {
            ICustomMetric metric = iterator.next();
            if (metric.getApplicableLevel().equals(getEntryLevel()) ||
                    ReportConstants.level_all.equals(getEntryLevel())) {
                metric.setBasicMetricData(basicMetricData);
                customMetrics.add(new CustomMetricWrapper(metric));
                metrics.put(metric.getName(), metric);
            }
        }
        customMetrics = Collections.unmodifiableSet(customMetrics);
    }

    private void buildMetricsMap(){
        //custom metrics should be add in the loadMetrics() method
        //add basicMetricData as IMetric
        IMetric metric = new BasicMetric(
                ReportConstants.metric_name_branch_coverage,
                ReportConstants.metric_name_branch_coverage_desc,
                basicMetricData.getBranchCoverageData().getCoverageRate());
        metrics.put(metric.getName(), metric);

        metric = new BasicMetric(
                ReportConstants.metric_name_line_coverage,
                ReportConstants.metric_name_line_coverage_desc,
                basicMetricData.getLineCoverage().getCoverageRate());
        metrics.put(metric.getName(), metric);

        metric = new BasicMetric(
                ReportConstants.metric_name_ccn,
                ReportConstants.metric_name_ccn_desc,
                basicMetricData.getCyclomaticCodeComplexity());
        metrics.put(metric.getName(), metric);

        metric = new BasicMetric(
                ReportConstants.metric_name_hits,
                ReportConstants.metric_name_hits_desc,
                basicMetricData.getHits());
        metrics.put(metric.getName(), metric);
    }
}
