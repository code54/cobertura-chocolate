package net.sourceforge.cobertura.reporting.generic;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.*;

public class GenericReportEntry {

    private static final Logger log = Logger.getLogger(GenericReportEntry.class);

    public static final String level_project = "project";
    public static final String level_package = "package";
    public static final String level_sourcefile = "sourcefile";
    public static final String level_class = "class";
    public static final String level_method = "method";
    public static final String level_all = "all";

    @Attribute
    private String entryLevel;
    @Attribute
    private String name;

    @Element
    private BasicMetricData basicMetricData;

    private Set<ICustomMetric>customMetrics;

    @ElementList(inline=true)
    private Set<GenericReportEntry>childs;

    public GenericReportEntry(){}

    public GenericReportEntry(String entryLevel, String name,
                              CoverageData branchCoverage, CoverageData lineCoverage,
                              double cyclomaticCodeComplexity, int numberOfClasses,
                              int numberOfSourceFiles){
        this.entryLevel = entryLevel;
        this.name = name;
        this.basicMetricData =
                new BasicMetricData(branchCoverage, lineCoverage,
                              cyclomaticCodeComplexity, numberOfClasses,
                              numberOfSourceFiles);

        childs = new HashSet<GenericReportEntry>();
        loadMetrics();
    }

    public void loadMetrics(){
        customMetrics = new HashSet<ICustomMetric>();

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addUrls(ClasspathHelper.forClass(ICustomMetric.class))
                .setScanners(new SubTypesScanner()));
        Iterator<Class<? extends ICustomMetric>> iterator =
                reflections.getSubTypesOf(ICustomMetric.class).iterator();

        Map<String, ICustomMetric> metrics = new HashMap<String, ICustomMetric>();
        while(iterator.hasNext()){
            try{
                ICustomMetric metric = (ICustomMetric)Class.forName(iterator.next().getName()).newInstance();
                if(metric.getApplicableLevel().equals(getEntryLevel())||
                    level_all.equals(getEntryLevel())){
                        metric.setBasicMetricData(basicMetricData);
                        customMetrics.add(metric);
                }
            }catch (Exception e){
                log.error("An error occurred while loading metrics", e);
            }
        }
    }

    public Set<ICustomMetric> getCustomMetrics(){
        return Collections.unmodifiableSet(customMetrics);
    }

    public void addChild(GenericReportEntry entry){
        childs.add(entry);
    }

    /**
     * Returns level to which this information applies.
     * @return
     */
    public String getEntryLevel(){
        return entryLevel;
    }

    public String getName(){
        return name;
    }


    public BasicMetricData getBasicMetricData() {
        return basicMetricData;
    }
}
