package net.sourceforge.cobertura.reporting.generic;

public class Levels {

    private String []levels;

    public Levels(){
        levels = new String[]{
                ReportConstants.level_method,
                ReportConstants.level_class,
                ReportConstants.level_sourcefile,
                ReportConstants.level_package,
                ReportConstants.level_project
        };
    }

    public String getNextLevel(String currentLevel){
        for(int j=0;j<levels.length;j++){
            if(levels[j].equals(currentLevel)&&
                    !currentLevel.equals(ReportConstants.level_project)){
                return levels[j+1];
            }
        }
        return null;
    }

    /**
     * Compares two levels.
     * @param firstLevel
     * @param secondLevel
     * @return
     *      - returns 1 if firstLevel is higher than the secondLevel;
     *      - returns -1 if firstLevel is lower than secondLevel
     *      - returns 0 if levels are same;
     * @throws RuntimeException if no matching level is found.
     */
    public int compare(String firstLevel, String secondLevel){
        if(firstLevel.equals(secondLevel)){
            return 0;
        }

        for(int j=0;j<levels.length;j++){
            //if we first meet the firstLevel, then secondLevel is higher
            if(levels[j].equals(firstLevel)){
                return 1;
            }
            //if we first meet the secondLevel, then firstLevel is higher
            if(levels[j].equals(secondLevel)){
                return -1;
            }
        }
        throw new RuntimeException("No matching level was found");
    }
}
