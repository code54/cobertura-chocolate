package net.sourceforge.cobertura.reporting.generic;

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
public interface IMetric {
    /**
     * Metrics name;
     * @return
     */
    String getName();

    /**
     * Metric value;
     * @return
     */
    double getValue();

    /**
     * Provide a metric description;
     * @return
     */
    String getMetricDescription();

    public enum BasicMetricsEnum{
        hits("Hits"), branch_coverage_rate("Branch coverage rate"), total_branches("Total branches"),
        covered_branches("Covered branches"), line_coverage_rate("Line coverage rate"),
        total_lines("Total lines"), covered_lines("Covered lines"), ccn("Cyclic Complexity Number");

        private String desc;

        BasicMetricsEnum(String desc){
            this.desc = desc;
        }

        public String desc(){
            return desc;
        }
    }
}
