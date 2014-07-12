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

package net.sf.microproperties.midp;

import java.io.IOException;

import javax.microedition.midlet.MIDlet;

import net.sf.microproperties.JarFileProperties;
import net.sf.microproperties.Properties;

/**
 * The <code>MIDletProperties</code> class is used to get the MIDlet properties.
 * It is possible to cache the values that are already read.
 * 
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 * 
 * @since 0.1
 * 
 */
public class MidletProperties extends Properties {
	
	MIDlet midlet;

	/**
	 * Create a <code>MidletProperties</code> object using the specified
	 * <code>MIDlet</code> to the the properties from.
	 */
	public MidletProperties(MIDlet midlet) {
		if (midlet == null) {
			throw new IllegalArgumentException("MIDlet must not be null.");
		}

		this.midlet = midlet;
	}

	public MidletProperties(MIDlet midlet, String jarFileName) {
		if (midlet == null) {
			throw new IllegalArgumentException("MIDlet must not be null.");
		}

		this.midlet = midlet;

		JarFileProperties properties = new JarFileProperties();
		try {
			properties.load(jarFileName);
			this.defaults = properties;
		} catch (IOException e) {
			System.err.println("Failed to load properties file.");
		}
	}

	/**
	 * @param defaults
	 */
	public MidletProperties(MIDlet midlet, Properties defaults) {
		this(midlet);

		this.defaults = defaults;
	}

	public String getProperty(String key, String defaultValue) {

		String property = this.getProperty(key);

		if (property == null) {
			property = defaultValue;
		}

		return property;
	}

	public String getProperty(String key) {

		String property = midlet.getAppProperty(key);

		if (property == null && defaults != null) {
			property = defaults.getProperty(key);
		}

		return property;
	}

}
