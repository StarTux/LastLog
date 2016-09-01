/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright 2012 StarTux.
 *
 * This file is part of LastLog.
 *
 * LastLog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LastLog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LastLog.  If not, see <http://www.gnu.org/licenses/>.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package edu.self.startux.lastLog;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.GregorianCalendar;
import java.util.Calendar;

// Date with more decent output
public class LastLogDate extends Date
{
        private static Pattern timePattern; // pattern for a timespan
        private static int[] timeFields = { Calendar.YEAR, Calendar.MONTH, Calendar.WEEK_OF_YEAR, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND };
        private static Pattern datePattern; // pattern for a date to parse
        private static int[] dateFields = { Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND };
        private static Pattern dayPattern;
        private static Pattern clockPattern;
        private static int[] months = { Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST, Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER };

        static {
                {
                        String formatChars = "ymwdHMS";
                        String p = "";
                        for (int i = 0; i < formatChars.length(); ++i) {
                                p += "(\\d+" + formatChars.charAt(i) + ")?";
                        }
                        timePattern = Pattern.compile(p);
                }
                datePattern = Pattern.compile("(\\d{4})-(\\d{1,2})-(\\d{1,2})-(\\d{1,2}):(\\d{1,2}):(\\d{1,2})");
                dayPattern = Pattern.compile("(\\d{4}-)?(\\d{1,2})-(\\d{1,2})");
                clockPattern = Pattern.compile("(\\d{1,2}):(\\d{1,2})(:\\d{1,2})?");
        }

        public LastLogDate() {
                super();
        }

        public LastLogDate(long time) {
                super(time);
        }

        public LastLogDate(Date date) {
                super(date.getTime());
        }

        public static LastLogDate parseArg(String arg) {
                Matcher matcher = timePattern.matcher(arg);
                if (matcher.matches()) {
                        Calendar cal = new GregorianCalendar();
                        for (int i = 0; i < matcher.groupCount() - 1; ++i) {
                                String group = matcher.group(i + 1);
                                if (group == null) continue;
                                int amount = Integer.parseInt(group.substring(0, group.length() - 1));
                                cal.add(timeFields[i], -amount);
                        }
                        return new LastLogDate(cal.getTime());
                }
                matcher = datePattern.matcher(arg);
                if (matcher.matches()) {
                        Calendar cal = new GregorianCalendar();
//                        cal.set(Integer.parseInt(matcher.group(1)), months[Integer.parseInt(matcher.group(2)) - 1], Integer.parseInt(matcher.group(3)));
                        for (int i = 0; i < matcher.groupCount() - 1; ++i) {
                                String group = matcher.group(i + 1);
                                if (group == null) continue;
                                int amount = Integer.parseInt(group);
                                try {
                                        if (dateFields[i] == Calendar.MONTH) amount = months[amount - 1];
                                } catch (ArrayIndexOutOfBoundsException aioobe) {
                                        throw new NumberFormatException("month is out of range");
                                }
                                cal.set(dateFields[i], amount);
                        }
                        return new LastLogDate(cal.getTime());
                }
                matcher = dayPattern.matcher(arg);
                if (matcher.matches()) {
                        Calendar cal = new GregorianCalendar();
                        int year, month, day;
                        year = cal.get(Calendar.YEAR);
                        month = cal.get(Calendar.MONTH);
                        day = cal.get(Calendar.DAY_OF_MONTH);
                        String s = matcher.group(1);
                        if (s != null) {
                                s = s.substring(0, s.length() - 1);
                                year = Integer.parseInt(s);
                        }
                        month = Integer.parseInt(matcher.group(2));
                        day = Integer.parseInt(matcher.group(3));
                        try {
                                cal.set(year, months[month - 1], day);
                        } catch (ArrayIndexOutOfBoundsException aioobe) {
                                throw new NumberFormatException("month out of range");
                        }
                        return new LastLogDate(cal.getTime());
                }
                matcher = clockPattern.matcher(arg);
                if (matcher.matches()) {
                        Calendar cal = new GregorianCalendar();
                        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher.group(1)));
                        cal.set(Calendar.MINUTE, Integer.parseInt(matcher.group(2)));
                        String s = matcher.group(3);
                        int second = 0;
                        if (s != null) {
                                second = Integer.parseInt(s.substring(1));
                        }
                        cal.set(Calendar.SECOND, second);
                        return new LastLogDate(cal.getTime());
                }
                throw new NumberFormatException();
        }

        @Override
        public String toString() {
                Calendar cal = new GregorianCalendar();
                cal.setTime(this);
                Locale locale = Locale.US;
                return String.format("%3s %3s %02d %04d %02d:%02d:%02d",
                                     cal.getDisplayName(cal.DAY_OF_WEEK, cal.SHORT, locale),
                                     cal.getDisplayName(cal.MONTH, cal.SHORT, locale),
                                     cal.get(cal.DAY_OF_MONTH),
                                     cal.get(cal.YEAR),
                                     cal.get(cal.HOUR_OF_DAY),
                                     cal.get(cal.MINUTE),
                                     cal.get(cal.SECOND));
        }
}
