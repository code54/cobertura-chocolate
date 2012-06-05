/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2003 jcoverage ltd.
 * Copyright (C) 2005 Mark Doliner
 * Copyright (C) 2005 Grzegorz Lukasik
 * Copyright (C) 2005 Björn Beskow
 * Copyright (C) 2006 John Lewis
 * Copyright (C) 2009 Chris van Es
 * Copyright (C) 2009 Ed Randall
 * Copyright (C) 2010 Charlie Squires
 * Copyright (C) 2010 Piotr Tabor
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

package net.sourceforge.cobertura.coveragedata;

import net.sourceforge.cobertura.util.FileLocker;
import org.apache.log4j.Logger;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.io.File;
import java.util.*;

@Root(name="projectdata")
public class ProjectData extends CoverageDataContainer<String> implements HasBeenInstrumented{

    private static final Logger log = Logger.getLogger(ProjectData.class);

	private static final long serialVersionUID = 6;

    @ElementMap(entry="children", key="key", valueType = CoverageData.class,
             keyType = String.class, attribute=true, inline=true, required = false)
    private Map<String, CoverageData> children;

	/** This collection is used for quicker access to the list of classes. */
    @ElementMap(entry="classeslookup", key="key", valueType = ClassData.class,
             keyType = String.class, attribute=true, inline=true, required = false)
	private Map<String, ClassData> classes = new HashMap<String, ClassData>();

    /*   This is needed for xml serialization   */
    public ProjectData(){
         children = new HashMap<String, CoverageData>();
    }

	public void addClassData(ClassData classData){
		lock.lock();
		try{
			String packageName = classData.getPackageName();
			PackageData packageData = (PackageData)getChildren().get(packageName);
			if (packageData == null){
				packageData = new PackageData(packageName);
				// Each key is a package name, stored as an String object.
				// Each value is information about the package, stored as a PackageData object.
				this.getChildren().put(packageName, packageData);
			}
			packageData.addClassData(classData);
			this.classes.put(classData.getName(), classData);
		}finally{
			lock.unlock();
		}
	}

	public ClassData getClassData(String name){
		lock.lock();
		try{
			return this.classes.get(name);
		}finally{
			lock.unlock();
		}
	}

	public ClassData getOrCreateClassData(String name){
		lock.lock();
		try{
			ClassData classData = this.classes.get(name);
			if (classData == null){
				classData = new ClassData(name);
				addClassData(classData);
			}
			return classData;
		}finally{
			lock.unlock();
		}
	}

	public Collection getClasses(){
		lock.lock();
		try{
			return this.classes.values();
		}finally{
			lock.unlock();
		}
	}

	public int getNumberOfClasses(){
		lock.lock();
		try{
			return this.classes.size();
		}finally{
			lock.unlock();
		}
	}

	public int getNumberOfSourceFiles(){
		return getSourceFiles().size();
	}

	public SortedSet getPackages(){
		lock.lock();
		try{
			return new TreeSet(getChildrenValues());
		}finally{
			lock.unlock();
		}
	}

	public Collection getSourceFiles(){
		SortedSet sourceFileDatas = new TreeSet();
		lock.lock();
		try{
			Iterator iter = getChildrenValues().iterator();
			while (iter.hasNext()){
				PackageData packageData = (PackageData)iter.next();
				sourceFileDatas.addAll(packageData.getSourceFiles());
			}
		}finally{
			lock.unlock();
		}
		return sourceFileDatas;
	}

	/**
	 * Get all subpackages of the given package. Includes also specified package if
	 * it exists.
	 *
	 * @param packageName The package name to find subpackages for.
	 *        For example, "com.example"
	 * @return A collection containing PackageData objects.  Each one
	 *         has a name beginning with the given packageName.  For
	 *         example: "com.example.io", "com.example.io.internal"
	 */
	public SortedSet getSubPackages(String packageName){
		SortedSet subPackages = new TreeSet();
		lock.lock();
		try{
			Iterator iter = getChildrenValues().iterator();
			while (iter.hasNext()){
				PackageData packageData = (PackageData)iter.next();
				if (packageData.getName().startsWith(packageName + ".")
                        || packageData.getName().equals(packageName)
                        || packageName.equals(""))
					subPackages.add(packageData);
			}
		}finally{
			lock.unlock();
		}
		return subPackages;
	}

	public void merge(CoverageData coverageData){
		if (coverageData == null) {
			return;
		}
		ProjectData projectData = (ProjectData)coverageData;
		getBothLocks(projectData);
		try{
			super.merge(coverageData);
	
			for (Iterator<String> iter = projectData.classes.keySet().iterator(); iter.hasNext();){
				String key = iter.next();
				if (!this.classes.containsKey(key)){
					this.classes.put(key, projectData.classes.get(key));
				}
			}
		}finally{
			lock.unlock();
			projectData.lock.unlock();
		}
	}

    @Override
    public Map<String, CoverageData> getChildren() {
        return children;
    }

    /*
     * We provide a project name,
     * so that different project reports can be identified
     */
    @Override
    public String getName() {
        StringBuilder builder = new StringBuilder();
        Iterator<String>it = this.classes.keySet().iterator();
        while(it.hasNext()){
            builder.append(it.next());
        }
        return ""+builder.toString().hashCode();
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("\n*"+getClass()+"\n");
        Iterator<CoverageData>it = children.values().iterator();
        while(it.hasNext()){
            builder.append("**"+it.next()+"\n");
        }
        return builder.toString();
    }
}
