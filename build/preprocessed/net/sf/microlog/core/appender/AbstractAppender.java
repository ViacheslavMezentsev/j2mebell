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
package net.sf.microlog.core.appender;

import java.io.IOException;

import net.sf.microlog.core.Appender;
import net.sf.microlog.core.Formatter;
import net.sf.microlog.core.Level;
import net.sf.microlog.core.format.SimpleFormatter;

/**
 * This is the abstract super class of all the appenders.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * @since 0.1
 */
public abstract class AbstractAppender implements Appender {

	/**
	 * This is the default formatter used by all subclasses. The subclass can
	 * change this as suitable.
	 */
	protected Formatter formatter = new SimpleFormatter();

	/**
	 * The logOpen shows whether the log is open or not. The implementing
	 * subclass is responsible for setting the correct value.
	 */
	protected boolean logOpen;

	/**
	 * Set the <code>Formatter</code> object that is used for formatting the
	 * output.
	 * 
	 * @see net.sf.microlog.core.Appender#setFormatter(net.sf.microlog.core.Formatter)
	 * @throws IllegalArgumentException
	 *             if the <code>formatter</code> is <code>null</code>.
	 */
	public final void setFormatter(Formatter formatter)
			throws IllegalArgumentException {
		if (formatter == null) {
			throw new IllegalArgumentException(
					"The formatter must not be null.");
		}

		this.formatter = formatter;

	}

	/**
	 * Get the <code>Formatter</code> object that is used for formatting the
	 * output.
	 * 
	 * @see net.sf.microlog.core.Appender#getFormatter()
	 */
	public final Formatter getFormatter() {
		return formatter;
	}

	/**
	 * Check if the log is open.
	 * 
	 * @return <code>true</code> if the log is open, otherwise
	 *         <code>false</code> is returned.
	 */
	public boolean isLogOpen() {
		return logOpen;
	}

	/**
	 * Do the logging.
	 * 
	 * @param level
	 *            the level at which the logging shall be done.
	 * @param message
	 *            the message to log.
	 * @param t
	 *            the exception to log.
	 */
	public abstract void doLog(String clientID, String name, long time,
			Level level, Object message, Throwable t);

	/**
	 * Clear the log.
	 * 
	 * @see net.sf.microlog.core.Appender#clear()
	 */
	public abstract void clear();

	/**
	 * Close the log.
	 * 
	 * @see net.sf.microlog.core.Appender#close()
	 */
	public abstract void close() throws IOException;

	/**
	 * Open the log.
	 * 
	 * @see net.sf.microlog.core.Appender#open()
	 */
	public abstract void open() throws IOException;

	/**
	 * @see Appender#getPropertyNames()
	 */
	public String[] getPropertyNames() {
		// Default implementation to be used when no properties are available.
		return null;
	}

	public void setProperty(String name, String value)
			throws IllegalArgumentException {
		if (name == null || name.length() == 0 || value == null
				|| value.length() == 0) {
			throw new IllegalArgumentException(
					"Name and value must not be null and a length > 0");
		}
		
		// The actual property is not set, this must be done in sub-classes.
	}

}
