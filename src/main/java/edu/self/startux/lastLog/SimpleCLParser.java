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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;

public class SimpleCLParser implements CommandLineParser {
        private static class FlagData {
                public Flag flag = null;
                public int argc = 0;
        }

        private static class SimpleResult implements ParseResult {
                private Map<Flag, ArrayList<ArrayList<String>>> map = new HashMap<Flag, ArrayList<ArrayList<String>>>();
                private List<String> orphans = new ArrayList<String>();

                public void addFlag(Flag flag, ArrayList<String> args) {
                        ArrayList<ArrayList<String>> result = map.get(flag);
                        if (result == null) {
                                result = new ArrayList<ArrayList<String>>();
                                map.put(flag, result);
                        }
                        result.add(args);
                }

                public void addOrphan(String orphan) {
                        orphans.add(orphan);
                }

                @Override
                public int countFlag(Flag flag) {
                        ArrayList<ArrayList<String>> result = map.get(flag);
                        if (result == null) return 0;
                        return result.size();
                }

                @Override
                public List<? extends List<String>> getArgs(Flag flag) {
                        ArrayList<ArrayList<String>> result = map.get(flag);
                        if (result == null) return new ArrayList<ArrayList<String>>(0);
                        return result;
                }

                @Override
                public List<String> getOrphans() {
                        return orphans;
                }
        }

        private List<FlagData> dataList = new ArrayList<FlagData>();

        private FlagData getData(Flag key) {
                for (FlagData data : dataList) {
                        if (key.equals(data.flag)) return data;
                }
                return null;
        }

        private FlagData getData(char key) {
                for (FlagData data : dataList) {
                        if (data.flag.matches(key)) return data;
                }
                return null;
        }

        private FlagData getData(String key) {
                for (FlagData data : dataList) {
                        if (data.flag.matches(key)) return data;
                }
                return null;
        }

        @Override
        public boolean addFlag(Flag flag) {
                for (FlagData data : dataList) { // check for duplicates
                        if (flag.conflictsWith(data.flag)) {
                                return false;
                        }
                }
                FlagData data = new FlagData();
                data.flag = new Flag(flag);
                dataList.add(data);
                return true;
        }

        @Override
        public Flag addFlag(char shortFlag) {
                Flag flag = new Flag(shortFlag, null);
                if (!addFlag(flag)) return null;
                return flag;
        }

        @Override
        public Flag addFlag(String longFlag) {
                Flag flag = new Flag('\0', longFlag);
                if (!addFlag(flag)) return null;
                return flag;
        }

        @Override
        public Flag addFlag(char shortFlag, String longFlag) {
                Flag flag = new Flag(shortFlag, longFlag);
                if (!addFlag(flag)) return null;
                return flag;
        }

        @Override
        public Flag getFlag(char shortFlag) {
                FlagData data = getData(shortFlag);
                if (data == null) return null;
                return data.flag;
        }

        @Override
        public Flag getFlag(String longFlag) {
                FlagData data = getData(longFlag);
                if (data == null) return null;
                return data.flag;
        }


        @Override
        public void setArgCount(Flag flag, int argc) {
                FlagData data = getData(flag);
                if (data == null) return;
                data.argc = argc;
        }

        private boolean isShortFlag(String arg) {
                if (arg.length() > 1 && arg.charAt(0) == '-' && arg.charAt(1) != '-') return true;
                return false;
        }

        private boolean isLongFlag(String arg) {
                if (arg.length() > 2 && arg.startsWith("--")) return true;
                return false;
        }

        private boolean isFlag(String arg) {
                return isShortFlag(arg) || isLongFlag(arg);
        }

        public ArrayList<String> parseFlag(FlagData data, ListIterator<String> iter) throws ParseException {
                ArrayList<String> ls = new ArrayList<String>();
                int pos = iter.previousIndex();
                for (int i = 0; i < data.argc; ++i) {
                        if (!iter.hasNext()) {
                                throw new ParseException(data.flag, pos, "argument required");
                        }
                        String val = iter.next();
                        if (isFlag(val)) {
                                throw new ParseException(data.flag, pos, "argument required");
                        }
                        ls.add(val);
                }
                return ls;
        }

        @Override
        public ParseResult parse(String[] args) throws ParseException {
                SimpleResult result = new SimpleResult();
                ListIterator<String> iter = Arrays.asList(args).listIterator();
                while (iter.hasNext()) {
                        String arg = iter.next();
                        if (arg.equals("--")) {
                                while (iter.hasNext()) {
                                        result.addOrphan(iter.next());
                                }
                        } else if (isLongFlag(arg)) {
                                arg = arg.substring(2);
                                int equalsPos = arg.indexOf('=');
                                ListIterator<String> useIter = iter;
                                if (equalsPos != -1) {
                                        String tmp = arg.substring(equalsPos + 1);
                                        useIter = Arrays.asList(tmp).listIterator();
                                        arg = arg.substring(0, equalsPos);
                                }
                                FlagData data = getData(arg);
                                if (data == null) {
                                        throw new ParseException(new Flag('\0', arg), iter.previousIndex(), "invalid option");
                                }
                                result.addFlag(data.flag, parseFlag(data, useIter));
                        } else if (isShortFlag(arg)) {
                                arg = arg.substring(1);
                                for (int i = 0; i < arg.length(); ++i) {
                                        char c = arg.charAt(i);
                                        FlagData data = getData(c);
                                        if (data == null) {
                                                throw new ParseException(new Flag(c, null), iter.previousIndex(), "invalid option");
                                        }
                                        if (data.argc > 0 && i != arg.length() - 1) {
                                                throw new ParseException(data.flag, iter.previousIndex(), "argument required");
                                        }
                                        result.addFlag(data.flag, parseFlag(data, iter));
                                }
                        } else {
                                result.addOrphan(arg);
                        }
                }
                return result;
        }

        @Override
        public Flag[] getFlags() {
                Flag[] flags = new Flag[dataList.size()];
                int i = 0;
                for (FlagData data : dataList) {
                        flags[i++] = new Flag(data.flag);
                }
                return flags;
        }
}