package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.reporting.generic.filter.Filter;
import net.sourceforge.cobertura.reporting.generic.filter.NameFilter;
import net.sourceforge.cobertura.reporting.generic.filter.RelationFilter;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

import java.util.*;

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
public class GenericReportEntry extends BaseNode implements Node{

    private static final Logger log = Logger.getLogger(GenericReportEntry.class);

    public static final String report = "report";
    public static final String project = "project";
    public static final String packag = "package";
    public static final String sourcefile = "sourcefile";
    public static final String clazz = "class";
    public static final String method = "method";
    public static final String line = "line";

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

    @ElementMap(key = "level", entry = "nodes")
    private Map<String, Set<GenericReportEntry>> nodes;



    public GenericReportEntry() {}

    public GenericReportEntry(String entryLevel, String name,
                              CoverageData branchCoverage, CoverageData lineCoverage,
                              double cyclomaticCodeComplexity, long hits) {
        metrics = new HashMap<String, IMetric>();
        nodes = new HashMap<String, Set<GenericReportEntry>>();

        this.entryLevel = entryLevel;
        this.name = name;
        this.basicMetricData =
                new BasicMetricData(branchCoverage, lineCoverage,
                        cyclomaticCodeComplexity, hits);

        loadMetrics();
        buildMetricsMap();
    }

    /**
     * Returns level to which this information applies.
     *
     * @return
     */
    public String getEntryLevel() {
        return entryLevel;
    }

    @Override
    public void addNode(String relation, Node node) {
        if(nodes.get(relation)==null){
            nodes.put(relation, new HashSet<GenericReportEntry>());
        }
        nodes.get(relation).add((GenericReportEntry)node);
    }

    @Override
    public Set<? extends Node> getNodes(Filter filter) {
        return filter.filter(this);
    }

    @Override
    public Set<Node> getAllNodes(Filter filter) {
        Set<Node>filteredNodes = new HashSet<Node>();
        if(!getNodes().isEmpty()){
            filteredNodes.addAll(filter.filter(this));
            for(Node entry : getNodes()){
                 filteredNodes.addAll(entry.getAllNodes(filter));
            }
        }
        return filteredNodes;
    }

    @Override
    public Set<? extends Node> getNodesForRelation(String relation) {
        return nodes.get(relation);
    }

    //TODO remove
//    @Override
//    @Deprecated
//    public void getAllNodesForRelation(Set<Node> nodes, String relation) {
//        nodes.addAll(getAllNodes(new RelationFilter(relation)));
//        Iterator<Map.Entry<String, Set<GenericReportEntry>>> iterator = this.nodes.entrySet().iterator();
//        while(iterator.hasNext()){
//            Map.Entry<String, Set<GenericReportEntry>> entry = iterator.next();
//            if(entry.getKey().equals(relation)){
//                nodes.addAll(getNodesForRelation(relation));
//            }else{
//                for(GenericReportEntry node:entry.getValue()){
//                    node.getAllNodesForRelation(nodes, relation);
//                }
//            }
//        }
//    }

    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return entryLevel;
    }

    //TODO review if this can be reformulated using filters: TypeFilter with ORListedCriteria with level_all or required level
    public void getEntriesForLevel(List<GenericReportEntry> entries, String level){
        if(ReportConstants.level_all.equals(level) ||
                //my level is lower than the required, propagate to nodes
                (new Levels().compare(entryLevel, level)==-1)){
            for(Set<GenericReportEntry>reportEntriesSet : nodes.values()){
                for(GenericReportEntry entry: reportEntriesSet){
                    entry.getEntriesForLevel(entries, level);
                }
            }
        }
        if(ReportConstants.level_all.equals(level) ||
                new Levels().compare(entryLevel, level)==0){
            entries.add(this);
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
        addBranchCoverageData();
        addLineCoverageData();

        IMetric metric = new BasicMetric(
                ReportConstants.metricName_ccn,
                ReportConstants.metricName_ccnDesc,
                basicMetricData.getCyclomaticCodeComplexity());
        metrics.put(metric.getName(), metric);

        metric = new BasicMetric(
                ReportConstants.metricName_hits,
                ReportConstants.metricName_hitsDesc,
                basicMetricData.getHits());
        metrics.put(metric.getName(), metric);
    }

    private void addBranchCoverageData(){
        IMetric metric = new BasicMetric(
                ReportConstants.metricName_branchCoverageRate,
                ReportConstants.metricName_branchCoverageRateDesc,
                basicMetricData.getBranchCoverageData().getCoverageRate());
        metrics.put(metric.getName(), metric);

        metric = new BasicMetric(
                ReportConstants.metricName_coveredBranches,
                ReportConstants.metricName_coveredBranchesDesc,
                basicMetricData.getBranchCoverageData().getCovered());
        metrics.put(metric.getName(), metric);

        metric = new BasicMetric(
                ReportConstants.metricName_totalBranches,
                ReportConstants.metricName_totalBranchesDesc,
                basicMetricData.getBranchCoverageData().getTotal());
        metrics.put(metric.getName(), metric);
    }

    private void addLineCoverageData(){
        IMetric metric = new BasicMetric(
                ReportConstants.metricName_lineCoverageRate,
                ReportConstants.metricName_lineCoverageRateDesc,
                basicMetricData.getLineCoverage().getCoverageRate());
        metrics.put(metric.getName(), metric);

        metric = new BasicMetric(
                ReportConstants.metricName_coveredLines,
                ReportConstants.metricName_coveredLinesDesc,
                basicMetricData.getLineCoverage().getCovered());
        metrics.put(metric.getName(), metric);

        metric = new BasicMetric(
                ReportConstants.metricName_totalLines,
                ReportConstants.metricName_totalLinesDesc,
                basicMetricData.getLineCoverage().getTotal());
        metrics.put(metric.getName(), metric);
    }
}
