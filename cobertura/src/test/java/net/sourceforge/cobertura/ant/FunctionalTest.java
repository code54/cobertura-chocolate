/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2008 John Lewis
 * Copyright (C) 2006 Mark Doliner
 * 
 * Note: This file is dual licensed under the GPL and the Apache
 * Source License 1.1 (so that it can be used from both the main
 * Cobertura classes and the ant tasks).
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

package net.sourceforge.cobertura.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.cobertura.Arguments;
import net.sourceforge.cobertura.Cobertura;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.coveragedata.TouchCollector;
import net.sourceforge.cobertura.reporting.JUnitXMLHelper;

import net.sourceforge.cobertura.testutil.Util;
import net.sourceforge.cobertura.util.DirectoryClassLoader;
import net.sourceforge.cobertura.util.ShutdownHooks;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Path.PathElement;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.junit.*;
import org.junit.Ignore;
import org.junit.runner.JUnitCore;

import static net.sourceforge.cobertura.testutil.Util.createRequiredDirectories;
import static net.sourceforge.cobertura.testutil.Util.removeRequiredDirectories;
import static net.sourceforge.cobertura.testutil.Util.removeTestReportFiles;
import static net.sourceforge.cobertura.util.ArchiveUtil.getFiles;
import static net.sourceforge.cobertura.util.ArchiveUtil.saveGlobalProjectData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * These tests generally exec ant to run a test.xml file.  A different target is used for
 * each test.  The text.xml file sets up a test, instruments, runs junit, and generates a
 * coverage xml report.  Then the xml report is parsed and checked.
 * 
 * @author jwlewi
 */
@Ignore("Temporarily ignoring failing tests.")
public class FunctionalTest{
    private static final Logger log = Logger.getLogger(FunctionalTest.class);
	private static int forkedJVMDebugPort = 0;

	private final static File basedir = new File((System.getProperty("basedir") != null) ? System
			.getProperty("basedir") : ".", "src/test/resources/integration/examples/functionaltest1");

    private File reports;
    private File instrumented;
    private File classes;
    private File lib;
    private File tmp;

    @Before
	public void setUp(){
        reports = new File(basedir, "reports");
        instrumented = new File(basedir, "instrumented");
        classes = new File(basedir, "classes");
        lib = new File(basedir, "lib");
        tmp = new File(basedir, "tmp");
        createRequiredDirectories(new File[]{reports, instrumented, classes, lib, tmp});
	}

    @After
    public void tearDown(){
        removeTestReportFiles(basedir);
        removeRequiredDirectories(new File[]{reports, instrumented, classes, lib, tmp});
	}

    @org.junit.Ignore("Not running ok due to antlib.xml location")
    @Test
	public void testInstrumentUsingDirSet() throws Exception{
		runTestAntScript("dirset", "test-dirset");
		verify("dirset");
	}

    @Test
	public void testInstrumentUsingIncludesAndExcludes() throws Exception{
		runTestAntScript("includes-and-excludes", "test-includes-and-excludes");
        saveGlobalProjectData(new ProjectData());
		verify("includes-and-excludes");
	}

    @Test
	public void testInstrumentUsingClassPath() throws Exception{
		runTestAntScript("classpath", "test-classpath");
        saveGlobalProjectData(new ProjectData());
		verify("classpath");
	}

    @Test
	public void testInstrumentUsingWar() throws Exception{
		runTestAntScript("classpath", "test-war");
		verify("war");
	}

    /*   Aux methods   */

	private static void verify(String testName) throws Exception{
		verifyXml(testName);
		verifyHtml(testName);
	}

	private static void verifyXml(String testName) throws Exception{
		Document document = getSummaryXmlReportDocument();
		verifyOverallComplexity(document);

		document = getXmlReportDocument();
		verifyOverallComplexity(document);
		
		// Get a list of all classes listed in the XML report
		List classesList = getClassElements(document);
		assertTrue("Test " + testName + ": Did not find any classes listed in the XML report.",
				classesList.size() > 0);

		// text.xml only instruments the two "A" classes, so make
		// sure those are the only classes listed in the XML report.
		boolean firstPackageFound = false;
		boolean secondPackageFound = false;
		for (Iterator iter = classesList.iterator(); iter.hasNext();){
			boolean verify = true;
			Element classElement = (Element)iter.next();
			String className = classElement.getAttributeValue("name");
			if (className.equals("test.first.A")){
				firstPackageFound = true;
			}else if (className.equals("test.second.A")){
				secondPackageFound = true;
			}else if (className.equals("test.first.RemoteInterface")
					|| (className.equals("test.first.RemoteListener"))
					|| (className.equals("test.first.RemoteListener_Stub"))){
				//just ignore - it is ok
				verify = false;
			}else
				fail("Test "
						+ testName
						+ ": Found a class with the name '"
						+ className
						+ "' in the XML report, but was only expecting either 'test.first.A' or 'test.second.A'.");
			if (verify){
				verifyClass(className, testName, classElement);
			}
		}
		assertTrue("Test " + testName + ": Did not find class 'test.first.A' in the XML report.",
				firstPackageFound);
		assertTrue("Test " + testName + ": Did not find class 'test.second.A' in the XML report.",
				secondPackageFound);
	}
	
