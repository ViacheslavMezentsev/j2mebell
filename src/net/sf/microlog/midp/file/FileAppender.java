/*
 * Copyright 2008 The Microlog project @sourceforge.net
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.microlog.midp.file;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import net.sf.microlog.core.IOUtil;
import net.sf.microlog.core.Level;
import net.sf.microlog.core.appender.AbstractAppender;

/**
 * A class that logs to a file. The class uses the FileConnection API from
 * JSR-75.
 * <p>
 * 
 * The file name can be passed with the property
 * <code>microlog.appender.FileAppender.filename</code>.
 * 
 * The directory can be passed with the property
 * <code>microlog.appender.FileAppender.directory</code>
 * 
 * The directory is possible to set with the <code>setDirectory()</code> method.
 * If this is not set the default directory is used. The default directory is
 * fetched by calling <code>FileSystemRegistry.listRoots()</code>, where the
 * first root is used.
 * 
 * @author Johan Karlsson
 * @author Karsten Ohme
 * @since 0.1
 */
public class FileAppender extends AbstractAppender {

	/**
	 * The protocol to be used for opening a <code>FileConnection</code> object.
	 */
	public static final String FILE_PROTOCOL = "file:///";
	
	public static final String FILE_NAME_PROPERTY = "filename";

	public static final String LINE_SEPARATOR_PROPERTY = "lineseparator";

	public static final String APPEND_PROPERTY = "append";

	/**
	 * The default log filename.
	 */
	public static final String DEFAULT_FILENAME = "microlog.txt";

	public static final String DEFAULT_LINE_SEPARATOR = "\r\n";

	/**
	 * The default buffer size for <code>StringBuffer</code> objects.
	 */
	public static final int DEFAULT_STRING_BUFFER_SIZE = 256;

	public static final String[] PROPERTY_NAMES = { FILE_NAME_PROPERTY,
			LINE_SEPARATOR_PROPERTY, APPEND_PROPERTY };

	protected String lineSeparator = System.getProperty("line.separator");

	protected String directory;

	protected String fileName = DEFAULT_FILENAME;

	protected boolean append = false;

	protected OutputStream outputStream;

	protected boolean fileConnectionIsSet = false;


	/**
	 * The <code>FileConnection</code> for accessing the log file.
	 */
	protected FileConnection fileConnection;

	public FileAppender() {
		super();
	}
	
	/**
	 * @see net.sf.microlog.core.appender.AbstractAppender#open()
	 */
	public synchronized void open() throws IOException {

		if (!fileConnectionIsSet) {
			String fileURI = createFileURI();
			createFile(fileURI);

			fileConnectionIsSet = true;
		}

		openOutputStream();

		logOpen = true;
	}

	/**
	 * Create the <code>fileURI</code> to be used as a log file.
	 * 
	 * @return the <code>fileURI</code>.
	 */
	protected String createFileURI() {
		StringBuffer fileURIStringBuffer = new StringBuffer(
				DEFAULT_STRING_BUFFER_SIZE);
//		fileURIStringBuffer.append(FILE_PROTOCOL);

		boolean fileNameContainsPath = (fileName.indexOf('/') != -1)
				|| (fileName.indexOf('\\') != -1);

		if (!fileNameContainsPath) {
			setDirectoryAsFirstRoot();
		}

		if (directory != null) {
			fileURIStringBuffer.append(directory);
		}

		fileURIStringBuffer.append(fileName);
		String fileURI = fileURIStringBuffer.toString();
		return fileURI;
	}

	/**
	 * Set the <code>directory</code> member variable to the first directory
	 * found by <code>FileSystemRegistry.listRoots()</code>.
	 */
	private void setDirectoryAsFirstRoot() {
		try {
			Enumeration rootsEnum = FileSystemRegistry.listRoots();

			if (rootsEnum.hasMoreElements()) {
				directory = (String) rootsEnum.nextElement();
			} else {
				System.err.println("No root directory is found.");
			}

		} catch (SecurityException e) {
			System.err.println("Not allowed to list the roots. " + e);
		}
	}

