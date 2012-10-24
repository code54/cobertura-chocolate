package net.sourceforge.cobertura.reporting.html;

import com.googlecode.jatl.Html;
import net.sourceforge.cobertura.comparator.GRENameComparator;
import net.sourceforge.cobertura.reporting.generic.*;
import net.sourceforge.cobertura.reporting.generic.filter.TypeFilter;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.Criteria;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.EqCriteria;
import net.sourceforge.cobertura.reporting.generic.filter.criteria.ORListedCriteria;
import net.sourceforge.cobertura.reporting.html.files.CopyFiles;
import net.sourceforge.cobertura.util.Header;
import net.sourceforge.cobertura.util.IOUtil;
import net.sourceforge.cobertura.util.StringUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;

/*
 * Cobertura - http://cobertura.sourceforge.net/
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
 * Implements HTML reporting strategy.
 */
public class HTMLReportFormatStrategy implements IReportFormatStrategy {

    private static final Logger log = Logger.getLogger(HTMLReportFormatStrategy.class);

    private static final String complexityDesc = "Average McCabe's cyclomatic code complexity for all methods.  " +
            "This is basically a count of the number of different code paths in a method (incremented by 1 for " +
            "each if statement, while loop, etc.)";
    private static final String lineCoverageDesc = "The percent of lines executed by this test run.";
    private static final String branchCoverageDesc = "The percent of branches executed by this test run.";

