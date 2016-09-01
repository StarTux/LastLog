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

import java.text.ParseException;
import java.util.List;

public interface CommandLineParser {
        public static class ParseException extends Exception {
                public ParseException(Flag flag, int pos, String message) {
                        super("Argument " + (pos + 1) + ": '" + flag.getFlags() + "': " + message);
                }
        }

        /**
         * Flag is a dumb convenience type representing a command
         * line flag.
         */
        public static class Flag {
                private char shortFlag = '\0';
                private String longFlag = null;

                /**
                 * @param shortFlag the short version of '\0' if
                 * there is none.
                 * @param longFlag the long version or null if
                 * there is none.
                 */
                public Flag(char shortFlag, String longFlag) {
                        this.shortFlag = shortFlag;
                        this.longFlag = longFlag;
                }

                /**
                 * Copy constructor.
                 */
                public Flag(Flag other) {
                        shortFlag = other.shortFlag;
                        longFlag = new String(other.longFlag);
                }

                @Override
                public int hashCode() {
                        if (shortFlag != '\0') return (int)shortFlag;
                        return longFlag.hashCode();
                }

                public String getFlags() {
                        if (shortFlag != '\0' && longFlag != null) {
                                return "-" + shortFlag + "/--" + longFlag;
                        } else if (shortFlag != '\0') {
                                return "-" + shortFlag;
                        }
                        return "--" + longFlag;
                }

                /**
                 * Check if a command line matches this flag.
                 * @param arg the command line option, without leading dashes
                 * @return true if the command line argument
                 * matches, false otherwise
                 */
                public boolean matches(String arg) {
                        if (arg.equals(longFlag)) return true;
                        return false;
                }

                /**
                 * Check if a command line matches this flag.
                 * @param arg the command line option, without leading dashes
                 * @return true if the command line argument
                 * matches, false otherwise
                 */
                public boolean matches(char arg) {
                        if (shortFlag != '\0' && arg == shortFlag) return true;
                        return false;
                }

                /**
                 * Check if this flag has either the same
                 * shortFlag or the same longFlag as another one.
                 * @param o the other Flag
                 * @return true if they have one thing in common, false otherwise
                 */
                public boolean conflictsWith(Flag o) {
                        if (shortFlag != '\0' && shortFlag == o.shortFlag) return true;
                        if (longFlag != null && longFlag.equals(o.longFlag)) return true;
                        return false;
                }

                @Override
                public boolean equals(Object o) {
                        if (o == null) return false;
                        if (!(o instanceof Flag)) return false;
                        Flag other = (Flag)o;
                        if (shortFlag != other.shortFlag) return false;
                        if (longFlag == null) {
                                if (other.longFlag != null) return false;
                        } else {
                                if (!longFlag.equals(other.longFlag)) return false;
                        }
                        return true;
                }
        }
        /**
         * The result of a parsed command line.
         */
        public static interface ParseResult {
                /**
                 * Return how often a flag has occured.
                 * @param flag the flag
                 * @return the occur count
                 */
                public int countFlag(Flag flag);

                /**
                 * Return the argument values that were followed by a
                 * flag.
                 * @param flag the flag
                 * @return the arguments of each occurence
                 */
                public List<? extends List<String>> getArgs(Flag flag);

                /**
                 * Get the arguments that do not belong to a flag.
                 * @param an array with the orphans.
                 */
                public List<String> getOrphans();
        }

        /**
         * Add another flag to the list of known command line options
         * @param flag the flag
         * @return true if the flag has been accepted, false if there is a conflict
         */
        public boolean addFlag(Flag flag);

        public Flag addFlag(char shortFlag);
        public Flag addFlag(String longFlag);
        public Flag addFlag(char shortFlag, String longFlag);
        public Flag getFlag(char shortFlag);
        public Flag getFlag(String longFlag);

        /**
         * Set the argument count of a flag.
         * @param flag the flag
         * @param argc the argument count
         */
        public void setArgCount(Flag flag, int argc);

        /**
         * Parse command line arguments.
         * @param args the arguments array
         * @return the ParseResult
         */
        public ParseResult parse(String[] args) throws ParseException;

        /**
         * Get a list of all registered flags.
         * @return an array with all the flags.
         */
        public Flag[] getFlags();
}