	/**
	 * Create the file from the specified <code>fileURI</code>. If the file
	 * already exists, no file is created.
	 * 
	 * @param fileURI
	 *            the <code>fileURI</code> to use for creation.
	 * @throws IOException
	 *             if the creation fails.
	 */
	protected void createFile(String fileURI) throws IOException {
		fileConnection = (FileConnection) Connector.open(fileURI,
				Connector.READ_WRITE);
		if (!fileConnection.exists()) {
			fileConnection.create();
		}

		System.out.println("The created file is " + fileConnection.getURL());
	}

	/**
	 * Open the <code>OutputStream</code> for the created file. The member
	 * variable <code>outputStream</code> shall be set after this method has
	 * been called.
	 * 
	 * @throws IOException
	 */
	protected synchronized void openOutputStream() throws IOException {
		if (fileConnectionIsSet) {
			if (append) {
				outputStream = fileConnection.openOutputStream(fileConnection
						.fileSize());
			} else {
				outputStream = fileConnection.openOutputStream(0);
			}
			logOpen = true;
		}
	}

	/**
	 * @see net.sf.microlog.core.appender.AbstractAppender#clear()
	 */
	public synchronized void clear() {
		if (fileConnection != null && fileConnection.isOpen()) {
			try {
				fileConnection.truncate(0);
			} catch (IOException e) {
				System.err.println("Failed to clear the log " + e);
			}
		}
	}

	/**
	 * @see net.sf.microlog.core.appender.AbstractAppender#close()
	 */
	public synchronized void close() throws IOException {
		if (logOpen) {
			IOUtil.closeSilent(outputStream);
			IOUtil.closeSilent(fileConnection);
			logOpen = false;
		}
	}

	/**
	 * Get the size of the log. This is equivalent of calling
	 * <code>fileSize()</code> on the created <code>FileConnection</code>.
	 * 
	 * @return the size of the log.
	 */
	public synchronized long getLogSize() {

		long logSize = SIZE_UNDEFINED;

		if (logOpen) {
			try {
				outputStream.flush();
				logSize = fileConnection.fileSize();
			} catch (IOException e) {
				System.err.println("Failed to get the logsize " + e);
			}
		}

		return logSize;
	}

	/**
	 * Get the total size. The total size is fetched by calling
	 * <code>totalSize()</code> on the created <code>FileConnection</code>.
	 * 
	 * @return the total size of the file system the connection's target resides
	 *         on.
	 */
	public synchronized long totalSize() {
		long totalSize = SIZE_UNDEFINED;

		if (logOpen) {
			try {
				outputStream.flush();
				totalSize = fileConnection.totalSize();
			} catch (IOException e) {
				System.err.println("Failed to get the total size." + e);
			}
		}

		return totalSize;
	}

	/**
	 * Get the used size. The total size is fetched by calling
	 * <code>usedSize()</code> on the created <code>FileConnection</code>.
	 * 
	 * @return Determines the used memory of a file system the connection's
	 *         target resides on. This may only be an estimate and may vary
	 *         based on platform-specific file system blocking and metadata
	 *         information.
	 */
	public synchronized long usedSize() {
		long usedSize = SIZE_UNDEFINED;

		if (logOpen) {
			try {
				outputStream.flush();
				usedSize = fileConnection.usedSize();
			} catch (IOException e) {
				System.err.println("Failed to get the total size. " + e);
			}
		}

		return usedSize;
	}

	/**
	 * Get the URL of the file that is opened, i.e. a call is made to
	 * <code>getURL()</code> on the opened <code>FileConnection</code>.
	 * 
	 * @return the URL of the opened connection. If no connection is opened, an
	 *         empty <code>String</code> is returned.
	 */
	public synchronized String getURL() {
		String url = "";

		if (fileConnection != null) {
			url = fileConnection.getURL();
		}

		return url;
	}

	/**
	 * @param fileConnection
	 *            the fileConnection to set
	 */
	synchronized void setFileConnection(FileConnection fileConnection) {
		this.fileConnection = fileConnection;
		if (this.fileConnection != null) {
			fileConnectionIsSet = true;
		} else {
			fileConnectionIsSet = false;
		}
	}

