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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import net.sf.microlog.core.Appender;
import net.sf.microlog.core.Formatter;
import net.sf.microlog.core.Level;
import net.sf.microlog.core.Logger;
import net.sf.microlog.core.LoggerFactory;
import net.sf.microlog.core.MicrologConstants;
import net.sf.microlog.core.StringUtil;
import net.sf.microproperties.JarFileProperties;
import net.sf.microproperties.Properties;

/**
 * This class is used to configure Microlog using a property file.
 * 
 * @author Johan Karlsson
 * @since 2.0
 * 
 */
public class PropertyConfigurator {
	
	/**
	 * The key for setting the root logger.
	 */
	public static final String ROOT_LOGGER_KEY = "microlog.rootLogger";

	/**
	 * The key for setting the logger.
	 */
	public static final String LOGGER_PREFIX_KEY = "microlog.logger";

	/**
	 * The key for setting the formatter.
	 */
	public static final String FORMATTER_PREFIX_KEY = "microlog.formatter";

	/**
	 * The key for setting the appender.
	 */
	public static final String APPENDER_PREFIX_KEY = "microlog.appender";

	/**
	 * The key for setting the level.
	 */
	public static final String LOG_LEVEL_PREFIX_KEY = "microlog.level";
	
	/**
	 * The formatter {@link String} for getting formatter properties.
	 */
	public static final String FORMATTER_PROPERTY = "formatter";
	
	/**
	 * The property delimiter used in Log4j property files.
	 */
	public static final String LOG4J_PROPERTY_DELIMITER = ",";
	
	/**
	 * The default properties file for microlog.
	 */
	public static final String DEFAULT_PROPERTY_FILE = "/microlog.properties";
	
	/**
	 * The default Log level (String)
	 */
	public static final String DEFAULT_LOG_LEVEL_STRING = "DEBUG";
	
	/**
	 * The default log level if none is specified.
	 */
	public static final Level DEFAULT_LOG_LEVEL = Level.DEBUG;
	
	/**
	 * The property delimiter used by microlog.
	 */
	public static final String PROPERTY_DELIMETER = ";";
	
	/**
	 * The aliases for formatters.
	 */
	public static final String[] FORMATTER_ALIASES = { "SimpleFormatter",
			"PatternFormatter" };
	
	/**
	 * The formatter class names (fully qualified).
	 */
	public static final String[] FORMATTER_CLASS_NAMES = {
			"net.sf.microlog.core.format.SimpleFormatter",
			"net.sf.microlog.core.format.PatternFormatter" };
	
	/**
	 * The aliases for the appenders.
	 */
	public static final String[] APPENDER_ALIASES = { "ConsoleAppender",
			"MemoryBufferAppender", "BluetoothSerialAppender",
			"CanvasAppender", "DatagramAppender", "HttpAppender",
			"FileAppender", "FormAppender", "MMSBufferAppender",
			"RecordStoreAppender", "SerialAppender", "SMSBufferAppender",
			"SocketAppender", "SyslogAppender" };
	
	/**
	 * The appender class names (fully qualified).
	 */
	public static final String[] APPENDER_CLASS_NAMES = {
			"net.sf.microlog.core.appender.ConsoleAppender",
			"net.sf.microlog.core.appender.MemoryBufferAppender",
			"net.sf.microlog.midp.bluetooth.BluetoothSerialAppender",
			"net.sf.microlog.midp.appender.CanvasAppender",
			"net.sf.microlog.midp.appender.DatagramAppender",
			"net.sf.microlog.midp.appender.HttpAppender",
			"net.sf.microlog.midp.file.FileAppender",
			"net.sf.microlog.midp.appender.FormAppender",
			"net.sf.microlog.midp.wma.MMSBufferAppender",
			"net.sf.microlog.midp.appender.RecordStoreAppender",
			"net.sf.microlog.midp.appender.SerialAppender",
			"net.sf.microlog.midp.wma.SMSBufferAppender",
			"net.sf.microlog.midp.appender.SocketAppender",
			"net.sf.microlog.midp.appender.SyslogAppender", };
	
	/**
	 * The map (Hashtable) that maps the formatter aliases to the formatter classes.
	 */
	static final Hashtable formatterMap = new Hashtable(7);
	
