package net.sf.microlog.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;

/**
 * Some I/O utilities to be used in a Java ME CLDC environment.
 * 
 * @author Johan Karlsson
 *
 */
public class IOUtil {

	/**
	 * Close the <code>InputStream</code> silent.
	 * 
	 * @param inputStream
	 *            the <code>InputStream</code> to close.
	 */
	public static void closeSilent(InputStream inputStream) {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Close the <code>OutputStream</code> silent.
	 * 
	 * @param inputStream
	 *            the <code>OutputStream</code> to close.
	 */
	public static void closeSilent(OutputStream outputStream) {
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Close the <code>Connection</code> silent.
	 * 
	 * @param connection
	 *            the <code>Connection</code> to close.
	 */
	public static void closeSilent(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
