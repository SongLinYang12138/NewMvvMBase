package com.bondex.library.ui;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 * 
 * @author zkq
 *
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class DateUtils {

	/**
	 * 时间格式yyyy-MM-dd
	 */
	private static final DateTimeFormatter YEAH_MONTH_DAY = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");

	/**
	 * 时间格式yyyy-MM-dd HH:mm:ss
	 */
	@SuppressLint("NewApi")
	private static final DateTimeFormatter YEAH_MONTH_DAY_HOUR_MINUTE_SECOND = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	public enum DateEnum {
		YEAR, MONTH, WEEK, DAY, HOUR, MINUTE, SECOND, DAYOFYEAR
	}

	public enum WeekEnum {
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
	}

	/**
	 * 获取当前时间,时间格式yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getSimpleNowDate() {
		return LocalDate.now().toString();
	}
	
	/**
	 * 获取当前时间,时间格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getFullNowDate() {
		return LocalDateTime.now().format(YEAH_MONTH_DAY_HOUR_MINUTE_SECOND);
	}
	
	/**
	 * 日期类型 yyyy-MM-dd 转换成字符串
	 * @param date
	 * @return yyyy-MM-dd
	 */
	public static String getSimpleDate2String(Date date) {
		Instant instant = date.toInstant();
		LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
		return localDate.toString();
	}
	 
	/**
	 * 日期类型 yyyy-MM-dd HH:mm:ss 转换成字符串
	 * @param date
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String getFullDate2String(Date date) {
		Instant instant = date.toInstant();
		LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
		return localDateTime.format(YEAH_MONTH_DAY_HOUR_MINUTE_SECOND);
	}
	
	/**
	 * String类型 yyyy-MM-dd 转 Date 类型
	 * @param date
	 * @return
	 */
	public static Date getString2SimpleDate(String date) {
		LocalDate localDate = LocalDate.parse(date);
		ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
		return Date.from(zonedDateTime.toInstant());
	}
	
	/**
	 * String类型 yyyy-MM-dd HH:mm:ss 转 Date 类型
	 * @param date
	 * @return
	 */
	public static Date getString2FullDate(String date) {
		LocalDateTime localDateTime = LocalDateTime.parse(date,YEAH_MONTH_DAY_HOUR_MINUTE_SECOND);
		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
		return Date.from(zonedDateTime.toInstant());
	}
	/**
	 * 根据日期格式,获取当前时间
	 * 
	 * @param formatterPattern
	 * @return
	 */
	public static String getNowDateByFormatter(String formatterPattern) {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(formatterPattern));
	}

	/**
	 * 获取当前日期的节点时间（年，月，周，日，时，分，秒）
	 * 
	 * @param dateEnum
	 * @return
	 */
	public static Integer getNowDateNode(DateEnum dateEnum) {
		LocalDateTime nowDate = LocalDateTime.now();
		Integer nowNode = null;
		switch (dateEnum) {
		case YEAR:
			nowNode = nowDate.getYear();
			break;
		case MONTH:
			nowNode = nowDate.getMonthValue();
			break;
		case WEEK:
			nowNode = conversionWeek2Num(WeekEnum.valueOf(nowDate.getDayOfWeek().toString()));
			break;
		case DAY:
			nowNode = nowDate.getDayOfMonth();
			break;
		case DAYOFYEAR:
			nowNode = nowDate.getDayOfYear();
			break;
		case HOUR:
			nowNode = nowDate.getHour();
			break;
		case MINUTE:
			nowNode = nowDate.getMinute();
			break;
		case SECOND:
			nowNode = nowDate.getSecond();
			break;
		default:
			break;
		}
		return nowNode;
	}

	/**
	 * 转换英文周为数字
	 * 
	 * @param weekEnum
	 * @return
	 */
	public static int conversionWeek2Num(WeekEnum weekEnum) {
		switch (weekEnum) {
		case MONDAY:
			return 1;
		case TUESDAY:
			return 2;
		case WEDNESDAY:
			return 3;
		case THURSDAY:
			return 4;
		case FRIDAY:
			return 5;
		case SATURDAY:
			return 6;
		case SUNDAY:
			return 7;
		default:
			return -1;
		}

	}

	/**
	 * 根据（年，月，周，日，时，分，秒）和 时间间隔获取当前时间之前或之后的日期时间
	 * 
	 * @param dateEnum
	 * @param time
	 * @return 返回yyyy-MM-dd HH:mm:ss时间格式
	 */
	public static String getPreOrAfterDateTime(DateEnum dateEnum, long time) {
		LocalDateTime nowDate = LocalDateTime.now();
		switch (dateEnum) {
		case YEAR:
			return nowDate.plusYears(time).format(YEAH_MONTH_DAY_HOUR_MINUTE_SECOND);
		case MONTH:
			return nowDate.plusMonths(time).format(YEAH_MONTH_DAY_HOUR_MINUTE_SECOND);
		case WEEK:
			return nowDate.plusWeeks(time).format(YEAH_MONTH_DAY_HOUR_MINUTE_SECOND);
		case DAY:
			return nowDate.plusDays(time).format(YEAH_MONTH_DAY_HOUR_MINUTE_SECOND);
		case HOUR:
			return nowDate.plusHours(time).format(YEAH_MONTH_DAY_HOUR_MINUTE_SECOND);
		case MINUTE:
			return nowDate.plusMinutes(time).format(YEAH_MONTH_DAY_HOUR_MINUTE_SECOND);
		case SECOND:
			return nowDate.plusSeconds(time).format(YEAH_MONTH_DAY_HOUR_MINUTE_SECOND);
		default:
			return null;
		}
	}

	/**
	 * 根据（年，月，周，日）和 时间间隔获取当前时间之前或之后的日期时间
	 * 
	 * @param dateEnum
	 * @param time
	 * @return 返回yyyy-MM-dd时期格式
	 */
	public static String getPreOrArterSimpleDate(DateEnum dateEnum, long time) {
		LocalDateTime nowDate = LocalDateTime.now();
		switch (dateEnum) {
		case YEAR:
			return nowDate.plusYears(time).format(YEAH_MONTH_DAY);
		case MONTH:
			return nowDate.plusMonths(time).format(YEAH_MONTH_DAY);
		case WEEK:
			return nowDate.plusWeeks(time).format(YEAH_MONTH_DAY);
		case DAY:
			return nowDate.plusDays(time).format(YEAH_MONTH_DAY);
		default:
			return null;
		}
	}

	/**
	 * 判断出入的年份是否是闰年
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isLeapYear(String date) {
		return LocalDate.parse(date.trim()).isLeapYear();
	}

	/**
	 * 判断两个yyyy-MM-dd日期格式的两个时间是之前还是之后
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static boolean compareSimpleDate(String begin, String end) {
		LocalDate beginDate = LocalDate.parse(begin, YEAH_MONTH_DAY);
		LocalDate endDate = LocalDate.parse(end, YEAH_MONTH_DAY);
		return beginDate.isBefore(endDate);
	}

	/**
	 * 判断两个yyyy-MM-dd HH:mm:ss日期格式的两个时间是之前还是之后
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public static boolean compareFullDate(String begin, String end) {
		LocalDate beginDate = LocalDate.parse(begin, YEAH_MONTH_DAY_HOUR_MINUTE_SECOND);
		LocalDate endDate = LocalDate.parse(end, YEAH_MONTH_DAY_HOUR_MINUTE_SECOND);
		return beginDate.isBefore(endDate);
	}
	
	/**
	 * 根据时间段获取两个时间段之间的相差的（年、月、日）
	 * @param begin
	 * @param end
	 * @param dateEnum
	 * @return
	 */
	public static Integer getPeridNum(String begin,String end,DateEnum dateEnum) {
		switch(dateEnum) {
		case YEAR:
			return Period.between(LocalDate.parse(begin), LocalDate.parse(end)).getYears();
		case MONTH:
			return Period.between(LocalDate.parse(begin), LocalDate.parse(end)).getMonths();
		case DAY:
			return Period.between(LocalDate.parse(begin), LocalDate.parse(end)).getDays();
		default:
			return null;
		}
	}

}
