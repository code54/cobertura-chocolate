/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2003 jcoverage ltd.
 * Copyright (C) 2005 Mark Doliner
 * Copyright (C) 2005 Nathan Wilson
 * Copyright (C) 2009 Charlie Squires
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

package net.sourceforge.cobertura.check;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.PackageData;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.util.Constants;
import net.sourceforge.cobertura.util.Header;
import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class Main{

	private static final Logger log = Logger.getLogger(Main.class);

	final Perl5Matcher pm = new Perl5Matcher();

	final Perl5Compiler pc = new Perl5Compiler();

	/**
	 * The default CoverageRate needed for a class to pass the check.
	 */
	CoverageRate minimumCoverageRate;

	/**
	 * The keys of this map contain regular expression Patterns that
	 * match against classes.  The values of this map contain
	 * CoverageRate objects that specify the minimum coverage rates
	 * needed for a class that matches the pattern.
	 */
	Map minimumCoverageRates = new HashMap();

    ProjectData projectData;

	double inRangeAndDivideByOneHundred(String coverageRateAsPercentage){
		return inRangeAndDivideByOneHundred(Integer.valueOf(
				coverageRateAsPercentage).intValue());
	}

	double inRangeAndDivideByOneHundred(int coverageRateAsPercentage){
		if ((coverageRateAsPercentage >= 0)
				&& (coverageRateAsPercentage <= 100)){
			return (double)coverageRateAsPercentage / 100;
		}
		throw new IllegalArgumentException("The value "
				+ coverageRateAsPercentage
				+ "% is invalid.  Percentages must be between 0 and 100.");
	}

	public Main(String[] args) throws MalformedPatternException{
		int exitStatus = 0;
		Header.print(System.out);

		File dataFile = CoverageDataFileHandler.getDefaultDataFile();
		double branchCoverageThreshold = -1.0;
		double lineCoverageThreshold = -1.0;
		double packageBranchCoverageThreshold = -1.0;
		double packageLineCoverageThreshold = -1.0;
		double totalBranchCoverageThreshold = -1.0;
		double totalLineCoverageThreshold = -1.0;

		for (int i = 0; i < args.length; i++){
			if (args[i].equals(Constants.branch)){
				branchCoverageThreshold = inRangeAndDivideByOneHundred(args[++i]);
			}else if (args[i].equals(Constants.datafile)){
				dataFile = new File(args[++i]);
			}else if (args[i].equals(Constants.line)){
				lineCoverageThreshold = inRangeAndDivideByOneHundred(args[++i]);
			}else if (args[i].equals(Constants.packagebranch)){
				packageBranchCoverageThreshold = inRangeAndDivideByOneHundred(args[++i]);
			}else if (args[i].equals(Constants.packageline)){
				packageLineCoverageThreshold = inRangeAndDivideByOneHundred(args[++i]);
			}else if (args[i].equals(Constants.totalbranch)){
				totalBranchCoverageThreshold = inRangeAndDivideByOneHundred(args[++i]);
			}else if (args[i].equals(Constants.totalline)){
				totalLineCoverageThreshold = inRangeAndDivideByOneHundred(args[++i]);
			}
		}

        log.info("Looking for project data in file "+dataFile.getAbsolutePath());

		projectData = CoverageDataFileHandler
				.loadCoverageData(dataFile);

		if (projectData == null){
			log.error("Error: Unable to read from data file "
					+ dataFile.getAbsolutePath());
			System.exit(1);
		}

		// If they didn't specify any thresholds, then use some defaults
		if ((branchCoverageThreshold == -1.0) && (lineCoverageThreshold == -1.0)
				&& (packageLineCoverageThreshold == -1.0)
				&& (packageBranchCoverageThreshold == -1.0)
				&& (totalLineCoverageThreshold == -1.0)
				&& (totalBranchCoverageThreshold == -1.0)
				&& (this.minimumCoverageRates.size() == 0)){
			branchCoverageThreshold = 0.5;
			lineCoverageThreshold = 0.5;
			packageBranchCoverageThreshold = 0.5;
			packageLineCoverageThreshold = 0.5;
			totalBranchCoverageThreshold = 0.5;
			totalLineCoverageThreshold = 0.5;
		}else{
            // If they specified one or more thresholds, default everything else to 0
			if (branchCoverageThreshold == -1.0)
				branchCoverageThreshold = 0.0;
			if (lineCoverageThreshold == -1.0)
				lineCoverageThreshold = 0.0;
			if (packageLineCoverageThreshold == -1.0)
				packageLineCoverageThreshold = 0.0;
			if (packageBranchCoverageThreshold == -1.0)
				packageBranchCoverageThreshold = 0.0;
			if (totalLineCoverageThreshold == -1.0)
				totalLineCoverageThreshold = 0.0;
			if (totalBranchCoverageThreshold == -1.0)
				totalBranchCoverageThreshold = 0.0;
		}

        if(totalBranchCoverageThreshold>projectData.getBranchCoverageRate()){
            log.error("Total branch coverage rate violation");
            System.exit(8);
        }
        if(totalLineCoverageThreshold>projectData.getLineCoverageRate()){
            log.error("Total line coverage rate violation");
            System.exit(16);
        }

        Iterator packages = projectData.getPackages().iterator();
        PackageData packagedata;
        while(packages.hasNext()){
            packagedata = (PackageData)packages.next();
            if(packageBranchCoverageThreshold>packagedata.getBranchCoverageRate()){
                log.error("Package branch coverage rate violation");
                exitStatus=32;
                break;
            }
            if(packageLineCoverageThreshold>packagedata.getLineCoverageRate()){
                log.error("Package line coverage rate violation");
                exitStatus=64;
                break;
            }
            Iterator classes = packagedata.getClasses().iterator();
            ClassData classdata;
            while(classes.hasNext()){
                classdata = (ClassData)classes.next();
                if(branchCoverageThreshold>classdata.getBranchCoverageRate()){
                    log.error("Class branch coverage rate violation");
                    exitStatus=2;
                    break;
                }
                if(lineCoverageThreshold>classdata.getLineCoverageRate()){
                    log.error("Class line coverage rate violation");
                    exitStatus=4;
                    break;
                }
            }
        }
		System.exit(exitStatus);
	}

	public static void main(String[] args) throws MalformedPatternException{
		new Main(args);
	}
}
