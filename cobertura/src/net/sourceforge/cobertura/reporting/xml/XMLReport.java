/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2003 jcoverage ltd.
 * Copyright (C) 2005 Mark Doliner <thekingant@users.sourceforge.net>
 * Copyright (C) 2005 Jeremy Thomerson <jthomerson@users.sourceforge.net>
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.LineData;
import net.sourceforge.cobertura.coveragedata.PackageData;
import net.sourceforge.cobertura.coveragedata.ProjectData;

import org.apache.log4j.Logger;

public class XMLReport
{

	private static final Logger logger = Logger.getLogger(XMLReport.class);

	private final int indentRate = 2;
	private final PrintWriter pw;

	private int indent = 0;

	public XMLReport(ProjectData projectData, File outputDir,
			File sourceDirectory) throws IOException
	{
		pw = new PrintWriter(new FileWriter(new File(outputDir,
				"coverage.xml")));

		try
		{
			println("<?xml version=\"1.0\"?>");
			println("<!DOCTYPE coverage SYSTEM \"http://cobertura.sourceforge.net/xml/coverage.dtd\">");
			println("");

			if (sourceDirectory == null)
			{
				// TODO: Set a schema?
				//println("<coverage xmlns=\"http://cobertura.sourceforge.net\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://cobertura.sourceforge.net/xml/coverage.xsd\">");
				println("<coverage>");
			}
			else
			{
				// TODO: Set a schema?
				//println("<coverage src=\"" + sourceDirectory + "\" xmlns=\"http://cobertura.sourceforge.net\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://cobertura.sourceforge.net/xml/coverage.xsd\">");
				println("<coverage src=\"" + sourceDirectory + "\">");
			}
			increaseIndentation();
			dumpPackages(projectData);
			decreaseIndentation();
			println("</coverage>");
		}
		finally
		{
			pw.close();
		}
	}

	void increaseIndentation()
	{
		indent += indentRate;
	}

	void decreaseIndentation()
	{
		indent -= indentRate;
	}

	void indent()
	{
		for (int i = 0; i < indent; i++)
		{
			pw.print("\t");
		}
	}

	void println(String ln)
	{
		indent();
		pw.println(ln);
	}

	private void dumpPackages(ProjectData projectData)
	{
		println("<packages>");
		increaseIndentation();

		Iterator it = projectData.getChildren().iterator();
		while (it.hasNext())
		{
			dumpPackage((PackageData)it.next());
		}

		decreaseIndentation();
		println("</packages>");
	}

	private void dumpPackage(PackageData packageData)
	{
		logger.debug("Dumping package " + packageData.getName());

		println("<package name=\"" + packageData.getName()
				+ "\" line-rate=\"" + packageData.getLineCoverageRate()
				+ "\" branch-rate=\"" + packageData.getBranchCoverageRate()
				+ "\"" + ">");
		increaseIndentation();
		dumpClasses(packageData);
		decreaseIndentation();
		println("</package>");
	}

	private void dumpClasses(PackageData packageData)
	{
		println("<classes>");
		increaseIndentation();

		Iterator it = packageData.getChildren().iterator();
		while (it.hasNext())
		{
			dumpClass((ClassData)it.next());
		}

		decreaseIndentation();
		println("</classes>");
	}

	private void dumpClass(ClassData classData)
	{
		logger.debug("Dumping class " + classData.getName());

		println("<class name=\"" + classData.getName() + "\" filename=\""
				+ classData.getSourceFileName() + "\" line-rate=\""
				+ classData.getLineCoverageRate() + "\" branch-rate=\""
				+ classData.getBranchCoverageRate() + "\"" + ">");
		increaseIndentation();

		dumpMethods(classData);
		dumpLines(classData);

		decreaseIndentation();
		println("</class>");
	}

	private void dumpMethods(ClassData classData)
	{
		println("<methods>");
		increaseIndentation();

		Iterator iter = classData.getMethodNamesAndDescriptors().iterator();
		while (iter.hasNext())
		{
			dumpMethod(classData, (String)iter.next());
		}

		decreaseIndentation();
		println("</methods>");
	}

	private void dumpMethod(ClassData classData, String nameAndSig)
	{
		String name = nameAndSig.substring(0, nameAndSig.indexOf('('));
		String signature = nameAndSig.substring(nameAndSig.indexOf('('));
		double lineRate = classData.getLineCoverageRate(nameAndSig);
		double branchRate = classData.getBranchCoverageRate(nameAndSig);

		println("<method name=\"" + xmlEscape(name) + "\" signature=\""
				+ xmlEscape(signature) + "\" line-rate=\"" + lineRate
				+ "\" branch-rate=\"" + branchRate + "\"/>");
	}

	private static String xmlEscape(String str)
	{
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		return str;
	}

	private void dumpLines(ClassData classData)
	{
		println("<lines>");
		increaseIndentation();

		Iterator iter = classData.getChildren().iterator();
		while (iter.hasNext())
		{
			dumpLine((LineData)iter.next());
		}

		decreaseIndentation();
		println("</lines>");
	}

	private void dumpLine(LineData lineData)
	{
		int lineNumber = lineData.getLineNumber();
		long hitCount = lineData.getHits();
		boolean isBranch = lineData.isBranch();

		println("<line number=\"" + lineNumber + "\" hits=\"" + hitCount
				+ "\" branch=\"" + isBranch + "\"/>");
	}

}
