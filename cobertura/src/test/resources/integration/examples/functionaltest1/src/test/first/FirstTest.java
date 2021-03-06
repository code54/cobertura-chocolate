/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2006 John Lewis
 *
 * Note: This file is dual licensed under the GPL and the Apache
 * Source License (so that it can be used from both the main
 * Cobertura classes and the ant tasks).
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

package test.first;

//import org.junit.Test;
import junit.framework.TestCase;

/**
 * Simple class used in functional testing.
 * 
 * @author John Lewis
 */
public class FirstTest extends TestCase{

	/**
	 * Call the methods called "call"
	 */
	//@org.junit.Test //if we use the annotation, it will cause a  java.lang.reflect.GenericSignatureFormatError if run from a JUnit4 test
	public void testMethod(){
		test.first.A a = new test.first.A();
		a.call();

		test.first.B b = new test.first.B();
		b.call();

		test.second.A a2 = new test.second.A();
		a2.call();

		test.second.B b2 = new test.second.B();
		b2.call();
	}

}
