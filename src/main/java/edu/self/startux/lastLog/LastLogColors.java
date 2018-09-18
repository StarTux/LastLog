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

import org.bukkit.ChatColor;

final class LastLogColors {
    private LastLogColors() { }

    public static final String PLAYER_NAME = "" + ChatColor.GOLD;
    public static final String HEADER = "" + ChatColor.AQUA;
    public static final String MINOR = "" + ChatColor.DARK_AQUA;
    public static final String DATE = "" + ChatColor.YELLOW;
    public static final String UNKNOWN = "" + ChatColor.GRAY;
    public static final String ERROR = "" + ChatColor.RED;
    public static final String RESET = "" + ChatColor.WHITE;
    public static final String ONLINE = "" + ChatColor.GREEN;
    public static final String OFFLINE = "" + ChatColor.RED;
}
