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
public interface ICustomMetric extends IMetric{
    public static final String level_project = ReportConstants.level_project;
    public static final String level_package = ReportConstants.level_package;
    public static final String level_class = ReportConstants.level_class;
    public static final String level_method = ReportConstants.level_method;

    /**
     * Returns the level to which the metric applies.
     * @return
     */
    String getApplicableLevel();

    /**
     * Sets data associated to the GenericReportEntry to which the
     * ICustomMetricInstance applies. Used to perform calculations
     * to get metrics value.
     * @param data
     */
    void setBasicMetricData(BasicMetricData data);
}
