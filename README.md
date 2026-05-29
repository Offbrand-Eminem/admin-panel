# Admin Panel Mod for Minecraft 1.21.1

A comprehensive admin panel mod for Minecraft 1.21.1 with role-based permissions via JSON configuration.

## Features

- **Role-based permissions** with JSON configuration
- **Owner and Admin roles** with different permission levels
- **Ban/Unban system** with custom reasons
- **Kick system** with custom reasons
- **Mute/Unmute system** with duration support
- **Gamemode control** (survival, creative, adventure, spectator)
- **Teleport commands** (tp, tphere)
- **Item giving** command
- **Inventory clearing**
- **Healing** (health, hunger, effects)
- **Flight mode** toggle
- **Speed control** (walk and fly speed)
- **Time control** (day, noon, night, midnight)
- **Weather control** (clear, rain, thunder)
- **Vanish mode** - Hide from players with fake leave/join messages
- **Spectate mode** - Watch players to detect cheating
- **Freeze system** - Freeze players in place
- **Inventory inspection** - View player inventories and ender chests
- **Kill command** - Instantly kill players
- **Broadcast system** - Send alerts to all players
- **God mode** - Invincibility mode
- **Spawn teleport** - Teleport to world spawn
- **Dynamic admin management** (add/remove admins and owners)
- **Configuration reload** without server restart
- **Admin GUI** - Graphical interface for quick admin actions

## Building the Mod

### Prerequisites
- Java 21 or higher
- Gradle 8.0 or higher

### Build Steps

1. Clone or download this project
2. Open a terminal in the project directory
3. Run the following command:
   ```bash
   ./gradlew build
   ```
   (On Windows: `gradlew.bat build`)

4. The compiled mod JAR will be in `build/libs/`

## Installation

1. Install Fabric Loader for Minecraft 1.21.1
2. Place the compiled JAR file in your server's `mods` folder
3. Start the server
4. A default configuration file will be created at `config/admin-panel.json`

## Configuration

The mod uses a JSON configuration file located at `config/admin-panel.json`:

```json
{
  "owners": [],
  "admins": []
}
```

### Setting Up Owners and Admins

**Method 1: Edit the JSON file directly**
- Open `config/admin-panel.json`
- Add player names to the `owners` or `admins` arrays
- Save the file
- Run `/adminreload` in-game to reload the configuration

**Method 2: Use in-game commands (Owner only)**
- `/setowner <player>` - Add a player to the owner list
- `/setadmin <player>` - Add a player to the admin list
- `/removeowner <player>` - Remove a player from the owner list
- `/removeadmin <player>` - Remove a player from the admin list

### Permission Levels

- **Owner**: Full access to all commands, can add/remove admins and owners
- **Admin**: Access to all moderation and management commands except owner management
- **Server OP**: Always has full permissions regardless of role

## Commands

### Player Management
- `/ban <player> [reason]` - Ban a player with optional reason
- `/unban <player>` - Unban a player
- `/kick <player> [reason]` - Kick a player with optional reason
- `/mute <player> [duration]` - Mute a player (duration in minutes or "permanent")
- `/unmute <player>` - Unmute a player
- `/freeze <player>` - Freeze a player in place
- `/unfreeze <player>` - Unfreeze a player
- `/kill [player]` - Kill a player

### Gamemode & Movement
- `/gm <gamemode> [player]` - Change gamemode (survival, creative, adventure, spectator)
- `/tp <player>` - Teleport to a player
- `/tphere <player>` - Teleport a player to you
- `/fly [player]` - Toggle flight mode
- `/speed <type> <value>` - Set speed (type: walk/fly, value: 0.1-10.0)
- `/spawn [player]` - Teleport to world spawn

### Inventory & Health
- `/give <player> <item> [count] [enchantments]` - Give items to a player with optional enchantments
- `/clearinv [player]` - Clear inventory
- `/heal [player]` - Heal a player (health, hunger, effects)
- `/god [player]` - Toggle god mode (invincibility)
- `/invsee <player>` - View a player's inventory
- `/endersee <player>` - View a player's ender chest

