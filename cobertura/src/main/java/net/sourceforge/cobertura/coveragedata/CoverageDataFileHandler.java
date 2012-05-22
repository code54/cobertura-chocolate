/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2003 jcoverage ltd.
 * Copyright (C) 2005 Mark Doliner
 * Copyright (C) 2007 Joakim Erdfelt
 * Copyright (C) 2007 Ignat Zapolsky
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

package net.sourceforge.cobertura.coveragedata;

import net.sourceforge.cobertura.util.ConfigurationUtil;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import sun.tools.asm.Cover;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * This contains methods used for reading and writing the
 * "cobertura.ser" file.
 */
public abstract class CoverageDataFileHandler implements HasBeenInstrumented{
	private static File defaultFile = null;

    private static final Logger log = Logger.getLogger(CoverageDataFileHandler.class);

	public static File getDefaultDataFile(){
		// return cached defaultFile
		if (defaultFile != null){
			return defaultFile;
		}

		// load and cache datafile configuration
		ConfigurationUtil config = new ConfigurationUtil();
		defaultFile = new File(config.getDatafile());
        
		return defaultFile;
	}

	public static ProjectData loadCoverageData(File dataFile){
		InputStream is = null;

		try{
//			is = new BufferedInputStream(new FileInputStream(dataFile), 16384);
//			return loadCoverageData(is);

            Serializer serializer = new Persister();
            return serializer.read(ProjectData.class, dataFile);
		}catch (IOException e){
			log.error("Cobertura: Error reading file "
					+ dataFile.getAbsolutePath() + ": "
					+ e.getLocalizedMessage());
			return null;
		} catch (Exception e) {
            log.error("Cobertura: Error while deserializing object", e);
            return null;
        }
//        finally{
//			if (is != null)
//				try{
//					is.close();
//				}catch (IOException e){
//					log.error("Cobertura: Error closing file "
//							+ dataFile.getAbsolutePath() + ": "
//							+ e.getLocalizedMessage());
//				}
//		}
	}

	private static ProjectData loadCoverageData(InputStream dataFile) throws IOException{
		ObjectInputStream objects = null;

		try{
			objects = new ObjectInputStream(dataFile);
			ProjectData projectData = (ProjectData)objects.readObject();
			log.info("Cobertura: Loaded information on "
					+ projectData.getNumberOfClasses() + " classes.");
			return projectData;
		}catch (IOException e) {
			throw e;
		}catch (Exception e){
			log.error("Cobertura: Error reading from object stream.");
			e.printStackTrace();
			return null;
		}finally{
			if (objects != null){
				try{
					objects.close();
				}catch (IOException e){
					log.error("Cobertura: Error closing object stream.", e);
				}
			}
		}
	}

	public static void saveCoverageData(ProjectData projectData,
			File dataFile){
		FileOutputStream os = null;

        log.info("\n***********saving coverage data - start***********************\n" +
                "Coverage data is: " + projectData + "\n" +
                "***********saving coverage data - end***********************");

        Serializer serializer = new Persister();
//        File result = new File("testexample.xml");

        log.info("Project data children is: "+projectData.getChildren());
        try {
            serializer.write(projectData, dataFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
//
//        try{
//			File dataDir = dataFile.getParentFile();
//			if( (dataDir != null) && !dataDir.exists() ){
//				dataDir.mkdirs();
//			}
//			os = new FileOutputStream(dataFile);
//			saveCoverageData(projectData, os);
//		}catch (IOException e){
//			log.error("Cobertura: Error writing file "
//                    + dataFile.getAbsolutePath(), e);
//		}finally{
//			if (os != null){
//				try{
//					os.close();
//				}catch (IOException e){
//					log.error("Cobertura: Error closing file "
//                            + dataFile.getAbsolutePath(), e);
//				}
//			}
//		}
	}

	private static void saveCoverageData(ProjectData projectData,
			OutputStream dataFile){
		ObjectOutputStream objects = null;
        
		try{
			objects = new ObjectOutputStream(dataFile);
			objects.writeObject(projectData);
			log.info("Cobertura: Saved information on " + projectData.getNumberOfClasses() + " classes.");
		}catch (IOException e){
			log.error("Cobertura: Error writing to object stream.", e);
		}finally{
			if (objects != null){
				try{
					objects.close();
				}catch (IOException e){
					log.error("Cobertura: Error closing object stream.", e);
				}
			}
		}
	}
}
