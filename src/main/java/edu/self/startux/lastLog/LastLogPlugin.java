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

import java.util.logging.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class LastLogPlugin extends JavaPlugin implements Listener {
        private Logger logger;
        final public static String NOTIFY_PERMISSION = "lastlog.notify";
        private PlayerList firstlogList;
        private PlayerList lastlogList;
        private LastLogExecutor firstLogExecutor = new LastLogExecutor(this, false);
        private LastLogExecutor lastLogExecutor = new LastLogExecutor(this, true);
        private LogInfoExecutor logInfoExecutor = new LogInfoExecutor(this);
        private HelpScreen helpScreen;

        // // debug function. requires command registration!
        // @Override
        // public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //         PlayerJoinEvent event = new PlayerJoinEvent(getServer().getOnlinePlayers()[0], "hello");
        //         getServer().getPluginManager().callEvent(event);
        //         return true;
        // }

        @Override
        public void onEnable() {
                getCommand("firstlog").setExecutor(firstLogExecutor);
                getCommand("lastlog").setExecutor(lastLogExecutor);
                getCommand("loginfo").setExecutor(logInfoExecutor);
                getServer().getPluginManager().registerEvents(this, this);
                logger = getServer().getLogger();
                // Apparently what takes the most time are the following I/O heavy instructions.
                // Hence, their output will be cached. Initialize the cache with bukkit data.
                OfflinePlayer[] players = getServer().getOfflinePlayers();
                firstlogList = new PlayerList(players, false);
                lastlogList = new PlayerList(players, true);
                helpScreen = new HelpScreen(this);
        }

        @Override
        public void onDisable() {
                logger = null;
                firstlogList = null;
                lastlogList = null;
                helpScreen = null;
        }

        /**
         * Update the appropriate caches when a player joins.
         * At this point we get the time from System rather than
         * from Bukkit as the latter turned out faulty at times.
         */
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
                String name = player.getName();
                long last = System.currentTimeMillis();
                lastlogList.set(name, last);
                if (!player.hasPlayedBefore()) {
                        long first = last;
                        firstlogList.set(name, first);
                        String message = LastLogColors.UNKNOWN + name + LastLogColors.HEADER + " has logged in for the first time";
                        // getServer().broadcast(message, NOTIFY_PERMISSION); // this acts weird
                        getServer().getConsoleSender().sendMessage(message);
                        for (Player rec : getServer().getOnlinePlayers()) {
                                if (rec.hasPermission(NOTIFY_PERMISSION)) {
                                        rec.sendMessage(message);
                                }
                        }
                }
        }

        public void help(CommandSender sender) {
                helpScreen.send(sender);
        }

        public PlayerList getPlayerList(boolean lastlog) {
                if (lastlog) return lastlogList;
                return firstlogList;
        }
}
