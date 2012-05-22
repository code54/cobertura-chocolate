package net.sourceforge.cobertura.util;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static net.sourceforge.cobertura.util.ArchiveUtil.getFiles;

//Some code extracted from http://kalanir.blogspot.com.ar/2010/01/how-to-write-custom-class-loader-to.html

/**
 * This class loader intentionally first looks
 * for the file version defined at the specified directory,
 * and if not found, looks if the system class loader can provide it.
 */
public class DirectoryClassLoader extends ClassLoader {
    private static final Logger log = Logger.getLogger(DirectoryClassLoader.class);

    private File directory; //Directory we want to extract .class files from
    private Hashtable classes; //used to cache already defined classes

    public DirectoryClassLoader(File directory) {
        //calls the parent class loader's constructor
        super(DirectoryClassLoader.class.getClassLoader());
        classes = new Hashtable();
        this.directory = directory;
    }

    public Class loadClass(String className) throws ClassNotFoundException {
        return findClass(className);
    }

    //TODO may improve by dinamically loading modified .class files!
    public Class findClass(String className) {
        byte classByte[];
        Class result = null;

        //checks cached classes
        result = (Class) classes.get(className);
        if (result != null) {
            return result;
        }

        try {
            File classFile = new File(directory,classNameToFile(className));
            if(classFile.exists()){
                log.info("will load "+classFile);
                InputStream is = new FileInputStream(classFile);
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                int nextValue = is.read();
                while (-1 != nextValue) {
                    byteStream.write(nextValue);
                    nextValue = is.read();
                }
                classByte = byteStream.toByteArray();
                result = defineClass(className, classByte, 0, classByte.length, null);
                classes.put(className, result);
                return result;
            }
        } catch (Exception e) {}
        try {
            log.info("Did not find class in directory. Delegating to system classloader...");
            return findSystemClass(className);
        } catch (Exception ex) {return null;}
    }

    private String classNameToFile(String className){
        String filename = "./"+className.replace(".","/") + ".class";
        log.info("Looking for class: "+className+"; at classFile: "+filename);
        return filename;
    }
}
