/*
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2005 Grzegorz Lukasik
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Grzegorz Lukasik
 */
public class IOUtilTest{

	private static final byte[] emptyByteArray = new byte[] {};

	private static final byte[] singletonByteArray = new byte[] { 7 };

	private static final byte[] smallByteArray = new byte[] { 1, 0, 2, -128, 127 };

	@Test
	public void testMoveFile() throws IOException{
		// Move file if destination does not exist
		File srcFile = createFileWithData(smallByteArray);
		File destFile = createFileWithData(emptyByteArray);
		destFile.delete();
		assertTrue(!destFile.isFile());
		IOUtil.moveFile(srcFile, destFile);
		assertTrue(destFile.isFile());
		InputStream in = new FileInputStream(destFile);
		for (int i = 0; i < smallByteArray.length; i++)
			assertEquals(smallByteArray[i], (byte)in.read());
		assertEquals(-1, in.read());
		in.close();

		// Move file if destination exists
		srcFile = createFileWithData(singletonByteArray);
		destFile = createFileWithData(smallByteArray);
		IOUtil.moveFile(srcFile, destFile);
		assertTrue(destFile.isFile());
		in = new FileInputStream(destFile);
		for (int i = 0; i < singletonByteArray.length; i++)
            Assert.assertEquals(singletonByteArray[i], (byte) in.read());
		Assert.assertEquals(-1, in.read());
		in.close();
	}

    @Test(expected = NullPointerException.class)
	public void testMoveFile_nullPointerIfDestinationFileIsNull() throws IOException{
		// Pass null values
		File srcFile = createFileWithData(smallByteArray);
		IOUtil.moveFile(srcFile, null);
	}

    @Test(expected = NullPointerException.class)
	public void testMoveFile_nullPointerIfSourceFileIsNull() throws IOException{
		// Pass null values
		File destFile = createFileWithData(smallByteArray);
		IOUtil.moveFile(null, destFile);
	}

    @Test
	public void testCopyStream() throws IOException{
		ByteArrayInputStream in = new ByteArrayInputStream(smallByteArray);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtil.copyStream(in, out);
		assertArrayEquals(smallByteArray, out.toByteArray());

		in = new ByteArrayInputStream(singletonByteArray);
		out = new ByteArrayOutputStream();
		IOUtil.copyStream(in, out);
		assertArrayEquals(singletonByteArray, out.toByteArray());

		in = new ByteArrayInputStream(emptyByteArray);
		out = new ByteArrayOutputStream();
		IOUtil.copyStream(in, out);
		assertArrayEquals(emptyByteArray, out.toByteArray());

		byte[] bigArray = generateBigByteArray();
		in = new ByteArrayInputStream(bigArray);
		out = new ByteArrayOutputStream();
		IOUtil.copyStream(in, out);
		assertArrayEquals(bigArray, out.toByteArray());
	}

    @Test(expected = NullPointerException.class)
	public void testCopyStream_nullPointerExceptionIfSourceFileIsNull() throws IOException{
		IOUtil.copyStream(null, new ByteArrayOutputStream());
	}

    @Test(expected = NullPointerException.class)
	public void testCopyStream_nullPointerExceptionIfDestinationFileIsNull() throws IOException{
		IOUtil.copyStream(new ByteArrayInputStream(generateBigByteArray()), null);
	}

    @Test(expected = NullPointerException.class)
	public void testFillByteArrayFromInputStream() throws IOException{
		byte[] out = IOUtil
				.createByteArrayFromInputStream(new ByteArrayInputStream(
						smallByteArray));
		assertArrayEquals(smallByteArray, out);

		out = IOUtil.createByteArrayFromInputStream(new ByteArrayInputStream(
				emptyByteArray));
		assertArrayEquals(emptyByteArray, out);

		out = IOUtil.createByteArrayFromInputStream(new ByteArrayInputStream(
				singletonByteArray));
		assertArrayEquals(singletonByteArray, out);

		byte[] bigArray = generateBigByteArray();
		out = IOUtil.createByteArrayFromInputStream(new ByteArrayInputStream(
				bigArray));
		assertArrayEquals(bigArray, out);
        IOUtil.createByteArrayFromInputStream(null);
	}

    /*   Aux methods   */
	private byte[] generateBigByteArray(){
		byte[] bigArray = new byte[1000000];
		for (int i = 0; i < bigArray.length; i++){
			bigArray[i] = (byte)i;
		}
		return bigArray;
	}

    private File createFileWithData(byte[] data) throws IOException{
		File file = File.createTempFile("IOUtilTest", ".txt");
		file.deleteOnExit();
		OutputStream src = new FileOutputStream(file);
		src.write(data);
		src.close();
		return file;
	}

}
