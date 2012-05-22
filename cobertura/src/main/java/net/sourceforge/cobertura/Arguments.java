package net.sourceforge.cobertura;

import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.util.RegexUtil;

import java.io.File;
import java.util.*;

/**
 * Encapsulates arguments;
 */
public class Arguments {

    private File baseDirectory;
    private File dataFile;
    private File destinationDirectory;
    private File commandsFile;

    private Collection ignoreRegexes;
	private Collection ignoreBranchesRegexes;
	private Collection ignoreMethodAnnotations;
	private Collection classPatternIncludeClassesRegexes;
    private Collection classPatternExcludeClassesRegexes;
    private boolean failOnError;
    private boolean ignoreTrivial;

    private String format;
    private String encoding;

    private double classLineThreshold;
    private double classBranchThreshold;
    private double packageLineThreshold;
    private double packageBranchThreshold;
    private double totalLineThreshold;
    private double totalBranchThreshold;

    private Set<File>filesToInstrument;
    private Set<File>filesToMerge;


    public Arguments(){
        initVariables();
    }

    public Arguments setBaseDirectory(String baseDir){
        baseDirectory = new File(baseDir);
        return this;
    }

    public Arguments setDataFile(String dataFile){
        this.dataFile = new File(dataFile);
        return this;
    }

    public Arguments setDestinationFile(String destinationFile){
        this.destinationDirectory = new File(destinationFile);
        return this;
    }

    public Arguments setCommandsFile(String commandsFile){
        this.commandsFile = new File(commandsFile);
        return this;
    }

    public Arguments addIgnoreRegex(String regex){
        RegexUtil.addRegex(ignoreRegexes, regex);
        return this;
    }

    public Arguments addIgnoreBranchRegex(String regex){
        RegexUtil.addRegex(ignoreBranchesRegexes, regex);
        return this;
    }

    public Arguments addIgnoreMethodAnnotation(String ignoreMethodAnnotation){
        ignoreMethodAnnotations.add(ignoreMethodAnnotation);
        return this;
    }

    public Arguments addExcludeClassesRegex(String regex){
        RegexUtil.addRegex(classPatternExcludeClassesRegexes, regex);
        return this;
    }

    public Arguments addIncludeClassesRegex(String regex){
        RegexUtil.addRegex(classPatternIncludeClassesRegexes, regex);
        return this;
    }

    public Arguments failOnError(boolean failOnError){
        this.failOnError=failOnError;
        return this;
    }

    public Arguments ignoreTrivial(boolean ignoreTrivial){
        this.ignoreTrivial=ignoreTrivial;
        return this;
    }

    public Arguments setFormat(String format){
        this.format = format;
        return this;
    }

    public Arguments setEncoding(String encoding){
        this.encoding = encoding;
        return this;
    }

    public Arguments setClassBranchCoverageThreshold(String coverageThreshold){
        classBranchThreshold = inRangeAndDivideByOneHundred(coverageThreshold);
        return this;
    }

    public Arguments setClassLineCoverageThreshold(String coverageThreshold){
        classLineThreshold = inRangeAndDivideByOneHundred(coverageThreshold);
        return this;
    }

    public Arguments setPackageBranchCoverageThreshold(String coverageThreshold){
        packageBranchThreshold = inRangeAndDivideByOneHundred(coverageThreshold);
        return this;
    }

    public Arguments setPackageLineCoverageThreshold(String coverageThreshold){
        packageLineThreshold = inRangeAndDivideByOneHundred(coverageThreshold);
        return this;
    }

    public Arguments setTotalBranchCoverageThreshold(String coverageThreshold){
        totalBranchThreshold = inRangeAndDivideByOneHundred(coverageThreshold);
        return this;
    }

    public Arguments setTotalLineCoverageThreshold(String coverageThreshold){
        totalLineThreshold = inRangeAndDivideByOneHundred(coverageThreshold);
        return this;
    }

    public Arguments addFileToInstrument(String file){
        filesToInstrument.add(new File(file));
        return this;
    }

    public Arguments addFileToMerge(String file){
        filesToMerge.add(new File(file));
        return this;
    }

    //TODO "--regex" is not considered, since seems there's no place where is considered...
    /*   Aux methods   */
    private void initVariables(){
        dataFile = CoverageDataFileHandler.getDefaultDataFile();
        ignoreRegexes = new Vector();
	    ignoreBranchesRegexes = new Vector();
        ignoreMethodAnnotations = new HashSet();
        classPatternExcludeClassesRegexes = new HashSet();
        classPatternIncludeClassesRegexes = new HashSet();
        filesToInstrument = new HashSet<File>();
        filesToMerge = new HashSet<File>();

        classBranchThreshold = 0.5;
        classLineThreshold = 0.5;
        packageBranchThreshold = 0.5;
        packageLineThreshold = 0.5;
        totalBranchThreshold = 0.5;
        totalLineThreshold = 0.5;
    }

    private double inRangeAndDivideByOneHundred(String coverageRateAsPercentage){
		int coverage = Integer.valueOf(coverageRateAsPercentage).intValue();
        if ((coverage >= 0)
				&& (coverage <= 100)){
			return (double)coverage/ 100;
		}
		throw new IllegalArgumentException("The value "
				+ coverageRateAsPercentage
				+ "% is invalid.  Percentages must be between 0 and 100.");
	}


    /*   Getters   */
    public File getBaseDirectory() {
        return baseDirectory;
    }

    public File getDataFile() {
        return dataFile;
    }

    public File getDestinationDirectory() {
        return destinationDirectory;
    }

    public File getCommandsFile() {
        return commandsFile;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public boolean isIgnoreTrivial() {
        return ignoreTrivial;
    }

    public String getFormat() {
        return format;
    }

    public String getEncoding() {
        return encoding;
    }

    public double getClassLineThreshold() {
        return classLineThreshold;
    }

    public double getClassBranchThreshold() {
        return classBranchThreshold;
    }

    public double getPackageLineThreshold() {
        return packageLineThreshold;
    }

    public double getPackageBranchThreshold() {
        return packageBranchThreshold;
    }

    public double getTotalLineThreshold() {
        return totalLineThreshold;
    }

    public double getTotalBranchThreshold() {
        return totalBranchThreshold;
    }
}
