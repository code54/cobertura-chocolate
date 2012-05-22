package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.PackageData;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.coveragedata.SourceFileData;
import net.sourceforge.cobertura.reporting.ComplexityCalculator;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

import java.util.*;

/**
 * This class groups data to be reported,
 * so that can be easily accessed when building reports.
 */
public class GenericReport {

    @Attribute
    private Date created;

    //TODO makes sense move this to GenericReportEntry, so that different thresholds
    //are supported per project, etc?  See CoverageData thresholds...
    @ElementMap(entry="threshold", key="name", valueType = java.lang.Double.class,
             keyType = String.class, attribute=true, inline=true, required = false)
    private Map<String, Double>thresholds;

    @ElementList(inline=true)
    private List<GenericReportEntry> projectsReport;

    public GenericReport(){
        init();
    }

    public GenericReport(List<ProjectData> projects, ComplexityCalculator complexity){
        init();
        created = new Date();

        for(ProjectData project : projects){
            buildPackageAndSourceFilesAndClassesReportEntries(project, complexity,
                buildProjectReportEntry(project, complexity));
        }
    }

    public void addThreshold(String name, double value){
        thresholds.put(name, value);
    }

    /*   Aux init method   */
    private void init(){
        projectsReport = new ArrayList<GenericReportEntry>();
        thresholds = new HashMap<String, Double>();
    }

    /*  Aux methods to extract data from model object   */
    private GenericReportEntry buildProjectReportEntry(
            ProjectData project, ComplexityCalculator complexity){
        CoverageData branchCoverage =
                new CoverageData(
                        project.getNumberOfValidBranches(),
                        project.getNumberOfCoveredBranches(),
                        project.getBranchCoverageRate(), 0);

        CoverageData lineCoverage =
                new CoverageData(
                        project.getNumberOfValidLines(),
                        project.getNumberOfCoveredLines(),
                        project.getLineCoverageRate(), 0);

        GenericReportEntry entry =
                new GenericReportEntry(GenericReportEntry.level_project,
                project.getName(), branchCoverage, lineCoverage,
                        complexity.getCCNForProject(project),
                        project.getClasses().size(),
                        project.getSourceFiles().size());
        projectsReport.add(entry);

        return entry;
    }

    private void buildPackageAndSourceFilesAndClassesReportEntries(
            ProjectData project, ComplexityCalculator complexity,
            GenericReportEntry projectEntry){

        Iterator<PackageData>iterator=project.getPackages().iterator();
        /*   Iterate packages and extract info   */
        while (iterator.hasNext()){
            PackageData data = iterator.next();

        CoverageData branchCoverage =
                new CoverageData(
                        data.getNumberOfValidBranches(),
                        data.getNumberOfCoveredBranches(),
                        data.getBranchCoverageRate(), 0);

        CoverageData lineCoverage =
                new CoverageData(
                        data.getNumberOfValidLines(),
                        data.getNumberOfCoveredLines(),
                        data.getLineCoverageRate(), 0);

         GenericReportEntry packageEntry =
                 new GenericReportEntry(GenericReportEntry.level_package,
                        data.getName(), branchCoverage, lineCoverage,
                        complexity.getCCNForPackage(data),
                        data.getClasses().size(),
                        data.getSourceFiles().size());
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
                        data.getBranchCoverageRate(), 0);

        CoverageData lineCoverage =
                new CoverageData(
                        data.getNumberOfValidLines(),
                        data.getNumberOfCoveredLines(),
                        data.getLineCoverageRate(), 0);

        ReportEntryWithCodeFragment entry =
                new ReportEntryWithCodeFragment(GenericReportEntry.level_sourcefile,
                     data.getName(), branchCoverage, lineCoverage,
                     complexity.getCCNForSourceFile(data),
                        data.getClasses().size(),1);

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
                        data.getBranchCoverageRate(), 0);

        CoverageData lineCoverage =
                new CoverageData(
                        data.getNumberOfValidLines(),
                        data.getNumberOfCoveredLines(),
                        data.getLineCoverageRate(), 0);

        GenericReportEntry entry =
                new GenericReportEntry(GenericReportEntry.level_class,
                     data.getName(), branchCoverage, lineCoverage,
                     complexity.getCCNForClass(data),1,1);

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
