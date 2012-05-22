package net.sourceforge.cobertura.reporting.xml;

import net.sourceforge.cobertura.reporting.generic.GenericReport;
import net.sourceforge.cobertura.reporting.generic.IReportFormatStrategy;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;

/**
 * Implements xml reporting strategy.
 */
public class XmlReportFormatStrategy implements IReportFormatStrategy{

    @Override
    public void save(GenericReport report) {
        Serializer serializer = new Persister();
        try {
            serializer.write(report, new File("coberturaXmlReport.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
