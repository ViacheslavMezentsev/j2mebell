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
package net.sf.microlog.core;

/**
 * A <code>String</code> utility class.
 * 
 * @author Johan Karlsson
 * 
 */
public class StringUtil {

	/**
	 * Extract a partial name of a class name, starting from the end.
	 * 
	 * @param string
	 *            the name of the class
	 * @param parts
	 *            the number of parts of the clas name that you want to be
	 *            returned.
	 * 
	 * @return the partial class name.
	 */
	public static String extractPartialClassName(String string, int parts) {
		String partialCategoryName = string;

		int nofDots = 0;
		int dotIndex = string.lastIndexOf('.');
		if (dotIndex != -1) {
			nofDots++;
		}

		while (nofDots < parts && dotIndex > -1) {
			dotIndex = string.lastIndexOf('.', dotIndex - 1);

			if (dotIndex != -1) {
				nofDots++;
			}
		}

		if (dotIndex > -1 && nofDots <= parts) {
			partialCategoryName = string.substring(dotIndex + 1);
		}

		return partialCategoryName;
	}

}
