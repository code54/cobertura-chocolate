package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This report presents results from validating data against thresholds.
 */
public class ValidateThresholdReportStrategy implements IReportFormatStrategy {
    private List<ThresholdReportEntry>reportEntries;

    public ValidateThresholdReportStrategy(){
        reportEntries = new ArrayList<ThresholdReportEntry>();
    }

    @Override
    public void save(GenericReport report) {
        List<GenericReportEntry> entries =
                report.getEntriesForLevel(ReportConstants.level_all);

        //retrieve all thresholds and compare for each entry
        for(GenericReportEntry e : entries){
            Iterator<Threshold>thresholds =
                    report.getThresholds(e).iterator();
            while (thresholds.hasNext()){
                Threshold t = thresholds.next();
                //if does not pass, add to result
                if(t.isBelowThreshold(e.getMetric(t.getMetricName()).getValue())){
                    reportEntries.add(
                            new ThresholdReportEntry(
                                    e.getName(),
                                    e.getEntryLevel(),
                                    t.getThreshold(),
                                    e.getMetric(t.getMetricName()).getValue()));
                }
            }
        }

        //produce an xml
        Serializer serializer = new Persister();
        try {
            serializer.write(report, new File("coberturaThresholdsReport.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
