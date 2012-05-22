/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2005 Grzegorz Lukasik
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
package net.sourceforge.cobertura.util;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Grzegorz Lukasik
 */
public class CommandLineBuilderTest{

    private static final String someOption = "--someoption";
    private static final String optionValue = "optionValue";

    @Test
	public void testExample() throws Exception {
		CommandLineBuilder builder = new CommandLineBuilder();
		builder.addArg(someOption);
		builder.addArg(optionValue);
		builder.saveArgs();
		
		String[] args = 
		CommandLineBuilder.preprocessCommandLineArguments(
				new String[] { Constants.commandsfile, builder.getCommandLineFile()});

		Assert.assertEquals(someOption, args[0]);
		Assert.assertEquals(optionValue, args[1]);
		
		builder.dispose();
	}

    @Test
	public void testExample_2() throws Exception {
		CommandLineBuilder builder = new CommandLineBuilder();
		builder.addArg(someOption, optionValue);
		builder.saveArgs();
		
		String[] args = 
		CommandLineBuilder.preprocessCommandLineArguments(
				new String[] {Constants.commandsfile, builder.getCommandLineFile()});

		assertEquals(someOption, args[0]);
		assertEquals(optionValue, args[1]);
		
		builder.dispose();
	}

    @Test
	public void testManyOptions() throws Exception {
		String[] options = new String[100000];
		for( int i=0; i<options.length; i++) {
			options[i] = "myOption" + i;
		}

		String[] args = testArguments(options);
		assertArrayEquals(options, args);
	}

    @Test
	public void testVariousOptions() throws Exception {
		String[] options = { "hello", " one", "two ", "  three , ", "\"'xx", " ", "file .java", "f.java",
				"#@()39340*(@0$#&%^@#&4098353856_*(_@735/896_udsknbfdvzxvkasd DSFWBXfqw']][.,=---3\\]];",
				"null", "!@#$%^&*()_+-={}|[]\\:\";'<>?,./'"
		};
		String[] args = testArguments( options);
		assertArrayEquals(options, args);
	}

    @Test
	public void testEmptyOptions() throws Exception {
		String[] args = testArguments(new String[0]);
		assertArrayEquals(new String[0], args);
	}

    @Test(expected = NullPointerException.class)
	public void testInvalidArguments_nullPointerWhenNullArgs() throws Exception {
		new CommandLineBuilder().addArg(null);
	}

    @Test(expected = NullPointerException.class)
	public void testInvalidArguments_nullPointerWhenArgValueNull() throws Exception {
		new CommandLineBuilder().addArg( "someArgument", null);
	}

    @Test(expected = NullPointerException.class)
	public void testInvalidArguments_nullPointerWhenArgNameNull() throws Exception {
		new CommandLineBuilder().addArg( null, "someValue");
	}

    @Test(expected = NullPointerException.class)
	public void testInvalidArguments_nullPointerWhenPreprocessingNull() throws Exception {
		new CommandLineBuilder().preprocessCommandLineArguments(null);
	}

    @Test(expected = NullPointerException.class)
	public void testInvalidArguments_nullPointerWhenPreprocessingArgsWithValueNull() throws Exception {
		new CommandLineBuilder().preprocessCommandLineArguments(new String[] { "Hello", null });
	}

    @Test(expected = NullPointerException.class)
	public void testInvalidArguments_nullPointerWhenPreprocessingArgsWithLastValueNull() throws Exception {
		new CommandLineBuilder().preprocessCommandLineArguments(new String[]{Constants.commandsfile, "hello", null});
	}

    @Test(expected = IllegalArgumentException.class)
	public void testCommandsFileOption_illegalArgument() throws Exception {
		String[] args = { "Hello", "world" };
		String[] result = CommandLineBuilder.preprocessCommandLineArguments( args);
		assertSame(args, result);
		
		args = new String[]{ "Hello", Constants.commandsfile};
		CommandLineBuilder.preprocessCommandLineArguments( args);
	}

    @Test(expected = IOException.class)
	public void testCommandsFileOption_IOException() throws Exception {
		String[] args = { "Hello", "world" };
		String[] result = CommandLineBuilder.preprocessCommandLineArguments( args);
		assertSame(args, result);

        args = new String[]{ "Hello", Constants.commandsfile, "hello.cmd" };
		CommandLineBuilder.preprocessCommandLineArguments(args);
	}

    /*  Aux methods    */
    private String[] testArguments(String[] args) throws Exception {
		CommandLineBuilder builder = new CommandLineBuilder();
		for( int i=0; i<args.length; i++)
			builder.addArg( args[i]);
		builder.saveArgs();

		File cmdFile = new File(builder.getCommandLineFile());
		assertTrue(cmdFile.isAbsolute());
		assertTrue(cmdFile.isFile());

		String[] result =
		CommandLineBuilder.preprocessCommandLineArguments(
				new String[] {Constants.commandsfile, builder.getCommandLineFile()});
		builder.dispose();

		return result;
	}
}
