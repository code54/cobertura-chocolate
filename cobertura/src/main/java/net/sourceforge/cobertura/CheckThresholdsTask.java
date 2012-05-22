package net.sourceforge.cobertura;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.PackageData;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import org.apache.log4j.Logger;

import java.util.Iterator;

public class CheckThresholdsTask {
    private static final Logger log = Logger.getLogger(CheckThresholdsTask.class);

    private ProjectData projectData;
    private int checkThresholdsExitStatus;

    /**
     * Checks thresholds.
     * @param arguments
     * @param projectData
     * @return
     */
    public CheckThresholdsTask checkThresholds(Arguments arguments, ProjectData projectData){
        if(arguments.getTotalBranchThreshold()>projectData.getBranchCoverageRate()){
            log.error("Total branch coverage rate violation");
            checkThresholdsExitStatus = 8;
            return this;
        }
        if(arguments.getTotalLineThreshold()>projectData.getLineCoverageRate()){
            log.error("Total line coverage rate violation");
            checkThresholdsExitStatus = 16;
            return this;
        }

        Iterator packages = projectData.getPackages().iterator();
        PackageData packagedata;
        while(packages.hasNext()){
            packagedata = (PackageData)packages.next();
            if(arguments.getPackageBranchThreshold()>packagedata.getBranchCoverageRate()){
                log.error("Package branch coverage rate violation");
                checkThresholdsExitStatus =32;
                break;
            }
            if(arguments.getPackageLineThreshold()>packagedata.getLineCoverageRate()){
                log.error("Package line coverage rate violation");
                checkThresholdsExitStatus =64;
                break;
            }
            Iterator classes = packagedata.getClasses().iterator();
            ClassData classdata;
            while(classes.hasNext()){
                classdata = (ClassData)classes.next();
                if(arguments.getClassBranchThreshold()>classdata.getBranchCoverageRate()){
                    log.error("Class branch coverage rate violation");
                    checkThresholdsExitStatus =2;
                    break;
                }
                if(arguments.getClassLineThreshold()>classdata.getLineCoverageRate()){
                    log.error("Class line coverage rate violation");
                    checkThresholdsExitStatus =4;
                    break;
                }
            }
        }
        return this;
    }

    public int getCheckThresholdsExitStatus() {
        return checkThresholdsExitStatus;
    }
}
