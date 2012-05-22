package net.sourceforge.cobertura.reporting.generic;

import java.util.Iterator;
import java.util.List;

/**
 * This report presents results from validating data against thresholds.
 */
public class ValidateThresholdReportStrategy implements IReportFormatStrategy {

    @Override
    public void save(GenericReport report) {
        List<GenericReportEntry> entries =
                report.getEntriesForLevel(ReportConstants.level_all);

        //retrieve all thresholds and compare for each entry
        //if dont pass, add to result
        for(GenericReportEntry e : entries){
            Iterator<Threshold>thresholds =
                    report.getThresholds(e).iterator();
            while (thresholds.hasNext()){
                Threshold t = thresholds.next();
                if(t.isBelowThreshold(e.getMetric(t.getMetricName()).getValue())){
                    //todo save metric+entry data
                }
            }
        }

        //produce an xml
    }
}
