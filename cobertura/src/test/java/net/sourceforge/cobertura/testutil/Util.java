/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2006 John Lewis
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

package net.sourceforge.cobertura.testutil;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import static net.sourceforge.cobertura.util.ArchiveUtil.deleteDir;

public class Util{

    private static final Logger log = Logger.getLogger(Util.class);

	public static File createTemporaryTextFile(String prefix) throws IOException{
		File outputFile;
		outputFile = File.createTempFile(prefix, ".txt");
		outputFile.deleteOnExit();
		return outputFile;
	}

	/**
	 * Returns the text of a file as a string.
	 * 
	 * @param file The file to read.
	 * @return A string containing the text of the file
	 */
	public static String getText(File file) throws FileNotFoundException, IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null){
				ps.println(line);
			}
			ps.close();
		}finally{
			ps.close();
			if (reader != null){
				try{
					reader.close();
				}catch (IOException e){
					log.error("IOException when closing file " + file.getAbsolutePath());
					e.printStackTrace();
				}
			}
		}
		return baos.toString();
	}

    public static void createRequiredDirectories(File[]files){
        for(File file : files){
            log.info("Creating directory "+file.getAbsolutePath());
            file.mkdirs();
        }
    }

    public static void removeRequiredDirectories(File[]files){
        for(File file : files){
            deleteDir(file);
        }
    }

    public static void removeTestReportFiles(File basedir){
        File[]files = basedir.listFiles();
        if(files!=null){
            for(File file : files){
                if(file.getName().startsWith("genericReport") ||
                        file.getName().equals("cobertura.ser")){
                    file.delete();
                }
            }
        }
    }

}
