package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.coveragedata.*;
import net.sourceforge.cobertura.reporting.ComplexityCalculator;
import net.sourceforge.cobertura.util.Constants;
import net.sourceforge.cobertura.util.FileFinder;
import net.sourceforge.cobertura.util.Source;
import org.apache.xerces.impl.xpath.regex.Match;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Handles ProjectData information and puts it into a GenericReport object.
 */
public class JavaReportBuilderStrategy implements IReportBuilderStrategy {

    private List<GenericReportEntry> entries;
    private Set<SourceFile> sourceFiles;

    private String encoding;
    private FileFinder fileFinder;

    public JavaReportBuilderStrategy(){}

    public GenericReport getReport(
            List<ProjectData> projects,
            String sourceEncoding, FileFinder finder){
        this.encoding = sourceEncoding;
        this.fileFinder = finder;
        entries = new ArrayList<GenericReportEntry>();
        sourceFiles = new HashSet<SourceFile>();
        ComplexityCalculator complexity = new ComplexityCalculator(fileFinder);

        for(ProjectData project : projects){
            buildPackageAndSourceFilesAndClassesReportEntries(project, complexity,
                buildProjectReportEntry(project, complexity));
            processSourceFileData(project);
        }
        return new GenericReport(entries, sourceFiles);
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
                        complexity.getCCNForProject(project),
                        project.getHits());
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
                        complexity.getCCNForPackage(data),
                         data.getHits());
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

        GenericReportEntry entry =
                new GenericReportEntry(ReportConstants.level_sourcefile,
                     data.getName(), branchCoverage, lineCoverage,
                     complexity.getCCNForSourceFile(data),
                        data.getHits());

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
                     complexity.getCCNForClass(data),
                        data.getHits());

        sfentry.addChild(entry);

        Iterator<String>methodNames = data.getMethodNamesAndDescriptors().iterator();

        while(methodNames.hasNext()){
            String methodName = methodNames.next();
            Iterator<LineData>lines = data.getLines(methodName).iterator();

            Set<GenericReportEntry>lineEntries = new HashSet<GenericReportEntry>();
            //declare method level variables to collect data
            long methodValidBranches = 0;
            long methodCoveredBranches = 0;
            long methodValidLines = 0;
            long methodCoveredLines = 0;
            long methodHits = 0;
            //TODO we are setting line level complexity to 0 and method level complexity to 0
            while(lines.hasNext()){
                LineData line = lines.next();
                lineEntries.add(
                        new GenericReportEntry(ReportConstants.level_line,
                                ""+line.getLineNumber(),
                                new CoverageData(line.getNumberOfCoveredBranches(),
                                    line.getNumberOfValidBranches(),
                                    line.getBranchCoverageRate()),
                                new CoverageData(line.getNumberOfCoveredLines(),
                                    line.getNumberOfValidLines(),
                                    line.getLineCoverageRate()),
                                0, line.getHits()));

                methodValidBranches+=line.getNumberOfValidBranches();
                methodCoveredBranches+=line.getNumberOfCoveredBranches();

                methodValidLines+=line.getNumberOfValidLines();
                methodCoveredLines+=line.getNumberOfCoveredLines();

                methodHits+=line.getHits();
            }

            GenericReportEntry methodLevelEntry =
                    new GenericReportEntry(
                            ReportConstants.level_method,
                            methodName,
                            new CoverageData(methodCoveredBranches,
                                    methodValidBranches,
                                    getRate(methodCoveredBranches,methodValidBranches)),
                            new CoverageData(methodCoveredLines,
                                    methodValidLines,
                                    getRate(methodCoveredLines,methodValidLines)),
                            -1,methodHits);

            entry.addChild(methodLevelEntry);

            for(GenericReportEntry lineLevelEntry : lineEntries){
                methodLevelEntry.addChild(lineLevelEntry);
            }
        }
    }

    private void processSourceFileData(ProjectData project){
        Iterator<SourceFileData>sourceFiles = project.getSourceFiles().iterator();
        SourceFileData sourceFileData;
        SourceFile sourceFile;
        while (sourceFiles.hasNext()){
            sourceFileData = sourceFiles.next();
            sourceFile = new SourceFile(sourceFileData.getName());
            this.sourceFiles.add(sourceFile);
            //we need to get the file and parse its lines
            Map<Integer, String> lines = getSourceFileLines(sourceFileData);

            //get class, method and line data
            Iterator<ClassData>classes = sourceFileData.getClasses().iterator();
            ClassData classData;
            while(classes.hasNext()){
                classData = classes.next();

                //TODO we should iterate lines, and add SourceFileEntries per line.
                //Some lines will correspond to methods, others to variables or comments, etc
                //See how to parse the lines to extract data
                //consider using http://code.google.com/p/javaparser/wiki/UsingThisParser#Changing_methods_from_a_class_with_a_visitor
                //to parse the java files

                Iterator<String>methods = classData.getMethodNamesAndDescriptors().iterator();
                String method;
                while(methods.hasNext()){
                    Iterator<LineData>line = classData.getLines(method = methods.next()).iterator();
                    while(line.hasNext()){
                        int linenumber = line.next().getLineNumber();
                        sourceFile.addEntry(
                                new SourceFileEntry(
                                        classData.getName(),
                                        method,
                                        linenumber,
                                        lines.get(linenumber)));
                    }
                }
            }
        }
    }

    private Map<Integer, String> getSourceFileLines(SourceFileData data) {
        Map<Integer, String> lines = new HashMap<Integer, String>();
        Source source = fileFinder.getSource(data.getName());

        if (source == null) {
            throw new RuntimeException("Unable to locate " + data.getName());
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(source.getInputStream(), encoding));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        try {
            String lineStr;
            int lineNumber = 1;
            while ((lineStr = br.readLine()) != null) {
                //we want to retrieve all the lines...
                lines.put(lineNumber, lineStr);
                lineNumber++;
            }
        } catch (IOException e) {
            //TODO see how to deal with this. We wont blow up just because of a singe file...
        }
        return lines;
    }

    private double getRate(double first, double total){
        if(total==0){
            return 0;
        }
        return first/total;
    }
}
