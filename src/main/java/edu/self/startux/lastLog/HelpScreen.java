/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright 2012-2018 StarTux.
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

import java.io.InputStreamReader;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

final class HelpScreen {
    private LastLogPlugin plugin;
    private String[] lines;

    HelpScreen(LastLogPlugin plugin) {
        this.plugin = plugin;
        ConfigurationSection config = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("help.yml")));
        lines = ChatColor.translateAlternateColorCodes('&', config.getString("helpmessage")).split("\n");
    }

    void send(CommandSender sender) {
        sender.sendMessage(LastLogColors.HEADER + "[LastLog] Help");
        for (String line : lines) {
            sender.sendMessage(line);
        }
    }
}
