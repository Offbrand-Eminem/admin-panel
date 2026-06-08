package de.rawnet.playerfreeze;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public final class PlayerFreezePlugin extends JavaPlugin implements Listener {
   private static final String PERMISSION = "playerfreeze.use";
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().character('&').hexColors().build();
   private static final PotionEffectType BLINDNESS_TYPE;
   private static final PotionEffect BLINDNESS;
   private static final Title.Times FROZEN_TITLE_TIMES;
   private final Set<UUID> frozenPlayers = new HashSet();
   private final Map<UUID, FlightState> flightStates = new HashMap();
   private PluginSettings settings;
   private PluginMessages messages;

   public void onEnable() {
      this.saveDefaultJson("config.json");
      this.saveDefaultJson("messages.json");
      this.settings = (PluginSettings)this.loadJson("config.json", PluginSettings.class, PlayerFreezePlugin.PluginSettings.defaults());
      this.messages = (PluginMessages)this.loadJson("messages.json", PluginMessages.class, PlayerFreezePlugin.PluginMessages.defaults());
      this.loadFrozenPlayers();
      FreezeCommand freezeCommand = new FreezeCommand(true);
      FreezeCommand unfreezeCommand = new FreezeCommand(false);
      this.registerCommand("freeze", freezeCommand);
      this.registerCommand("unfreeze", unfreezeCommand);
      this.registerAdmitCommand();
      Bukkit.getPluginManager().registerEvents(this, this);

      for(Player player : Bukkit.getOnlinePlayers()) {
         if (this.isFrozen(player)) {
            this.applyFreezeEffects(player);
         }
      }

   }

   public void onDisable() {
      for(Player player : Bukkit.getOnlinePlayers()) {
         if (this.isFrozen(player)) {
            this.clearFreezeEffects(player);
         }
      }

      this.saveFrozenPlayers();
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onPlayerMove(PlayerMoveEvent event) {
      Player player = event.getPlayer();
      if (this.isFrozen(player) && event.getTo() != null && this.hasChangedPosition(event.getFrom(), event.getTo())) {
         Location lockedLocation = event.getFrom().clone();
         lockedLocation.setYaw(event.getTo().getYaw());
         lockedLocation.setPitch(event.getTo().getPitch());
         event.setTo(lockedLocation);
      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      if (this.isFrozen(player)) {
         this.applyFreezeEffects(player);
         this.send(player, this.messages.stillFrozen);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onBlockBreak(BlockBreakEvent event) {
      if (this.isFrozen(event.getPlayer())) {
         event.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onBlockPlace(BlockPlaceEvent event) {
      if (this.isFrozen(event.getPlayer())) {
         event.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onItemDrop(PlayerDropItemEvent event) {
      if (this.isFrozen(event.getPlayer())) {
         event.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onItemPickup(PlayerPickupItemEvent event) {
      if (this.isFrozen(event.getPlayer())) {
         event.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onEntityDamage(EntityDamageEvent event) {
      Entity var3 = event.getEntity();
      if (var3 instanceof Player player) {
         if (this.isFrozen(player)) {
            event.setCancelled(true);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      Player damager = this.getPlayerDamager(event);
      if (damager != null && this.isFrozen(damager)) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      if (this.isFrozen(player)) {
         this.runConfiguredCommands(this.settings.logoutCommands, player);
         this.unfreezeAfterBan(player);
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
      this.handlePossibleBanCommand(event.getMessage());
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onServerCommand(ServerCommandEvent event) {
      this.handlePossibleBanCommand(event.getCommand());
   }

   private void registerCommand(String name, FreezeCommand executor) {
      PluginCommand command = this.getCommand(name);
      if (command == null) {
         this.getLogger().severe("Command '" + name + "' is missing in plugin.yml.");
      } else {
         command.setExecutor(executor);
         command.setTabCompleter(executor);
      }
   }

   private void registerAdmitCommand() {
      PluginCommand command = this.getCommand("admit");
      if (command == null) {
         this.getLogger().severe("Command 'admit' is missing in plugin.yml.");
      } else {
         command.setExecutor(new AdmitCommand());
      }
   }

   private void freeze(Player target, CommandSender sender) {
      if (!this.frozenPlayers.add(target.getUniqueId())) {
         this.send(sender, this.format(this.messages.alreadyFrozen, target.getName(), (String)null));
      } else {
         this.applyFreezeEffects(target);
         this.saveFrozenPlayers();
         this.send(sender, this.format(this.messages.frozenSender, target.getName(), (String)null));
         this.send(target, this.messages.frozenTarget);
      }
   }

   private void unfreeze(Player target, CommandSender sender) {
      if (!this.frozenPlayers.remove(target.getUniqueId())) {
         this.send(sender, this.format(this.messages.notFrozen, target.getName(), (String)null));
      } else {
         this.clearFreezeEffects(target);
         this.saveFrozenPlayers();
         this.send(sender, this.format(this.messages.unfrozenSender, target.getName(), (String)null));
         this.send(target, this.messages.unfrozenTarget);
      }
   }

   private void admit(Player player) {
      if (!this.isFrozen(player)) {
         this.send(player, this.messages.admitOnlyFrozen);
      } else {
         this.runConfiguredCommands(this.settings.admitCommands, player);
         this.unfreezeAfterBan(player);
      }
   }

   private void handlePossibleBanCommand(String commandLine) {
      String[] parts = commandLine.strip().split("\\s+");
      if (parts.length >= 2 && this.isBanCommand(parts[0])) {
         Bukkit.getScheduler().runTask(this, () -> {
            Player target = Bukkit.getPlayerExact(parts[1]);
            if (target != null) {
               this.unfreezeAfterBan(target);
            }

         });
      }
   }

   private boolean isBanCommand(String commandName) {
      String normalized = commandName.startsWith("/") ? commandName.substring(1) : commandName;
      int namespaceSeparator = normalized.indexOf(58);
      if (namespaceSeparator >= 0) {
         normalized = normalized.substring(namespaceSeparator + 1);
      }

      return normalized.equalsIgnoreCase("ban");
   }

   private Player getPlayerDamager(EntityDamageByEntityEvent event) {
      Entity var3 = event.getDamager();
      if (var3 instanceof Player player) {
         return player;
      } else {
         var3 = event.getDamager();
         if (var3 instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player player) {
               return player;
            }
         }

         return null;
      }
   }

   private void unfreezeAfterBan(Player player) {
      if (this.frozenPlayers.remove(player.getUniqueId())) {
         this.clearFreezeEffects(player);
         this.saveFrozenPlayers();
      }
   }

   private void runConfiguredCommands(List<String> commands, Player player) {
      for(String command : commands) {
         String formatted = this.format(command, player.getName(), (String)null).strip();
         if (formatted.startsWith("/")) {
            formatted = formatted.substring(1);
         }

         if (!formatted.isBlank()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formatted);
         }
      }

   }

   private void applyFreezeEffects(Player player) {
      this.storeFlightState(player);
      player.setAllowFlight(true);
      if (!player.isOnGround()) {
         player.setFlying(true);
      }

      if (this.settings.blindnessEnabled) {
         player.addPotionEffect(BLINDNESS);
      } else {
         player.removePotionEffect(BLINDNESS_TYPE);
      }

      player.setVelocity(player.getVelocity().zero());
      if (!this.settings.titleEnabled) {
         player.clearTitle();
      } else {
         player.showTitle(Title.title(LEGACY.deserialize(this.messages.freezeTitle), LEGACY.deserialize(this.messages.freezeSubtitle), FROZEN_TITLE_TIMES));
      }
   }

   private void clearFreezeEffects(Player player) {
      player.removePotionEffect(BLINDNESS_TYPE);
      player.clearTitle();
      this.restoreFlightState(player);
   }

   private void storeFlightState(Player player) {
      this.flightStates.putIfAbsent(player.getUniqueId(), new FlightState(player.getAllowFlight(), player.isFlying()));
   }

   private void restoreFlightState(Player player) {
      FlightState state = (FlightState)this.flightStates.remove(player.getUniqueId());
      if (state != null) {
         if (!state.allowFlight) {
            player.setFlying(false);
         } else {
            player.setAllowFlight(true);
            player.setFlying(state.flying);
         }

         player.setAllowFlight(state.allowFlight);
      } else {
         boolean shouldAllowFlight = player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR;
         player.setFlying(false);
         player.setAllowFlight(shouldAllowFlight);
      }
   }

   private boolean isFrozen(Player player) {
      return this.frozenPlayers.contains(player.getUniqueId());
   }

   private boolean hasChangedPosition(Location from, Location to) {
      return from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();
   }

   private void loadFrozenPlayers() {
      this.frozenPlayers.clear();
      FrozenPlayersData data = (FrozenPlayersData)this.loadJson("frozen-players.json", FrozenPlayersData.class, new FrozenPlayersData());

      for(String value : data.frozenPlayers) {
         try {
            this.frozenPlayers.add(UUID.fromString(value));
         } catch (IllegalArgumentException var5) {
            this.getLogger().warning("Invalid UUID in frozen-players.json: " + value);
         }
      }

   }

   private void saveFrozenPlayers() {
      FrozenPlayersData data = new FrozenPlayersData();
      data.frozenPlayers = this.frozenPlayers.stream().map(UUID::toString).sorted().toList();
      this.saveJson("frozen-players.json", data);
   }

   private void send(CommandSender receiver, String message) {
      String prefix = this.messages.prefix == null ? "" : this.messages.prefix;
      String formatted = prefix.isBlank() ? message : prefix + " " + message;
      receiver.sendMessage(LEGACY.deserialize(formatted));
   }

   private String format(String text, String playerName, String commandLabel) {
      String formatted = text == null ? "" : text;
      if (playerName != null) {
         formatted = formatted.replace("[User]", playerName).replace("[user]", playerName).replace("[Player]", playerName).replace("[player]", playerName).replace("{user}", playerName).replace("{player}", playerName).replace("%user%", playerName).replace("%player%", playerName);
      }

      if (commandLabel != null) {
         formatted = formatted.replace("[Command]", commandLabel).replace("[command]", commandLabel).replace("{command}", commandLabel).replace("%command%", commandLabel);
      }

      return formatted;
   }

   private void saveDefaultJson(String fileName) {
      if (!this.getDataFolder().exists() && !this.getDataFolder().mkdirs()) {
         this.getLogger().severe("Could not create plugin data folder.");
      } else {
         Path target = this.getDataFolder().toPath().resolve(fileName);
         if (!Files.exists(target, new LinkOption[0])) {
            this.saveResource(fileName, false);
         }
      }
   }

   private <T> T loadJson(String fileName, Class<T> type, T fallback) {
      Path path = this.getDataFolder().toPath().resolve(fileName);
      if (!Files.exists(path, new LinkOption[0])) {
         this.saveJson(fileName, fallback);
         return fallback;
      } else {
         try {
            Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);

            Object var7;
            try {
               T loaded = (T)GSON.fromJson(reader, type);
               var7 = loaded == null ? fallback : loaded;
            } catch (Throwable var9) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (reader != null) {
               reader.close();
            }

            return (T)var7;
         } catch (JsonSyntaxException | IOException exception) {
            this.getLogger().warning("Could not load " + fileName + ": " + ((Exception)exception).getMessage());
            return fallback;
         }
      }
   }

   private void saveJson(String fileName, Object value) {
      if (!this.getDataFolder().exists() && !this.getDataFolder().mkdirs()) {
         this.getLogger().severe("Could not create plugin data folder.");
      } else {
         Path path = this.getDataFolder().toPath().resolve(fileName);

         try {
            Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);

            try {
               GSON.toJson(value, writer);
            } catch (Throwable var8) {
               if (writer != null) {
                  try {
                     writer.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (writer != null) {
               writer.close();
            }
         } catch (IOException exception) {
            this.getLogger().warning("Could not save " + fileName + ": " + exception.getMessage());
         }

      }
   }

   static {
      BLINDNESS_TYPE = PotionEffectType.BLINDNESS;
      BLINDNESS = new PotionEffect(BLINDNESS_TYPE, Integer.MAX_VALUE, 0, false, false, true);
      FROZEN_TITLE_TIMES = Times.times(Duration.ZERO, Duration.ofHours(24L), Duration.ZERO);
   }

   private final class FreezeCommand implements CommandExecutor, TabCompleter {
      private final boolean freeze;

      private FreezeCommand(boolean freeze) {
         this.freeze = freeze;
      }

      public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
         if (!sender.hasPermission("playerfreeze.use")) {
            PlayerFreezePlugin.this.send(sender, PlayerFreezePlugin.this.messages.noPermission);
            return true;
         } else if (args.length != 1) {
            PlayerFreezePlugin.this.send(sender, PlayerFreezePlugin.this.format(PlayerFreezePlugin.this.messages.usagePlayer, (String)null, label));
            return true;
         } else {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
               PlayerFreezePlugin.this.send(sender, PlayerFreezePlugin.this.format(PlayerFreezePlugin.this.messages.playerNotFound, args[0], (String)null));
               return true;
            } else {
               if (this.freeze) {
                  PlayerFreezePlugin.this.freeze(target, sender);
               } else {
                  PlayerFreezePlugin.this.unfreeze(target, sender);
               }

               return true;
            }
         }
      }

      public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
         if (sender.hasPermission("playerfreeze.use") && args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> names = new ArrayList();

            for(Player player : Bukkit.getOnlinePlayers()) {
               if (player.getName().toLowerCase().startsWith(prefix)) {
                  names.add(player.getName());
               }
            }

            names.sort(String.CASE_INSENSITIVE_ORDER);
            return names;
         } else {
            return List.of();
         }
      }
   }

   private final class AdmitCommand implements CommandExecutor {
      public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
         if (sender instanceof Player player) {
            if (args.length != 0) {
               PlayerFreezePlugin.this.send(sender, PlayerFreezePlugin.this.format(PlayerFreezePlugin.this.messages.usageAdmit, (String)null, label));
               return true;
            } else {
               PlayerFreezePlugin.this.admit(player);
               return true;
            }
         } else {
            PlayerFreezePlugin.this.send(sender, PlayerFreezePlugin.this.messages.onlyPlayers);
            return true;
         }
      }
   }

   private static final class PluginSettings {
      private boolean blindnessEnabled = true;
      private boolean titleEnabled = true;
      private List<String> admitCommands = List.of("ban [User] 14d Cheating - Admitted");
      private List<String> logoutCommands = List.of("ban [User] 30d Logged out whilst frozen");

      private static PluginSettings defaults() {
         return new PluginSettings();
      }
   }

   private static final class PluginMessages {
      private String prefix = "";
      private String noPermission = "&cYou do not have permission to use this command.";
      private String usagePlayer = "&cUsage: /[Command] <player>";
      private String usageAdmit = "&cUsage: /[Command]";
      private String playerNotFound = "&cPlayer '&f[User]&c' was not found.";
      private String alreadyFrozen = "&e[User] is already frozen.";
      private String frozenSender = "&a[User] has been frozen.";
      private String frozenTarget = "&#ff0000&lYou have been frozen.";
      private String notFrozen = "&e[User] is not frozen.";
      private String unfrozenSender = "&a[User] has been unfrozen.";
      private String unfrozenTarget = "&aYou have been unfrozen.";
      private String stillFrozen = "&cYou are still frozen.";
      private String admitOnlyFrozen = "&cYou can only use this command while frozen.";
      private String onlyPlayers = "&cOnly players can use this command.";
      private String freezeTitle = "&#ff0000&lYou are frozen!";
      private String freezeSubtitle = "&fLogging out will result in a ban";

      private static PluginMessages defaults() {
         return new PluginMessages();
      }
   }

   private static final class FrozenPlayersData {
      private List<String> frozenPlayers = List.of();
   }

   private static record FlightState(boolean allowFlight, boolean flying) {
   }
}
