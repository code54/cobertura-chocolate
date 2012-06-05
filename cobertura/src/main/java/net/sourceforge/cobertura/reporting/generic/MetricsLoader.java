package net.sourceforge.cobertura.reporting.generic;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
public class MetricsLoader {
    private static final Logger log = Logger.getLogger(MetricsLoader.class);

    private Set<ICustomMetric> customMetrics;

    public MetricsLoader() {
        customMetrics = new HashSet<ICustomMetric>();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addUrls(ClasspathHelper.forClass(ICustomMetric.class))
                .setScanners(new SubTypesScanner()));
        Iterator<Class<? extends ICustomMetric>> iterator =
                reflections.getSubTypesOf(ICustomMetric.class).iterator();
        while (iterator.hasNext()) {
            try {
                customMetrics.add((ICustomMetric) Class.forName(iterator.next().getName()).newInstance());
            } catch (Exception e) {
                log.error("An error occurred while loading metrics", e);
            }
        }
    }

    public Set<ICustomMetric> getMetrics() {
        return Collections.unmodifiableSet(customMetrics);
    }
}