    @Override
    public void save(GenericReport report) {
        log.info("Will save report to " + destinationDir.getAbsolutePath());
        this.projectData = report;

        try {
            CopyFiles.copy(destinationDir);
            alt();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void alt() throws IOException {
        generatePackageListAlt();
        generateSourceFileListsAlt();
        generateOverviewsAlt();
        generateSourceFilesAlt();
    }

    private void noalt() throws IOException {
        generatePackageList();
        generateSourceFileLists();
        generateOverviews();
        generateSourceFiles();
    }

    /*
     * The following lines were copied from HTMLReport and adapted to GenericReport
     * this code should be refactored to use some library to generate HTML markup.
     * See: http://code.google.com/p/jatl/
     */

    private File destinationDir;
    private GenericReport projectData;
    private String encoding;

    /**
     * Create a coverage report
     *
     * @param encoding
     */
    public HTMLReportFormatStrategy(File outputDir, String encoding) {
        this.destinationDir = outputDir;
        this.encoding = encoding;
    }

    private String generatePackageName(GenericReportEntry packageData) {
        if (packageData.getName().equals(""))
            return "(default)";
        return packageData.getName();
    }

    private void generatePackageListAlt() {
        StringWriter sw = new StringWriter();
        new Html(sw) {{
            html().xmlns("http://www.w3.org/1999/xhtml").attr("xml:lang", "en").attr("lang", "en");

            head();
            meta().httpEquiv("Content-Type").content("text/html").charset("UTF-8");
            title().text("Coverage Report").end();
            link().title("Style").type("text/css").rel("stylesheet").href("css/main.css").end();
            end();

            body();
            h5().text("Packages").end();
            table().width("100%").
                    tr().td().nowrap("nowrap").
                    a().href("frame-summary.html").
                    onclick("parent.sourceFileList.location.href=\"frame-sourcefiles.html\"").
                    target("summary").text("All").end();
            end().end();

            Iterator<GenericReportEntry> packages =
                    projectData.getEntriesForLevel(ReportConstants.level_package).iterator();
            while (packages.hasNext()) {
                GenericReportEntry packageData = packages.next();
                String packageSummaryUrl = "frame-summary-" + packageData.getName() + ".html";
                String sourceFileSummaryUrl = "frame-sourcefiles-" + packageData.getName() + ".html";
                tr().td().nowrap("nowrap");
                a().href(packageSummaryUrl).
                        onclick("parent.sourceFileList.location.href=\"" + sourceFileSummaryUrl + "\"").
                        target("summary").
                        text(generatePackageName(packageData)).end();
                end().end();
            }
            end();
            endAll();
            done();
        }};

        printToFile(new File(destinationDir, "frame-packages.html"), sw.getBuffer().toString());
    }

    private static void printToFile(File file, String html){
        PrintWriter out = null;
        try {
            out = IOUtil.getPrintWriter(file);
            out.println(html);
        } catch (UnsupportedEncodingException e) {
            log.error(e);
        } catch (FileNotFoundException e) {
            log.error(e);
        }finally {
            out.close();
        }
    }

    @Deprecated
    private void generatePackageList() throws IOException {
        File file = new File(destinationDir, "frame-packages.html");
        PrintWriter out = null;

        try {
            out = IOUtil.getPrintWriter(file);

            out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
            out.println("           \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");

            out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
            out.println("<head>");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
            out.println("<title>Coverage Report</title>");
            out.println("<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\" />");
            out.println("</head>");
            out.println("<body>");
            out.println("<h5>Packages</h5>");
            out.println("<table width=\"100%\">");
            out.println("<tr>");
            out.println("<td nowrap=\"nowrap\"><a href=\"frame-summary.html\" onclick='parent.sourceFileList.location.href=\"frame-sourcefiles.html\"' target=\"summary\">All</a></td>");
            out.println("</tr>");

            Iterator<GenericReportEntry> packages =
                    projectData.getEntriesForLevel(ReportConstants.level_package).iterator();
            while (packages.hasNext()) {
                GenericReportEntry packageData = packages.next();
                String url1 = "frame-summary-" + packageData.getName()
                        + ".html";
                String url2 = "frame-sourcefiles-" + packageData.getName()
                        + ".html";
                out.println("<tr>");
                out.println("<td nowrap=\"nowrap\"><a href=\"" + url1
                        + "\" onclick='parent.sourceFileList.location.href=\""
                        + url2 + "\"' target=\"summary\">"
                        + generatePackageName(packageData) + "</a></td>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Deprecated
    private void generateSourceFileLists() throws IOException {
        generateSourceFileList(null);
        Iterator<GenericReportEntry> packages =
                projectData.getEntriesForLevel(ReportConstants.level_package).iterator();
        while (packages.hasNext()) {
            GenericReportEntry packageData = packages.next();
            generateSourceFileList(packageData);
        }
    }

    private void generateSourceFileListsAlt() throws IOException {
        generateSourceFileListAlt(null);
        Iterator<GenericReportEntry> packages =
                projectData.getEntriesForLevel(ReportConstants.level_package).iterator();
        while (packages.hasNext()) {
            GenericReportEntry packageData = packages.next();
            generateSourceFileListAlt(packageData);
        }
    }

    private void generateSourceFileListAlt(final GenericReportEntry packageData) {
        String filename;
        Collection sourceFiles;
        if (packageData == null) {
            filename = "frame-sourcefiles.html";
        } else {
            filename = "frame-sourcefiles-" + packageData.getName() + ".html";
        }
        sourceFiles = projectData.getEntriesForLevel(ReportConstants.level_sourcefile);

        /*
         * sourceFiles may be sorted, but if so it's sorted by
         * the full path to the file, and we only want to sort
         * based on the file's basename.
         */
        final Vector sortedSourceFiles = new Vector();
        sortedSourceFiles.addAll(sourceFiles);
        Collections.sort(sortedSourceFiles,
                new SourceFileDataBaseNameComparator2());

        StringWriter sw = new StringWriter();
        new Html(sw) {{
            html().xmlns("http://www.w3.org/1999/xhtml").attr("xml:lang", "en").attr("lang", "en");

            head();
            meta().httpEquiv("Content-Type").content("text/html").charset("UTF-8");
            title().text("Coverage Report").end();
            link().title("Style").type("text/css").rel("stylesheet").href("css/main.css").end();
            end();

            body();
            h5().text(packageData == null ? "All Packages" : generatePackageName(packageData)).end();
            div().classAttr("separator").text(" ");
            h5().text("Classes").end();

            if (!sortedSourceFiles.isEmpty()) {
                table().width("100%").tbody();
                for (Iterator iter = sortedSourceFiles.iterator(); iter.hasNext(); ) {
                    GenericReportEntry sourceFileData = (GenericReportEntry) iter.next();
                    String percentCovered;
                    if (sourceFileData.getPayload().getMetric(ReportConstants.metricName_lineCoverageRate).getValue() > 0) {
                        percentCovered = getPercentValue(
                                sourceFileData.getPayload().getMetric(ReportConstants.metricName_lineCoverageRate).getValue()
                        );
                    } else {
                        percentCovered = "N/A";
                    }
                    tr().td().nowrap("nowrap");

                    String filename = sourceFileData.getName().replaceAll("/", "\\.");
                    a().target("summary").href( filename + ".html").text(sourceFileData.getName()).end();
                    i().text("(" + percentCovered + ")").end();
                    end().end();
                }
                end().end();
            }

            endAll();
            done();
        }};

        printToFile(new File(destinationDir, filename), sw.getBuffer().toString());
    }

    @Deprecated
    private void generateSourceFileList(GenericReportEntry packageData)
            throws IOException {
        String filename;
        Collection sourceFiles;
        if (packageData == null) {
            filename = "frame-sourcefiles.html";
        } else {
            filename = "frame-sourcefiles-" + packageData.getName() + ".html";
        }
        sourceFiles = projectData.getEntriesForLevel(ReportConstants.level_sourcefile);

        // sourceFiles may be sorted, but if so it's sorted by
        // the full path to the file, and we only want to sort
        // based on the file's basename.
        Vector sortedSourceFiles = new Vector();
        sortedSourceFiles.addAll(sourceFiles);
        Collections.sort(sortedSourceFiles,
                new SourceFileDataBaseNameComparator2());

        File file = new File(destinationDir, filename);
        PrintWriter out = null;
        try {
            out = IOUtil.getPrintWriter(file);

            out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
            out.println("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");

            out.println("<html>");
            out.println("<head>");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
            out.println("<title>Coverage Report Classes</title>");
            out.println("<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\"/>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h5>");
            out.println(packageData == null ? "All Packages"
                    : generatePackageName(packageData));
            out.println("</h5>");
            out.println("<div class=\"separator\">&nbsp;</div>");
            out.println("<h5>Classes</h5>");
            if (!sortedSourceFiles.isEmpty()) {
                out.println("<table width=\"100%\">");
                out.println("<tbody>");

                for (Iterator iter = sortedSourceFiles.iterator(); iter
                        .hasNext(); ) {
                    GenericReportEntry sourceFileData = (GenericReportEntry) iter.next();
                    out.println("<tr>");
                    String percentCovered;
                    if (sourceFileData.getPayload().getMetric(ReportConstants.metricName_lineCoverageRate).getValue() > 0)
                        percentCovered = getPercentValue(
                                sourceFileData.getPayload().getMetric(ReportConstants.metricName_lineCoverageRate).getValue()
                        );
                    else
                        percentCovered = "N/A";
                    out.println("<td nowrap=\"nowrap\"><a target=\"summary\" href=\""
                            + sourceFileData.getName()
                            + ".html\">"
                            + sourceFileData.getName()
                            + "</a> <i>("
                            + percentCovered
                            + ")</i></td>");
                    out.println("</tr>");
                }
                out.println("</tbody>");
                out.println("</table>");
            }

            out.println("</body>");
            out.println("</html>");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Deprecated
    private void generateOverviews() throws IOException {
        generateOverview(null);
        Iterator<GenericReportEntry> iter = projectData.getEntriesForLevel(ReportConstants.level_package).iterator();
        while (iter.hasNext()) {
            GenericReportEntry packageData = iter.next();
            generateOverview(packageData);
        }
    }

    private void generateOverviewsAlt() throws IOException {
        generateOverviewAlt(null);
        Iterator<GenericReportEntry> iter = projectData.getEntriesForLevel(ReportConstants.level_package).iterator();
        while (iter.hasNext()) {
            GenericReportEntry packageData = iter.next();
            generateOverviewAlt(packageData);
        }
    }

    private void generateOverviewAlt(final GenericReportEntry packageData) throws IOException {
        String filename;
        if (packageData == null) {
            filename = "frame-summary.html";
        } else {
            filename = "frame-summary-" + packageData.getName() + ".html";
        }

        StringWriter sw = new StringWriter();
        new Html(sw) {
            {
                html().xmlns("http://www.w3.org/1999/xhtml").attr("xml:lang", "en").attr("lang", "en");

                head();
                meta().httpEquiv("Content-Type").content("text/html").charset("UTF-8");
                title().text("Coverage Report").end();
                link().title("Style").type("text/css").rel("stylesheet").href("css/main.css").end();
                link().title("Style").type("text/css").rel("stylesheet").href("css/sorteable.css").end();
                script().type("text/javascript").src("js/popup.js").end();
                script().type("text/javascript").src("js/sorteable.js").end();
                script().type("text/javascript").src("js/customsorttypes.js").end();
                end();

                body();
                h5().text("Coverage Report - " + (packageData == null ? "All Packages" : generatePackageName(packageData))).end();
                div().classAttr("separator").text(" ").end();
                table().classAttr("report").id("packageResults");

                //Generate table header
                thead().tr().
                        td().classAttr("heading").text("Package").end();
                    td().classAttr("heading").text("# Classes").end();
                    td().classAttr("heading");
                        generateHelpURLAlt(this, "Line coverage", lineCoverageDesc);
                    end();
                    td().classAttr("heading");
                        generateHelpURLAlt(this, "Branch coverage", branchCoverageDesc);
                    end();
                    td().classAttr("heading");
                        generateHelpURLAlt(this, "complexity", complexityDesc);
                    end();
                end().end(); //end tr and thread

                tbody();
                SortedSet packages = new TreeSet(new GRENameComparator());
                if (packageData == null) {
                    // Output a summary line for all packages
                    GenericReportEntry project =
                            projectData.getEntriesForLevel(ReportConstants.level_project).get(0);
                    //generate table row for total
                    tr().
                            td().b().text("All Packages").end().end();
                            td().classAttr("value").
                            text("" + projectData.getEntriesForLevel(ReportConstants.level_class).size()).end();

                            generateTableColumnsFromDataAlt(this, project);
                    end();

                    // Get packages
                    packages.addAll(projectData.getEntriesForLevel(ReportConstants.level_package));
                } else {
                    // Get subpackages
                    packages.addAll(getSubPackages(packageData));
                }

                // Output a line for each package or subpackage
                Iterator<GenericReportEntry> iter = packages.iterator();
                while (iter.hasNext()) {
                    GenericReportEntry subPackageData = iter.next();
                    generateTableRowForPackageAlt(this, subPackageData);
                }
                end();//tbody
                end();//table
                String js1 = "var packageTable = new SortableTable(document.getElementById(\"packageResults\")," +
                        "[\"String\", \"Percentage\", \"Percentage\", \"FormattedNumber\"]);" +
                        "classTable.sort(0);";
                script().type("text/javascript").text(js1).end();

                // Get the list of source files in this package
                Collection classes = new HashSet();
                Iterator<GenericReportEntry> classIt = packages.iterator();
                while (classIt.hasNext()) {
                    classes.add(classIt.next());
                }

                // Output a line for each source file
                if (classes.size() > 0) {
                    div().classAttr("separator").text(" ").end();
                    table().classAttr("report").attr("id", "classResults");
                    generateTableHeaderAlt(this, "Classes in this Package", false);
                    tbody();

                    iter = classes.iterator();
                    while (iter.hasNext()) {
                        generateTableRowsForSourceFileAlt(this, iter.next());
                    }
                    end();//tbody
                    end();//table
                    String js2 = "var classTable = new SortableTable(document.getElementById(\"classResults\")," +
                            "[\"String\", \"Percentage\", \"Percentage\", \"FormattedNumber\"]);" +
                            "classTable.sort(0);";
                    script().type("text/javascript").
                            text(js2).end();
                }
                generateFooterAlt(this);

                end();//body
                end();//html
            }
        };

        printToFile(new File(destinationDir, filename), sw.getBuffer().toString());
    }

    @Deprecated
    private void generateOverview(GenericReportEntry packageData) throws IOException {
        Iterator<GenericReportEntry> iter;

        String filename;
        if (packageData == null) {
            filename = "frame-summary.html";
        } else {
            filename = "frame-summary-" + packageData.getName() + ".html";
        }
        File file = new File(destinationDir, filename);
        PrintWriter out = null;

        try {
            out = IOUtil.getPrintWriter(file);

            out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
            out.println("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");

            out.println("<html>");
            out.println("<head>");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
            out.println("<title>Coverage Report</title>");
            out.println("<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\"/>");
            out.println("<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"css/sortabletable.css\"/>");
            out.println("<script type=\"text/javascript\" src=\"js/popup.js\"></script>");
            out.println("<script type=\"text/javascript\" src=\"js/sortabletable.js\"></script>");
            out.println("<script type=\"text/javascript\" src=\"js/customsorttypes.js\"></script>");
            out.println("</head>");
            out.println("<body>");

            out.print("<h5>Coverage Report - ");
            out.print(packageData == null ? "All Packages"
                    : generatePackageName(packageData));
            out.println("</h5>");
            out.println("<div class=\"separator\">&nbsp;</div>");
            out.println("<table class=\"report\" id=\"packageResults\">");
            out.println(generateTableHeader("Package", true));
            out.println("<tbody>");

            SortedSet packages;
            if (packageData == null) {
                // Output a summary line for all packages
                out.println(generateTableRowForTotal());

                // Get packages
                packages = new TreeSet(projectData.getEntriesForLevel(ReportConstants.level_package));
            } else {
                // Get subpackages
                packages = new TreeSet(getSubPackages(packageData));
            }

            // Output a line for each package or subpackage
            iter = packages.iterator();
            while (iter.hasNext()) {
                GenericReportEntry subPackageData = iter.next();
                out.println(generateTableRowForPackage(subPackageData));
            }

            out.println("</tbody>");
            out.println("</table>");
            out.println("<script type=\"text/javascript\">");
            out.println("var packageTable = new SortableTable(document.getElementById(\"packageResults\"),");
            out.println("    [\"String\", \"Number\", \"Percentage\", \"Percentage\", \"FormattedNumber\"]);");
            out.println("packageTable.sort(0);");
            out.println("</script>");

            // Get the list of source files in this package
            Collection classes = new HashSet();
            Iterator<GenericReportEntry> classIt = packages.iterator();
            while (classIt.hasNext()) {
                classes.add(classIt.next());
            }

            // Output a line for each source file
            if (classes.size() > 0) {
                out.println("<div class=\"separator\">&nbsp;</div>");
                out.println("<table class=\"report\" id=\"classResults\">");
                out.println(generateTableHeader("Classes in this Package",
                        false));
                out.println("<tbody>");

                iter = classes.iterator();
                while (iter.hasNext()) {
                    out.println(generateTableRowsForSourceFile(iter.next()));
                }

                out.println("</tbody>");
                out.println("</table>");
                out.println("<script type=\"text/javascript\">");
                out.println("var classTable = new SortableTable(document.getElementById(\"classResults\"),");
                out.println("[\"String\", \"Percentage\", \"Percentage\", \"FormattedNumber\"]);");
                out.println("classTable.sort(0);");
                out.println("</script>");
            }

            out.println(generateFooter());

            out.println("</body>");
            out.println("</html>");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Deprecated
    private void generateSourceFiles() {
        Iterator<GenericReportEntry> iter = projectData.getEntriesForLevel(ReportConstants.level_class).iterator();
        while (iter.hasNext()) {
            GenericReportEntry clazz = iter.next();
            try {
                SourceFile sfile = (SourceFile) ((Collection)
                        projectData.getSourceLinesByClass(clazz.getName())).iterator().next();
                generateSourceFile(sfile.getEntries(), clazz);
            } catch (IOException e) {
                log.info("Could not generate HTML file for source file "
                        + clazz.getName() + ": "
                        + e.getLocalizedMessage());
            }
        }
    }

    private void generateSourceFilesAlt() {
        Iterator<GenericReportEntry> iter = projectData.getEntriesForLevel(ReportConstants.level_class).iterator();
        while (iter.hasNext()) {
            GenericReportEntry clazz = iter.next();
            try {
                generateSourceFileAlt(projectData.getSourceLinesByClass(clazz.getName()), clazz);
            } catch (IOException e) {
                log.info("Could not generate HTML file for source file "
                        + clazz.getName() + ": "+ e.getLocalizedMessage());
            }
        }
    }

    private void generateSourceFile(Collection<SourceFileEntry> sourceFileData, GenericReportEntry clazz)
            throws IOException {
        if (sourceFileData.size() < 0) {
            log.info("Data file does not contain instrumentation "
                    + "information for the file " + clazz.getName()
                    + ".  Ensure this class was instrumented, and this "
                    + "data file contains the instrumentation information.");
        }

        String[] className = clazz.getName().split("\\.");
        String filename = className[className.length - 1].toLowerCase() + ".html";
        File file = new File(destinationDir, filename);
        PrintWriter out = null;

        try {
            out = IOUtil.getPrintWriter(file);

            out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"");
            out.println("           \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");

            out.println("<html>");
            out.println("<head>");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
            out.println("<title>Coverage Report</title>");
            out.println("<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\"/>");
            out.println("<script type=\"text/javascript\" src=\"js/popup.js\"></script>");
            out.println("</head>");
            out.println("<body>");
            out.print("<h5>Coverage Report - ");

            out.print(clazz.getName());
            out.println("</h5>");

            // Output the coverage summary for methods in this class
            // TODO

            // Output this class's source code with syntax and coverage highlighting
            out.println("<div class=\"separator\">&nbsp;</div>");
            out.println(generateHtmlizedJavaSource(sourceFileData, clazz));

            out.println(generateFooter());

            out.println("</body>");
            out.println("</html>");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void generateSourceFileAlt(final Collection<SourceFileEntry> sourceFileData, final GenericReportEntry clazz)
            throws IOException {
        if (sourceFileData.size() < 0) {
            log.info("Data file does not contain instrumentation "
                    + "information for the file " + clazz.getName()
                    + ".  Ensure this class was instrumented, and this "
                    + "data file contains the instrumentation information.");
        }

        String filename = clazz.getName()+".java.html";

        StringWriter sw = new StringWriter();
        new Html(sw) {
            {
                html().xmlns("http://www.w3.org/1999/xhtml").attr("xml:lang", "en").attr("lang", "en");
                head();
                meta().httpEquiv("Content-Type").content("text/html").charset("UTF-8");
                title().text("Coverage Report").end();
                link().title("Style").type("text/css").rel("stylesheet").href("css/main.css").end();
                script().type("text/javascript").src("js/popup.js").end();
                end();

                body();
                h5().text("Coverage Report - " + clazz.getName()).end();
                // Output the coverage summary for methods in this class
                // TODO
                // Output this class's source code with syntax and coverage highlighting
                div().classAttr("separator").text(" ").end();
                generateHtmlizedJavaSourceAlt(this, sourceFileData, clazz);
                endAll();
            }
        };

        log.info("Exporting source file to: "+new File(destinationDir, filename).getAbsolutePath());
        log.info(sw.getBuffer().toString());
        printToFile(new File(destinationDir, filename), sw.getBuffer().toString());
    }

    @Deprecated
    private String generateBranchInfo(GenericReportEntry lineData, String content) {
        boolean hasBranch = (lineData != null) &&
                lineData.getPayload().getMetric(ReportConstants.metricName_totalBranches).getValue() > 0;
        if (hasBranch) {
            StringBuffer ret = new StringBuffer();
            ret.append("<a title=\"Line ").append(lineData.getName()).append(": Conditional coverage ")
                    .append(lineData.getPayload().getMetric(ReportConstants.metricName_lineCoverageRate));

            ret.append(".\">").append(content).append("</a>");
            return ret.toString();
        } else {
            return content;
        }
    }

    private void generateBranchInfoAlt(Html html, Node lineData, String content) {
        boolean hasBranch = (lineData != null) &&
                lineData.getPayload().getMetric(ReportConstants.metricName_totalBranches).getValue() > 0;
        if (hasBranch) {
            html.a().title("line"+lineData.getName()+": Conditional coverage "+
                    lineData.getPayload().getMetric(ReportConstants.metricName_branchCoverageRate).getValue()).
                    text(content).end();
        } else {
            html.text(content);
        }
    }

    @Deprecated
    private String generateHtmlizedJavaSource(Collection<SourceFileEntry> sourceFileData, GenericReportEntry clazz) {
        Iterator<SourceFileEntry> entries = new TreeSet<SourceFileEntry>(sourceFileData).iterator();

        if (sourceFileData == null) {
            return "<p>Unable to locate " + clazz.getName()
                    + ".  Have you specified the source directory?</p>";
        }

        //TODO remove
//        List<GenericReportEntry> lines = new ArrayList<GenericReportEntry>();
//        clazz.getEntriesForLevel(lines, ReportConstants.level_line);
//        Iterator<GenericReportEntry> it = lines.iterator();
//
        Map<String, Node> lineMap = new HashMap<String, Node>();
        Set<? extends Node>lines = getEntriesForType(clazz, NodeType.LINE);

        for (Node line : lines) {
            lineMap.put(line.getName(), line);
        }

        StringBuffer ret = new StringBuffer();
        ret.append("<table cellspacing=\"0\" cellpadding=\"0\" class=\"src\">\n");
        JavaToHtml javaToHtml = new JavaToHtml();
        while (entries.hasNext()) {
            SourceFileEntry entry = entries.next();
            ret.append("<tr>");
            ret.append("  <td class=\"numLineCover\">&nbsp;"
                    + entry.getLineNumber() + "</td>");
            Node codeLine = lineMap.get(entry.getCodeLine());

            int hits = 0;
            if (codeLine != null) {
                hits = (int) codeLine.getPayload().getMetric(ReportConstants.metricName_hits).getValue();
            }
            if (hits > 0) {
                ret.append("  <td class=\"nbHitsCovered\">"
                        + generateBranchInfo(clazz, "&nbsp;" + hits)
                        + "</td>");
                ret.append("  <td class=\"src\"><pre class=\"src\">&nbsp;"
                        + generateBranchInfo(clazz, javaToHtml.process(entry.getCodeLine()))
                        + "</pre></td>");
            } else {
                ret.append("  <td class=\"nbHitsUncovered\">"
                        + generateBranchInfo(clazz, "&nbsp;" + hits)
                        + "</td>");
                ret.append("  <td class=\"src\"><pre class=\"src\"><span class=\"srcUncovered\">&nbsp;"
                        + generateBranchInfo(clazz, javaToHtml.process(entry.getCodeLine()))
                        + "</span></pre></td>");
            }
            ret.append("</tr>\n");
        }
        ret.append("</table>\n");
        return ret.toString();
    }

    private void generateHtmlizedJavaSourceAlt(Html html, Collection<SourceFileEntry> sourceFileData, GenericReportEntry clazz) {
        Iterator<SourceFileEntry> entries = new TreeSet<SourceFileEntry>(sourceFileData).iterator();

        if (sourceFileData == null) {
            html.p().text("Unable to locate " + clazz.getName()+ ".  Have you specified the source directory?").end();
            return;
        }

        Map<String, Node> lineMap = new HashMap<String, Node>();
        Set<? extends Node>lines = getEntriesForType(clazz, NodeType.LINE);

        for (Node line : lines) {
            lineMap.put(line.getName(), line);
        }

        html.table().cellspacing("0").cellpadding("0").classAttr("src");

        while (entries.hasNext()) {
            SourceFileEntry entry = entries.next();
            Node codeLine = lineMap.get(""+entry.getLineNumber());
            int hits = 0;
            if (codeLine != null) {
                hits = (int) codeLine.getPayload().getMetric(ReportConstants.metricName_hits).getValue();
            }
            String classString;
            html.tr();
            if(hits<=0){classString = "red";}else{classString = "green";}
            html.classAttr(classString);

            html.td().classAttr("numLineCover").text(""+entry.getLineNumber()).end();
            classString = (hits>0)?"nbHitsCovered":"nbHitsUncovered";
                html.td().classAttr(classString);
                generateBranchInfoAlt(html, codeLine, "" + hits);
                html.end();
                html.td().classAttr("src");
                html.pre().classAttr("src");
                generateBranchInfoAlt(html, codeLine, entry.getCodeLine());
            html.end().end();//end pre and td
        }
        html.end();//end table
    }

    @Deprecated
    private static String generateFooter() {
        return "<div class=\"footer\">Report generated by "
                + "<a href=\"http://cobertura.sourceforge.net/\" target=\"_top\">Cobertura</a> "
                + Header.version() + " on "
                + DateFormat.getInstance().format(new Date()) + ".</div>";
    }

    private static void generateFooterAlt(Html html) {
        String version = (Header.version()!=null)?Header.version():"";
        html.div().classAttr("footer").
            a().href("http://cobertura.sourceforge.net/").text("Report generated by Cobertura").target("_top").end().
            text( version + " on " + DateFormat.getInstance().format(new Date()));
        html.end();
    }

    @Deprecated
    private static String generateTableHeader(String title, boolean showColumnForNumberOfClasses) {
        StringBuffer ret = new StringBuffer();
        ret.append("<thead>");
        ret.append("<tr>");
        ret.append("  <td class=\"heading\">" + title + "</td>");
        if (showColumnForNumberOfClasses) {
            ret.append("  <td class=\"heading\"># Classes</td>");
        }
        ret.append("  <td class=\"heading\">"
                + generateHelpURL("Line Coverage",
                "The percent of lines executed by this test run.")
                + "</td>");
        ret.append("  <td class=\"heading\">"
                + generateHelpURL("Branch Coverage",
                "The percent of branches executed by this test run.")
                + "</td>");
        ret.append("  <td class=\"heading\">"
                + generateHelpURL(
                "Complexity",
                "Average McCabe's cyclomatic code complexity for all methods.  " +
                        "This is basically a count of the number of different " +
                        "code paths in a method (incremented by 1 for each if " +
                        "statement, while loop, etc.)")
                + "</td>");
        ret.append("</tr>");
        ret.append("</thead>");
        return ret.toString();
    }

    private void generateTableHeaderAlt(Html html, String title, boolean showColumnForNumberOfClasses) {
        html.thead().tr().
                td().attr("class", "heading").text(title).end();
            if(showColumnForNumberOfClasses){
                html.td().attr("class", "heading").text("# Classes").end();
            }
            html.td().attr("class", "heading");
                generateHelpURLAlt(html, "Line Coverage", lineCoverageDesc);
            html.end();
            html.td().attr("class", "heading");
                generateHelpURLAlt(html, "Branch Coverage", branchCoverageDesc);
            html.end();
            html.td().attr("class", "heading");
                generateHelpURLAlt(html, "Complexity", complexityDesc);
            html.end();
        html.end().end();
    }

    @Deprecated
    private static String generateHelpURL(String text, String description) {
        StringBuffer ret = new StringBuffer();
        boolean popupTooltips = false;
        if (popupTooltips) {
            ret.append("<a class=\"hastooltip\" href=\"help.html\" onclick=\"popupwindow('help.html'); return false;\">");
            ret.append(text);
            ret.append("<span>" + description + "</span>");
            ret.append("</a>");
        } else {
            ret.append("<a class=\"dfn\" href=\"help.html\" onclick=\"popupwindow('help.html'); return false;\">");
            ret.append(text);
            ret.append("</a>");
        }
        return ret.toString();
    }

    private void generateHelpURLAlt(Html html, String text, String description) {
        boolean popupTooltips = false;
        String classString = (popupTooltips) ? "hastooltip" : "dfn";

        html.a().attr("class", classString).href("help.html").onclick("popupwindow('help.html'); return false;").text(text);
        if(popupTooltips){
            html.span().text(description).end();
        }
        html.end();
    }

    @Deprecated
    private String generateTableRowForTotal() {
        GenericReportEntry project =
                projectData.getEntriesForLevel(ReportConstants.level_project).get(0);
        StringBuffer ret = new StringBuffer();

        ret.append("  <tr>");
        ret.append("<td><b>All Packages</b></td>");
        ret.append("<td class=\"value\">"
                + projectData.getEntriesForLevel(ReportConstants.level_class).size() + "</td>");
        ret.append(generateTableColumnsFromData(project));
        ret.append("</tr>");
        return ret.toString();
    }

    private void generateTableRowForTotalAlt(Html html) {
        GenericReportEntry project =
                projectData.getEntriesForLevel(ReportConstants.level_project).get(0);
        html.tr().
                td().b().text("All Packages").end().end().
                td().attr("class", "value").text(
                "" + projectData.getEntriesForLevel(ReportConstants.level_class).size()).end();
        generateTableColumnsFromDataAlt(html, project);
        html.end();
    }

    @Deprecated
    private String generateTableRowForPackage(GenericReportEntry packageData) {
        StringBuffer ret = new StringBuffer();
        String url1 = "frame-summary-" + packageData.getName() + ".html";
        String url2 = "frame-sourcefiles-" + packageData.getName() + ".html";
        double ccn = packageData.getPayload().getMetric(ReportConstants.metricName_ccn).getValue();

        Set<? extends Node>childs =
                getEntriesForType(packageData, new JavaNodeTypeHierarchy().getLower(packageData.getType()));

        ret.append("  <tr>");
        ret.append("<td><a href=\"" + url1
                + "\" onclick='parent.sourceFileList.location.href=\"" + url2
                + "\"'>" + generatePackageName(packageData) + "</a></td>");
        ret.append("<td class=\"value\">" + childs.size()
                + "</td>");
        ret.append(generateTableColumnsFromData(packageData));
        ret.append("</tr>");
        return ret.toString();
    }

    private void generateTableRowForPackageAlt(Html html, GenericReportEntry packageData) {
        StringBuffer ret = new StringBuffer();
        String url1 = "frame-summary-" + packageData.getName() + ".html";
        String url2 = "frame-sourcefiles-" + packageData.getName() + ".html";
        double ccn = packageData.getPayload().getMetric(ReportConstants.metricName_ccn).getValue();

        Set<? extends Node>childs =
                getEntriesForType(packageData, new JavaNodeTypeHierarchy().getLower(packageData.getType()));

        html.tr().
                td().
                a().href(url1).onclick("parent.sourceFileList.location.href=\"" + url2 + "\"").
                text(generatePackageName(packageData)).end();
        html.end();
        html.td().classAttr("value").text("" + childs.size()).end();
        generateTableColumnsFromDataAlt(html, packageData);
        html.end();
    }

    @Deprecated
    private String generateTableRowsForSourceFile(GenericReportEntry entry) {
        StringBuffer ret = new StringBuffer();
        String sourceFileName = getNormalizedName(entry.getName());

        Set<? extends Node>childs = getEntriesForType(entry, new JavaNodeTypeHierarchy().getLower(entry.getType()));

        for (Node child : childs) {
            ret.append(generateTableRowForClass(child, sourceFileName));
        }

        return ret.toString();
    }

    private void generateTableRowsForSourceFileAlt(Html html, GenericReportEntry entry) {
        String sourceFileName = getNormalizedName(entry.getName());

        Set<? extends Node>childs = getEntriesForType(entry, new JavaNodeTypeHierarchy().getLower(entry.getType()));

        for (Node child : childs) {
            generateTableRowForClassAlt(html, child);
        }
    }

    @Deprecated
    private String generateTableRowForClass(Node classData,
                                            String sourceFileName) {
        StringBuffer ret = new StringBuffer();

        ret.append("  <tr>");
        // TODO: URL should jump straight to the class (only for inner classes?)
        ret.append("<td><a href=\"" + sourceFileName
                + ".html\">" + classData.getName() + "</a></td>");
        ret.append(generateTableColumnsFromData(classData));
        ret.append("</tr>\n");
        return ret.toString();
    }

    private void generateTableRowForClassAlt(Html html, Node classData) {
        String className = classData.getName().replaceAll("/","\\.");
        html.tr().
                td().a().href( className+ ".html").text(classData.getName()).end().end();
        generateTableColumnsFromDataAlt(html, classData);
        html.end();
    }

    /**
     * Return a string containing three HTML table cells.  The first
     * cell contains a graph showing the line coverage, the second
     * cell contains a graph showing the branch coverage, and the
     * third cell contains the code complexity.
     *
     * @return A string containing the HTML for three table cells.
     */
    @Deprecated
    private String generateTableColumnsFromData(Node entry) {
        int numLinesCovered = (int) entry.getPayload().getMetric(ReportConstants.metricName_coveredLines).getValue();
        int numLinesValid = (int) entry.getPayload().getMetric(ReportConstants.metricName_totalLines).getValue();
        int numBranchesCovered = (int) entry.getPayload().getMetric(ReportConstants.metricName_coveredBranches).getValue();
        int numBranchesValid = (int) entry.getPayload().getMetric(ReportConstants.metricName_totalLines).getValue();

        // The "hidden" CSS class is used below to write the ccn without
        // any formatting so that the table column can be sorted correctly
        return "<td>" + generatePercentResult(numLinesCovered, numLinesValid)
                + "</td><td>"
                + generatePercentResult(numBranchesCovered, numBranchesValid)
                + "</td><td class=\"value\"><span class=\"hidden\">"
                + entry.getPayload().getMetric(ReportConstants.metricName_ccn).getValue()
                + ";</span>"
                + getDoubleValue(entry.getPayload().getMetric(ReportConstants.metricName_ccn).getValue())
                + "</td>";
    }

    /**
     * Return a string containing three HTML table cells.  The first
     * cell contains a graph showing the line coverage, the second
     * cell contains a graph showing the branch coverage, and the
     * third cell contains the code complexity.
     *
     * @return A string containing the HTML for three table cells.
     */
    private void generateTableColumnsFromDataAlt(Html html, Node entry) {
        int numLinesCovered = (int) entry.getPayload().getMetric(ReportConstants.metricName_coveredLines).getValue();
        int numLinesValid = (int) entry.getPayload().getMetric(ReportConstants.metricName_totalLines).getValue();
        int numBranchesCovered = (int) entry.getPayload().getMetric(ReportConstants.metricName_coveredBranches).getValue();
        int numBranchesValid = (int) entry.getPayload().getMetric(ReportConstants.metricName_totalLines).getValue();

        // The "hidden" CSS class is used below to write the ccn without
        // any formatting so that the table column can be sorted correctly
        html.td();
            generatePercentResultAlt(html, numLinesCovered, numLinesValid);
        html.end();
        html.td();
            generatePercentResultAlt(html, numBranchesCovered, numBranchesValid);
        html.end();
        html.td().classAttr("value").
                span().classAttr("hidden").text("" + entry.getPayload().getMetric(ReportConstants.metricName_ccn).getValue()).end().
                text("" + getDoubleValue(entry.getPayload().getMetric(ReportConstants.metricName_ccn).getValue())).end();
    }

    /**
     * This is crazy complicated, and took me a while to figure out,
     * but it works.  It creates a dandy little percentage meter, from
     * 0 to 100.
     *
     * @param dividend The number of covered lines or branches.
     * @param divisor  The number of valid lines or branches.
     * @return A percentage meter.
     */
    @Deprecated
    private static String generatePercentResult(int dividend, int divisor) {
        StringBuffer sb = new StringBuffer();

        sb.append("<table cellpadding=\"0px\" cellspacing=\"0px\" class=\"percentgraph\"><tr class=\"percentgraph\"><td align=\"right\" class=\"percentgraph\" width=\"40\">");
        if (divisor > 0)
            sb.append(getPercentValue((double) dividend / divisor));
        else
            sb.append(generateHelpURL(
                    "N/A",
                    "Line coverage and branch coverage will appear as \"Not Applicable\" when Cobertura can not find line number information in the .class file.  This happens for stub and skeleton classes, interfaces, or when the class was not compiled with \"debug=true.\""));
        sb.append("</td><td class=\"percentgraph\"><div class=\"percentgraph\">");
        if (divisor > 0) {
            sb.append("<div class=\"greenbar\" style=\"width:"
                    + (dividend * 100 / divisor) + "px\">");
            sb.append("<span class=\"text\">");
            sb.append(dividend);
            sb.append("/");
            sb.append(divisor);
        } else {
            sb.append("<div class=\"na\" style=\"width:100px\">");
            sb.append("<span class=\"text\">");
            sb.append(generateHelpURL(
                    "N/A",
                    "Line coverage and branch coverage will appear as \"Not Applicable\" when Cobertura can not find line number information in the .class file.  This happens for stub and skeleton classes, interfaces, or when the class was not compiled with \"debug=true.\""));
        }
        sb.append("</span></div></div></td></tr></table>");

        return sb.toString();
    }

    private void generatePercentResultAlt(Html html, int dividend, int divisor) {
        String classPercentgraph = "percentgraph";
        html.table().cellpadding("0px").cellspacing("0px").classAttr(classPercentgraph).
                tr().classAttr(classPercentgraph).
                td().align("right").classAttr(classPercentgraph).width("40");
                    if(divisor>0){
                        html.text(getPercentValue((double) dividend / divisor));
                    }else{
                        String desc = "Line coverage and branch coverage will appear as \"Not Applicable\" " +
                                "when Cobertura can not find line number information in the .class file.  " +
                                "This happens for stub and skeleton classes, interfaces, or when the class " +
                                "was not compiled with \"debug=true.\"";
                        generateHelpURLAlt(html, "N/A", desc);
                    }
                html.end();
                html.td().classAttr(classPercentgraph).div().classAttr(classPercentgraph);
                    if(divisor>0){
                        html.div().classAttr("greenbar").style("width:"+(dividend * 100 / divisor) + "px").
                                span().classAttr("text").text(dividend+"/"+divisor).end();
                        html.end();
                    }else{
                        html.div().classAttr("na").style("width:100px").span().classAttr("text");
                        String desc = "Line coverage and branch coverage will appear as \"Not Applicable\" " +
                                "when Cobertura can not find line number information in the .class file.  This " +
                                "happens for stub and skeleton classes, interfaces, or when the class was not " +
                                "compiled with \"debug=true.\"";
                        generateHelpURLAlt(html, "NA", desc);
                        html.end().end();
                    }
                html.end().end();//end td and div
            html.end();//end tr
        html.end();//end table
    }

    private static String getDoubleValue(double value) {
        return new DecimalFormat().format(value);
    }

    private static String getPercentValue(double value) {
        return StringUtil.getPercentValue(value);
    }

    private SortedSet<GenericReportEntry> getSubPackages(GenericReportEntry entry) {
        List<GenericReportEntry> packages = projectData.getEntriesForLevel(ReportConstants.level_package);
        SortedSet<GenericReportEntry> entries = new TreeSet<GenericReportEntry>();
        for (GenericReportEntry packageEntry : packages) {
            if (packageEntry.getName().startsWith(entry.getName())) {
                entries.add(packageEntry);
            }
        }
        return entries;
    }

    /**
     * @return The name of this source file without the file extension
     *         in the format
     *         "net.sourceforge.cobertura.coveragedata.SourceFileData"
     */
    private String getNormalizedName(String name) {
        String fullNameWithoutExtension;
        int lastDot = name.lastIndexOf('.');
        if (lastDot == -1) {
            fullNameWithoutExtension = name;
        } else {
            fullNameWithoutExtension = name.substring(0, lastDot);
        }
        return StringUtil.replaceAll(fullNameWithoutExtension, "/", ".");
    }

    private class SourceFileDataBaseNameComparator2 implements Comparator, Serializable {

        private static final long serialVersionUID = 0L;

        public int compare(Object arg0, Object arg1) {
            GenericReportEntry sourceFileData0 = (GenericReportEntry) arg0;
            GenericReportEntry sourceFileData1 = (GenericReportEntry) arg1;
            int comparison = sourceFileData0.getName().compareTo(sourceFileData1.getName());
            if (comparison != 0)
                return comparison;
            return sourceFileData0.getName().compareTo(sourceFileData1.getName());
        }
    }

    private Set<? extends Node>getEntriesForType(Node entry, NodeType type){
        Criteria all = new EqCriteria(NodeType.ALL);
        Criteria thisLevel = new EqCriteria(entry.getType());
        Set<Criteria>criterias = new HashSet<Criteria>();
        criterias.add(all);
        criterias.add(thisLevel);
        Criteria orCriteria = new ORListedCriteria(criterias);
        return entry.getNodes(true, new TypeFilter(orCriteria));
    }
}
