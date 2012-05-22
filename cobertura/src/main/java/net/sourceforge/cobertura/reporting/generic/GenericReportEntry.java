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

    @Element
    private BasicMetricData basicMetricData;

    @ElementList(inline = true)
    private Set<CustomMetricWrapper> customMetrics;

    @ElementList(inline = true)
    private Set<GenericReportEntry> childs;

    public GenericReportEntry() {}

    public GenericReportEntry(String entryLevel, String name,
                              CoverageData branchCoverage, CoverageData lineCoverage,
                              double cyclomaticCodeComplexity, long hits) {
        this.entryLevel = entryLevel;
        this.name = name;
        this.basicMetricData =
                new BasicMetricData(branchCoverage, lineCoverage,
                        cyclomaticCodeComplexity, hits);

        childs = new HashSet<GenericReportEntry>();
        loadMetrics();
    }

    public Set<CustomMetricWrapper> getCustomMetrics() {
        return customMetrics;
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

    public BasicMetricData getBasicMetricData() {
        return basicMetricData;
    }

    public void getEntriesForLevel(List<GenericReportEntry> entries, String level){
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
            }
        }
        customMetrics = Collections.unmodifiableSet(customMetrics);
    }
}