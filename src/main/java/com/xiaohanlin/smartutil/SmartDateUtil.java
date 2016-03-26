package com.xiaohanlin.smartutil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

/**
 * 线程安全的date工具类
 * 
 * @author jiaozi
 *
 */
public class SmartDateUtil extends DateUtils {
	private static final ThreadLocal<DateFormats> dateFormats = new ThreadLocal<DateFormats>() {
		protected DateFormats initialValue() {
			return new DateFormats();
		}
	};

	public static final int HOUR_MIN = 60;

	public static final int DAY_MI_SECOND = 24 * 60 * 60 * 1000;

	public static String formatYMD(Date date) {
		return dateFormats.get().ymd.format(date);
	}

	public static String formatYM(Date date) {
		return dateFormats.get().ym.format(date);
	}

	public static String formatHMS(Date date) {
		return dateFormats.get().hms.format(date);
	}

	public static String formatHM(Date date) {
		return dateFormats.get().hm.format(date);
	}

	public static String formatYMDHM(Date date) {
		return dateFormats.get().ymdhm.format(date);
	}

	public static String formatYMDHMS(Date date) {
		return dateFormats.get().ymdhms.format(date);
	}

	public static String formatYMDChinese(Date date) {
		return dateFormats.get().ymdChinese.format(date);
	}

	public static String formatYMDSlash(Date date) {
		return dateFormats.get().ymdSlash.format(date);
	}

	public static Date parseYMD(String dateStr) {
		return parse(dateFormats.get().ymd, dateStr);
	}

	public static Date parseYM(String dateStr) {
		return parse(dateFormats.get().ym, dateStr);
	}

	public static Date parseYMDHMS(String dateStr) {
		return parse(dateFormats.get().ymdhms, dateStr);
	}

	public static Date parseTodayHMS(String dateStr) {
		String today = formatYMD(new Date());
		String todayDateStr = String.format("%s %s", today, dateStr);
		return parse(dateFormats.get().ymdhms, todayDateStr);
	}

	/**
	 * 判断当前时间是否在某段时间内 参数不区分先后顺序
	 */
	public static boolean isDuringTwoDate(Date date, Date another) {
		long dateTime = date.getTime();
		long anotherTime = another.getTime();
		long currentTime = new Date().getTime();

		if (currentTime > dateTime && currentTime < anotherTime) {
			return true;
		} else if (currentTime > anotherTime && currentTime < dateTime) {
			return true;
		} else {
			return false;
		}
	}

	public static Date parse(SimpleDateFormat format, String dateStr) {
		try {
			Date d = format.parse(dateStr);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			int year = c.get(Calendar.YEAR);
			if (year >= 1000 && year <= 9999) {
				return d;
			} else {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}


	public static long daysOffset(Date date1, Date date2) {
		date1 = parseYMD(formatYMD(date1));
		date2 = parseYMD(formatYMD(date2));
		return (date1.getTime() - date2.getTime()) / DAY_MI_SECOND;
	}

	/**
	 * 今天是星期几 , 7表示星期日
	 * 
	 * @return
	 */
	public static int getTodayDayOfWeek() {
		Calendar now = Calendar.getInstance();
		int dayOfweek = now.get(Calendar.DAY_OF_WEEK);
		dayOfweek--;
		if (dayOfweek == 0) {
			dayOfweek = 7;
		}
		return dayOfweek;
	}

	public static boolean isTodaytDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Calendar todayCalendar = Calendar.getInstance();
		if (calendar.get(Calendar.YEAR) != todayCalendar.get(Calendar.YEAR)) {
			return false;
		} else if (calendar.get(Calendar.MONTH) != todayCalendar.get(Calendar.MONTH)) {
			return false;
		} else if (calendar.get(Calendar.DAY_OF_MONTH) != todayCalendar.get(Calendar.DAY_OF_MONTH)) {
			return false;
		}
		return true;
	}

}

class DateFormats {
	public final SimpleDateFormat hms = new SimpleDateFormat("HH:mm:ss");
	public final SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
	public final SimpleDateFormat ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
	public final SimpleDateFormat ym = new SimpleDateFormat("yyyy-MM");
	public final SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final SimpleDateFormat ymdChinese = new SimpleDateFormat("yyyy年MM月dd");
	public final SimpleDateFormat ymdSlash = new SimpleDateFormat("yyyy/MM/dd");
}