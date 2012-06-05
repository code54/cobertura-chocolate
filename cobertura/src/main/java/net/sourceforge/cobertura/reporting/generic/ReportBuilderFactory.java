package net.sourceforge.cobertura.reporting.generic;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2012 Jose M. Rozanec
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
public class ReportBuilderFactory implements IReportBuilderFactory{
    private static final Logger log = Logger.getLogger(ReportBuilderFactory.class);

    private Map<String, IReportBuilderStrategy> strategies;

    public ReportBuilderFactory(){
        strategies = new HashMap<String, IReportBuilderStrategy>();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addUrls(ClasspathHelper.forClass(IReportBuilderStrategy.class))
                .setScanners(new SubTypesScanner()));
        Iterator<Class<? extends IReportBuilderStrategy>> iterator =
                reflections.getSubTypesOf(IReportBuilderStrategy.class).iterator();
        while (iterator.hasNext()) {
            try {
                IReportBuilderStrategy strategy =
                        (IReportBuilderStrategy) Class.forName(iterator.next().getName()).newInstance();
                strategies.put(strategy.getTargetedLanguage(),strategy);
            } catch (Exception e) {
                log.error("An error occurred while loading metrics", e);
            }
        }
    }

    public IReportBuilderStrategy getInstance(String targetedLanguage){
        return strategies.get(targetedLanguage);
    }
}
