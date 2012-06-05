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
public class CodeLine implements Comparable{

    private int lineNumber;
    private boolean isCovered;
    private int hits;
    private boolean isConditional;
    private double conditionalCoverage;

    public CodeLine(){}
    public CodeLine(int lineNumber, boolean isCovered, int hits,
                    boolean isConditional, double conditionalCoverage){
        this.lineNumber = lineNumber;
        this.isCovered = isCovered;
        this.hits=hits;
        this.isConditional = isConditional;
        this.conditionalCoverage = conditionalCoverage;
    }

    @Override
    public int compareTo(Object o) {
        return lineNumber-((CodeLine)o).getLineNumber();
    }

    public int getLineNumber(){
        return lineNumber;
    }

    public boolean isCovered(){
        return isCovered;
    }

    public int getHits(){
        return hits;
    }

    public boolean isConditional(){
        return isConditional;
    }

    public double getConditionalCoverage(){
        return conditionalCoverage;
    }

}