	private static void verifyOverallComplexity(Document document){
		String complexity = document.getRootElement().getAttributeValue("complexity");
		assertEquals("Invalid overall complexity ", "1.0", complexity);
	}

	private static Document getXmlReportDocument() throws IOException, JDOMException{
		File xmlFile = new File(basedir, "reports/cobertura-xml/coverage.xml");
		Document document = JUnitXMLHelper.readXmlFile(xmlFile, true);
		return document;
	}

	private static Document getSummaryXmlReportDocument() throws IOException, JDOMException{
		File xmlFile = new File(basedir, "reports/cobertura-xml/coverage-summary.xml");
		Document document = JUnitXMLHelper.readXmlFile(xmlFile, true);
		return document;
	}

	/**
	 * Use XPath to get all &lt;class&gt; elements in the
	 * cobertura.xml file under the given directory.
	 * @return A list of JDOM Elements.
	 */
	private static List getClassElements(Document document) throws IOException, JDOMException{
		XPath xpath = XPath.newInstance("/coverage/packages/package/classes/class");
		List classesList = xpath.selectNodes(document);
		return classesList;
	}

	/**
	 * Verify that the class's expected methods are found.  Look for
	 * a method called "call" which should have a hit count of 1.
	 * The method called "dontCall" should have a hit count of 0.
	 * @param testName 
	 */
	private static void verifyClass(String className, String testName, Element classElement){
		verifyComplexity(className, classElement);
		
		// Get a list of methods
		Element methodsElement = classElement.getChild("methods");
		List methodList = methodsElement.getChildren("method");
		assertTrue("Test " + testName + ": Did not find any methods listed in the class "
				+ classElement.getAttributeValue("name"), methodList.size() > 0);
		boolean callMethodFound = false;
		boolean dontCallMethodFound = false;
		for (Iterator iter = methodList.iterator(); iter.hasNext();){
			Element methodElement = (Element)iter.next();
			String methodName = methodElement.getAttributeValue("name");
			if (methodName.equals("call")){
				if (callMethodFound){
					fail("Test " + testName
							+ ": Found more than one instance of the method 'call' in the class "
							+ classElement.getAttributeValue("name"));
				}
				callMethodFound = true;
				verifyMethod(testName, classElement, methodElement, 1);
			}else if (methodName.equals("dontCall")){
				if (dontCallMethodFound){
					fail("Test "
							+ testName
							+ ": Found more than one instance of the method 'dontCall' in the class "
							+ classElement.getAttributeValue("name"));
				}
				dontCallMethodFound = true;
				verifyMethod(testName, classElement, methodElement, 0);
			}else if (methodName.equals("<init>") || methodName.equals("someMethod")){
				// These methods are ok--ignore them.
			}else{
				fail("Test " + testName + ": Found method " + methodName + " in the class "
						+ classElement.getAttributeValue("name")
						+ ", but was only expecting either 'call' or 'dontCall'.");
			}
		}
		assertTrue("Test " + testName + ": Did not find method 'call' in the class "
				+ classElement.getAttributeValue("name"), callMethodFound);
		assertTrue("Test " + testName + ": Did not find method 'dontCall' in the class "
				+ classElement.getAttributeValue("name"), dontCallMethodFound);
	}

	private static void verifyComplexity(String className, Element classElement){
		String complexity = classElement.getAttributeValue("complexity");
		assertEquals("Invalid complexity with class " + className, "1.0", complexity);
	}

