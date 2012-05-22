package net.sourceforge.cobertura.reporting.html;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.LineData;
import net.sourceforge.cobertura.coveragedata.PackageData;
import net.sourceforge.cobertura.coveragedata.SourceFileData;
import net.sourceforge.cobertura.reporting.generic.*;
import net.sourceforge.cobertura.reporting.html.files.CopyFiles;
import net.sourceforge.cobertura.util.Header;
import net.sourceforge.cobertura.util.IOUtil;
import net.sourceforge.cobertura.util.Source;
import net.sourceforge.cobertura.util.StringUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Implements HTML reporting strategy.
 */
public class HTMLReportFormatStrategy  implements IReportFormatStrategy {

//    private static final Logger log = Logger.getLogger(HTMLReportFormatStrategy.class);
//
    @Override
    public void save(GenericReport report) {
//        try{
//            CopyFiles.copy(destinationDir);
//            generatePackageList();
//            generateSourceFileLists();
//            generateOverviews();
//            generateSourceFiles();
//        }catch (Exception e){
//            throw new RuntimeException(e);
//        }
    }
//
//
//    /*
//     * The following lines were copied from HTMLReport and adapted to GenericReport
//     * this code should be refactored to use some library to generate HTML markup.
//     * See: http://code.google.com/p/jatl/
//     */
//
//	private File destinationDir;
//	private GenericReport projectData;
//	private String encoding;
//
//	/**
//	 * Create a coverage report
//	 * @param encoding
//	 */
//	public HTMLReportFormatStrategy(GenericReport report, File outputDir, String encoding){
//		this.destinationDir = outputDir;
//		this.projectData = report;
//		this.encoding = encoding;
//	}
//
//	private String generatePackageName(GenericReportEntry packageData){
//		if (packageData.getName().equals(""))
//			return "(default)";
//		return packageData.getName();
//	}
//
//	private void generatePackageList() throws IOException {
//		File file = new File(destinationDir, "frame-packages.html");
//		PrintWriter out = null;
//
//		try{
//			out = IOUtil.getPrintWriter(file);
//
//			out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
//			out.println("           \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
//
//			out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
//			out.println("<head>");
//			out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
//			out.println("<title>Coverage Report</title>");
//			out.println("<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\" />");
//			out.println("</head>");
//			out.println("<body>");
//			out.println("<h5>Packages</h5>");
//			out.println("<table width=\"100%\">");
//			out.println("<tr>");
//			out.println("<td nowrap=\"nowrap\"><a href=\"frame-summary.html\" onclick='parent.sourceFileList.location.href=\"frame-sourcefiles.html\"' target=\"summary\">All</a></td>");
//			out.println("</tr>");
//
//			Iterator<GenericReportEntry> packages =
//                    projectData.getEntriesForLevel(ReportConstants.level_package).iterator();
//			while (packages.hasNext()){
//				GenericReportEntry packageData = packages.next();
//				String url1 = "frame-summary-" + packageData.getName()
//						+ ".html";
//				String url2 = "frame-sourcefiles-" + packageData.getName()
//						+ ".html";
//				out.println("<tr>");
//				out.println("<td nowrap=\"nowrap\"><a href=\"" + url1
//						+ "\" onclick='parent.sourceFileList.location.href=\""
//						+ url2 + "\"' target=\"summary\">"
//						+ generatePackageName(packageData) + "</a></td>");
//				out.println("</tr>");
//			}
//			out.println("</table>");
//			out.println("</body>");
//			out.println("</html>");
//		}finally{
//			if (out != null){
//                out.close();
//			}
//		}
//	}
//
//	private void generateSourceFileLists() throws IOException{
//		generateSourceFileList(null);
//		Iterator<GenericReportEntry> packages =
//                    projectData.getEntriesForLevel(ReportConstants.level_package).iterator();
//		while (packages.hasNext()){
//			GenericReportEntry packageData = packages.next();
//			generateSourceFileList(packageData);
//		}
//	}
//
//	private void generateSourceFileList(GenericReportEntry packageData)
//			throws IOException{
//		String filename;
//		Collection sourceFiles;
//		if (packageData == null){
//			filename = "frame-sourcefiles.html";
//		}else{
//			filename = "frame-sourcefiles-" + packageData.getName() + ".html";
//		}
//        sourceFiles = projectData.getEntriesForLevel(ReportConstants.level_sourcefile);
//
//		// sourceFiles may be sorted, but if so it's sorted by
//		// the full path to the file, and we only want to sort
//		// based on the file's basename.
//		Vector sortedSourceFiles = new Vector();
//		sortedSourceFiles.addAll(sourceFiles);
//		Collections.sort(sortedSourceFiles,
//                new SourceFileDataBaseNameComparator());
//
//		File file = new File(destinationDir, filename);
//		PrintWriter out = null;
//		try{
//			out = IOUtil.getPrintWriter(file);
//
//			out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
//			out.println("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
//
//			out.println("<html>");
//			out.println("<head>");
//			out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
//			out.println("<title>Coverage Report Classes</title>");
//			out.println("<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\"/>");
//			out.println("</head>");
//			out.println("<body>");
//			out.println("<h5>");
//			out.println(packageData == null ? "All Packages"
//					: generatePackageName(packageData));
//			out.println("</h5>");
//			out.println("<div class=\"separator\">&nbsp;</div>");
//			out.println("<h5>Classes</h5>");
//			if (!sortedSourceFiles.isEmpty()){
//				out.println("<table width=\"100%\">");
//				out.println("<tbody>");
//
//				for (Iterator iter = sortedSourceFiles.iterator(); iter
//						.hasNext();){
//					GenericReportEntry sourceFileData = (GenericReportEntry)iter.next();
//					out.println("<tr>");
//					String percentCovered;
//					if (sourceFileData.getBasicMetricData().getLineCoverage().getTotal() > 0)
//						percentCovered = getPercentValue(
//                                sourceFileData.getBasicMetricData().getLineCoverage().getCoverageRate()
//                        );
//					else
//						percentCovered = "N/A";
//					out.println("<td nowrap=\"nowrap\"><a target=\"summary\" href=\""
//                            + sourceFileData.getName()
//                            + ".html\">"
//                            + sourceFileData.getName()
//                            + "</a> <i>("
//                            + percentCovered
//                            + ")</i></td>");
//					out.println("</tr>");
//				}
//				out.println("</tbody>");
//				out.println("</table>");
//			}
//
//			out.println("</body>");
//			out.println("</html>");
//		}finally{
//			if (out != null){
//				out.close();
//			}
//		}
//	}
//
//	private void generateOverviews() throws IOException{
//		generateOverview(null);
//		Iterator<GenericReportEntry> iter = projectData.getEntriesForLevel(ReportConstants.level_package).iterator();
//		while (iter.hasNext()){
//			GenericReportEntry packageData = iter.next();
//			generateOverview(packageData);
//		}
//	}
//
//	private void generateOverview(GenericReportEntry packageData) throws IOException{
//		Iterator<GenericReportEntry> iter;
//
//		String filename;
//		if (packageData == null){
//			filename = "frame-summary.html";
//		}else{
//			filename = "frame-summary-" + packageData.getName() + ".html";
//		}
//		File file = new File(destinationDir, filename);
//		PrintWriter out = null;
//
//		try{
//			out = IOUtil.getPrintWriter(file);;
//
//			out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
//			out.println("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
//
//			out.println("<html>");
//			out.println("<head>");
//			out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
//			out.println("<title>Coverage Report</title>");
//			out.println("<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\"/>");
//			out.println("<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"css/sortabletable.css\"/>");
//			out.println("<script type=\"text/javascript\" src=\"js/popup.js\"></script>");
//			out.println("<script type=\"text/javascript\" src=\"js/sortabletable.js\"></script>");
//			out.println("<script type=\"text/javascript\" src=\"js/customsorttypes.js\"></script>");
//			out.println("</head>");
//			out.println("<body>");
//
//			out.print("<h5>Coverage Report - ");
//			out.print(packageData == null ? "All Packages"
//					: generatePackageName(packageData));
//			out.println("</h5>");
//			out.println("<div class=\"separator\">&nbsp;</div>");
//			out.println("<table class=\"report\" id=\"packageResults\">");
//			out.println(generateTableHeader("Package", true));
//			out.println("<tbody>");
//
//			SortedSet packages;
//			if (packageData == null){
//				// Output a summary line for all packages
//				out.println(generateTableRowForTotal());
//
//				// Get packages
//				packages = new TreeSet(projectData.getEntriesForLevel(ReportConstants.level_package));
//			}else{
//				// Get subpackages
//				packages = new TreeSet(getSubPackages(packageData));
//			}
//
//			// Output a line for each package or subpackage
//			iter = packages.iterator();
//			while (iter.hasNext()){
//				GenericReportEntry subPackageData = iter.next();
//				out.println(generateTableRowForPackage(subPackageData));
//			}
//
//			out.println("</tbody>");
//			out.println("</table>");
//			out.println("<script type=\"text/javascript\">");
//			out.println("var packageTable = new SortableTable(document.getElementById(\"packageResults\"),");
//			out.println("    [\"String\", \"Number\", \"Percentage\", \"Percentage\", \"FormattedNumber\"]);");
//			out.println("packageTable.sort(0);");
//			out.println("</script>");
//
//			// Get the list of source files in this package
//			Collection sourceFiles;
//			if (packageData == null){
//				PackageData defaultPackage = (PackageData)projectData
//						.getChild("");
//				if (defaultPackage != null){
//					sourceFiles = defaultPackage.getSourceFiles();
//				}else{
//					sourceFiles = new TreeSet();
//				}
//			}else{
//				sourceFiles = packageData.getSourceFiles();
//			}
//
//			// Output a line for each source file
//			if (sourceFiles.size() > 0){
//				out.println("<div class=\"separator\">&nbsp;</div>");
//				out.println("<table class=\"report\" id=\"classResults\">");
//				out.println(generateTableHeader("Classes in this Package",
//						false));
//				out.println("<tbody>");
//
//				iter = sourceFiles.iterator();
//				while (iter.hasNext()){
//					SourceFileData sourceFileData = (SourceFileData)iter.next();
//					out.println(generateTableRowsForSourceFile(sourceFileData));
//				}
//
//				out.println("</tbody>");
//				out.println("</table>");
//				out.println("<script type=\"text/javascript\">");
//				out.println("var classTable = new SortableTable(document.getElementById(\"classResults\"),");
//				out.println("[\"String\", \"Percentage\", \"Percentage\", \"FormattedNumber\"]);");
//				out.println("classTable.sort(0);");
//				out.println("</script>");
//			}
//
//			out.println(generateFooter());
//
//			out.println("</body>");
//			out.println("</html>");
//		}finally{
//			if (out != null){
//				out.close();
//			}
//		}
//	}
//
//	private void generateSourceFiles(){
//		Iterator iter = projectData.getSourceFiles().iterator();
//		while (iter.hasNext()){
//			SourceFileData sourceFileData = (SourceFileData)iter.next();
//			try{
//				generateSourceFile(sourceFileData);
//			}catch (IOException e){
//				log.info("Could not generate HTML file for source file "
//						+ sourceFileData.getName() + ": "
//						+ e.getLocalizedMessage());
//			}
//		}
//	}
//
//	private void generateSourceFile(SourceFileData sourceFileData)
//			throws IOException{
//		if (!sourceFileData.containsInstrumentationInfo()){
//			log.info("Data file does not contain instrumentation "
//					+ "information for the file " + sourceFileData.getName()
//					+ ".  Ensure this class was instrumented, and this "
//					+ "data file contains the instrumentation information.");
//		}
//
//		String filename = sourceFileData.getNormalizedName() + ".html";
//		File file = new File(destinationDir, filename);
//		PrintWriter out = null;
//
//		try{
//			out = IOUtil.getPrintWriter(file);
//
//			out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
//			out.println("           \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
//
//			out.println("<html>");
//			out.println("<head>");
//			out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
//			out.println("<title>Coverage Report</title>");
//			out.println("<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\"/>");
//			out.println("<script type=\"text/javascript\" src=\"js/popup.js\"></script>");
//			out.println("</head>");
//			out.println("<body>");
//			out.print("<h5>Coverage Report - ");
//			String classPackageName = sourceFileData.getPackageName();
//			if ((classPackageName != null) && classPackageName.length() > 0){
//				out.print(classPackageName + ".");
//			}
//			out.print(sourceFileData.getBaseName());
//			out.println("</h5>");
//
//			// Output the coverage summary for this class
//			out.println("<div class=\"separator\">&nbsp;</div>");
//			out.println("<table class=\"report\">");
//			out.println(generateTableHeader("Classes in this File", false));
//			out.println(generateTableRowsForSourceFile(sourceFileData));
//			out.println("</table>");
//
//			// Output the coverage summary for methods in this class
//			// TODO
//
//			// Output this class's source code with syntax and coverage highlighting
//			out.println("<div class=\"separator\">&nbsp;</div>");
//			out.println(generateHtmlizedJavaSource(sourceFileData));
//
//			out.println(generateFooter());
//
//			out.println("</body>");
//			out.println("</html>");
//		}finally{
//			if (out != null){
//				out.close();
//			}
//		}
//	}
//
//	private String generateBranchInfo(LineData lineData, String content) {
//		boolean hasBranch = (lineData != null) ? lineData.hasBranch() : false;
//		if (hasBranch) {
//			StringBuffer ret = new StringBuffer();
//			ret.append("<a title=\"Line ").append(lineData.getLineNumber()).append(": Conditional coverage ")
//			   .append(lineData.getConditionCoverage());
//			if (lineData.getConditionSize() > 1){
//				ret.append(" [each condition: ");
//				for (int i = 0; i < lineData.getConditionSize(); i++){
//					if (i > 0)
//						ret.append(", ");
//					ret.append(lineData.getConditionCoverage(i));
//				}
//				ret.append("]");
//			}
//			ret.append(".\">").append(content).append("</a>");
//			return ret.toString();
//		}else{
//			return content;
//		}
//	}
//
//	private String generateHtmlizedJavaSource(SourceFileData sourceFileData){
//		Source source = finder.getSource(sourceFileData.getName());
//
//		if (source == null){
//			return "<p>Unable to locate " + sourceFileData.getName()
//					+ ".  Have you specified the source directory?</p>";
//		}
//
//		BufferedReader br = null;
//		try{
//			br = new BufferedReader(new InputStreamReader(source.getInputStream(), encoding));
//		}catch (UnsupportedEncodingException e){
//			return "<p>Unable to open " + source.getOriginDesc()
//					+ ": The encoding '" + encoding +"' is not supported by your JVM.</p>";
//		}catch (Throwable t){
//			return "<p>Unable to open " + source.getOriginDesc() + ": " + t.getLocalizedMessage() + "</p>";
//		}
//
//		StringBuffer ret = new StringBuffer();
//		ret.append("<table cellspacing=\"0\" cellpadding=\"0\" class=\"src\">\n");
//		try{
//			String lineStr;
//			JavaToHtml javaToHtml = new JavaToHtml();
//			int lineNumber = 1;
//			while ((lineStr = br.readLine()) != null){
//				ret.append("<tr>");
//				if (sourceFileData.isValidSourceLineNumber(lineNumber)){
//					LineData lineData = sourceFileData.getLineCoverage(lineNumber);
//					ret.append("  <td class=\"numLineCover\">&nbsp;"
//							+ lineNumber + "</td>");
//					if ((lineData != null) && (lineData.isCovered())){
//						ret.append("  <td class=\"nbHitsCovered\">"
//								+ generateBranchInfo(lineData, "&nbsp;" + ((lineData != null) ? lineData.getHits() : 0))
//								+ "</td>");
//						ret.append("  <td class=\"src\"><pre class=\"src\">&nbsp;"
//									+ generateBranchInfo(lineData, javaToHtml.process(lineStr))
//									+ "</pre></td>");
//					}else{
//						ret.append("  <td class=\"nbHitsUncovered\">"
//								+ generateBranchInfo(lineData, "&nbsp;" + ((lineData != null) ? lineData.getHits() : 0))
//								+ "</td>");
//						ret.append("  <td class=\"src\"><pre class=\"src\"><span class=\"srcUncovered\">&nbsp;"
//									+ generateBranchInfo(lineData, javaToHtml.process(lineStr))
//									+ "</span></pre></td>");
//					}
//				}else{
//					ret.append("  <td class=\"numLine\">&nbsp;" + lineNumber
//							+ "</td>");
//					ret.append("  <td class=\"nbHits\">&nbsp;</td>\n");
//					ret.append("  <td class=\"src\"><pre class=\"src\">&nbsp;"
//							+ javaToHtml.process(lineStr) + "</pre></td>");
//				}
//				ret.append("</tr>\n");
//				lineNumber++;
//			}
//		}catch (IOException e){
//			ret.append("<tr><td>Error reading "
//					+ source.getOriginDesc() + ": "
//					+ e.getLocalizedMessage() + "</td></tr>\n");
//		}finally{
//			try{
//				br.close();
//				source.close();
//			}catch (IOException e){}
//		}
//
//		ret.append("</table>\n");
//
//		return ret.toString();
//	}
//
//	private static String generateFooter(){
//		return "<div class=\"footer\">Report generated by "
//				+ "<a href=\"http://cobertura.sourceforge.net/\" target=\"_top\">Cobertura</a> "
//				+ Header.version() + " on "
//				+ DateFormat.getInstance().format(new Date()) + ".</div>";
//	}
//
//	private static String generateTableHeader(String title,
//			boolean showColumnForNumberOfClasses){
//		StringBuffer ret = new StringBuffer();
//		ret.append("<thead>");
//		ret.append("<tr>");
//		ret.append("  <td class=\"heading\">" + title + "</td>");
//		if (showColumnForNumberOfClasses){
//			ret.append("  <td class=\"heading\"># Classes</td>");
//		}
//		ret.append("  <td class=\"heading\">"
//				+ generateHelpURL("Line Coverage",
//						"The percent of lines executed by this test run.")
//				+ "</td>");
//		ret.append("  <td class=\"heading\">"
//				+ generateHelpURL("Branch Coverage",
//						"The percent of branches executed by this test run.")
//				+ "</td>");
//		ret.append("  <td class=\"heading\">"
//						+ generateHelpURL(
//								"Complexity",
//								"Average McCabe's cyclomatic code complexity for all methods.  " +
//                                        "This is basically a count of the number of different " +
//                                        "code paths in a method (incremented by 1 for each if " +
//                                        "statement, while loop, etc.)")
//						+ "</td>");
//		ret.append("</tr>");
//		ret.append("</thead>");
//		return ret.toString();
//	}
//
//	private static String generateHelpURL(String text, String description){
//		StringBuffer ret = new StringBuffer();
//		boolean popupTooltips = false;
//		if (popupTooltips){
//			ret.append("<a class=\"hastooltip\" href=\"help.html\" onclick=\"popupwindow('help.html'); return false;\">");
//			ret.append(text);
//			ret.append("<span>" + description + "</span>");
//			ret.append("</a>");
//		}else{
//			ret.append("<a class=\"dfn\" href=\"help.html\" onclick=\"popupwindow('help.html'); return false;\">");
//			ret.append(text);
//			ret.append("</a>");
//		}
//		return ret.toString();
//	}
//
//	private String generateTableRowForTotal(){
//        GenericReportEntry project =
//                projectData.getEntriesForLevel(ReportConstants.level_project).get(0);
//		StringBuffer ret = new StringBuffer();
//
//		ret.append("  <tr>");
//		ret.append("<td><b>All Packages</b></td>");
//		ret.append("<td class=\"value\">"
//				+ projectData.getEntriesForLevel(ReportConstants.level_class).size() + "</td>");
//		ret.append(generateTableColumnsFromData(project.getBasicMetricData()));
//		ret.append("</tr>");
//		return ret.toString();
//	}
//
//	private String generateTableRowForPackage(GenericReportEntry packageData){
//		StringBuffer ret = new StringBuffer();
//		String url1 = "frame-summary-" + packageData.getName() + ".html";
//		String url2 = "frame-sourcefiles-" + packageData.getName() + ".html";
//		double ccn = packageData.getBasicMetricData().getCyclomaticCodeComplexity();
//
//		ret.append("  <tr>");
//		ret.append("<td><a href=\"" + url1
//				+ "\" onclick='parent.sourceFileList.location.href=\"" + url2
//				+ "\"'>" + generatePackageName(packageData) + "</a></td>");
//		ret.append("<td class=\"value\">" + packageData.getNumberOfChildren()
//				+ "</td>");
//		ret.append(generateTableColumnsFromData(packageData.getBasicMetricData()));
//		ret.append("</tr>");
//		return ret.toString();
//	}
//
//	private String generateTableRowsForSourceFile(SourceFileData sourceFileData){
//		StringBuffer ret = new StringBuffer();
//		String sourceFileName = sourceFileData.getNormalizedName();
//
//		Iterator iter = sourceFileData.getClasses().iterator();
//		while (iter.hasNext()){
//			ClassData classData = (ClassData)iter.next();
//			ret.append(generateTableRowForClass(classData, sourceFileName));
//		}
//
//		return ret.toString();
//	}
//
//	private String generateTableRowForClass(ClassData classData,
//			String sourceFileName){
//		StringBuffer ret = new StringBuffer();
//
//		ret.append("  <tr>");
//		// TODO: URL should jump straight to the class (only for inner classes?)
//		ret.append("<td><a href=\"" + sourceFileName
//				+ ".html\">" + classData.getBaseName() + "</a></td>");
//		ret.append(generateTableColumnsFromData(classData));
//		ret.append("</tr>\n");
//		return ret.toString();
//	}
//
//	/**
//	 * Return a string containing three HTML table cells.  The first
//	 * cell contains a graph showing the line coverage, the second
//	 * cell contains a graph showing the branch coverage, and the
//	 * third cell contains the code complexity.
//	 *
//	 * @return A string containing the HTML for three table cells.
//	 */
//	private static String generateTableColumnsFromData(BasicMetricData coverageData){
//		int numLinesCovered = (int)coverageData.getLineCoverage().getCovered();
//		int numLinesValid = (int)coverageData.getLineCoverage().getTotal();
//		int numBranchesCovered = (int)coverageData.getBranchCoverageData().getCovered();
//		int numBranchesValid = (int)coverageData.getBranchCoverageData().getTotal();
//
//		// The "hidden" CSS class is used below to write the ccn without
//		// any formatting so that the table column can be sorted correctly
//		return "<td>" + generatePercentResult(numLinesCovered, numLinesValid)
//				+"</td><td>"
//				+ generatePercentResult(numBranchesCovered, numBranchesValid)
//				+ "</td><td class=\"value\"><span class=\"hidden\">"
//				+ coverageData.getCyclomaticCodeComplexity()
//                + ";</span>"
//                + getDoubleValue(coverageData.getCyclomaticCodeComplexity())
//                + "</td>";
//	}
//
//	/**
//	 * This is crazy complicated, and took me a while to figure out,
//	 * but it works.  It creates a dandy little percentage meter, from
//	 * 0 to 100.
//	 * @param dividend The number of covered lines or branches.
//	 * @param divisor  The number of valid lines or branches.
//	 * @return A percentage meter.
//	 */
//	private static String generatePercentResult(int dividend, int divisor){
//		StringBuffer sb = new StringBuffer();
//
//		sb.append("<table cellpadding=\"0px\" cellspacing=\"0px\" class=\"percentgraph\"><tr class=\"percentgraph\"><td align=\"right\" class=\"percentgraph\" width=\"40\">");
//		if (divisor > 0)
//			sb.append(getPercentValue((double)dividend / divisor));
//		else
//			sb.append(generateHelpURL(
//					"N/A",
//					"Line coverage and branch coverage will appear as \"Not Applicable\" when Cobertura can not find line number information in the .class file.  This happens for stub and skeleton classes, interfaces, or when the class was not compiled with \"debug=true.\""));
//		sb.append("</td><td class=\"percentgraph\"><div class=\"percentgraph\">");
//		if (divisor > 0){
//			sb.append("<div class=\"greenbar\" style=\"width:"
//					+ (dividend * 100 / divisor) + "px\">");
//			sb.append("<span class=\"text\">");
//			sb.append(dividend);
//			sb.append("/");
//			sb.append(divisor);
//		}else{
//			sb.append("<div class=\"na\" style=\"width:100px\">");
//			sb.append("<span class=\"text\">");
//			sb.append(generateHelpURL(
//					"N/A",
//					"Line coverage and branch coverage will appear as \"Not Applicable\" when Cobertura can not find line number information in the .class file.  This happens for stub and skeleton classes, interfaces, or when the class was not compiled with \"debug=true.\""));
//		}
//		sb.append("</span></div></div></td></tr></table>");
//
//		return sb.toString();
//	}
//
//	private static String getDoubleValue(double value){
//		return new DecimalFormat().format(value);
//	}
//
//	private static String getPercentValue(double value){
//		return StringUtil.getPercentValue(value);
//	}
//
//    private SortedSet<GenericReportEntry>getSubPackages(GenericReportEntry entry){
//        List<GenericReportEntry>packages = projectData.getEntriesForLevel(ReportConstants.level_package);
//        SortedSet<GenericReportEntry>entries = new TreeSet<GenericReportEntry>();
//        for(GenericReportEntry packageEntry : packages){
//            if(packageEntry.getName().startsWith(entry.getName())){
//                entries.add(packageEntry);
//            }
//        }
//        return entries;
//    }
}
