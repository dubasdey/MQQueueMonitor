package org.erc.qmm.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The Class FileUtils.
 */
public abstract class FileUtils {
	
	/**
	 * Copy.
	 *
	 * @param source the source
	 * @param dest the dest
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void copy(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	
	/**
	 * Store.
	 *
	 * @param file the file
	 * @param content the content
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void store(File file,String content) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
		bw.write(content);
		bw.close();
	}
	
}
