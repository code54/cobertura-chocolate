package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.reporting.generic.filter.Relation;
import org.apache.log4j.Logger;

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
public class GenericReportEntry extends BaseNode implements Node{

    private static final Logger log = Logger.getLogger(GenericReportEntry.class);

    public GenericReportEntry() {}

    public GenericReportEntry(NodeType level, String name,
                              CoverageData branchCoverage, CoverageData lineCoverage,
                              double cyclomaticCodeComplexity, long hits) {
        nodes = new HashMap<Relation, Set<Node>>();

        super.type = level;
        super.name = name;
        buildPayload(branchCoverage, lineCoverage, cyclomaticCodeComplexity, hits);
    }

    private void buildPayload(CoverageData branchCoverage, CoverageData lineCoverage,
                              double cyclomaticCodeComplexity, long hits){
        Payload payload = new NodePayload(branchCoverage, lineCoverage, cyclomaticCodeComplexity, hits);

        Set<ICustomMetric>metrics = new MetricsLoader().getMetrics();
        //set data to all custom metrics
        for(ICustomMetric metric: metrics){
            if (metric.getApplicableType().equals(getType()) ||
                    NodeType.ALL.equals(getType())) {
                payload.putMetric(metric);
            }
        }
        setPayload(payload);
    }
}
