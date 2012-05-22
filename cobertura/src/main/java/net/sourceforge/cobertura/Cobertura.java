package net.sourceforge.cobertura;

import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.instrument.CodeInstrumentationTask;

public class Cobertura {

    private Arguments args;
    ProjectData projectData;
    private CodeInstrumentationTask instrumentationTask;
    private CheckThresholdsTask checkThresholdsTask;

    /*
     * Private constructor so we get sure Cobertura
     * is always initialized with Arguments
     */
    private Cobertura(){}

    public Cobertura(Arguments arguments){
        args=arguments;
        instrumentationTask = new CodeInstrumentationTask();
        checkThresholdsTask = new CheckThresholdsTask();
    }

    public Cobertura instrumentCode() throws Throwable {
        instrumentationTask.instrument(args,getProjectDataInstance());
        return this;
    }

    public ThresholdInformation checkThresholds(){
        checkThresholdsTask.checkThresholds(args,getProjectDataInstance());
        return new ThresholdInformation(checkThresholdsTask.getCheckThresholdsExitStatus());
    }

    public Cobertura report(String format){
        //the format may be a reporting strategy, not a string
        //todo: should return a report instance,
        // that wrappes a specific report and is able to perform
        //generic actions as persist, etc + respond with GenericReportData
        return this;
    }

    public Cobertura saveProjectData(){
        CoverageDataFileHandler.saveCoverageData(getProjectDataInstance(), args.getDataFile());
        return this;
    }

    /*  Aux methods  */
    private ProjectData getProjectDataInstance(){
        // Load coverage data; see notes at the beginning of CodeInstrumentationTask class
        if(projectData!=null){
            return projectData;
        }
		if (args.getDataFile().isFile())
			projectData = CoverageDataFileHandler.loadCoverageData(args.getDataFile());
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
