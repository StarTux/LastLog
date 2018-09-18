# LastLog
*Show listing of last logged in players.*

## Description
LastLog will list players who last joined your server or joined for the first time.  Furthermore, it can display the first and last login of specific players.  It comes with a rich set of filters to extract precise date ranges of player joins.  Its intended purpose is that server owners and their staff can easily see new players or who joined when.  There is no data storage; LastLog utilizes Bukkit player information which is already readily available and presents in an accessible manor.  Because said methods are computationally expensive, this plugin is not recommended for larger scale servers.

## Links
- [Source code](https://github.com/StarTux/LastLog) on Github
- [BukkitDev plugin page](https://dev.bukkit.org/projects/lastlog)

## Commands

- `/firstlog [pagenumber]` - List the most recent first logins of players
- `/lastlog [pagenumber]` - List the most recent last logins of players
- `/loginfo <playername>` - Display first and last login date of a player

## Permissions

- `lastlog.*` - Get all permission nodes
- `lastlog.lastlog` - Permit use of /lastlog
- `lastlog.firstlog` - Permit use of /firstlog
- `lastlog.loginfo` - Permit use of /loginfo
- `lastlog.notify` - Receive notification when a player joins for the first time

## Installation

Copy the LastLog.jar into the plugins folder. Restart Spigot.

## Configuration

Except for permissions, none.
