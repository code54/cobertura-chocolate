package net.sourceforge.cobertura.util;

import net.sourceforge.cobertura.coveragedata.*;

import static net.sourceforge.cobertura.util.ArchiveUtil.saveGlobalProjectData;

public class ShutdownHooks {

	public static void saveGlobalProjectDataWithTomcatInits(ProjectData projectData){
		// Hack for Tomcat - by saving project data right now we force loading
		// of classes involved in this process (like ObjectOutputStream)
		// so that it won't be necessary to load them on JVM shutdown
		if (System.getProperty("catalina.home") != null){
			saveGlobalProjectData(projectData);

			// Force the class loader to load some classes that are
			// required by our JVM shutdown hook.
			// TODO: Use ClassLoader.loadClass("whatever"); instead
			ClassData.class.toString();
			CoverageData.class.toString();
			CoverageDataContainer.class.toString();
			FileLocker.class.toString();
			HasBeenInstrumented.class.toString();
			LineData.class.toString();
			PackageData.class.toString();
			SourceFileData.class.toString();
		}

		// Add a hook to save the data when the JVM exits
		Runtime.getRuntime().addShutdownHook(new Thread(new SaveTimer()));

		// Possibly also save the coverage data every x seconds?
		//Timer timer = new Timer(true);
		//timer.schedule(saveTimer, 100);
	}
}
