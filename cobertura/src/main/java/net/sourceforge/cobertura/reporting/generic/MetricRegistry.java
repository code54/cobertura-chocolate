package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.ElementList;
import sun.misc.BASE64Encoder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MetricRegistry {

    private Map<String, IMetric>metrics;

    @ElementList
    private Set<MetricWrapper>wrappedMetrics;
    private Set<IMetric>unwrappedMetrics;

    public MetricRegistry(){
        metrics = new HashMap<String, IMetric>();
        wrappedMetrics = new HashSet<MetricWrapper>();
        unwrappedMetrics = new HashSet<IMetric>();
    }

    public void register(IMetric metric){
        if(metric instanceof ICustomMetric){
            ((ICustomMetric) metric).setMetricRegistry(this);
        }
        metrics.put(metric.getName(), metric);

        unwrappedMetrics.add(metric);
        wrapMetrics();
    }

    public IMetric get(String metric){
        return metrics.get(metric);
    }

    private void wrapMetrics(){
        Set<IMetric>toBeRemoved = new HashSet<IMetric>();
        for(IMetric metric : unwrappedMetrics){
            try{
                MetricWrapper wrapper = new MetricWrapper(metric);
                wrappedMetrics.add(wrapper);
                toBeRemoved.add(metric);
            }catch (InsufficientInfoException e){}
        }
        unwrappedMetrics.removeAll(toBeRemoved);
    }
}
