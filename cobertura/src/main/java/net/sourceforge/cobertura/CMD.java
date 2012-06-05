package net.sourceforge.cobertura;

import net.sourceforge.cobertura.util.Constants;

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

/**
 * This class receives cmd parameters,
 * parses them and passes them to the
 * ArgumentBuilder instance.
 */
public class CMD {

    private Arguments arguments;

    public CMD(){}

    public CMD parseArguments(String[] args){
        this.arguments = new Arguments();

        for (int i = 0; i < args.length; i++){
			if (args[i].equals(Constants.basedir))
				arguments.setBaseDirectory(args[++i]);
			else if (args[i].equals(Constants.datafile))
                arguments.setDataFile(args[++i]);
			else if (args[i].equals(Constants.destination))
				arguments.setDestinationFile(args[++i]);
			else if (args[i].equals(Constants.ignore)){
                arguments.addIgnoreRegex(args[++i]);
			}else if (args[i].equals(Constants.ignoreBranches)){
                arguments.addIgnoreBranchRegex(args[++i]);
			}else if (args[i].equals(Constants.ignoreMethodAnnotation)) {
                arguments.addIgnoreMethodAnnotation(args[++i]);
			}else if (args[i].equals(Constants.ignoreTrivial)) {
				arguments.ignoreTrivial(true);
			}else if (args[i].equals(Constants.includeClasses)){
                arguments.addIncludeClassesRegex(args[++i]);
			}else if (args[i].equals(Constants.excludeClasses)){
                arguments.addExcludeClassesRegex(args[++i]);
			}else if (args[i].equals(Constants.failOnError)){
				arguments.failOnError(true);
			}else if (args[i].equals(Constants.branch)){
                arguments.setClassBranchCoverageThreshold(args[++i]);
			}else if (args[i].equals(Constants.line)){
                arguments.setClassLineCoverageThreshold(args[++i]);
			}else if (args[i].equals(Constants.packagebranch)){
                arguments.setPackageBranchCoverageThreshold(args[++i]);
			}else if (args[i].equals(Constants.packageline)){
                arguments.setPackageLineCoverageThreshold(args[++i]);
			}else if (args[i].equals(Constants.totalbranch)){
                arguments.setTotalBranchCoverageThreshold(args[++i]);
			}else if (args[i].equals(Constants.totalline)){
                arguments.setTotalLineCoverageThreshold(args[++i]);
			}else{
                //if no constant is specified,
                // we assume the string indicates a file to be instrumented
                arguments.addFileToInstrument(args[i]);
                //TODO see that at merge task files are specified in the same way:
                //we should decide if in the future is more convenient to merge reports...
			}
		}
        return this;
    }

    public Arguments getArguments(){
        return arguments;
    }
}
