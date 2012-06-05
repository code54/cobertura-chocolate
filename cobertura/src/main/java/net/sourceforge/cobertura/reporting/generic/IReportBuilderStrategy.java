package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.util.FileFinder;

import java.util.List;

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

/**
 * Establishes a strategy to build a GenericReport based on ProjectData.
 * This would allow us to support multiple JVM languages, since
 * an implementation should know how to deal with (auto)generated classes
 * and how the information should be displayed.
 */
public interface IReportBuilderStrategy {

    /**
     * Returns a GenericReport instance with collected data.
     * @return
     */
    GenericReport getReport(List<ProjectData> projects,
                            String sourceEncoding, FileFinder finder);

    /**
     * Returns the name of the targeted lang is capable
     * of interpreting information from.
     * @return
     */
    String getTargetedLanguage();
}
