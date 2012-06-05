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
public class ReportConstants {
    public static final String level_project = "project";
    public static final String level_package = "package";
    public static final String level_sourcefile = "sourcefile";
    public static final String level_class = "class";
    public static final String level_method = "method";
    public static final String level_line = "line";
    public static final String level_all = "all";

    public static final String threshold_criteria_all = "all";

    public static final String metricName_branchCoverageRate = "branch_coverage_rate";
    public static final String metricName_branchCoverageRateDesc = "Branch coverage rate";

    public static final String metricName_totalBranches = "total_branches";
    public static final String metricName_totalBranchesDesc = "Total branches";

    public static final String metricName_coveredBranches = "covered_branches";
    public static final String metricName_coveredBranchesDesc = "Covered branches";

    public static final String metricName_lineCoverageRate = "line_coverage_rate";
    public static final String metricName_lineCoverageRateDesc = "Line coverage rate";

    public static final String metricName_totalLines = "total_lines";
    public static final String metricName_totalLinesDesc = "Total lines";

    public static final String metricName_coveredLines = "covered_lines";
    public static final String metricName_coveredLinesDesc = "Covered lines";

    public static final String metricName_ccn = "ccn";
    public static final String metricName_ccnDesc = "Cyclic Complexity Number";
    public static final String metricName_hits = "hits";
    public static final String metricName_hitsDesc = "Hits";
}
