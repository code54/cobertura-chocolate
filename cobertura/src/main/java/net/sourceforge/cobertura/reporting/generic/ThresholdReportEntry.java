package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2011 Jose M. Rozanec
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
@Root(name = "entry")
public class ThresholdReportEntry {

    @Attribute
    private String metricName;
    @Attribute
    private NodeType level;
    @Attribute
    private double thresholdValue;
    @Attribute
    private double metricValue;

    public ThresholdReportEntry(
            String metricName, NodeType level,
            double thresholdValue, double metricValue){
        this.metricName = metricName;
        this.level = level;
        this.thresholdValue = thresholdValue;
        this.metricValue = metricValue;
    }
}
