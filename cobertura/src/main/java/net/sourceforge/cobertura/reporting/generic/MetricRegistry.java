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


    public static void main(String[] args) {
        BASE64Encoder encoder = new BASE64Encoder();
        String test = "hostname=devsoa01\nnode_type=zookeeper\nzookeeper_server_id=1\nserver.1=zk1.perf.tendrilinc.com:2888:3888\nserver.2=zk2.perf.tendrilinc.com:2888:3888\nserver.3=zk3.perf.tendrilinc.com:2888:3888\n";
        String encode = "hostname=devsoa06\nnode_type=rabbitmq\nrabbitmq_server_erl_args=+K true +A30 +P 1048576 -kernel inet_default_connect_options [{nodelay,true}] -kernel inet_dist_listen_min 4370 -kernel inet_dist_listen_max 4372\nrabbitmq_ctl_erl_args=-setCookie devsoaClusterCookie\nrabbitmq_server_start_args=-setCookie devsoaClusterCookie\nrabbit_cluster_nodes=['rabbit@devsoa06']\nrabbit_disk_free_limit={mem_relative, 0.4}\n";
//        try {
            String encodedBytes = encoder.encodeBuffer(encode.getBytes());
            System.out.println(encodedBytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