	/**
	 * The map (Hashtable) that maps the appender aliases to the appender classes.
	 */
	static final Hashtable appenderMap = new Hashtable(37);
	
	/**
	 * The logger repository that this configurator uses to configure microlog.
	 */
	protected LoggerRepository loggerRepository;
	
	/*
	 * The static initializer
	 */
	{
		for (int formatterNo = 0; formatterNo < FORMATTER_ALIASES.length; formatterNo++) {
			formatterMap.put(FORMATTER_ALIASES[formatterNo],
					FORMATTER_CLASS_NAMES[formatterNo]);
		}

		for (int appenderNo = 0; appenderNo < APPENDER_ALIASES.length; appenderNo++) {
			appenderMap.put(APPENDER_ALIASES[appenderNo],
					APPENDER_CLASS_NAMES[appenderNo]);
		}
	}

	/**
	 * Create a <code>ProperttConfigurator</code>. This is protected to protect
	 * it to be constructed from elsewhere.
	 */
	protected PropertyConfigurator() {
		loggerRepository = LoggerFactory.getLoggerRepository();
	}

	/**
	 * Configure using the default properties file, i.e. microlog.properties.
	 */
	public static void configure() {
		PropertyConfigurator.configure(DEFAULT_PROPERTY_FILE);
	}

	/**
	 * Configure using the specified file.
	 * 
	 * @param fileName
	 *            the file name to use for property loading.
	 */
	public static void configure(String fileName) {
		JarFileProperties jarFileProperties = new JarFileProperties();
		System.out.println("Trying to load properties from " + fileName);

		try {
			jarFileProperties.load(fileName);
			PropertyConfigurator configurator = new PropertyConfigurator();
			configurator.configure(jarFileProperties);
		} catch (IOException e) {
			System.err.println("Failed to load properties file: " + fileName);
		}
	}

	/**
	 * Configure using the specified <code>Properties</code> object.
	 * 
	 * @param properties
	 *            the properties object to use for configuration.
	 */
	public void configure(Properties properties) {
		String rootLogger = properties
				.getProperty(PropertyConfigurator.ROOT_LOGGER_KEY);

		if (rootLogger != null) {
			configureLog4jStyle(properties);
		} else {
			configureMicrologClassicStyle(properties);
		}
	}

	/**
	 * Add an appender alias for the specified <code>Appender</code>. The alias
	 * could be used when calling the <code>configure()</code> method.
	 * 
	 * @param appender
	 *            the <code>Appender</code> to add alias for.
	 */
	public static void addAppenderAlias(Appender appender) {
		String fullyQualifiedClassName = appender.getClass().getName();
		String aliasName = StringUtil.extractPartialClassName(
				fullyQualifiedClassName, 1);
		appenderMap.put(aliasName, fullyQualifiedClassName);
	}

	/**
	 * Add a formatter alias for the specified <code>Formatter</code>. The alias
	 * could be used when calling the <code>configure()</code> method.
	 * 
	 * @param formatter the <code>Formatter</code> to add an alias for.
	 */
	public static void addFormatterAlias(Formatter formatter) {
		String fullyQualifiedClassName = formatter.getClass().getName();
		String aliasName = StringUtil.extractPartialClassName(
				fullyQualifiedClassName, 1);
		formatterMap.put(aliasName, fullyQualifiedClassName);
	}

