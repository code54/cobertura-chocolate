/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2005 Mark Doliner
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

package net.sourceforge.cobertura.reporting.xml;

import java.io.File;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.reporting.ComplexityCalculator;
import net.sourceforge.cobertura.reporting.JUnitXMLHelper;
import net.sourceforge.cobertura.util.FileFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.sourceforge.cobertura.testutil.Util.createRequiredDirectories;
import static net.sourceforge.cobertura.testutil.Util.removeRequiredDirectories;
import static net.sourceforge.cobertura.testutil.Util.removeTestReportFiles;

public class XMLReportTest{

	private final static String BASEDIR = (System.getProperty("basedir") != null) ? System
			.getProperty("basedir") : ".";
	private final static String PATH_TO_TEST_OUTPUT = BASEDIR + "/build/test/XMLReportTest";
	private File tmp;

    @Before
	public void setUp(){
		tmp = new File(PATH_TO_TEST_OUTPUT);
        createRequiredDirectories(new File[]{tmp});
	}

    @After
	public void tearDown(){
		removeTestReportFiles(new File(BASEDIR));
        removeRequiredDirectories(new File[]{tmp});
	}

    @Test
	public void testXMLReportWithNonSourceLines() throws Exception{
		ProjectData projectData = new ProjectData();

		// Adding line to the project data that hasn't been yet marked as source line 
		ClassData cd = projectData.getOrCreateClassData(XMLReport.class.getName());
		cd.touch(7777,1);

		File reportDir = File.createTempFile("XMLReportTest", "");
		reportDir.delete();
		reportDir.mkdir();

		FileFinder fileFinder = new FileFinder();
		ComplexityCalculator complexity = new ComplexityCalculator(fileFinder);

		new XMLReport(projectData, reportDir, fileFinder, complexity);

		File coverageFile = new File(reportDir, "coverage.xml");
		JUnitXMLHelper.readXmlFile(coverageFile, true);

		coverageFile.delete();
		reportDir.delete();
	}

}
