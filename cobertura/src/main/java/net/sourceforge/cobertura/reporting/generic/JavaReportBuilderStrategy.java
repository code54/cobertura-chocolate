package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.coveragedata.*;
import net.sourceforge.cobertura.reporting.ComplexityCalculator;
import net.sourceforge.cobertura.reporting.generic.filter.CompositeFilter;
import net.sourceforge.cobertura.reporting.generic.filter.NameFilter;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.EqCriteria;
import net.sourceforge.cobertura.reporting.generic.filter.RelationFilter;
import net.sourceforge.cobertura.util.Constants;
import net.sourceforge.cobertura.util.FileFinder;

import java.util.*;

/**
 * Handles ProjectData information and puts it into a GenericReport object.
 * Assumes ProjectData information corresponds to a Java project.
 */
public class JavaReportBuilderStrategy implements IReportBuilderStrategy {

    //    private List<GenericReportEntry> entries;
    private Set<SourceFile> sourceFiles;

    private String encoding;
    private FileFinder fileFinder;

    public JavaReportBuilderStrategy() {
    }

    public GenericReport getReport(
            List<ProjectData> projects,
            String sourceEncoding, FileFinder finder) {
        this.encoding = sourceEncoding;
        this.fileFinder = finder;
        List<GenericReportEntry> projectEntries = new ArrayList<GenericReportEntry>();
        sourceFiles = new HashSet<SourceFile>();
        ComplexityCalculator complexity = new ComplexityCalculator(fileFinder);

        for (ProjectData project : projects) {
            processSourceFileData(project);
//            buildMetricsForProject(projectEntries);//TODO complete this!
            GenericReportEntry proj = buildRecursively(project, complexity);
            bindToLineNodes(proj);


            //TODO add all lines from sourceFiles and complete the missing data

            for (SourceFile sourceFile : sourceFiles) {
                Node sourceEntry = proj.getNodes(false,
                        new NameFilter(new EqCriteria(sourceFile.getName()))).iterator().next();

                if(sourceEntry!=null){
                    CompositeFilter filter =
                            new CompositeFilter(new RelationFilter(new EqCriteria(NodeType.LINE)));
                    for (SourceFileEntry line : sourceFile.getEntries()) {
                        Node lineEntry = sourceEntry.getNodes(false, filter.addFilter(
                                new NameFilter(new EqCriteria(line.getLineNumber())))).iterator().next();
                        if(lineEntry==null){
                            //TODO add to source entry and class (we do no support methods by now!)
//                            sourceEntry.addNode(GenericReportEntry.line, );
                        }
                    }
                }
            }
        }

        //TODO remove source files from GenericReport: the report must just hold the graph and creation date.
        //TODO remove the rest of the structures and provide a good graph API
        return new GenericReport(null, sourceFiles);
    }

    @Override
    public String getTargetedLanguage() {
        return Constants.targeted_lang_java;
    }

    private void buildMetricsForProject(List<GenericReportEntry> projectEntries) {
        double branchRate = 0;
        double cnn = 0;
        double covBranches = 0;
        double covLines = 0;
        for (GenericReportEntry entry : projectEntries) {
            branchRate += entry.getPayload().getMetric(ReportConstants.metricName_branchCoverageRate).getValue();
            cnn += entry.getPayload().getMetric(ReportConstants.metricName_ccn).getValue();
            covBranches += entry.getPayload().getMetric(ReportConstants.metricName_coveredBranches).getValue();
            covLines += entry.getPayload().getMetric(ReportConstants.metricName_coveredLines).getValue();
        }
    }

    /*  Aux methods to extract data from model object   */
    private GenericReportEntry buildProjectReportEntry(
            ProjectData project, ComplexityCalculator complexity, List<GenericReportEntry> projects) {
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
                new GenericReportEntry(NodeType.PROJECT,
                        project.getName(), branchCoverage, lineCoverage,
                        complexity.getCCNForProject(project),
                        project.getHits());
        projects.add(entry);

        return entry;
    }

