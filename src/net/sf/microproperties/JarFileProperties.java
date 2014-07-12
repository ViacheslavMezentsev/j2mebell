/*
 * Copyright 2008 The Microproperties project @sourceforge.net
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

package net.sf.microproperties;

import java.io.IOException;
import java.io.InputStream;

/**
 * The <code>JarFilesProperties</code> class is used for reading properties
 * from a file contained in the JAR file. The class overloads the
 * <code>load()</code> method that takes a filename as input.
 * 
 * Note that you could not save the properties into a file that is included in a
 * JAR file.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * 
 */
public class JarFileProperties extends Properties {
	
	private static final long serialVersionUID = -2691499438729081809L;

	/**
	 * Load the specified file from the JAR file.
	 * 
	 * @param fileName the filename 
	 */
	public void load(String fileName) throws IOException{

		InputStream inputStream = null;

		if (fileName != null && fileName.length() > 0) {
			inputStream = this.getClass().getResourceAsStream(fileName);
		}

		if (inputStream != null) {
			load(inputStream);
		} else {
			throw new IOException("Could find file in jar: "+fileName);
		}
	}

}