	/**
	 * Configure using the Log4j style.
	 * 
	 * @param properties
	 *            the properties to use for configuration.
	 */
	protected void configureLog4jStyle(Properties properties) {

		String rootLoggerProperty = properties
				.getProperty(PropertyConfigurator.ROOT_LOGGER_KEY);

		int endIndex = rootLoggerProperty.indexOf(LOG4J_PROPERTY_DELIMITER);
		String levelString = DEFAULT_LOG_LEVEL_STRING;
		if (endIndex != -1) {
			levelString = rootLoggerProperty.substring(0, endIndex);
		} else {
			levelString = rootLoggerProperty;
		}

		Logger rootLogger = loggerRepository.getRootLogger();
		// We make a call and try to set the log level for the root logger. If
		// it fails, we assume that the user has left out the optional logging
		// level.
		Level rootLogLevel = setRootLevel(levelString);
		doConfigureHierarchyLogLevels(properties);

		int beginIndex = 0;
		if (rootLogLevel != null) {
			beginIndex = endIndex + 1;
			endIndex = rootLoggerProperty.indexOf(LOG4J_PROPERTY_DELIMITER,
					beginIndex);
		} else {
			rootLogger.setLevel(DEFAULT_LOG_LEVEL);
		}

		if (endIndex == -1) {
			endIndex = rootLoggerProperty.length();
		}

		while (beginIndex < rootLoggerProperty.length()) {
			String appenderName = rootLoggerProperty.substring(beginIndex,
					endIndex).trim();
			doConfigureAppender(rootLogger, appenderName, properties);

			beginIndex = endIndex + 1;
			endIndex = rootLoggerProperty.indexOf(LOG4J_PROPERTY_DELIMITER,
					beginIndex);

			if (endIndex == -1) {
				endIndex = rootLoggerProperty.length();
			}
		}
	}

	/**
	 * Configure the log levels for the hierarchy.
	 * 
	 * @param properties
	 *            the properties to get the setup for the log levels.
	 */
	protected void doConfigureHierarchyLogLevels(Properties properties) {

		Enumeration propertyEnumeration = properties.keys();
		while (propertyEnumeration.hasMoreElements()) {
			String propertyKey = (String) propertyEnumeration.nextElement();
			if (propertyKey.startsWith(PropertyConfigurator.LOGGER_PREFIX_KEY)) {
				// Strip of the leading characters.
				String path = propertyKey
						.substring(PropertyConfigurator.LOGGER_PREFIX_KEY
								.length() + 1);

				// Get the property value
				String levelString = properties.getProperty(propertyKey);
				Level level = stringToLevel(levelString);

				if (level != null) {
					System.out.println("Setting level " + level + " to path "
							+ path);
					loggerRepository.setLevel(path, level);
				} else {
					System.err.println("Level " + levelString
							+ " is not a valid level.");
				}
			}
		}
	}

	/**
	 * Configure the specified appender with using the properties.
	 * 
	 * @param logger
	 *            the logger to configure
	 * @param appenderName
	 *            the name of the appender to configure
	 * @param properties
	 *            the properties to be used for configuration.
	 */
	protected void doConfigureAppender(Logger logger, String appenderName,
			Properties properties) {
		Appender appender = createAppender(appenderName, properties);

		if (appender != null) {
			System.out.println("Adding appender " + appender);
			Formatter formatter = doConfigureFormatter(appenderName, properties);

			if (formatter != null) {
				appender.setFormatter(formatter);
			}

			setAppenderSpecificProperties(appenderName, properties, appender);

			logger.addAppender(appender);
		}
	}

	/**
	 * Configure the specified <code>Formatter</code>
	 * 
	 * @param appenderName
	 *            the name of the appender.
	 * @param properties
	 *            the <code>Properties</code> object to get data from
	 * 
	 * @return a configured <code>Formatter</code> object.
	 */
	private Formatter doConfigureFormatter(String appenderName,
			Properties properties) {
		Formatter formatter = createFormatter(appenderName, properties);

		if (formatter != null) {
			String[] formatterProperties = formatter.getPropertyNames();
			if (formatterProperties != null && formatterProperties.length > 0) {
				for (int property = 0; property < formatterProperties.length; property++) {
					StringBuffer propertyKeyBuffer = new StringBuffer(64);
					propertyKeyBuffer.append(PropertyConfigurator.APPENDER_PREFIX_KEY);
					propertyKeyBuffer.append(MicrologConstants.DOT_CHAR);
					propertyKeyBuffer.append(appenderName);
					propertyKeyBuffer.append(MicrologConstants.DOT_CHAR);
					propertyKeyBuffer
							.append(PropertyConfigurator.FORMATTER_PROPERTY);
					propertyKeyBuffer.append(MicrologConstants.DOT_CHAR);
					String propertyName = formatterProperties[property];
					propertyKeyBuffer.append(propertyName);

					String propertyKey = propertyKeyBuffer.toString();
					String propertyValue = properties.getProperty(propertyKey);

					if (propertyValue != null) {
						System.out.println("Setting property " + propertyName
								+ "=" + propertyValue);
						formatter.setProperty(propertyName, propertyValue);
					}
				}
			}
		}

		return formatter;
	}

