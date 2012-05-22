package net.sourceforge.cobertura;

import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.instrument.CodeInstrumentationTask;
import net.sourceforge.cobertura.reporting.generic.GenericReport;
import net.sourceforge.cobertura.reporting.generic.IReportBuilderFactory;
import net.sourceforge.cobertura.reporting.generic.ReportBuilderFactory;
import net.sourceforge.cobertura.util.FileFinder;

import java.util.ArrayList;
import java.util.List;

import static net.sourceforge.cobertura.coveragedata.TouchCollector.applyTouchesOnProjectData;

public class Cobertura {

    private Arguments args;
    private ProjectData projectData;
    private CodeInstrumentationTask instrumentationTask;
    private CheckThresholdsTask checkThresholdsTask;
    private IReportBuilderFactory reportBuilderFactory;


    private boolean didApplyInstrumentationResults;

    /*
     * Private constructor so we get sure Cobertura
     * is always initialized with Arguments
     */
    private Cobertura(){}

    public Cobertura(Arguments arguments){
        args=arguments;
        instrumentationTask = new CodeInstrumentationTask();
        checkThresholdsTask = new CheckThresholdsTask();
        reportBuilderFactory = new ReportBuilderFactory();

        didApplyInstrumentationResults = false;
    }

    public Cobertura instrumentCode() throws Throwable {
        instrumentationTask.instrument(args, getProjectDataInstance());
        return this;
    }

    /**
     * This should be invoked after running tests.
     * @return
     */
    public void applyInstrumentationResults(){
        applyTouchesOnProjectData(projectData);
        didApplyInstrumentationResults = true;
    }

    public ThresholdInformation checkThresholds(){
        checkThresholdsTask.checkThresholds(args,getProjectDataInstance());
        return new ThresholdInformation(checkThresholdsTask.getCheckThresholdsExitStatus());
    }

    public GenericReport report(){
        if(!didApplyInstrumentationResults){
            applyInstrumentationResults();
        }
        List<ProjectData> projects = new ArrayList<ProjectData>();
        projects.add(getProjectDataInstance());

        //TODO currently we set up a fileFinder with baseDir; see how to restructure args...
        FileFinder fileFinder = new FileFinder();
        fileFinder.addSourceDirectory(args.getBaseDirectory().getAbsolutePath());

        return reportBuilderFactory.getInstance(args.getTargetedLanguage())
                .getReport(projects, args.getEncoding(), fileFinder);
    }

    public Cobertura saveProjectData(){
        CoverageDataFileHandler.saveProjectData(getProjectDataInstance(), args.getDataFile());
        return this;
    }

    /*  Aux methods  */
    private ProjectData getProjectDataInstance(){
        // Load project data; see notes at the beginning of CodeInstrumentationTask class
        if(projectData!=null){
            return projectData;
        }
		if (args.getDataFile().isFile())
			projectData = CoverageDataFileHandler.loadProjectData(args.getDataFile());
		if (projectData == null)
			projectData = new ProjectData();

        return projectData;
    }

    /*   Aux classes   */

    public static class ThresholdInformation{
        private int thresholdsExitStatus;

        private ThresholdInformation(){}

        public ThresholdInformation(int thresholdsExitStatus){
            this.thresholdsExitStatus = thresholdsExitStatus;
        }

        public int getCheckThresholdsExitStatus() {
            return thresholdsExitStatus;
        }
    }
}