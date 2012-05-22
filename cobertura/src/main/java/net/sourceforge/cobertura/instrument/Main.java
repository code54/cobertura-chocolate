/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2003 jcoverage ltd.
 * Copyright (C) 2005 Mark Doliner
 * Copyright (C) 2005 Joakim Erdfelt
 * Copyright (C) 2005 Grzegorz Lukasik
 * Copyright (C) 2006 John Lewis
 * Copyright (C) 2006 Jiri Mares 
 * Copyright (C) 2008 Scott Frederick
 * Copyright (C) 2010 Tad Smith 
 * Contact information for the above is given in the COPYRIGHT file.
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

package net.sourceforge.cobertura.instrument;

import net.sourceforge.cobertura.CMD;
import net.sourceforge.cobertura.Cobertura;
import net.sourceforge.cobertura.util.CommandLineBuilder;
import net.sourceforge.cobertura.util.Header;
import org.apache.log4j.Logger;

public class Main{

    private static final Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args) throws Throwable{
		Header.print(System.out);
		long startTime = System.currentTimeMillis();

        try {
			args = CommandLineBuilder.preprocessCommandLineArguments(args);
		} catch(Exception ex) {
			log.error( "Error: Cannot process arguments: " + ex.getMessage());
			System.exit(1);
		}

		new Cobertura(new CMD().parseArguments(args).getArguments()).instrumentCode().saveProjectData();

		log.info("Instrument time: " + (System.currentTimeMillis() - startTime) + "ms");
	}
}
