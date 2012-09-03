package net.sourceforge.cobertura.comparator;

import net.sourceforge.cobertura.reporting.generic.GenericReportEntry;

import java.util.Comparator;

/**
 * GenericReportEntry name comparator
 */
public class GRENameComparator implements Comparator<GenericReportEntry>{

    @Override
    public int compare(GenericReportEntry o1, GenericReportEntry o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
