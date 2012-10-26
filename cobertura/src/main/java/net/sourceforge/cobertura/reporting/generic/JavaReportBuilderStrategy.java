package net.sourceforge.cobertura.reporting.generic;
/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2011 Jose M. Rozanec
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
import net.sourceforge.cobertura.coveragedata.*;
import net.sourceforge.cobertura.reporting.ComplexityCalculator;
import net.sourceforge.cobertura.reporting.generic.filter.CompositeFilter;
import net.sourceforge.cobertura.reporting.generic.filter.NameFilter;
import net.sourceforge.cobertura.reporting.generic.filter.Relation;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.EqCriteria;
import net.sourceforge.cobertura.reporting.generic.filter.RelationFilter;
import net.sourceforge.cobertura.util.Constants;
import net.sourceforge.cobertura.util.FileFinder;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Handles ProjectData information and puts it into a GenericReport object.
 * Assumes ProjectData information corresponds to a Java project.
 */
public class JavaReportBuilderStrategy implements IReportBuilderStrategy {
    private static final Logger log = Logger.getLogger(JavaReportBuilderStrategy.class);
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
        sourceFiles = new HashSet<SourceFile>();
        ComplexityCalculator complexity = new ComplexityCalculator(fileFinder);

        Set<Node>nodes = new HashSet<Node>();
        for (ProjectData project : projects) {
            processSourceFileData(project);
//            buildMetricsForProject(projectEntries);//TODO complete this!
            GenericReportEntry proj = buildRecursively(project, complexity);
            nodes.add(proj);
            bindToLineNodes(proj);

            for (SourceFile sourceFile : sourceFiles) {
                log.info("Building entries for "+sourceFile.getName());
                for(Node sourceEntry: proj.getAllNodes(false, new NameFilter(new EqCriteria(sourceFile.getName())))){
                        CompositeFilter filter =
                                new CompositeFilter(new RelationFilter(new EqCriteria(NodeType.LINE)));
                        for (SourceFileEntry line : sourceFile.getEntries()) {
                            Node lineEntry = sourceEntry.getNodes(false, filter.addFilter(
                                    new NameFilter(new EqCriteria(line.getLineNumber())))).iterator().next();
                            //complete missing lines with sourcefile data
                            if(lineEntry==null){
                                Node node = new GenericReportEntry(NodeType.LINE, ""+line.getLineNumber(),
                                        new CoverageData(), new CoverageData(),0, 0);
                                sourceEntry.addNode(Relation.LINE, node);
                            }else{
                                lineEntry.getPayload().setContent(line.getCodeLine());
                            }
                            log.info("Added code line: "+line.getCodeLine());
                        }
                    }
                }
            }
        return new GenericReport(nodes);
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
    @Deprecated
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

    @Deprecated
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
            projectEntry.addNode(Relation.PACKAGE, packageEntry);

            /*   Extract source files for package   */
            Iterator<SourceFileData> sourceFiles = data.getSourceFiles().iterator();
            while (sourceFiles.hasNext()) {
                SourceFileData sfdata = sourceFiles.next();
                GenericReportEntry sfentry = buildSourceFileReportEntry(sfdata, complexity);
                packageEntry.addNode(Relation.SOURCE, sfentry);

                /*  Extract classes for source file    */
                Iterator<ClassData> classes = sfdata.getClasses().iterator();
                while (classes.hasNext()) {
                    buildClassReportEntry(classes.next(), complexity, sfentry);
                }
            }
        }
    }

    @Deprecated
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

    @Deprecated
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

        sfentry.addNode(Relation.CLASS, entry);

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

            entry.addNode(Relation.METHOD, methodLevelEntry);

            for (GenericReportEntry lineLevelEntry : lineEntries) {
                methodLevelEntry.addNode(Relation.LINE, lineLevelEntry);
            }
        }
    }

    /**
     * Builds source file data. Retrieves all lines from source files and builds SourceFile instances.
     * @param project
     */
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
                log.error(e);
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
        log.info("*** Building recursively: "+data.getClass());
        Iterator<net.sourceforge.cobertura.coveragedata.CoverageData> childs = data.getChildrenValues().iterator();
        GenericReportEntry newNode = buildNode(data, complexity);
        while (childs.hasNext()) {
            net.sourceforge.cobertura.coveragedata.CoverageData child = childs.next();
            GenericReportEntry newChild = null;
            switch (getLevel(child)) {
                case LINE:
                    newChild = buildLineNode(child);
                    break;
                default:
                    CoverageDataContainer castedChild = (CoverageDataContainer) child;
                    newChild = buildRecursively(castedChild, complexity);
                    break;
            }
            newNode.addNode(getRelation(child), newChild);
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
                new GenericReportEntry(getLevel(data),
                        data.getName(), branchCoverage, lineCoverage,
                        getComplexity(data, complexity),
                        data.getHits());

        return entry;
    }

    private GenericReportEntry buildLineNode(LineData data) {
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


    private GenericReportEntry buildLineNode(net.sourceforge.cobertura.coveragedata.CoverageData data) {
        return buildLineNode((LineData)data);
    }

    private void bindToLineNodes(GenericReportEntry data) {
        if (data.getType().equals(NodeType.SOURCE.toString()) ||
                data.getType().equals(NodeType.CLASS.toString()) ||
                data.getType().equals(NodeType.METHOD.toString())) {

            bindToLineNodeForRelation(data, Relation.SOURCE);
            bindToLineNodeForRelation(data, Relation.CLASS);
            bindToLineNodeForRelation(data, Relation.METHOD);

            Set<? extends Node> lines = data.getAllNodes(false,
                    new RelationFilter(new EqCriteria(NodeType.LINE.toString())));
            for (Node line : lines) {
                data.addNode(Relation.LINE, line);
            }
        } else {
            Set<? extends Node> sources = data.getAllNodes(false,
                    new RelationFilter(new EqCriteria(NodeType.LINE.toString())));
            for (Node source : sources) {
                bindToLineNodes((GenericReportEntry) source);
            }
        }
    }

    private void bindToLineNodeForRelation(GenericReportEntry data, Relation relation) {
        Set<? extends Node> sources = data.getNodesForRelation(relation);
        for (Node source : sources) {
            bindToLineNodes((GenericReportEntry) source);
        }
    }

    private double getComplexity(CoverageDataContainer data, ComplexityCalculator complexity) {
        if (data.getClass().equals(ProjectData.class)) {
            return complexity.getCCNForProject((ProjectData) data);
        }
        if (data.getClass().equals(PackageData.class)) {
            return complexity.getCCNForPackage((PackageData) data);
        }
        if (data.getClass().equals(SourceFileData.class)) {
            return complexity.getCCNForSourceFile((SourceFileData) data);
        }
        if (data.getClass().equals(ClassData.class)) {
            return complexity.getCCNForClass((ClassData) data);
        }
        return -1;
    }

    private NodeType getLevel(Object data) {
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

    private Relation getRelation(net.sourceforge.cobertura.coveragedata.CoverageData data) {
        if (data.getClass().equals(ProjectData.class)) {
            return Relation.PROJECT;
        }
        if (data.getClass().equals(PackageData.class)) {
            return Relation.PACKAGE;
        }
        if (data.getClass().equals(SourceFileData.class)) {
            return Relation.SOURCE;
        }
        if (data.getClass().equals(ClassData.class)) {
            return Relation.CLASS;
        }
        if (data.getClass().equals(LineData.class)) {
            return Relation.LINE;
        }
        return Relation.METHOD;
    }

    private CoverageData castToCoverageData(Object object){
        return (CoverageData)object;
    }
}
