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

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LogInfoExecutor implements CommandExecutor {
        private LastLogPlugin plugin;

        public LogInfoExecutor(LastLogPlugin plugin) {
                this.plugin = plugin;
        }
        
        public OfflinePlayer findPlayer(UUID uuid) {
                // try match exact name with any known player
                OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
                
                // if none is found, be more lenient with online players
                if (player != null && !player.hasPlayedBefore()) {
                        player = plugin.getServer().getPlayer(uuid);
                }

                return player;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (args.length == 0) {
                        if (sender.equals(plugin.getServer().getConsoleSender())) {
                                return false;
                        }
                        args = new String[1];
                        args[0] = sender.getName();
                }
                for (String arg : args) {
                        if (arg.equalsIgnoreCase(sender.getName())) {
                                if (!sender.hasPermission("lastlog.loginfo") && !sender.hasPermission("lastlog.self")) {
                                        sender.sendMessage(LastLogColors.ERROR + "You don't have permission!");
                                        return true;
                                }
                        } else {
                                if (!sender.hasPermission("lastlog.loginfo")) {
                                        sender.sendMessage(LastLogColors.ERROR + "You don't have permission!");
                                        return true;
                                }
                        }
                        OfflinePlayer player = findByName(arg);
                        if (player == null) {
                                sender.sendMessage("Player "
                                                   + LastLogColors.UNKNOWN + arg + LastLogColors.RESET
                                                   + " is unknown");
                        } else {
                        	    UUID uuid = player.getUniqueId();
                                String name = player.getName();
                                long first = player.getFirstPlayed();
                                // Sometimes Bukkit stubbornly reports bogus dates even though they were accurate during initialization.
                                // In those cases, fetch them from the cache.
                                if (first == 0l) {
                                	 PlayerList.Entry entry = findByName(plugin.getPlayerList(false), name);
                                        if (entry != null) {
                                                first = entry.time;
                                        }
                                }
                                long last = player.getLastPlayed();
                                if (last == 0l) {
                                        PlayerList.Entry entry = findByName(plugin.getPlayerList(true), name);
                                        if (entry != null) {
                                                last = entry.time;
                                        }
                                }
                                sender.sendMessage("Player " + LastLogColors.PLAYER_NAME + name + " " + (player.isOnline() ? LastLogColors.ONLINE + "Online" : LastLogColors.OFFLINE + "Offline"));
                                sender.sendMessage(LastLogColors.DATE
                                                   + new LastLogDate(first)
                                                   + LastLogColors.RESET
                                                   + " First Login");
                                sender.sendMessage(LastLogColors.DATE
                                                   + new LastLogDate(last)
                                                   + LastLogColors.RESET
                                                   + " Last Login");
                        }
                }
                return true;
        }
        
        public PlayerList.Entry findByName(PlayerList playerList, String name){
        	for(PlayerList.Entry entry:playerList){
        		if(entry.name.equalsIgnoreCase(name))
        			return entry;
        	}
        	return null;
        }
        
        public OfflinePlayer findByName(String name){
        	
        	 OfflinePlayer[] players = plugin.getServer().getOfflinePlayers();
        	
        	for(OfflinePlayer player: players){
        		
        		//Not sure why these nulls can happen but they do... 
        		if(player != null && player.getName() != null && player.getName().equalsIgnoreCase(name))
        			return player;
        	}
        	return null;
        }
}
