package net.sourceforge.cobertura.reporting.generic;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MetricsLoader {
    private static final Logger log = Logger.getLogger(MetricsLoader.class);

    private Set<ICustomMetric> customMetrics;

    public MetricsLoader() {
        customMetrics = new HashSet<ICustomMetric>();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addUrls(ClasspathHelper.forClass(ICustomMetric.class))
                .setScanners(new SubTypesScanner()));
        Iterator<Class<? extends ICustomMetric>> iterator =
                reflections.getSubTypesOf(ICustomMetric.class).iterator();
        while (iterator.hasNext()) {
            try {
                customMetrics.add((ICustomMetric) Class.forName(iterator.next().getName()).newInstance());
            } catch (Exception e) {
                log.error("An error occurred while loading metrics", e);
            }
        }
    }

    public Set<ICustomMetric> getMetrics() {
        return Collections.unmodifiableSet(customMetrics);
    }
}