    private void buildPackageAndSourceFilesAndClassesReportEntries(
            ProjectData project, ComplexityCalculator complexity,
            GenericReportEntry projectEntry) {

        Iterator<PackageData> iterator = project.getPackages().iterator();
        /*   Iterate packages and extract info   */
        while (iterator.hasNext()) {
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
                    new GenericReportEntry(NodeType.PACKAGE,
                            data.getName(), branchCoverage, lineCoverage,
                            complexity.getCCNForPackage(data),
                            data.getHits());
            projectEntry.addNode(NodeType.PACKAGE.toString(), packageEntry);

            /*   Extract source files for package   */
            Iterator<SourceFileData> sourceFiles = data.getSourceFiles().iterator();
            while (sourceFiles.hasNext()) {
                SourceFileData sfdata = sourceFiles.next();
                GenericReportEntry sfentry = buildSourceFileReportEntry(sfdata, complexity);
                packageEntry.addNode(NodeType.SOURCE.toString(), sfentry);

                /*  Extract classes for source file    */
                Iterator<ClassData> classes = sfdata.getClasses().iterator();
                while (classes.hasNext()) {
                    buildClassReportEntry(classes.next(), complexity, sfentry);
                }
            }
        }
    }

    private GenericReportEntry buildSourceFileReportEntry(
            SourceFileData data, ComplexityCalculator complexity) {
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
                new GenericReportEntry(NodeType.SOURCE,
                        data.getName(), branchCoverage, lineCoverage,
                        complexity.getCCNForSourceFile(data),
                        data.getHits());

        return entry;
    }

    private void buildClassReportEntry(
            ClassData data, ComplexityCalculator complexity,
            GenericReportEntry sfentry) {
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
                new GenericReportEntry(NodeType.CLASS,
                        data.getName(), branchCoverage, lineCoverage,
                        complexity.getCCNForClass(data),
                        data.getHits());

        sfentry.addNode(NodeType.CLASS.toString(), entry);

        Iterator<String> methodNames = data.getMethodNamesAndDescriptors().iterator();

        while (methodNames.hasNext()) {
            String methodName = methodNames.next();
            Iterator<LineData> lines = data.getLines(methodName).iterator();

            Set<GenericReportEntry> lineEntries = new HashSet<GenericReportEntry>();
            //declare method level variables to collect data
            long methodValidBranches = 0;
            long methodCoveredBranches = 0;
            long methodValidLines = 0;
            long methodCoveredLines = 0;
            long methodHits = 0;
            //TODO we are setting line level complexity to 0 and method level complexity to 0
            while (lines.hasNext()) {
                LineData line = lines.next();
                lineEntries.add(
                        new GenericReportEntry(NodeType.LINE,
                                "" + line.getLineNumber(),
                                new CoverageData(line.getNumberOfCoveredBranches(),
                                        line.getNumberOfValidBranches(),
                                        line.getBranchCoverageRate()),
                                new CoverageData(line.getNumberOfCoveredLines(),
                                        line.getNumberOfValidLines(),
                                        line.getLineCoverageRate()),
                                0, line.getHits()));

                methodValidBranches += line.getNumberOfValidBranches();
                methodCoveredBranches += line.getNumberOfCoveredBranches();

                methodValidLines += line.getNumberOfValidLines();
                methodCoveredLines += line.getNumberOfCoveredLines();

                methodHits += line.getHits();
            }

            GenericReportEntry methodLevelEntry =
                    new GenericReportEntry(
                            NodeType.METHOD,
                            methodName,
                            new CoverageData(methodCoveredBranches,
                                    methodValidBranches,
                                    getRate(methodCoveredBranches, methodValidBranches)),
                            new CoverageData(methodCoveredLines,
                                    methodValidLines,
                                    getRate(methodCoveredLines, methodValidLines)),
                            -1, methodHits);

            entry.addNode(NodeType.METHOD.toString(), methodLevelEntry);

            for (GenericReportEntry lineLevelEntry : lineEntries) {
                methodLevelEntry.addNode(NodeType.LINE.toString(), lineLevelEntry);
            }
        }
    }

    private void processSourceFileData(ProjectData project) {
        Iterator<SourceFileData> sourceFiles = project.getSourceFiles().iterator();
        SourceFileData sourceFileData;
        SourceFile sourceFile;
        while (sourceFiles.hasNext()) {
            sourceFileData = sourceFiles.next();

            Iterator<ClassData> classes = sourceFileData.getClasses().iterator();
            Set<String> methodsAndDescriptors = new HashSet<String>();
            while (classes.hasNext()) {
                methodsAndDescriptors.addAll(classes.next().getMethodNamesAndDescriptors());
            }

            try {
                sourceFile = new JavaSourceFileBuilder().build(fileFinder, sourceFileData.getName(), encoding,
                        methodsAndDescriptors);
                this.sourceFiles.add(sourceFile);
            } catch (Exception e) {
                //TODO
            }
        }
    }


    private double getRate(double first, double total) {
        if (total == 0) {
            return 0;
        }
        return first / total;
    }


//    ????????????????????????????????????????????????????????
//    REFACTOR METHODS

