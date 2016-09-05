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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class PlayerList implements Iterable<PlayerList.Entry> {
	final private static int PAGE_LENGTH = 10; // how many lines per page?
	private ArrayList<Entry> playerList;
	static private Comparator<Entry> comparator;

	static {
		comparator = new Comparator<Entry>() {
			public int compare(Entry a, Entry b) {
				if (a.time > b.time)
					return -1;
				if (a.time < b.time)
					return 1;
				return 0;
			}
		};
	}

	public static class Options {
		int pageNumber = 0;
		LastLogDate after = null;
		LastLogDate before = null;
	}

	public class Entry {
		public String name;
		public UUID uuid;
		public long time;

		public Entry(UUID uuid, String name, long time) {
			this.uuid = uuid;
			this.name = name;
			this.time = time;
		}
	}

	public class PlayerListIterator implements Iterator<Entry> {
		private int i = -1;
		PlayerList list;

		public PlayerListIterator(PlayerList list) {
			this.list = list;
		}

		@Override
		public boolean hasNext() {
			if (i + 1 >= list.getLength())
				return false;
			return true;
		}

		@Override
		public Entry next() {
			return list.getEntry(++i);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public PlayerList(OfflinePlayer[] playerList, boolean lastlog) {
		this.playerList = new ArrayList<Entry>();

		for (OfflinePlayer player : playerList) {
			long time = (lastlog ? player.getLastPlayed() : player.getFirstPlayed());

			// Not sure why, but sometimes a player has a name = null
			// To avoid NullPointerExceptions, we'll skip this entry
			if (player.getUniqueId() != null && player.getName() != null)
				this.playerList.add(new Entry(player.getUniqueId(), player.getName(), time));
		}
	}

	public void sort() {
		Collections.sort(playerList, comparator);
	}

	public Iterator<Entry> iterator() {
		return new PlayerListIterator(this);
	}

	public int getLength() {
		return playerList.size();
	}

	public Entry getEntry(int index) {
		if (index >= playerList.size()) {
			throw new IndexOutOfBoundsException("PlayerList.playerList");
		}
		return playerList.get(index);
	}

	public Entry getEntry(UUID uuid) {
		for (Entry entry : playerList) {
			if (entry.uuid != null && entry.uuid.compareTo(uuid)==0)
				return entry;
		}
		return null;
	}

	public Entry addEntry(UUID uuid, String name) {

		// Null check
		if (name == null)
			throw new IllegalArgumentException("PlayerList.playerList");

		Entry entry = new Entry(uuid, name, 0);
		playerList.add(entry);
		return entry;
	}

	public void set(UUID uuid, String name, long time) {
		Entry entry = getEntry(uuid);
		if (entry == null) {
			entry = addEntry(uuid, name);
		}
		entry.time = time;
	}

	public int getPageCount() {
		return ((getLength() - 1) / PAGE_LENGTH) + 1;
	}

	public void displayPage(Options options, boolean lastlog, CommandSender sender) {
		int minIndex = 0, maxIndex = getLength() - 1;

        // find minIndex thru after option
        if (options.after != null) {
                for (int i = maxIndex; i >= minIndex; --i) {
                        if (getEntry(i).time >= options.after.getTime()) {
                                maxIndex = i;
                                break;
                        }
                        maxIndex = minIndex - 1;
                }
        }
		
        // find max index thru before option
        if (options.before != null) {
                for (int i = minIndex; i <= maxIndex; ++i) {
                        if (getEntry(i).time <= options.before.getTime()) {
                                minIndex = i;
                                break;
                        }
                        minIndex = maxIndex + 1;
                }
        }
        
		// check length and page count
		int length = maxIndex - minIndex + 1;
		
		if (length == 0) {
			if (options.after != null) {
				sender.sendMessage(LastLogColors.MINOR + "After " + options.after);
			}
			if (options.before != null) {
				sender.sendMessage(LastLogColors.MINOR + "Before " + options.before);
			}
			sender.sendMessage(LastLogColors.ERROR + "[LastLog] Empty list!");
			return;
		}
		
		int pageCount = ((length - 1) / PAGE_LENGTH) + 1;
		
		if (options.pageNumber >= pageCount) {
			sender.sendMessage(LastLogColors.ERROR + "[LastLog] Page " + (options.pageNumber + 1)
					+ " selected, but only " + pageCount + " available!");
			return;
		}
		sender.sendMessage(LastLogColors.HEADER + (lastlog ? "Last login" : "First login") + " - " + length
				+ " Players - Page [" + (options.pageNumber + 1) + "/" + pageCount + "]");
		
		if (options.after != null) {
			sender.sendMessage(LastLogColors.MINOR + "After " + options.after);
		}
		
		if (options.before != null) {
			sender.sendMessage(LastLogColors.MINOR + "Before " + options.before);
		}
		
		for (int i = 0; i < PAGE_LENGTH; ++i) {
			int index = minIndex + options.pageNumber * PAGE_LENGTH + i;
			if (index > maxIndex || index < minIndex)
				break;
			Entry entry = getEntry(index);
			String nameColor = Bukkit.getServer().getOfflinePlayer(entry.uuid).isOnline() ? LastLogColors.ONLINE
					: LastLogColors.RESET;
			sender.sendMessage(LastLogColors.DATE + new LastLogDate(entry.time) + " " + nameColor + entry.name);
		}
	}
}