	/**
	 * 
	 * Create the specified appender.
	 * 
	 * @param appenderName
	 *            the name of the appender.
	 * @param properties
	 *            the properties to be used for configuration.
	 * @return the created <code>Appender</code>.
	 */
	protected Appender createAppender(String appenderName, Properties properties) {
		String appenderClassNameProperty = PropertyConfigurator.APPENDER_PREFIX_KEY
				+ MicrologConstants.DOT_CHAR + appenderName;

		String appenderClassName = properties
				.getProperty(appenderClassNameProperty);

		Appender appender = null;

		if (appenderClassName != null) {
			appenderClassName = checkForAliasAppender(appenderClassName);

			try {
				Class appenderClass = Class.forName(appenderClassName);
				appender = (Appender) appenderClass.newInstance();
			} catch (ClassNotFoundException e) {
				System.err.println(MicrologConstants.MICROLOG_MESSAGE_PREFIX
						+ "Could not find appender class " + appenderName);
			} catch (InstantiationException e) {
				System.err.println(MicrologConstants.MICROLOG_MESSAGE_PREFIX
						+ "Could not instantiate appender class "
						+ appenderName);
			} catch (IllegalAccessException e) {
				System.err.println(MicrologConstants.MICROLOG_MESSAGE_PREFIX
						+ "Not allowed to create appender class "
						+ appenderName);
			}
		}

		return appender;
	}

	/**
	 * Check if the specified appender is an alias. If so the appender name is
	 * converted to fully qualified class name.
	 * 
	 * @param appenderName
	 *            the name to convert.
	 * @return the name of the appender. If it was an alias it is converted,
	 *         otherwise the same string is returned.
	 */
	private String checkForAliasAppender(String appenderName) {
		String className = appenderName;

		String aliasClassName = (String) appenderMap.get(appenderName);
		if (aliasClassName != null) {
			className = aliasClassName;
		}
		return className;
	}

	/**
	 * Create a <code>Formatter</code> object.
	 * 
	 * @param appenderName
	 *            the name of tha <code>Appender</code>.
	 * @param properties
	 *            the <code>Properties</code> to get the information from.
	 * 
	 * @return the created <code>Formatter</code>
	 */
	protected Formatter createFormatter(String appenderName,
			Properties properties) {
		System.out.println("createFormatter: " + appenderName);

		StringBuffer formatterKey = new StringBuffer(64);
		formatterKey.append(PropertyConfigurator.APPENDER_PREFIX_KEY);
		formatterKey.append(MicrologConstants.DOT_CHAR);
		formatterKey.append(appenderName);
		formatterKey.append(MicrologConstants.DOT_CHAR);
		formatterKey.append(PropertyConfigurator.FORMATTER_PROPERTY);

		String formatterClassName = properties.getProperty(formatterKey
				.toString());

		Formatter formatter = null;

		if (formatterClassName != null) {
			formatterClassName = checkForAliasFormatter(formatterClassName);
			try {
				Class formatterClass = Class.forName(formatterClassName);
				formatter = (Formatter) formatterClass.newInstance();
			} catch (ClassNotFoundException e) {
				System.err.println(MicrologConstants.MICROLOG_MESSAGE_PREFIX
						+ "Could not find the formatter class " + appenderName);
			} catch (InstantiationException e) {
				System.err.println(MicrologConstants.MICROLOG_MESSAGE_PREFIX
						+ "Could not find the formatter class " + appenderName);
			} catch (IllegalAccessException e) {
				System.err.println(MicrologConstants.MICROLOG_MESSAGE_PREFIX
						+ "Could not find the formatter class " + appenderName);
			}

		} else {
			System.err.println(MicrologConstants.MICROLOG_MESSAGE_PREFIX
					+ "No formatter class defined");
		}

		return formatter;
	}

