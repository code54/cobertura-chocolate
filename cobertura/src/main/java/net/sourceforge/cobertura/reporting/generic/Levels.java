package net.sourceforge.cobertura.reporting.generic;

/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2011 Jose M. Rozanec
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

public class Levels {

    private String []levels;

    public Levels(){
        levels = new String[]{
                ReportConstants.level_line,
                ReportConstants.level_method,
                ReportConstants.level_class,
                ReportConstants.level_sourcefile,
                ReportConstants.level_package,
                ReportConstants.level_project
        };
    }

    /**
     * Returns the immediate higher level or null if is the highest one.
     * @param currentLevel
     * @return
     */
    public String getHigherLevel(String currentLevel){
        for(int j=0;j<levels.length;j++){
            if(levels[j].equals(currentLevel)&&
                    !currentLevel.equals(ReportConstants.level_project)){
                return levels[j+1];
            }
        }
        return null;
    }

    /**
     * Returns the immediate lower level or null if is the lowest one.
     * @param currentLevel
     * @return
     */
    public String getLowerLevel(String currentLevel){
        for(int j=0;j<levels.length;j++){
            if(levels[j].equals(currentLevel)&&
                    !currentLevel.equals(ReportConstants.level_method)){
                return levels[j-1];
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
