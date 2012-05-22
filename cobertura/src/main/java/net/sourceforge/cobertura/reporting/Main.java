/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2003 jcoverage ltd.
 * Copyright (C) 2005 Mark Doliner
 * Copyright (C) 2005 Jeremy Thomerson
 * Copyright (C) 2005 Grzegorz Lukasik
 * Copyright (C) 2006 Dan Godfrey
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

package net.sourceforge.cobertura.reporting;

import java.io.File;

import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.reporting.html.HTMLReport;
import net.sourceforge.cobertura.reporting.xml.SummaryXMLReport;
import net.sourceforge.cobertura.reporting.xml.XMLReport;
import net.sourceforge.cobertura.util.CommandLineBuilder;
import net.sourceforge.cobertura.util.Constants;
import net.sourceforge.cobertura.util.FileFinder;
import net.sourceforge.cobertura.util.Header;

import org.apache.log4j.Logger;

public class Main {

	private static final Logger log = Logger.getLogger(Main.class);

	private String format = Constants.report_html;
	private File dataFile = null;
	private File destinationDir = null;
	private String encoding = "UTF-8";
	
	private void parseArguments(String[] args) throws Exception {
		FileFinder finder = new FileFinder();
		String baseDir = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(Constants.basedir)) {
				baseDir = args[++i];
			} else if (args[i].equals(Constants.datafile)) {
				setDataFile( args[++i]);
			} else if (args[i].equals(Constants.destination)) {
				setDestination( args[++i]);
			} else if (args[i].equals(Constants.format)) {
				setFormat( args[++i]);
			} else if (args[i].equals(Constants.encoding)) {
				setEncoding( args[++i]);
			} else {
				if( baseDir==null) {
					finder.addSourceDirectory( args[i]);
				} else {
					finder.addSourceFile( baseDir, args[i]);
				}
			}
		}

		if (dataFile == null)
			dataFile = CoverageDataFileHandler.getDefaultDataFile();

		if (destinationDir == null)
		{
			System.err.println("Error: destination directory must be set");
			System.exit(1);
		}

		if (format == null)
		{
			System.err.println("Error: format must be set");
			System.exit(1);
		}
		
		if (log.isDebugEnabled()){
			log.debug("format is " + format + " encoding is " + encoding);
			log.debug("dataFile is " + dataFile.getAbsolutePath());
			log.debug("destinationDir is "
					+ destinationDir.getAbsolutePath());
		}

		ProjectData projectData = CoverageDataFileHandler.loadCoverageData(dataFile);

		if (projectData == null) {
			log.error("Error: Unable to read from data file " + dataFile.getAbsolutePath());
			System.exit(1);
		}

		ComplexityCalculator complexity = new ComplexityCalculator(finder);
		if (format.equalsIgnoreCase(Constants.report_html)) {
			new HTMLReport(projectData, destinationDir, finder, complexity, encoding);
		} else if (format.equalsIgnoreCase(Constants.report_xml)) {
			new XMLReport(projectData, destinationDir, finder, complexity);
		} else if (format.equalsIgnoreCase(Constants.report_summaryXml)) {
			new SummaryXMLReport(projectData, destinationDir, finder, complexity);
		}
	}
	
	private void setFormat(String value){
		format = value;
		if (!format.equalsIgnoreCase(Constants.report_html)
				&& !format.equalsIgnoreCase(Constants.report_xml)
				&& !format.equalsIgnoreCase(Constants.report_summaryXml)) {
			log.error("" + "Error: format \"" + format +
                    "\" is invalid. Must be either html or xml or summaryXml");
			System.exit(1);
		}
	}

	private void setDataFile(String value) {
		dataFile = new File(value);
		if (!dataFile.exists()){
			log.error("Error: data file " + dataFile.getAbsolutePath()
					+ " does not exist");
			System.exit(1);
		}
		if (!dataFile.isFile()){
			log.error("Error: data file " + dataFile.getAbsolutePath()
					+ " must be a regular file");
			System.exit(1);
		}
	}

	private void setDestination(String value){
		destinationDir = new File(value);
		if (destinationDir.exists() && !destinationDir.isDirectory()){
			log.error("Error: destination directory " + destinationDir
					+ " already exists but is not a directory");
			System.exit(1);
		}
		destinationDir.mkdirs();
	}

	private void setEncoding(String encoding){
		this.encoding = encoding;
	}
	
	public static void main(String[] args) throws Exception {
		Header.print(System.out);

		long startTime = System.currentTimeMillis();

		Main main = new Main();

		try {
			args = CommandLineBuilder.preprocessCommandLineArguments( args);
		}catch( Exception ex) {
			log.error( "Error: Cannot process arguments: " + ex.getMessage());
			System.exit(1);
		}
		
		main.parseArguments(args);

		long stopTime = System.currentTimeMillis();
		log.info("Report time: " + (stopTime - startTime) + "ms");
	}
}
