package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.PackageData;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.coveragedata.SourceFileData;
import net.sourceforge.cobertura.reporting.ComplexityCalculator;
import net.sourceforge.cobertura.util.Constants;
import net.sourceforge.cobertura.util.FileFinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles ProjectData information and puts it into a GenericReport object.
 */
public class JavaReportBuilderStrategy implements IReportBuilderStrategy {

    private List<GenericReportEntry> entries;

    public JavaReportBuilderStrategy(){}

    public GenericReport getReport(List<ProjectData> projects){
        entries = new ArrayList<GenericReportEntry>();
        ComplexityCalculator complexity = new ComplexityCalculator(new FileFinder());

        for(ProjectData project : projects){
            buildPackageAndSourceFilesAndClassesReportEntries(project, complexity,
                buildProjectReportEntry(project, complexity));
        }
        return new GenericReport(entries);
    }

    @Override
    public String getTargetedLanguage() {
        return Constants.targeted_lang_java;
    }


    /*  Aux methods to extract data from model object   */
    private GenericReportEntry buildProjectReportEntry(
            ProjectData project, ComplexityCalculator complexity){
        CoverageData branchCoverage =
                new CoverageData(
                        project.getNumberOfValidBranches(),
                        project.getNumberOfCoveredBranches(),
                        project.getBranchCoverageRate());

        CoverageData lineCoverage =
                new CoverageData(
                        project.getNumberOfValidLines(),
                        project.getNumberOfCoveredLines(),
                        project.getLineCoverageRate());

        GenericReportEntry entry =
                new GenericReportEntry(ReportConstants.level_project,
                project.getName(), branchCoverage, lineCoverage,
                        complexity.getCCNForProject(project));
        entries.add(entry);

        return entry;
    }

    private void buildPackageAndSourceFilesAndClassesReportEntries(
            ProjectData project, ComplexityCalculator complexity,
            GenericReportEntry projectEntry){

        Iterator<PackageData> iterator=project.getPackages().iterator();
        /*   Iterate packages and extract info   */
        while (iterator.hasNext()){
            PackageData data = iterator.next();

        CoverageData branchCoverage =
                new CoverageData(
                        data.getNumberOfValidBranches(),
                        data.getNumberOfCoveredBranches(),
                        data.getBranchCoverageRate());

        CoverageData lineCoverage =
                new CoverageData(
                        data.getNumberOfValidLines(),
                        data.getNumberOfCoveredLines(),
                        data.getLineCoverageRate());

         GenericReportEntry packageEntry =
                 new GenericReportEntry(ReportConstants.level_package,
                        data.getName(), branchCoverage, lineCoverage,
                        complexity.getCCNForPackage(data));
            projectEntry.addChild(packageEntry);

            /*   Extract source files for package   */
            Iterator<SourceFileData>sourceFiles = data.getSourceFiles().iterator();
            while(sourceFiles.hasNext()){
                SourceFileData sfdata = sourceFiles.next();
                GenericReportEntry sfentry = buildSourceFileReportEntry(sfdata, complexity);
                packageEntry.addChild(sfentry);

                /*  Extract classes for source file    */
                Iterator<ClassData>classes = sfdata.getClasses().iterator();
                while(classes.hasNext()){
                    buildClassReportEntry(classes.next(), complexity, sfentry);
                }
            }
        }
    }

    private GenericReportEntry buildSourceFileReportEntry(
            SourceFileData data, ComplexityCalculator complexity){
        CoverageData branchCoverage =
                new CoverageData(
                        data.getNumberOfValidBranches(),
                        data.getNumberOfCoveredBranches(),
                        data.getBranchCoverageRate());

        CoverageData lineCoverage =
                new CoverageData(
                        data.getNumberOfValidLines(),
                        data.getNumberOfCoveredLines(),
                        data.getLineCoverageRate());

        ReportEntryWithCodeFragment entry =
                new ReportEntryWithCodeFragment(ReportConstants.level_sourcefile,
                     data.getName(), branchCoverage, lineCoverage,
                     complexity.getCCNForSourceFile(data));

        //TODO retrieve lines from sourcefile and add them to entry

        return entry;
    }

    private void buildClassReportEntry(
            ClassData data, ComplexityCalculator complexity,
            GenericReportEntry sfentry){
        CoverageData branchCoverage =
                new CoverageData(
                        data.getNumberOfValidBranches(),
                        data.getNumberOfCoveredBranches(),
                        data.getBranchCoverageRate());

        CoverageData lineCoverage =
                new CoverageData(
                        data.getNumberOfValidLines(),
                        data.getNumberOfCoveredLines(),
                        data.getLineCoverageRate());

        GenericReportEntry entry =
                new GenericReportEntry(ReportConstants.level_class,
                     data.getName(), branchCoverage, lineCoverage,
                     complexity.getCCNForClass(data));

        sfentry.addChild(entry);

        //TODO finish adding method level data support
        /*
        Iterator<String>methodNames = data.getMethodNamesAndDescriptors().iterator();
        while(methodNames.hasNext()){
            String methodName = methodNames.next();
            Iterator<LineData>lines = data.getLines(methodName).iterator();

            //declare method level variables to collect data
            while(lines.hasNext()){
                LineData line = lines.next();
                //retrieve and aggregate data
            }
        }
        */
    }

}
