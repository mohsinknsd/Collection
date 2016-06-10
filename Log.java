import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

import kafka.producer.KeyedMessage;

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
	 * Date format in the whole application can be configured from the config.properties file.
	 * Here formatter will be initialized using default date format.
	 */
	private static SimpleDateFormat formatter;
	
	/**
	 * A collection of all messages that will be logged to the console.
	 */
	private static Properties messages = new Properties();
	
	private static final String INF = "INF";
	private static final String ERR = "ERR";
	private static final String WRN = "WRN";
	private static final String DBG = "DBG";
	
	/**
	 * Loading all messages at the beginning for the faster access to messages.properties file.
	 * This instance will be used to fetch messages using their keys.
	 */
	static {
		try {
			//first loading configuration to get default configuration of logger and date time format 
			messages.load(new FileReader(new File("resources/config.properties")));
			path = Boolean.parseBoolean(messages.getProperty("write.log.files")) ? messages.getProperty("log.file.dir") 
					+ ((SystemUtils.IS_OS_WINDOWS) ? "iot-hub//" : "iot-hub\\") : null;			
			formatter = new SimpleDateFormat(messages.getProperty("display.date.format"), Locale.US);
			
			//now loading all messages
			messages.load(new FileReader(new File("resources/messages.properties")));
		} catch (IOException | ExceptionInInitializerError e) {			
			e.printStackTrace();
		}
	}

	private synchronized static String out(Class<?> c, String x, String s) {
		/*System.out.printf("[" + formatter.format(new Date(System.currentTimeMillis()))
		+ "]-[INF]-[%-"+LIMIT+"."+LIMIT+"s] %s%n", get(c.getSimpleName()), s);*/
		String log = "[" + formatter.format(new Date(System.currentTimeMillis()))
		+ "]-[" + x + "]-[" + get(c.getSimpleName()).substring(0, LIMIT)  + "] " + s + "\n";
		System.out.print(log);
		return log;
	}

	private synchronized static String err(Class<?> c, String x, String s) {
		/*System.err.printf("[" + formatter.format(new Date(System.currentTimeMillis()))
		+ "]-[" + x + "]-[%-"+LIMIT+"."+LIMIT+"s] %s%n", get(c.getSimpleName()), s);*/
		String log = "[" + formatter.format(new Date(System.currentTimeMillis()))
		+ "]-[" + x +"]-[" + get(c.getSimpleName()).substring(0, LIMIT)  + "] " + s + "\n";
		System.err.print(log);
		return log;
	}

	/**
	 * INFORMATION LOG
	 * Method will print log on system console using {@code System.out.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param params first string will be key for the message stored in messages.properties & remaining will be parameters
	 */
	public static void i(Class<?> c, String ... params) {
		writeToFile(out(c, INF, ((params.length > 1) ? MessageFormat.format(messages.getProperty(params[0]), 
				(Object[]) Arrays.copyOfRange(params, 1, params.length)) : messages.getProperty(params[params.length - 1]))));
	}

	/**
	 * WARNING LOG
	 * Method will print log on system console using {@code System.err.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param params first string will be key for the message stored in messages.properties & remaining will be parameters
	 */
	public static void w(Class<?> c, String ... params) {
		writeToFile(err(c, WRN, ((params.length > 1) ? MessageFormat.format(messages.getProperty(params[0]), 
				(Object[]) Arrays.copyOfRange(params, 1, params.length)) : messages.getProperty(params[params.length - 1]))));		
	}
	
	/**
	 * ERROR LOG
	 * Method will print log on system console using {@code System.err.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param params first string will be key for the message stored in messages.properties & remaining will be parameters
	 */
	public static void e(Class<?> c, String ... params) {
		writeToFile(err(c, ERR, ((params.length > 1) ? MessageFormat.format(messages.getProperty(params[0]), 
				(Object[]) Arrays.copyOfRange(params, 1, params.length)) : messages.getProperty(params[params.length - 1]))));
	}
	
	/**
	 * ERROR LOG
	 * Method will print log on system console using {@code System.err.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param e exception that arises 
	 */
	public static void e(Class<?> c, Exception exp) {
		StringWriter sw = new StringWriter();
	    exp.printStackTrace(new PrintWriter(sw));
		writeToFile(err(c, ERR, sw.toString()));		
	}
	
	/**
	 * ERROR LOG
	 * Method will print {@link KeyedMessage} log on system console using {@code System.out.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param msgs main content of the log
	 */
	public static void e(Class<?> c, KeyedMessage<String, String> msg) {
		if (msg != null) writeToFile(err(c, ERR,  new String(msg.message())));		
	}

	/**
	 * DEBUG LOG
	 * Method will print log on system console using {@code System.out.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param msg main content of the log
	 */
	public static void d(Class<?> c, String msg) {
		out(c, DBG, msg);
	}
	
	/**
	 * DEBUG LOG
	 * Method will print {@link KeyedMessage} log on system console using {@code System.out.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param msgs main content of the log
	 */
	public static void d(Class<?> c, KeyedMessage<String, String> msg) {
		if (msg != null) out(c, DBG, new String(msg.message()));		
	}
	
	/**
	 * A method to write data on files. It will take decisions according to log levels attached with the logs e.g. ERR or INF.
	 * Another thing is that file name will be a combination of dd-M-yyyy It mean log files will be generated daily.
	 * @param log to write in files
	 */
	private static void writeToFile(String log) {
		if (path != null) {
			File file = new File(path);
			file.mkdirs();
			Calendar c = Calendar.getInstance();
			try (FileWriter writer = new FileWriter(new File(file,
					c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.YEAR) + ".txt"
					+ ((log.contains(ERR) || log.contains(WRN)) ? " ERR LOG " : " INF LOG ")
					), true)) {
				writer.write(log);
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	private static String get(String s) {
		if (s.length() < LIMIT)
			for(int i = s.length(); i < LIMIT; i++)
				s += ".";
		return s;
	}
}
