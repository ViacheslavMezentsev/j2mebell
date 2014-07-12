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

package net.sf.microlog.midp;

import javax.microedition.midlet.MIDlet;

import net.sf.microlog.core.config.PropertyConfigurator;
import net.sf.microproperties.midp.MidletProperties;

/**
 * The <code>MIDletPropertyConfigurator</code> configures Microlog with a
 * @author Johan Karlsson (johan.karlsson@jayway.se)
 *
 */
public class MIDletPropertyConfigurator extends PropertyConfigurator {
	
	protected MIDletPropertyConfigurator(){
		super();
	}
	
	/**
	 * Configure Microlog using the supplied midlet to get the properties from. 
	 * 
	 * @param midlet
	 */
	public static void configure(MIDlet midlet){
		PropertyConfigurator configurator = new MIDletPropertyConfigurator();
		MidletProperties properties = new MidletProperties(midlet);
		configurator.configure(properties);
	}
	
	public static void configure(String jarFileName, MIDlet midlet){
		PropertyConfigurator.configure(jarFileName);
		MIDletPropertyConfigurator.configure(midlet);
	}

}
