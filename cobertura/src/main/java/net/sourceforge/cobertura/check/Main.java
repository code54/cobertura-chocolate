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

import net.sourceforge.cobertura.Arguments;
import net.sourceforge.cobertura.CMD;
import net.sourceforge.cobertura.CheckThresholdsTask;
import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.util.Header;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;

public class Main{
    private static final Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args) throws MalformedPatternException{
        Header.print(System.out);
		Arguments arguments = new CMD().parseArguments(args).getArguments();
        ProjectData projectData = CoverageDataFileHandler
				.loadCoverageData(arguments.getDataFile());

		if (projectData == null){
			log.error("Error: Unable to read from data file "
					+ arguments.getDataFile().getAbsolutePath());
			System.exit(1);
		}

		System.exit(new CheckThresholdsTask().checkThresholds(arguments, projectData).getCheckThresholdsExitStatus());
	}
}
