package net.sourceforge.cobertura.instrument;

import net.sourceforge.cobertura.Arguments;
import net.sourceforge.cobertura.LoggerWrapper;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.util.ArchiveUtil;
import net.sourceforge.cobertura.util.IOUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <p>
 * Add coverage instrumentation to existing classes.
 * </p>
 *
 * <h3>What does that mean, exactly?</h3>
 * <p>
 * It means Cobertura will look at each class you give it.  It
 * loads the bytecode into memory.  For each line of source,
 * Cobertura adds a few extra instructions.  These instructions
 * do the following:
 * </p>
 *
 * <ol>
 * <li>Get an instance of the ProjectData class.</li>
 * <li>Call a method in this ProjectData class that increments
 * a counter for this line of code.
 * </ol>
 *
 * <p>
 * After every line in a class has been "instrumented," Cobertura
 * edits the bytecode for the class one more time and adds
 * "implements net.sourceforge.cobertura.coveragedata.HasBeenInstrumented"
 * This is basically just a flag used internally by Cobertura to
 * determine whether a class has been instrumented or not, so
 * as not to instrument the same class twice.
 * </p>
 */
public class CodeInstrumentationTask {

    private static final LoggerWrapper log = new LoggerWrapper();

	private File destinationDirectory;
	private Collection ignoreRegexes;
	private Collection ignoreBranchesRegexes;
	private Collection ignoreMethodAnnotations;
	private ClassPattern classPattern;
	private boolean ignoreTrivial;
	private ProjectData projectData;


    public CodeInstrumentationTask instrument(Arguments arguments, ProjectData projectData) throws Throwable {

        this.projectData = projectData;
        destinationDirectory = arguments.getDestinationDirectory();
        ignoreRegexes = arguments.getIgnoreRegexes();
        ignoreBranchesRegexes = arguments.getIgnoreBranchesRegexes();
        ignoreMethodAnnotations = arguments.getIgnoreMethodAnnotations();
        classPattern = new ClassPattern();
        classPattern.addExcludeClassesRegex(arguments.getClassPatternExcludeClassesRegexes());
        classPattern.addIncludeClassesRegex(arguments.getClassPatternIncludeClassesRegexes());
        ignoreTrivial = arguments.ignoreTrivial();

        Set<File> files  = arguments.getFilesToInstrument();
		// Instrument classes
		log.info("Instrumenting "	+ files.size() + " "
				+ (files.size() == 1 ? "file" : "files")
				+ (arguments.getDestinationDirectory() != null ? " to "
						+ arguments.getDestinationDirectory().getAbsoluteFile() : ""));

		Iterator iter = files.iterator();
		while (iter.hasNext()){
			CoberturaFile coberturaFile = (CoberturaFile)iter.next();
			if (coberturaFile.isArchive()){
				addInstrumentationToArchive(coberturaFile);
			}else{
				addInstrumentation(coberturaFile);
			}
		}

        return this;
    }

    /*  Instrumentation methods    */

	private boolean addInstrumentationToArchive(CoberturaFile file, InputStream archive,
			OutputStream output) throws Throwable{
		ZipInputStream zis = null;
		ZipOutputStream zos = null;

		try{
			zis = new ZipInputStream(archive);
			zos = new ZipOutputStream(output);
			return addInstrumentationToArchive(file, zis, zos);
		}finally{
			zis = (ZipInputStream) IOUtil.closeInputStream(zis);
			zos = (ZipOutputStream)IOUtil.closeOutputStream(zos);
		}
	}

	private boolean addInstrumentationToArchive(CoberturaFile file, ZipInputStream archive,
			ZipOutputStream output) throws Throwable{
		/*
		 * "modified" is returned and indicates that something was instrumented.
		 * If nothing is instrumented, the original entry will be used by the
		 * caller of this method.
		 */
		boolean modified = false;
		ZipEntry entry;
		while ((entry = archive.getNextEntry()) != null){
			try{
				String entryName = entry.getName();

				/*
				 * If this is a signature file then don't copy it,
				 * but don't set modified to true.  If the only
				 * thing we do is strip the signature, just use
				 * the original entry.
				 */
				if (ArchiveUtil.isSignatureFile(entry.getName())){
					continue;
				}
				ZipEntry outputEntry = new ZipEntry(entry.getName());
				outputEntry.setComment(entry.getComment());
				outputEntry.setExtra(entry.getExtra());
				outputEntry.setTime(entry.getTime());
				output.putNextEntry(outputEntry);

				// Read current entry
				byte[] entryBytes = IOUtil
						.createByteArrayFromInputStream(archive);

				// Instrument embedded archives if a classPattern has been specified
				if ((classPattern.isSpecified()) && ArchiveUtil.isArchive(entryName)){
					Archive archiveObj = new Archive(file, entryBytes);
					addInstrumentationToArchive(archiveObj);
					if (archiveObj.isModified()){
						modified = true;
						entryBytes = archiveObj.getBytes();
						outputEntry.setTime(System.currentTimeMillis());
					}
				}else if (ArchiveUtil.isClass(entry) && classPattern.matches(entryName)){
					try{
						// Instrument class
						ClassReader cr = new ClassReader(entryBytes);
						ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
						ClassInstrumenter cv = new ClassInstrumenter(projectData,
								cw, ignoreRegexes, ignoreBranchesRegexes,
								ignoreMethodAnnotations, ignoreTrivial);
						cr.accept(cv, 0);

						// If class was instrumented, get bytes that define the
						// class
						if (cv.isInstrumented()){
							log.debug("Putting instrumented entry: "
									+ entry.getName());
							entryBytes = cw.toByteArray();
							modified = true;
							outputEntry.setTime(System.currentTimeMillis());
						}
					}catch (Throwable t){
						if (entry.getName().endsWith("_Stub.class")){
							//no big deal - it is probably an RMI stub, and they don't need to be instrumented
							log.debug("Problems instrumenting archive entry: " + entry.getName(), t);
						}else{
							log.warn("Problems instrumenting archive entry: " + entry.getName(), t);
						}
					}
				}

				// Add entry to the output
				output.write(entryBytes);
				output.closeEntry();
				archive.closeEntry();
			}catch (Exception e){
				log.warn("Problems with archive entry: " + entry.getName(), e);
			}catch (Throwable t){
				log.warn("Problems with archive entry: " + entry.getName(), t);
			}
			output.flush();
		}
		return modified;
	}

