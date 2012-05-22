/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2005 Grzegorz Lukasik
 * Copyright (C) 2006 John Lewis
 * Copyright (C) 2006 Mark Doliner
 *
 * Note: This file is dual licensed under the GPL and the Apache
 * Source License (so that it can be used from both the main
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

package net.sourceforge.cobertura.util;

import net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.coveragedata.TouchCollector;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.zip.ZipEntry;

import static net.sourceforge.cobertura.coveragedata.CoverageDataFileHandler.loadProjectData;

/**
 * Utility methods for working with archives.
 *
 * @author John Lewis
 */
public abstract class ArchiveUtil {
    private static final Logger log = Logger.getLogger(ArchiveUtil.class);

    /**
     * Return true if the given name ends with .jar, .zip,
     * .war, .ear, or .sar (case insensitive).
     *
     * @param name The file name.
     * @return true if the name is an archive.
     */
    public static boolean isArchive(String name) {
        name = name.toLowerCase();
        return name.endsWith(".jar") || name.endsWith(".zip") || name.endsWith(".war")
                || name.endsWith(".ear") || name.endsWith(".sar");
    }

    /**
     * Check to see if the given file name is a signature file
     * (meta-inf/*.rsa or meta-inf/*.sf).
     *
     * @param name The file name.  Commonly a ZipEntry name.
     * @return true if the name is a signature file.
     */
    public static boolean isSignatureFile(String name) {
        name = name.toLowerCase();
        return (name.startsWith("meta-inf/") && (name.endsWith(".rsa") || name.endsWith(".sf")));
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    /**
	 * @param entry A zip entry.
	 * @return True if the specified entry has "class" as its extension,
	 * false otherwise.
	 */
	public static boolean isClass(ZipEntry entry){
		return entry.getName().endsWith(".class");
	}

    public static void getFiles(File baseDir, String validExtension, List<File> files){
        String[] children = baseDir.list();
        if (children == null) {
            // Either dir does not exist or is not a directory
        } else {
            for (String filename : children) {
                File file = new File(baseDir, filename);
                if(filename.endsWith(validExtension)){
                    files.add(file);
                }else{
                    if(file.isDirectory()){
                        getFiles(file, validExtension, files);
                    }
                }
            }
        }
    }

    public static void saveGlobalProjectData(ProjectData projectData){
        TouchCollector.applyTouchesOnProjectData(projectData);

        // Get a file lock
        File dataFile = CoverageDataFileHandler.getDefaultDataFile();

        /*
         * A note about the next synchronized block:  Cobertura uses static fields to
         * hold the data.   When there are multiple classloaders, each classloader
         * will keep track of the line counts for the classes that it loads.
         *
         * The static initializers for the Cobertura classes are also called for
         * each classloader.   So, there is one shutdown hook for each classloader.
         * So, when the JVM exits, each shutdown hook will try to write the
         * data it has kept to the datafile.   They will do this at the same
         * time.   Before Java 6, this seemed to work fine, but with Java 6, there
         * seems to have been a change with how file locks are implemented.   So,
         * care has to be taken to make sure only one thread locks a file at a time.
         *
         * So, we will synchronize on the string that represents the path to the
         * dataFile.  Apparently, there will be only one of these in the JVM
         * even if there are multiple classloaders.  I assume that is because
         * the String class is loaded by the JVM's root classloader.
         */
        synchronized (dataFile.getPath().intern() ) {
            FileLocker fileLocker = new FileLocker(dataFile);

            try{
                // Read the old data, merge our current data into it, then
                // write a new ser file.
                if (fileLocker.lock()){
                    ProjectData datafileProjectData = loadProjectData(dataFile);
                    if (datafileProjectData == null){
                        datafileProjectData = projectData;
                    }else{
                        datafileProjectData.merge(projectData);
                    }
                    CoverageDataFileHandler.saveProjectData(datafileProjectData, dataFile);
                }
            }finally{
                // Release the file lock
                fileLocker.release();
            }
        }
    }
}
