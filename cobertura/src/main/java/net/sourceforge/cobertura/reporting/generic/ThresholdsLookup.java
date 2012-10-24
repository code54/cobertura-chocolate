package net.sourceforge.cobertura.reporting.generic;

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

/**
 * This class groups thresholds by level and criteria,
 * so we can quickly access the ones that apply to some GenericReportEntry.
 */
public class ThresholdsLookup {
    /*   thresholdsLookup: Map<level+name, Map<metricName, Threshold>>   */
    private Map<String, Map<String, Threshold>> thresholdsLookup;
    private Map<String, Set<ICustomMetric>> metricsLookup;
    private JavaNodeTypeHierarchy levels;

    private ThresholdsLookup(){}

    public ThresholdsLookup(Set<ICustomMetric>metrics){
        thresholdsLookup = new HashMap<String, Map<String, Threshold>>();
        metricsLookup = new HashMap<String, Set<ICustomMetric>>();
        levels = new JavaNodeTypeHierarchy();
        Iterator<ICustomMetric>it = metrics.iterator();
        while(it.hasNext()){
            addMetric(it.next());
        }
    }

    public void add(Threshold threshold){
        Map<String, Threshold>thresholds;
        String key;
        if((thresholds = thresholdsLookup.get(key=getKey(threshold)))==null){
            thresholds = new HashMap<String, Threshold>();
            thresholdsLookup.put(key,thresholds);
        }
        thresholds.put(threshold.getMetricName(),threshold);
    }

    /**
     * Retrieves thresholds for the required level and levelName.
     * If no specific thresholds are set, it will get default ones.
     * @param level
     * @param levelName
     */
    public Set<Threshold> getThresholds(NodeType level, String levelName){
        String key = buildKey(level.toString(), levelName);
        Set<Threshold>thresholds = new HashSet<Threshold>();
        thresholds.addAll(thresholdsLookup.get(key).values());
        Set<ICustomMetric>levelMetrics;
        //see if we have thresholds for all metrics
        if(thresholds.size()!=(levelMetrics=metricsLookup.get(level)).size()){
            //if not, look for missing thresholds...
            Iterator<ICustomMetric>metricsIterator = levelMetrics.iterator();
            while(metricsIterator.hasNext()){
                ICustomMetric metric = metricsIterator.next();
                if(!thresholdsLookup.get(key).containsKey(metric.getName())){
                    //for a given metric, see if there is a threshold defined at upper levels
                    Threshold threshold = getThresholdForMetric(metric, level);
                    if(threshold==null){
                        //if not, create a default threshold
                        threshold = new Threshold(levelName, level.toString(), ReportConstants.threshold_criteria_all,0);
                    }
                    thresholds.add(threshold);
                }
            }
        }
        return Collections.unmodifiableSet(thresholds);
    }

    /*   Aux methods   */
    private String getKey(Threshold threshold){
        return buildKey(threshold.getLevel(),threshold.getCriteria());
    }

    private String buildKey(String level, String levelName){
        return level+levelName;
    }

    private void addMetric(ICustomMetric metric){
        if(ReportConstants.level_all.equals(metric.getApplicableType())){
            addMetric(metric, ReportConstants.level_project);
            addMetric(metric, ReportConstants.level_package);
            addMetric(metric, ReportConstants.level_sourcefile);
            addMetric(metric, ReportConstants.level_class);
            addMetric(metric, ReportConstants.level_method);
        }else{
            //TODO
//            addMetric(metric, metric.getApplicableType());
        }
    }

    private void addMetric(ICustomMetric metric, String level){
        Set<ICustomMetric>metrics = null;
        if((metrics = metricsLookup.get(level))==null){
            metrics = new HashSet<ICustomMetric>();
            metricsLookup.put(level, metrics);
        }
        metrics.add(metric);
    }

    /**
     * We look for metric thresholds defined at this and upper levels,
     * that were defined for all elements of that level if no other threshold
     * overwrites them.
     * @param metric
     * @param currentLevel
     * @return
     */
    private Threshold getThresholdForMetric(ICustomMetric metric, NodeType currentLevel){
        Threshold threshold = null;
        //check the metric applies to all levels.
        //If so, is safe to search for some definition at upper levels.
        if(ReportConstants.level_all.equals(metric.getApplicableType())){
            String levelName = ReportConstants.threshold_criteria_all;
            do{
                if(thresholdsLookup.get(buildKey(currentLevel.toString(), levelName))!=null &&
                        thresholdsLookup.get(buildKey(currentLevel.toString(), levelName)).containsKey(metric.getName())){
                    threshold = thresholdsLookup.get(buildKey(currentLevel.toString(), levelName)).get(metric.getName());
                }
            }while(threshold==null && ((currentLevel=levels.getHigher(currentLevel))!=null));
        }
        return threshold;
    }
}
