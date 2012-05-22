package net.sourceforge.cobertura.reporting.generic;

import java.util.*;

/**
 * This class groups thresholds by level and criteria,
 * so we can quickly access the ones that apply to some GenericReportEntry.
 */
public class ThresholdsLookup {
    /*   thresholdsLookup: Map<level+name, Map<metricName, Threshold>>   */
    private Map<String, Map<String, Threshold>> thresholdsLookup;
    private Map<String, Set<ICustomMetric>> metricsLookup;

    private ThresholdsLookup(){}

    public ThresholdsLookup(Set<ICustomMetric>metrics){
        thresholdsLookup = new HashMap<String, Map<String, Threshold>>();
        metricsLookup = new HashMap<String, Set<ICustomMetric>>();
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
    public Set<Threshold> getThresholds(String level, String levelName){
        String key = buildKey(level, levelName);
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
                        threshold = new Threshold(levelName, level, ReportConstants.threshold_criteria_all,0);
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
        if(ReportConstants.level_all.equals(metric.getApplicableLevel())){
            addMetric(metric, ReportConstants.level_project);
            addMetric(metric, ReportConstants.level_package);
            addMetric(metric, ReportConstants.level_sourcefile);
            addMetric(metric, ReportConstants.level_class);
            addMetric(metric, ReportConstants.level_method);
        }else{
            addMetric(metric, metric.getApplicableLevel());
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
    private Threshold getThresholdForMetric(ICustomMetric metric, String currentLevel){
        Threshold threshold = null;
        //check the metric applies to all levels.
        //If so, is safe to search for some definition at upper levels.
        if(ReportConstants.level_all.equals(metric.getApplicableLevel())){
            String levelName = ReportConstants.threshold_criteria_all;
            do{
                if(thresholdsLookup.get(buildKey(currentLevel, levelName))!=null &&
                        thresholdsLookup.get(buildKey(currentLevel, levelName)).containsKey(metric.getName())){
                    threshold = thresholdsLookup.get(buildKey(currentLevel, levelName)).get(metric.getName());
                }
            }while(threshold==null && ((currentLevel=getNextLevel(currentLevel))!=null));
        }
        return threshold;
    }

    private String getNextLevel(String currentLevel){
        String []levels = new String[]{
                ReportConstants.level_method,
                ReportConstants.level_class,
                ReportConstants.level_sourcefile,
                ReportConstants.level_package,
                ReportConstants.level_project
        };
        for(int j=0;j<levels.length;j++){
            if(levels[j].equals(currentLevel)&&
                    !currentLevel.equals(ReportConstants.level_project)){
                return levels[j+1];
            }
        }
        return null;
    }
}
