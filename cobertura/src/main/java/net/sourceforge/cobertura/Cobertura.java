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

    /**
     * Instruments the code. Should be invoked after compiling.
     * Classes to be instrumented are taken from constructor args
     * @return
     * @throws Throwable
     */
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

    /**
     * Checks metrics values against thresholds
     * @return
     */
    public ThresholdInformation checkThresholds(){
        checkThresholdsTask.checkThresholds(args,getProjectDataInstance());
        return new ThresholdInformation(checkThresholdsTask.getCheckThresholdsExitStatus());
    }

    /**
     * Creates a report with coverage and metrics data
     * @return
     */
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

    /**
     * Serializes project data to file specified in constructor args
     * @return
     */
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
