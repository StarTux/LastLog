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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LastLogExecutor implements CommandExecutor {
        private LastLogPlugin plugin;
        private boolean lastlog;
        private CommandLineParser parser = new SimpleCLParser();

        public LastLogExecutor(LastLogPlugin plugin, boolean lastlog) {
                this.plugin = plugin;
                this.lastlog = lastlog;
                parser.setArgCount(parser.addFlag('a', "after"), 1);
                parser.setArgCount(parser.addFlag('b', "before"), 1);
                parser.addFlag('h', "help");
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                CommandLineParser.ParseResult result;
                try {
                        result = parser.parse(args);
                } catch (CommandLineParser.ParseException pe) {
                        sender.sendMessage(LastLogColors.ERROR + "Error! " + pe.getMessage());
                        return true;
                }
                if (result.getOrphans().size() > 1) {
                        sender.sendMessage(LastLogColors.ERROR + "Too many arguments!");
                        return true;
                }
                if (result.countFlag(parser.getFlag('a')) > 1) {
                        sender.sendMessage(LastLogColors.ERROR + "Option '-a' used more than once!");
                        return true;
                }
                if (result.countFlag(parser.getFlag('b')) > 1) {
                        sender.sendMessage(LastLogColors.ERROR + "Option '-b' used more than once!");
                        return true;
                }
                if (result.countFlag(parser.getFlag('h')) > 0) {
                        plugin.help(sender);
                        return true;
                }
                PlayerList.Options options = new PlayerList.Options();
                if (result.getOrphans().size() == 1) {
                        try {
                                options.pageNumber = Integer.parseInt(result.getOrphans().get(0)) - 1;
                        } catch (NumberFormatException nfe) {
                                sender.sendMessage(LastLogColors.ERROR + "Not a number: \"" + result.getOrphans().get(0) + "\"");
                                return true;
                        }
                }
                if (options.pageNumber < 0) options.pageNumber = 0;
                if (result.countFlag(parser.getFlag('a')) == 1) {
                        String arg = result.getArgs(parser.getFlag('a')).get(0).get(0);
                        try {
                                options.after = LastLogDate.parseArg(arg);
                        } catch (NumberFormatException nfe) {
                                sender.sendMessage(LastLogColors.ERROR + "Invalid format: " + arg);
                                return true;
                        }
                }
                if (result.countFlag(parser.getFlag('b')) == 1) {
                        String arg = result.getArgs(parser.getFlag('b')).get(0).get(0);
                        try {
                                options.before = LastLogDate.parseArg(arg);
                        } catch (NumberFormatException nfe) {
                                sender.sendMessage(LastLogColors.ERROR + "Invalid format: " + arg);
                                return true;
                        }
                }

                PlayerList playerList = plugin.getPlayerList(lastlog);
                playerList.sort();
                playerList.displayPage(options, lastlog, sender);
                return true;
        }
}
