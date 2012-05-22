package net.sourceforge.cobertura.reporting.generic;

public interface IReportBuilderFactory {

    IReportBuilderStrategy getInstance(String targetedLanguage);
}
