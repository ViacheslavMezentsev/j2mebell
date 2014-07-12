/*
 * Copyright 2009 The Microlog project @sourceforge.net
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
package net.sf.microlog.core.config;

import net.sf.microlog.core.Level;
import net.sf.microlog.core.Logger;

public interface LoggerRepository {

	/**
	 * Get the root <code>Logger</code> instance.
	 * 
	 * @return the root <code>Logger</code>.
	 */
	public Logger getRootLogger();

	/**
	 * Get the specified <code>Logger</code> instance. This method shall never
	 * return <code>null</code>.
	 * 
	 * @return a logger with the specified name.
	 */
	public Logger getLogger(String name);

	/**
	 * Set the level on the specified part of the logging hierarchy.
	 * 
	 * @param name
	 *            the name of the logging hierarchy to set the level on.
	 * @param level
	 *            the level to set.
	 */
	public void setLevel(String name, Level level);

	/**
	 * Get the effective level for the specified logger. Note that the level of
	 * the current logger is not checked, since this level could be get from the
	 * logger itself.
	 * 
	 * @return the effective <code>Level</code>
	 */
	public Level getEffectiveLevel(Logger logger);

	/**
	 * Check if the <code>LoggerRepository</code> contains the specified
	 * <code>Logger</code> object.
	 * 
	 * @param name
	 *            the name of the <code>Logger</code> to check for.
	 * 
	 * @return <code>true</code> if the <code>LoggerTree</code> contains the
	 *         specified <code>Logger</code>, otherwise <code>false</code>.
	 */
	public boolean contains(String name);

	/**
	 * Get the number of leaf nodes, i.e. the number of <code>Logger</code>
	 * classes.
	 * 
	 * @return the number of leaf nodes.
	 */
	public int numberOfLeafNodes();

	/**
	 * Reset the logger repository.
	 */
	public void reset();

	/**
	 * Shutdown the <code>LoggerRepository</code>, i.e. release all the
	 * resources.
	 */
	public void shutdown();

}