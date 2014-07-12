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

package net.sf.microlog.core.format.command;

import net.sf.microlog.core.Level;

/**
 * This command does not do any formatting. It just stores the
 * <code>preFormatString</code> and returns it.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 */
public class NoFormatCommand implements FormatCommandInterface {

	private String preFormatString = "";

	/**
	 * @see net.sf.microlog.core.format.command.FormatCommandInterface#init(String)
	 */
	public void init(String preFormatString) {
		this.preFormatString = preFormatString;
	}

	/**
	 * Convert, i.e. return the <code>preFormatString</code>.
	 * 
	 * @see net.sf.microlog.core.format.command.FormatCommandInterface#execute(String,
	 *      String, long, net.sf.microlog.core.Level, java.lang.Object, java.lang.Throwable)
	 */
	public String execute(String clientID, String name, long time,
			Level level, Object message, Throwable throwable) {
		return preFormatString;
	}

}