    private int[] m_arPhoneCharDiv = {(int)'А' - 192, (int)'Ё' - 168, (int)'ё' - 184};
    public String ToCP1251(String strIn)
    {
        byte[] arOut=new byte[strIn.length()];
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<strIn.length();++i)
        {
            int ch=(int)strIn.charAt(i);
            switch((char)ch)
            {
                case 'Ё':
                    ch-=m_arPhoneCharDiv[1];
                    break;
                case 'ё':
                    ch-=m_arPhoneCharDiv[2];
                    break;
                default:
                    if(ch>=192)
                        ch-=m_arPhoneCharDiv[0];
            }
            arOut[i]=(byte)ch;
        }
        String str=new String(arOut);
        return  str;
    }

	/**
	 * @see net.sf.microlog.core.Appender#doLog(String, String, long, Level,
	 *      Object, Throwable)
	 */
	public synchronized void doLog(String clientID, String name, long time,
			Level level, Object message, Throwable t) {

		if (logOpen && formatter != null) {
//			String logString = formatter.format(clientID, name, time, level,
//					message, t);
			String logString = ToCP1251( formatter.format(clientID, name, time, level,
					message, t) );
                    try {
				byte[] stringData = logString.getBytes();
				outputStream.write(stringData);
				if (lineSeparator == null) {
					lineSeparator = DEFAULT_LINE_SEPARATOR;
				}
				outputStream.write(lineSeparator.getBytes());
				outputStream.flush();
			} catch (IOException e) {
				System.err.println("Failed to log message " + e);
			}
		}
	}

	/**
	 * Get the filename of the logfile.
	 * 
	 * @return the fileName
	 */
	public synchronized String getFileName() {
		return fileName;
	}

	/**
	 * Set the filename of the logfile. It could be the full path of the file,
	 * like "C:/other/microlog.txt" or only the filename. If only the filename
	 * is specified, the first directory of the <code>Enumeration</code> from a
	 * call to <code>FileSystemRegistry.listRoots()</code> is used.
	 * 
	 * Note that changing this after the logfile has been opened has no effect.
	 * 
	 * @param fileName
	 *            the fileName to set
	 * @throws IllegalArgumentException
	 *             if the filename is null.
	 */
	public synchronized void setFileName(String fileName)
			throws IllegalArgumentException {
		if (fileName == null) {
			throw new IllegalArgumentException("The filename must not be null.");
		}

		this.fileName = fileName;
	}

	/**
	 * Get the line separator.
	 * 
	 * @return the lineSeparator
	 */
	public synchronized String getLineSeparator() {
		return lineSeparator;
	}

	/**
	 * Set the line separator.
	 * 
	 * @param lineSeparator
	 *            the lineSeparator to set
	 */
	public synchronized void setLineSeparator(String lineSeparator)
			throws IllegalArgumentException {
		if (lineSeparator == null) {
			throw new IllegalArgumentException(
					"The line separator must not be null.");
		}

		this.lineSeparator = lineSeparator;
	}

	/**
	 * Set if the logging shall be appended or if the logging shall start all
	 * over again when starting to log again.
	 * 
	 * @param append
	 *            <code>true</code> if the log shall be appended,
	 *            <code>false</code> if the logging shall start at byte 0.
	 */
	public void setAppend(boolean append) {
		this.append = append;
	}

	public String[] getPropertyNames() {
		return PROPERTY_NAMES;
	}

	public void setProperty(String name, String value)
			throws IllegalArgumentException {
		super.setProperty(name, value);

		if (name.equals(FILE_NAME_PROPERTY)) {
			setFileName(value);
		} else if (name.equals(LINE_SEPARATOR_PROPERTY)) {
			setLineSeparator(value);
		} else if (name.equals(APPEND_PROPERTY)) {
			if (value.equals("true") || value.equals("on")) {
				setAppend(true);
			}
		}
	}

}