	/**
	 * Check if this is an alias for a real formatter.
	 * 
	 * @param formatterName
	 *            the name of formetter.
	 * 
	 * @return the fully quialified class name if it was an alias, otherwise the
	 *         same <code>String</code> is returned.
	 */
	private String checkForAliasFormatter(String formatterName) {
		String className = formatterName;
		String aliasClassName = (String) formatterMap.get(formatterName);
		if (aliasClassName != null) {
			className = aliasClassName;
		}
		return className;
	}

	/**
	 * Set the appender specific properties
	 * 
	 * @param appenderName
	 *            the name of the appender
	 * @param properties
	 *            the <code>Properties</code> obect to get the data from.
	 * @param the
	 *            appender to set the properties on.
	 * 
	 * @param appender
	 *            the configured <code>Appender</code>.
	 */
	protected void setAppenderSpecificProperties(String appenderName,
			Properties properties, Appender appender) {
		String[] appenderSpecificProperties = appender.getPropertyNames();
		StringBuffer propertyNameBuffer = new StringBuffer(64);

		if (appenderSpecificProperties != null
				&& appenderSpecificProperties.length > 0) {
			for (int index = 0; index < appenderSpecificProperties.length; index++) {
				propertyNameBuffer.delete(0, propertyNameBuffer.length());
				propertyNameBuffer.append(PropertyConfigurator.APPENDER_PREFIX_KEY);
				propertyNameBuffer.append(MicrologConstants.DOT_CHAR);
				if (appenderName != null) {
					propertyNameBuffer.append(appenderName);
					propertyNameBuffer.append(MicrologConstants.DOT_CHAR);
				}
				propertyNameBuffer.append(appenderSpecificProperties[index]);

				String propertyName = propertyNameBuffer.toString();
				String propertyValue = properties.getProperty(propertyName);
				if (propertyValue != null) {
					appender.setProperty(appenderSpecificProperties[index],
							propertyValue);
				}
			}
		}
	}

	/**
	 * Configure the logging using classic Microlog style.
	 * 
	 * @param properties
	 *            the properties to use for configuration.
	 */
	private void configureMicrologClassicStyle(Properties properties) {
		doConfigureLogLevel(properties);
		configureAppender(properties);
		configureFormatter(properties);
	}

	/**
	 * Configure the log level.
	 * 
	 * @param properties
	 *            the properties object to get the properties from.
	 */
	private void doConfigureLogLevel(Properties properties) {
		String levelString = properties.getProperty(
				PropertyConfigurator.LOG_LEVEL_PREFIX_KEY, DEFAULT_LOG_LEVEL_STRING);
		setRootLevel(levelString);
	}

	/**
	 * Set the root level.
	 * 
	 * @param levelString
	 *            the String to set the Level from.
	 */
	private Level setRootLevel(String levelString) {

		Level level = stringToLevel(levelString);

		if (level != null) {
			Logger logger = loggerRepository.getRootLogger();
			logger.setLevel(level);
		} else {
			System.err.println("Level " + levelString
					+ " is not a valid level.");
		}

		return level;
	}

	/**
	 * Convert a <code>String</code> containing a level to a <code>Level</code>
	 * object.
	 * 
	 * @return the level that corresponds to the levelString if it was a valid
	 *         <code>String</code>, <code>null</code> otherwise.
	 */
	private Level stringToLevel(String levelString) {
		Level level = null;

		if (levelString.equalsIgnoreCase(Level.FATAL_STRING)) {
			level = Level.FATAL;
		} else if (levelString.equalsIgnoreCase(Level.ERROR_STRING)) {
			level = Level.ERROR;
		} else if (levelString.equalsIgnoreCase(Level.WARN_STRING)) {
			level = Level.WARN;
		} else if (levelString.equalsIgnoreCase(Level.INFO_STRING)) {
			level = Level.INFO;
		} else if (levelString.equalsIgnoreCase(Level.DEBUG_STRING)) {
			level = Level.DEBUG;
		} else if (levelString.equalsIgnoreCase(Level.TRACE_STRING)) {
			level = Level.TRACE;
		}

		return level;
	}

