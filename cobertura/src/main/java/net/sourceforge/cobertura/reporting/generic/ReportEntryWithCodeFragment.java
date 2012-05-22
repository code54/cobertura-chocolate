package net.sourceforge.cobertura.reporting.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class ReportEntryWithCodeFragment extends GenericReportEntry{

    private List<CodeLine> codeFragment;

    public ReportEntryWithCodeFragment(){
        codeFragment = new ArrayList<CodeLine>();
    }

    public ReportEntryWithCodeFragment(String entryLevel, String name,
                              CoverageData branchCoverage, CoverageData lineCoverage,
                              double cyclomaticCodeComplexity, int numberOfClasses,
                              int numberOfSourceFiles){
        super(entryLevel, name, branchCoverage,
                lineCoverage, cyclomaticCodeComplexity,
                numberOfClasses, numberOfSourceFiles);
        codeFragment = new ArrayList<CodeLine>();
    }

    public void addLine(CodeLine line){
        codeFragment.add(line);
    }

    public List<CodeLine>getCodeFragment(){
        Collections.sort(codeFragment);
        return codeFragment;
    }
}
