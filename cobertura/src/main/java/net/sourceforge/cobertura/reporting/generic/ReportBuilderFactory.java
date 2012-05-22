package net.sourceforge.cobertura.reporting.generic;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReportBuilderFactory implements IReportBuilderFactory{
    private static final Logger log = Logger.getLogger(ReportBuilderFactory.class);

    private Map<String, IReportBuilderStrategy> strategies;

    public ReportBuilderFactory(){
        strategies = new HashMap<String, IReportBuilderStrategy>();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addUrls(ClasspathHelper.forClass(IReportBuilderStrategy.class))
                .setScanners(new SubTypesScanner()));
        Iterator<Class<? extends IReportBuilderStrategy>> iterator =
                reflections.getSubTypesOf(IReportBuilderStrategy.class).iterator();
        while (iterator.hasNext()) {
            try {
                IReportBuilderStrategy strategy =
                        (IReportBuilderStrategy) Class.forName(iterator.next().getName()).newInstance();
                strategies.put(strategy.getTargetedLanguage(),strategy);
            } catch (Exception e) {
                log.error("An error occurred while loading metrics", e);
            }
        }
    }

    public IReportBuilderStrategy getInstance(String targetedLanguage){
        return strategies.get(targetedLanguage);
    }
}
