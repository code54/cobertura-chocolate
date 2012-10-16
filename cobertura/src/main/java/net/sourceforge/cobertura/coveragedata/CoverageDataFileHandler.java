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
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.IOException;

/**
 * This contains methods used for reading and writing the
 * "cobertura.xml" file.
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

	public static ProjectData loadProjectData(File dataFile){
		try{
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
	}

	public static void saveProjectData(ProjectData projectData, File dataFile){
        Serializer serializer = new Persister();
        try {
            serializer.write(projectData, dataFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
