package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
