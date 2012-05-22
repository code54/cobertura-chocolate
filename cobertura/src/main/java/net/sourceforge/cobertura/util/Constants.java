package net.sourceforge.cobertura.util;

public class Constants {

    /*   Command line /parameters constants   */
    public static final String commandsfile = "--commandsfile";
    public static final String datafile = "--datafile";
    public static final String destination = "--destination";
    public static final String ignore = "--ignore";
    public static final String ignoreBranches = "--ignoreBranches";
    public static final String ignoreMethodAnnotation = "--ignoreMethodAnnotation";
    public static final String includeClasses = "--includeClasses";
    public static final String excludeClasses = "--excludeClasses";
    public static final String ignoreTrivial = "--ignoreTrivial";
    public static final String failOnError = "--failOnError";
    public static final String format = "--format";
    public static final String encoding = "--encoding";
    public static final String basedir = "--basedir";
    public static final String branch = "--branch";
    public static final String line = "--line";
    public static final String regex = "--regex";
    public static final String packagebranch = "--packagebranch";
    public static final String packageline = "--packageline";
    public static final String totalbranch = "--totalbranch";
    public static final String totalline = "--totalline";
    public static final String fileToInstrument = "filetoinstrument";
    public static final String fileToMerge = "filetomerge";

    /*   Reporting format constants   */
    public static final String report_html = "html";
    public static final String report_xml = "xml";
    public static final String report_summaryXml = "summaryXml";

    /*   HTML reporting constants   */
    public static final String css = "css";
    public static final String images = "images";
    public static final String js = "js";

    public static final String helpcss = "help.css";
    public static final String maincss = "main.css";
    public static final String sortabletablecss = "sortabletable.css";
    public static final String sourceviewercss = "source-viewer.css";
    public static final String tooltipcss = "tooltip.css";
    public static final String blankpng = "blank.png";
    public static final String downsimplepng = "downsimple.png";
    public static final String upsimplepng = "upsimple.png";
    public static final String customsorttypesjs = "customsorttypes.js";
    public static final String popupjs = "popup.js";
    public static final String sortabletablejs = "sortabletable.js";
    public static final String stringbuilderjs = "stringbuilder.js";
    public static final String helphtml = "help.html";
    public static final String indexhtml = "index.html";


    /*   Some hardcoded classes names used for Ant   */
    public static final String check_main = "net.sourceforge.cobertura.check.Main";
    public static final String instrument_main = "net.sourceforge.cobertura.instrument.Main";
    public static final String merge_main = "net.sourceforge.cobertura.merge.Main";
    public static final String reporting_main = "net.sourceforge.cobertura.reporting.BuildReportMain";

}
