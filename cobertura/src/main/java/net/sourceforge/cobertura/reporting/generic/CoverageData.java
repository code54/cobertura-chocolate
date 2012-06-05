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
public class CoverageData {

    public CoverageData(){}

    public CoverageData(double total,
                        double covered,
                        double coverageRate){
        this.total = total;
        this.covered=covered;
        this.coverageRate=coverageRate;
    }
    @Attribute
    private double total;
    @Attribute
    private double covered;

    /*   Percentage we have   */
    @Attribute
    private double coverageRate;

    public double getTotal() {
        return total;
    }

    public double getCovered() {
        return covered;
    }

    public double getCoverageRate() {
        return coverageRate;
    }
}
