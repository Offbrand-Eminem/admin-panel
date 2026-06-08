package me.drex.vanish.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Collections;
import me.drex.vanish.VanishMod;
import me.drex.vanish.api.VanishAPI;
import me.drex.vanish.config.ConfigManager;
import me.drex.vanish.util.VanishManager;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.class_11560;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2191;
import net.minecraft.class_2561;
import net.minecraft.class_3222;
import net.minecraft.class_7157;

public class VanishCommand {
   public static void register(CommandDispatcher<class_2168> dispatcher, class_7157 buildContext, class_2170.class_5364 selection) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("vanish").requires((src) -> Permissions.check(src, "vanish.command.vanish", 2))).executes(VanishCommand::vanish)).then(((LiteralArgumentBuilder)class_2170.method_9247("reload").requires((src) -> Permissions.check(src, "vanish.command.vanish.reload", 2))).executes(VanishCommand::reload))).then(((LiteralArgumentBuilder)class_2170.method_9247("on").executes((ctx) -> vanish((class_2168)ctx.getSource(), true, Collections.singleton(((class_2168)ctx.getSource()).method_9207().method_72498())))).then(((RequiredArgumentBuilder)class_2170.method_9244("players", class_2191.method_9329()).requires((src) -> Permissions.check(src, "vanish.command.vanish.other", 2))).executes((ctx) -> vanish((class_2168)ctx.getSource(), true, class_2191.method_9330(ctx, "players")))))).then(((LiteralArgumentBuilder)class_2170.method_9247("off").executes((ctx) -> vanish((class_2168)ctx.getSource(), false, Collections.singleton(((class_2168)ctx.getSource()).method_9207().method_72498())))).then(((RequiredArgumentBuilder)class_2170.method_9244("players", class_2191.method_9329()).requires((src) -> Permissions.check(src, "vanish.command.vanish.other", 2))).executes((ctx) -> vanish((class_2168)ctx.getSource(), false, class_2191.method_9330(ctx, "players"))))));
   }

   public static int vanish(CommandContext<class_2168> ctx) throws CommandSyntaxException {
      class_3222 player = ((class_2168)ctx.getSource()).method_9207();
      boolean vanished = VanishAPI.isVanished(player);
      return vanish((class_2168)ctx.getSource(), !vanished, Collections.singleton(player.method_72498()));
   }

   public static int reload(CommandContext<class_2168> ctx) {
      try {
         ConfigManager.load();
         ((class_2168)ctx.getSource()).method_9226(() -> class_2561.method_43471("text.vanish.command.vanish.reload"), false);
         return 1;
      } catch (Exception e) {
         ((class_2168)ctx.getSource()).method_9213(class_2561.method_43471("text.vanish.command.vanish.reload.error"));
         VanishMod.LOGGER.error("An error occurred while loading the config, keeping old values", e);
         return 0;
      }
   }

   public static int vanish(class_2168 src, boolean vanish, Collection<class_11560> targets) throws CommandSyntaxException {
      int result = 0;

      for(class_11560 target : targets) {
         if (VanishManager.setVanished(target, src.method_9211(), vanish)) {
            class_3222 player = src.method_44023();
            if (player != null && player.method_7334().equals(target)) {
               src.method_9226(() -> class_2561.method_43471(vanish ? "text.vanish.command.vanish.enable" : "text.vanish.command.vanish.disable"), false);
            } else {
               src.method_9226(() -> class_2561.method_43469(vanish ? "text.vanish.command.vanish.enable.other" : "text.vanish.command.vanish.disable.other", new Object[]{target.comp_4423()}), false);
            }

            ++result;
         }
      }

      return result;
   }
}