	/**
	 * Configure the appender for the specified logger.
	 * 
	 * @param appenderString
	 *            the <code>String</code> to use for configuring the
	 *            <code>Appender</code>.
	 */
	void configureAppender(Properties properties) {
		Logger logger = loggerRepository.getRootLogger();
		logger.removeAllAppenders();
		String appenderString = properties
				.getProperty(PropertyConfigurator.APPENDER_PREFIX_KEY);

		if ((appenderString != null) && (appenderString.length() > 0)) {
			try {
				int delimiterPos = appenderString.indexOf(PROPERTY_DELIMETER);
				if (delimiterPos == -1) {
					appenderString = checkForAliasAppender(appenderString);

					// There is only one appender
					Class appenderClass = Class.forName(appenderString);
					Appender appender = (Appender) appenderClass.newInstance();
					String appenderName = StringUtil.extractPartialClassName(
							appenderString, 1);
					setAppenderSpecificProperties(appenderName, properties,
							appender);
					logger.addAppender(appender);
				} else {
					// Loop through all the Appenders in appenderString
					int startPos = 0;
					int endPos;
					boolean finished = false;
					do {
						// find out if and where the next string is
						delimiterPos = appenderString.indexOf(
								PROPERTY_DELIMETER, startPos);
						if (delimiterPos == -1) {
							// this is the last appender
							endPos = appenderString.length();
							finished = true;
						} else {
							// has a delimiter at the end
							endPos = delimiterPos;
						}

						// get the appender string
						String singleAppenderString = appenderString.substring(
								startPos, endPos);

						// Advance the start position
						startPos = endPos + 1;

						// create the appender
						if (singleAppenderString.length() > 0) {
							singleAppenderString = checkForAliasAppender(singleAppenderString);

							Class appenderClass = Class
									.forName(singleAppenderString);

							Appender appender = (Appender) appenderClass
									.newInstance();
							String appenderName = StringUtil
									.extractPartialClassName(
											singleAppenderString, 1);
							setAppenderSpecificProperties(appenderName,
									properties, appender);
							logger.addAppender(appender);
							System.out.println("Added appender " + appender);
						}
					} while (!finished);
				}
			} catch (ClassNotFoundException e) {
				System.err.println("Did not find the appender class. " + e);
			} catch (InstantiationException e) {
				System.err
						.println("Did not manage to initiate the appender class. "
								+ e);
			} catch (IllegalAccessException e) {
				System.err
						.println("Did not have access to create the appender class. "
								+ e);
			}
		}
	}

	/**
	 * Configure the formatter.
	 * 
	 * @param formatterString
	 *            the <code>String</code> to use for configuring the
	 *            <code>Formatter</code>.
	 */
	private void configureFormatter(Properties properties) {

		Logger logger = loggerRepository.getRootLogger();
		String className = properties
				.getProperty(PropertyConfigurator.FORMATTER_PREFIX_KEY);

		Formatter formatter = null;

		if (className != null) {
			try {
				className = checkForAliasFormatter(className);
				Class formatterClass = Class.forName(className);
				formatter = (Formatter) formatterClass.newInstance();
				System.out.println("Using formatter " + formatter.getClass());
			} catch (ClassNotFoundException e) {
				System.err.println("Did not find the formatter class. " + e);
			} catch (InstantiationException e) {
				System.err
						.println("Did not manage to initiate the formatter class. "
								+ e);
			} catch (IllegalAccessException e) {
				System.err
						.println("Did not have access to create the formatter class. "
								+ e);
			}

		}

		if (formatter != null) {
			String[] formatterProperties = formatter.getPropertyNames();

			if (formatterProperties != null && formatterProperties.length > 0) {
				for (int property = 0; property < formatterProperties.length; property++) {
					String propertyName = formatterProperties[property];
					String patternClassName = className.substring(className
							.lastIndexOf(MicrologConstants.DOT_CHAR) + 1);
					String propertyValue = properties
							.getProperty(PropertyConfigurator.FORMATTER_PREFIX_KEY
									+ MicrologConstants.DOT_CHAR
									+ patternClassName
									+ MicrologConstants.DOT_CHAR + propertyName);
					if (propertyValue != null) {
						formatter.setProperty(propertyName, propertyValue);
					}
				}
			}

			int nofAppenders = logger.getNumberOfAppenders();
			for (int index = 0; index < nofAppenders; index++) {
				Appender appender = logger.getAppender(index);
				if (appender != null) {
					appender.setFormatter(formatter);
				}
			}

		}

	}

}
