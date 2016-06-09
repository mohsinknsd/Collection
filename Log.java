import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

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
 * @author mohsin.khan
 * @date 2 June 2016
 */

public class Log {
	/**
	 * A collection of all messages that will be logged to the console.
	 */
	private static Properties messages = new Properties(); 

	/**
	 * Date format in the whole application can be configured from the config.properties file.
	 * Here formatter will be initialized using default date format.
	 */
	private static SimpleDateFormat formatter;
	
	/**
	 * It is max class name length limit in the logs. If class name is less than this limit, then remaining length
	 * will be covered by dots(.) using {@code get()} method. 
	 */
	private static final int LIMIT = 12;

	/**
	 * Loading all messages at the beginning for the faster access to messages.properties file.
	 * This instance will be used to fetch messages using their keys.
	 */
	static {
		try {
			messages.load(Class.class.getResourceAsStream("/config.properties"));			
			formatter = new SimpleDateFormat(messages.getProperty("display.date.format"), Locale.US);
			messages.load(Class.class.getResourceAsStream("/messages.properties"));
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	private static void out(Class<?> c, String s) {
		/*System.out.println("[" + formatter.format(new Date(System.currentTimeMillis()))
		+ "]-[INF]-[" + c.getSimpleName() + "] " + s);*/
		System.out.printf("[" + formatter.format(new Date(System.currentTimeMillis()))
		+ "]-[INF]-[%-"+LIMIT+"."+LIMIT+"s] %s%n", get(c.getSimpleName()), s);
	}

	private static void err(Class<?> c, String x, String s) {
		/*System.err.println("[" + formatter.format(new Date(System.currentTimeMillis()))
		+ "]-[WRN]-[" + c.getSimpleName() + "] " + s);*/
		System.err.printf("[" + formatter.format(new Date(System.currentTimeMillis()))
		+ "]-[" + x + "]-[%-"+LIMIT+"."+LIMIT+"s] %s%n", get(c.getSimpleName()), s);
	}

	/**
	 * INFORMATION LOG
	 * Method will print log on system console using {@code System.out.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param params first string will be key for the message stored in messages.properties & remaining will be parameters
	 */
	public static void i(Class<?> c, String ... params) {
		out(c, ((params.length > 1) ? MessageFormat.format(messages.getProperty(params[0]), 
				(Object[]) Arrays.copyOfRange(params, 1, params.length)) : messages.getProperty(params[params.length - 1])));
	}

	/**
	 * WARNING LOG
	 * Method will print log on system console using {@code System.err.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param params first string will be key for the message stored in messages.properties & remaining will be parameters
	 */
	public static void w(Class<?> c, String ... params) {
		err(c, "WRN", ((params.length > 1) ? MessageFormat.format(messages.getProperty(params[0]), 
				(Object[]) Arrays.copyOfRange(params, 1, params.length)) : messages.getProperty(params[params.length - 1])));		
	}
	
	/**
	 * ERROR LOG
	 * Method will print log on system console using {@code System.err.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param params first string will be key for the message stored in messages.properties & remaining will be parameters
	 */
	public static void e(Class<?> c, String ... params) {
		err(c, "ERR", ((params.length > 1) ? MessageFormat.format(messages.getProperty(params[0]), 
				(Object[]) Arrays.copyOfRange(params, 1, params.length)) : messages.getProperty(params[params.length - 1])));		
	}

	/**
	 * DEBUG LOG
	 * Method will print log on system console using {@code System.out.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param msg main content of the log
	 */
	public static void d(Class<?> c, String msg) {
		out(c, msg);
	}
	
	/**
	 * DEBUG LOG
	 * Method will print {@link KeyedMessage} log on system console using {@code System.out.println()}
	 * @param c provide class, to know which log belongs to which class
	 * @param msgs main content of the log
	 */
	public static void d(Class<?> c, KeyedMessage<String, String> msg) {
		out(c, new String(msg.message()));		
	}
	
	private static String get(String s) {		
		if (s.length() < LIMIT)
			for(int i = s.length(); i < LIMIT; i++)
				s += ".";
		return s;
	}
}