    private GenericReportEntry buildLine(SourceFileEntry entry){
        GenericReportEntry genericReportEntry = new GenericReportEntry(NodeType.LINE,
                ""+entry.getLineNumber(), new CoverageData(0,0,0), new CoverageData(0,0,0), 0,0);

        return genericReportEntry;
    }

    private GenericReportEntry buildRecursively(CoverageDataContainer data, ComplexityCalculator complexity) {
        Iterator<CoverageData> childs = data.getChildrenValues().iterator();
        GenericReportEntry newNode = buildNode(data, complexity);
        while (childs.hasNext()) {
            CoverageData child = childs.next();
            GenericReportEntry newChild = null;
            switch (getLevel(child)) {
                case LINE:
                    newChild = buildLineNode((LineData) (Object) child, complexity);
                    break;
                default:
                    CoverageDataContainer castedChild = (CoverageDataContainer) (Object) child;
                    newChild = buildRecursively(castedChild, complexity);
                    break;
            }
            newNode.addNode(getLevel(child).toString(), newChild);
        }
        return newNode;
    }

    //valid for all data types except for lineData, which is CoverageData interface!
    private GenericReportEntry buildNode(CoverageDataContainer data, ComplexityCalculator complexity) {
        //TODO replace CoverageData for BasicMetrics and use Node API
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
                new GenericReportEntry(getLevel((CoverageData) (Object) data),
                        data.getName(), branchCoverage, lineCoverage,
                        getComplexity(data, complexity),
                        data.getHits());

        return entry;
    }

    private GenericReportEntry buildLineNode(LineData data, ComplexityCalculator complexity) {
        return new GenericReportEntry(NodeType.LINE,
                "" + data.getLineNumber(),
                new CoverageData(data.getNumberOfCoveredBranches(),
                        data.getNumberOfValidBranches(),
                        data.getBranchCoverageRate()),
                new CoverageData(data.getNumberOfCoveredLines(),
                        data.getNumberOfValidLines(),
                        data.getLineCoverageRate()),
                0, data.getHits());
    }

    private void bindToLineNodes(GenericReportEntry data) {
        if (data.getType().equals(NodeType.SOURCE.toString()) ||
                data.getType().equals(NodeType.CLASS.toString()) ||
                data.getType().equals(NodeType.METHOD.toString())) {

            bindToLineNodeForRelation(data, NodeType.SOURCE.toString());
            bindToLineNodeForRelation(data, NodeType.CLASS.toString());
            bindToLineNodeForRelation(data, NodeType.METHOD.toString());

            Set<? extends Node> lines = data.getAllNodes(false,
                    new RelationFilter(new EqCriteria(NodeType.LINE.toString())));
            for (Node line : lines) {
                data.addNode(NodeType.LINE.toString(), line);
            }
        } else {
            Set<? extends Node> sources = data.getAllNodes(false,
                    new RelationFilter(new EqCriteria(NodeType.LINE.toString())));
            for (Node source : sources) {
                bindToLineNodes((GenericReportEntry) source);
            }
        }
    }

    private void bindToLineNodeForRelation(GenericReportEntry data, String relation) {
        Set<? extends Node> sources = data.getNodesForRelation(relation);
        for (Node source : sources) {
            bindToLineNodes((GenericReportEntry) source);
        }
    }

    private double getComplexity(CoverageDataContainer data, ComplexityCalculator complexity) {
        if (data.getClass().equals(ProjectData.class)) {
            return complexity.getCCNForSourceFile((SourceFileData) data);
        }
        if (data.getClass().equals(PackageData.class)) {
            return complexity.getCCNForSourceFile((SourceFileData) data);
        }
        if (data.getClass().equals(SourceFileData.class)) {
            return complexity.getCCNForSourceFile((SourceFileData) data);
        }
        if (data.getClass().equals(ClassData.class)) {
            return complexity.getCCNForSourceFile((SourceFileData) data);
        }
        return -1;
    }

    private NodeType getLevel(CoverageData data) {
        if (data.getClass().equals(ProjectData.class)) {
            return NodeType.PROJECT;
        }
        if (data.getClass().equals(PackageData.class)) {
            return NodeType.PACKAGE;
        }
        if (data.getClass().equals(SourceFileData.class)) {
            return NodeType.SOURCE;
        }
        if (data.getClass().equals(ClassData.class)) {
            return NodeType.CLASS;
        }
        if (data.getClass().equals(LineData.class)) {
            return NodeType.LINE;
        }
        return NodeType.METHOD;
    }
}
