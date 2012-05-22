package net.sourceforge.cobertura.reporting.generic;

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