### World Control
- `/time <time>` - Set time (day, noon, night, midnight, or number)
- `/weather <weather>` - Set weather (clear, rain, thunder)

### Admin Management (Owner only)
- `/setowner <player>` - Add owner
- `/setadmin <player>` - Add admin
- `/removeowner <player>` - Remove owner
- `/removeadmin <player>` - Remove admin

### Utility
- `/adminpanel` - Show mod information
- `/adminpanel list` - List all owners and admins
- `/adminreload` - Reload configuration
- `/broadcast <message>` - Send a broadcast message to all players (aliases: /bc, /alert)
- `/adminui` - Open the admin panel graphical interface (alias: /gui)

### Stealth & Monitoring
- `/vanish [player]` - Toggle vanish mode (hides from tab list, makes invisible, sends fake leave/join messages)
- `/spectate <player>` - Spectate a player to watch for cheating (alias: /watch)
- `/stopspectate` - Stop spectating (alias: /stopwatch)

## Usage Examples

### First Time Setup

1. Start the server with the mod installed
2. The config file will be created at `config/admin-panel.json`
3. Edit the file to add your Minecraft username as an owner:
   ```json
   {
     "owners": ["YourUsername"],
     "admins": []
   }
   ```
4. Run `/adminreload` in the server console or as an OP
5. You now have full access to all admin commands

### Adding an Admin

```
/setadmin Steve
```

### Banning a Player

```
/ban Griefer123 "Griefing spawn area"
```

### Changing Gamemode

```
/gm creative
/gm adventure Steve
```

### Teleporting

```
/tp Steve
/tphere Alex
```

### Setting Time and Weather

```
/time day
/weather clear
```

### Vanishing to Watch Players

```
/vanish
# You now appear to have left the game to other players
/spectate SuspiciousPlayer
# Watch them for cheating
/stopspectate
/vanish
# You reappear to have joined the game
```

### Freezing a Player

```
/freeze Griefer123
# Player cannot move
/unfreeze Griefer123
```

### Viewing Player Inventory

```
/invsee Steve
/endersee Alex
```

### Broadcasting Messages

```
/broadcast "Server restart in 5 minutes!"
/alert "Event starting at spawn!"
```

### Giving Items with Enchantments

```
/give Steve diamond_sword 1 "minecraft:sharpness:5,minecraft:looting:3"
/give Steve diamond_pickaxe 1 "minecraft:efficiency:5,minecraft:fortune:3,minecraft:unbreaking:3"
/give Alex netherite_chestplate "minecraft:protection:4,minecraft:unbreaking:5"
/give Steve bow "minecraft:power:5,minecraft:infinity:1,minecraft:flame:1"
```

Enchantment format: `enchantment_id:level,enchantment_id:level` (comma-separated)
- Use Minecraft enchantment IDs (e.g., `minecraft:sharpness`, `minecraft:protection`)
- Levels can be any positive integer (no limit!)
- If no level is specified, defaults to 1

### Using the Admin GUI

```
/adminui
# Opens a graphical interface with quick action buttons for:
# - Heal Self, Fly Toggle, God Mode
# - Kick All, Mute All, Clear Inv All
# - Day, Night, Clear Weather
# - Server info display
```

## Notes

- Server OPs (permission level 4) always have full access to all commands
- Owners can add/remove other owners and admins
- Admins cannot modify the owner or admin lists
- The configuration file is automatically saved when changes are made via commands
- Use `/adminreload` to reload the configuration after manual file edits

## Troubleshooting

**Commands not working?**
- Ensure you're listed as an owner or admin in the config
- Check that you have the correct permission level
- Try reloading the config with `/adminreload`

**Config file not creating?**
- Ensure the server has write permissions to the config directory
- Check the server logs for any errors

**Players not being banned/kicked?**
- Verify the player name is spelled correctly
- Check that you have the required permissions

## License

MIT License

## Version

- Minecraft Version: 1.21.1
- Fabric Loader: 0.16.0+
- Java Version: 21+
