package net.sourceforge.cobertura.reporting.generic;

import org.simpleframework.xml.Attribute;

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
 * Represents a threshold for a specific metric,
 * and applies to a level (project/package/etc)
 * and criteria (all or a project/package/class/method name)
 */
public class Threshold {
    @Attribute
    private String metricName;
    @Attribute
    private String level;
    @Attribute
    private String criteria;//all or a level name (ex. some method name)
    @Attribute
    private double threshold;

    private Threshold(){};

    public Threshold(String metricName, String level, String criteria, double threshold){
        this.metricName = metricName;
        this.level = level;
        this.criteria = criteria;
        this.threshold = threshold;
    }

    public boolean isBelowThreshold(double value){
        return value<threshold;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getLevel() {
        return level;
    }

    public String getCriteria() {
        return criteria;
    }

    public double getThreshold(){
        return threshold;
    }
}
