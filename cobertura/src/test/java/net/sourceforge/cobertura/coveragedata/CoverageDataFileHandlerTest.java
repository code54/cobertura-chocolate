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

package net.sourceforge.cobertura.coveragedata;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static net.sourceforge.cobertura.testutil.Util.createRequiredDirectories;
import static net.sourceforge.cobertura.testutil.Util.removeRequiredDirectories;
import static net.sourceforge.cobertura.testutil.Util.removeTestReportFiles;

public class CoverageDataFileHandlerTest{

	private final static String basedir = (System.getProperty("basedir") != null)
			? System.getProperty("basedir")
			: ".";
	private final static String pathToTestOutput = basedir
			+ "/build/test/CoverageDataFileHandlerTest";

	private ProjectData a;
	private File tmp;

    @Before
	public void setUp(){
        tmp = new File(pathToTestOutput);
        a = new ProjectData();

		// Create the directory for our serialized coverage data
		createRequiredDirectories(new File[]{tmp});
	}

    @After
	public void tearDown(){
        removeTestReportFiles(new File(basedir));
        removeRequiredDirectories(new File[]{new File(basedir,"/build")});
	}

    @Test
	public void testSaveAndRestore(){
        // Create some coverage data
		ClassData classData;
		assertEquals(0, a.getNumberOfClasses());
		assertEquals(0, a.getNumberOfChildren());

		classData = new ClassData("com.example.HelloWorld");
		classData.setSourceFileName("com/example/HelloWorld.java");
		for (int i = 0; i < 10; i++)
			classData.addLine(i, "test", "(I)B");
		a.addClassData(classData);
		assertEquals(1, a.getNumberOfClasses());
		assertEquals(1, a.getNumberOfChildren());

		classData = new ClassData("com.example.HelloWorldHelper");
		classData.setSourceFileName("com/example/HelloWorldHelper.java");
		for (int i = 0; i < 14; i++)
			classData.addLine(i, "test", "(I)B");
		a.addClassData(classData);
		assertEquals(2, a.getNumberOfClasses());
		assertEquals(1, a.getNumberOfChildren());


		File dataFile = new File(tmp, "cobertura.ser");
		CoverageDataFileHandler.saveCoverageData(a, dataFile);

		ProjectData b;
		b = CoverageDataFileHandler.loadCoverageData(dataFile);
        //TODO temporary solution; we should provide a correct equals for ProjectData
		assertEquals(a.toString(), b.toString());
	}

}
