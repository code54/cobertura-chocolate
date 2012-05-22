package net.sourceforge.cobertura.reporting.generic;

import net.sourceforge.cobertura.coveragedata.ProjectData;

import java.util.List;

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
    GenericReport getReport(List<ProjectData> projects);

    /**
     * Returns the name of the targeted lang is capable
     * of interpreting information from.
     * @return
     */
    String getTargetedLanguage();
}
