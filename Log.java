package com.practice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;

/**
 * A default logger for the whole project to print logs on console or/and file. Specification of different log levels are 
 * as following... 
 * <ul>
 * <li><b>Log.e</b> for errors</li>
 * <li><b>Log.i</b> for information</li>
 * <li><b>Log.w</b> for warnings</li>
 * <li><b>Log.d</b> for debug</li>
 * </ul>
 * 
 * Note : debug logs will not be written into the files
 * 
 * @author mohsin.khan
 * @date 2 June 2016
 */

public class Log {

	/**
	 * If log writing in files is enabled in main configuration file, then this {@code path} will be initialized and 
	 * logger will write files of logs on this file path 
	 */
	private static String path = null;

	/**
	 * It is max class name length limit in the logs. If class name is less than this limit, then remaining length
	 * will be covered by dots(.) using {@code get()} method. 
	 */
	private static final int LIMIT = 12;

	/**
	 * INF log will come on the console with log level [INF] and will be stored in INF Log file also.
	 * In this type of logs, every activity of the system will be recorded like initializing some instance, conecting to
	 * the remote server, sending data, receiving data etc.
	 * <b>Source of Log :</b> Any string can be passed to INF log, if this string exists as a key in messages.properties file then
	 * that message will be logged otherwise this String will be printed.
	 */
	private static final String INF = "INF";

	/**
	 * As the name implies, this log level will be used to approach errors only. In this category, exceptions also can be used.
	 * This type of logs will also be recorded in ERR log file.
	 * <b>Source of Log :</b> log source will be messages.properties file, any string or {@link Exception}.
	 */
	private static final String ERR = "ERR";

	/**
	 * It is for warnings. These warnings will also be recorded in ERR log file and console. This type of logs should be
	 * anything which will inform that something is not right but does not effecting the system.
	 * <b>Source of Log :</b> Any string can be passed to WRN log, if this string exists as a key in messages.properties file then
	 * that message will be logged otherwise this String will be printed.
	 */
	private static final String WRN = "WRN";

	/**
	 * DEBUG logs are only for development purpose or debugging, It mean these can be removed after project deployment. 
	 * Fact is that, these logs will not be recorded in log files.  
	 * <b>Source of Log :</b> Any string can be passed to DBG log but message.properties is not supported here.
	 */
	private static final String DBG = "DBG";
	
	/**
	 * Date format in the whole application can be configured from the config.properties file.
	 * Here formatter will be initialized using default date format.
	 */
	private static SimpleDateFormat formatter;

	/**
	 * A collection of all messages that will be logged to the console.
	 */
	private static Properties messages = new Properties();

	/**
	 * Loading all messages at the beginning for the faster access to messages.properties file.
	 * This instance will be used to fetch messages using their keys.
	 */
	static {
		try {
			InputStream stream = Log.class.getResourceAsStream("/config.properties");
			if (stream != null) {
				//first loading configuration to get default configuration of logger and date time format 			
				messages.load(stream);
				path = Boolean.parseBoolean(messages.getProperty("write.log.files")) ? messages.getProperty("log.file.dir") 
						+ ((SystemUtils.IS_OS_WINDOWS) ? "system-logs" + File.separator : "system-logs" + File.separator) : null;			
						formatter = new SimpleDateFormat(messages.getProperty("display.date.format"), Locale.US);

						//now loading all messages
						//messages.load(new FileReader(new File("resources/messages.properties")));
						stream = Log.class.getResourceAsStream("/messages.properties");
						if (stream != null) {
							messages.load(stream);	
						}					
			} else {
				formatter = new SimpleDateFormat("dd MMM yyyy hh:mm:ss", Locale.US);
			}

		} catch (IOException | ExceptionInInitializerError e) {			
			e.printStackTrace();
		}
	}

	/**
	 * This will simply arrange the data in particular format and print it on system console using output stream.
	 * @param c class name to know origin
	 * @param x log level like INF or DBG
	 * @param s original content to print
	 * @return complete log with the format and time-stamp
	 */
	private synchronized static String out(String c, String x, String s) {
		/*System.out.printf("[" + formatter.format(new Date(System.currentTimeMillis()))
		+ "]-[INF]-[%-"+LIMIT+"."+LIMIT+"s] %s%n", get(c.getSimpleName()), s);*/
		String log = "[" + formatter.format(new Date(System.currentTimeMillis()))
		+ "]-[" + x + "]-[" + get(c).substring(0, LIMIT)  + "]" + s + "\n";
		System.out.print(log);
		return log;
	}

