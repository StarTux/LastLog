/*
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
 */

package edu.self.startux.lastLog;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

// Date with more decent output
public class LastLogDate extends Date
{
        public LastLogDate() {
                super();
        }

        public LastLogDate(long time) {
                super(time);
        }

        @Override
        public String toString() {
                Calendar cal = new GregorianCalendar();
                cal.setTime(this);
                Locale locale = Locale.US;
                ByteArrayOutputStream string = new ByteArrayOutputStream(24);
                PrintStream printStream = new PrintStream(string);
                printStream.format("%3s %3s %02d %04d %02d:%02d:%02d",
                                   cal.getDisplayName(cal.DAY_OF_WEEK, cal.SHORT, locale),
                                   cal.getDisplayName(cal.MONTH, cal.SHORT, locale),
                                   cal.get(cal.DAY_OF_MONTH),
                                   cal.get(cal.YEAR),
                                   cal.get(cal.HOUR_OF_DAY),
                                   cal.get(cal.MINUTE),
                                   cal.get(cal.SECOND));
                return string.toString();
        }
}