	/**
	 * Look at all lines in a method and make sure they have hit counts that
	 * match the expectedHits.
	 */
	private static void verifyMethod(
            String testName, Element classElement, Element methodElement,
			int expectedHits){
		Element linesElement = methodElement.getChild("lines");
		List lineList = linesElement.getChildren("line");
		assertTrue("Test " + testName + ", class " + classElement.getAttributeValue("name")
				+ ": Did not find any lines in the method "
				+ methodElement.getAttributeValue("name"), lineList.size() > 0);

		for (Iterator iter = lineList.iterator(); iter.hasNext();){
			Element lineElement = (Element)iter.next();
			String hitsString = lineElement.getAttributeValue("hits");
			int hits = Integer.parseInt(hitsString);
			assertEquals("Test " + testName + ", class " + classElement.getAttributeValue("name")
					+ ": Found incorrect hit count for the method "
					+ methodElement.getAttributeValue("name"), expectedHits, hits);
		}
	}

	private static void verifyHtml(String testName) throws Exception{
		File htmlReportDir = new File(basedir, "reports/cobertura-html");

		// Get all files from report directory
		String htmlFiles[] = htmlReportDir.list(new FilenameFilter(){

			public boolean accept(File dir, String name){
				return name.endsWith(".html");
			}
		});
		Arrays.sort(htmlFiles);

		assertTrue(htmlFiles.length >= 5);

		// Assert that all required files are there
		String[] requiredFiles = { "index.html", "help.html", "frame-packages.html",
				"frame-summary.html", "frame-sourcefiles.html" , "test.first.A.html"};

		for (int i = 0; i < requiredFiles.length; i++){
			if (!containsFile(htmlFiles, requiredFiles[i])){
				fail("Test " + testName + ": File " + requiredFiles[i]
						+ " not found among report files");
			}
		}

		// Validate selected files
		String previousPrefix = "NONE";
		for (int i = 0; i < htmlFiles.length; i++){
			// Validate file if has prefix different than previous one, or is required file
			if (containsFile(requiredFiles, htmlFiles[i])
					|| !htmlFiles[i].startsWith(previousPrefix)){
				JUnitXMLHelper.readXmlFile(new File(htmlReportDir, htmlFiles[i]), true);
			}
            if (htmlFiles[i].length() > 7){
				previousPrefix = htmlFiles[i].substring(0, 7);
			}else{
				previousPrefix = htmlFiles[i];
			}
		}
		BufferedReader reader = new BufferedReader(new FileReader(new File(htmlReportDir, "test.first.A.html")));
		String line;
		boolean foundSomeMethod = false;
		while ((line = reader.readLine()) != null) {
			if (line.matches(".*someMethod.*")) {
				foundSomeMethod = true;
			}
		}
		assertTrue("someMethod not found in test.first.A.html", foundSomeMethod);
	}

	private static boolean containsFile(String[] files, String fileName){
		for (int i = 0; i < files.length; i++){
			if (files[i].equals(fileName))
				return true;
		}
		return false;
	}

	/**
	 * Use the ant 'java' task to run the test.xml
	 * file and the specified target.
	 */
	private static void runTestAntScript(String testName, String target) throws IOException{
		Java task = new Java();
		task.setTaskName("java");
		task.setProject(new Project());
		task.init();

		// Call ant launcher.  Requires ant-lancher.jar.
		task.setClassname("org.apache.tools.ant.launch.Launcher");
		task.setFork(true);

		AntUtil.transferCoberturaDataFileProperty(task);
		
		if (forkedJVMDebugPort > 0){
			task.createJvmarg().setValue("-Xdebug");
			task.createJvmarg().setValue("-Xrunjdwp:transport=dt_socket,address=" + forkedJVMDebugPort + ",server=y,suspend=y");
		}


		task.createArg().setValue("-f");
		task.createArg().setValue(basedir + "/build.xml");
		task.createArg().setValue(target);

		task.setFailonerror(true);

		// Set output to go to a temp file
		File outputFile = Util.createTemporaryTextFile("cobertura-test");
		task.setOutput(outputFile);

		// Set the classpath to the same classpath as this JVM
		Path classpath = task.createClasspath();
		PathElement pathElement = classpath.createPathElement();
		pathElement.setPath(System.getProperty("java.class.path"));

		try{
			task.execute();
		}finally{
			if (outputFile.exists()){
				// Put the contents of the output file in the exception
				log.info("\n\n\nOutput from Ant for " + testName
						+ " test:\n----------------------------------------\n"
						+ Util.getText(outputFile) + "----------------------------------------");
				outputFile.delete();
			}
		}
	}
}