	/**
	 * This will simply arrange the data in particular format and print it on system console using error stream.
	 * @param c class name to know origin
	 * @param x log level like ERR or WRN
	 * @param s original content to print
	 * @return complete log with the format and time-stamp
	 */
	private synchronized static String err(String c, String x, String s) {
		/*System.err.printf("[" + formatter.format(new Date(System.currentTimeMillis()))
		+ "]-[" + x + "]-[%-"+LIMIT+"."+LIMIT+"s] %s%n", get(c.getSimpleName()), s);*/
		String log = "[" + formatter.format(new Date(System.currentTimeMillis()))
		+ "]-[" + x +"]-[" + get(c).substring(0, LIMIT)  + "]" + s + "\n";
		System.err.print(log);
		return log;
	}

	/**
	 * INFORMATION LOG <br>
	 * Method will print log on system console using {@code System.out.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param params first string will be key for the message stored in messages.properties and remaining will be parameters
	 */
	public static void i(String ... params) {	
		String s = Thread.currentThread().getStackTrace()[2].getClassName();
		write(out(s.substring(s.lastIndexOf('.') + 1, s.length()), INF, " " + ((params.length > 1) ? 
				MessageFormat.format(messages.getProperty(params[0]), (Object[]) Arrays.copyOfRange(params, 1, params.length)) 
				: messages.getProperty(params[0]) != null ? messages.getProperty(params[0]) : params[0])));
	}

	/**
	 * WARNING LOG <br>
	 * Method will print log on system console using {@code System.err.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param params first string will be key for the message stored in messages.properties and remaining will be parameters
	 */
	public static void w(String ... params) {
		String s = Thread.currentThread().getStackTrace()[2].getClassName();
		write(err(s.substring(s.lastIndexOf('.') + 1, s.length()), WRN, " " + ((params.length > 1) 
				? MessageFormat.format(messages.getProperty(params[0]), (Object[]) Arrays.copyOfRange(params, 1, params.length)) 
						: messages.getProperty(params[0]) != null ? messages.getProperty(params[0]) : params[0])));		
	}

	/**
	 * ERROR LOG <br>
	 * Method will print log on system console using {@code System.err.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param params first string will be key for the message stored in messages.properties and remaining will be parameters
	 */
	public static void e(String ... params) {
		String s = Thread.currentThread().getStackTrace()[2].getClassName();
		write(err(s.substring(s.lastIndexOf('.') + 1, s.length()), ERR, " " + ((params.length > 1) 
				? MessageFormat.format(messages.getProperty(params[0]), (Object[]) Arrays.copyOfRange(params, 1, params.length)) 
						: messages.getProperty(params[0]) != null ? messages.getProperty(params[0]) : params[0])));
	}

	/**
	 * ERROR LOG <br>
	 * Method will print log on system console using {@code System.err.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param exp exception that arises 
	 */
	public static void e(Exception exp) {
		String s = Thread.currentThread().getStackTrace()[2].getClassName();
		StringWriter sw = new StringWriter();
		exp.printStackTrace(new PrintWriter(sw));
		write(err(s.substring(s.lastIndexOf('.') + 1, s.length()), ERR, " " + sw.toString()));
	}

	/**
	 * DEBUG LOG <br>
	 * Method will print log on system console using {@code System.out.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param msg main content of the log
	 */
	public static void d(String msg) {
		String s = Thread.currentThread().getStackTrace()[2].getClassName();
		out(s.substring(s.lastIndexOf('.') + 1, s.length()), DBG, " " + msg);
	}

	/**
	 * A method to write data on files. It will take decisions according to log levels attached with the logs e.g. ERR or INF.
	 * Another thing is that file name will be a combination of dd-M-yyyy It mean log files will be generated daily.
	 * @param log to write in files
	 */
	private static void write(String log) {
		if (path != null) {
			File file = new File(path);
			file.mkdirs();
			Calendar c = Calendar.getInstance();
			try (FileWriter writer = new FileWriter(new File(file,
					((log.contains(ERR) || log.contains(WRN)) ? "ERR LOGS " : "INFO LOGS ") 
					+ c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.YEAR) + ".LOG"
					), true)) {
				writer.write(log);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}

	/**
	 * This method will adjust string length according to LIMIT specified above. It will add dots(.) in the string if string
	 * length is less than LIMIT to cover length. Suppose we have filename "Admin" and LIMIT is 12 then it will be converted into
	 * "Admin......." and on the other hand we have a filename "ProducerNetwork" then it will be converted to "ProducerNetw".
	 * @param s class file name to adjust
	 * @return a fix length string according to limit
	 */
	private static String get(String s) {
		if (s.length() < LIMIT)
			for(int i = s.length(); i < LIMIT; i++)
				s += ".";
		return s;
	}
}