	private void addInstrumentationToArchive(Archive archive) throws Throwable{
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try{
			in = archive.getInputStream();
			out = new ByteArrayOutputStream();
			boolean modified = addInstrumentationToArchive(archive.getCoberturaFile(), in, out);

			if (modified){
				out.flush();
				byte[] bytes = out.toByteArray();
				archive.setModifiedBytes(bytes);
			}
		}finally{
			in = IOUtil.closeInputStream(in);
			out = (ByteArrayOutputStream)IOUtil.closeOutputStream(out);
		}
	}

	private void addInstrumentationToArchive(CoberturaFile archive) throws Throwable{
		log.debug("Instrumenting archive " + archive.getAbsolutePath());

		File outputFile = null;
		ZipInputStream input = null;
		ZipOutputStream output = null;
		boolean modified = false;
		try{
			// Open archive
			try{
				input = new ZipInputStream(new FileInputStream(archive));
			}catch (FileNotFoundException e){
				log.warn("Cannot open archive file: "
						+ archive.getAbsolutePath(), e);
				return;
			}

			// Open output archive
			try{
				// check if destination folder is set
				if (destinationDirectory != null){
					// if so, create output file in it
					outputFile = new File(destinationDirectory, archive.getPathname());
				}else{
					// otherwise create output file in temporary location
					outputFile = File.createTempFile(
							"CoberturaInstrumentedArchive", "jar");
					outputFile.deleteOnExit();
				}
				output = new ZipOutputStream(new FileOutputStream(outputFile));
			}catch (IOException e){
				log.warn("Cannot open file for instrumented archive: "
						+ archive.getAbsolutePath(), e);
				return;
			}

			// Instrument classes in archive
			try{
				modified = addInstrumentationToArchive(archive, input, output);
			}catch (Throwable e){
				log.warn("Cannot instrument archive: "
						+ archive.getAbsolutePath(), e);
				return;
			}
		}finally{
			input = (ZipInputStream)IOUtil.closeInputStream(input);
			output = (ZipOutputStream)IOUtil.closeOutputStream(output);
		}

		// If destination folder was not set, overwrite orginal archive with
		// instrumented one
		if (modified && (destinationDirectory == null)){
			try{
				log.debug("Moving " + outputFile.getAbsolutePath() + " to "
						+ archive.getAbsolutePath());
				IOUtil.moveFile(outputFile, archive);
			}catch (IOException e){
				log.warn("Cannot instrument archive: "
						+ archive.getAbsolutePath(), e);
				return;
			}
		}
		if ((destinationDirectory != null) && (!modified)){
			outputFile.delete();
		}
	}

	private void addInstrumentationToSingleClass(File file) throws Throwable{
		log.debug("Instrumenting class " + file.getAbsolutePath());

		InputStream inputStream = null;
		ClassWriter cw;
		ClassInstrumenter cv;
		try{
			inputStream = new FileInputStream(file);
			ClassReader cr = new ClassReader(inputStream);
			cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cv = new ClassInstrumenter(projectData, cw, ignoreRegexes, ignoreBranchesRegexes,
	                   ignoreMethodAnnotations, ignoreTrivial);
			cr.accept(cv, 0);
		}catch (Throwable t){
			log.warn("Unable to instrument file " + file.getAbsolutePath(),t);
			return;
		}finally{
			inputStream = IOUtil.closeInputStream(inputStream);
		}

		OutputStream outputStream = null;
		try{
			if (cv.isInstrumented()){
				// If destinationDirectory is null, then overwrite
				// the original, uninstrumented file.
				File outputFile;
				if (destinationDirectory == null)
					outputFile = file;
				else
					outputFile = new File(destinationDirectory, cv
							.getClassName().replace('.', File.separatorChar)
							+ ".class");

				File parentFile = outputFile.getParentFile();
				if (parentFile != null){
					parentFile.mkdirs();
				}

				byte[] instrumentedClass = cw.toByteArray();
				outputStream = new FileOutputStream(outputFile);
				outputStream.write(instrumentedClass);
			}
		}catch (Throwable t){
			log.warn("Unable to instrument file " + file.getAbsolutePath(), t);
			return;
		}finally{
			outputStream = IOUtil.closeOutputStream(outputStream);
		}
	}

	// TODO: Don't attempt to instrument a file if the outputFile already
	//       exists and is newer than the input file, and the output and
	//       input file are in different locations?
	private void addInstrumentation(CoberturaFile coberturaFile) throws Throwable{
		if (coberturaFile.isClass() && classPattern.matches(coberturaFile.getPathname())){
			addInstrumentationToSingleClass(coberturaFile);
		}else if (coberturaFile.isDirectory()){
			String[] contents = coberturaFile.list();
			for (int i = 0; i < contents.length; i++){
				File relativeFile = new File(coberturaFile.getPathname(), contents[i]);
				CoberturaFile relativeCoberturaFile = new CoberturaFile(coberturaFile.getBaseDir(),
						relativeFile.toString());
				//recursion!
				addInstrumentation(relativeCoberturaFile);
			}
		}
	}

}
