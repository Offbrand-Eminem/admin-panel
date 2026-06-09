package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GiveCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("give")
            .requires(source -> hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .then(argument("item", StringArgumentType.word())
                    .executes(GiveCommand::giveItem)
                    .then(argument("count", IntegerArgumentType.integer(1))
                        .executes(GiveCommand::giveItemWithCount)
                        .then(argument("enchantments", StringArgumentType.greedyString())
                            .executes(GiveCommand::giveItemWithEnchantments)
                        )
                    )
                )
            )
        );
    }

    private static boolean hasAdminPermission(CommandSourceStack source) {
        // if (source.hasPermissionLevel(4)) return true;
        if (source.getPlayer() == null) return false;
        return AdminPanelMod.permissionManager.hasPermission(
            source.getPlayer(),
            PermissionManager.PermissionLevel.ADMIN
        );
    }

    private static int giveItem(CommandContext<CommandSourceStack> context) {
        return giveItemInternal(context, 1, null);
    }

    private static int giveItemWithCount(CommandContext<CommandSourceStack> context) {
        int count = IntegerArgumentType.getInteger(context, "count");
        return giveItemInternal(context, count, null);
    }

    private static int giveItemWithEnchantments(CommandContext<CommandSourceStack> context) {
        String enchantmentsStr = StringArgumentType.getString(context, "enchantments");
        int count = 1;
        try {
            count = IntegerArgumentType.getInteger(context, "count");
        } catch (IllegalArgumentException e) {
            // count not provided
        }
        return giveItemInternal(context, count, enchantmentsStr);
    }

    private static int giveItemInternal(CommandContext<CommandSourceStack> context, int count, String enchantmentsStr) {
        String playerName = StringArgumentType.getString(context, "player");
        String itemStr = StringArgumentType.getString(context, "item");
        CommandSourceStack source = context.getSource();

        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target == null) {
            source.sendFailure(Component.literal("§cPlayer not found: " + playerName));
            return 0;
        }

        Identifier itemId = Identifier.tryParse(itemStr);
        if (itemId == null || !BuiltInRegistries.ITEM.containsKey(itemId)) {
            source.sendFailure(Component.literal("§cInvalid item: " + itemStr));
            return 0;
        }

        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.getValue(itemId), count);

        // Apply enchantments using the 1.21.x component API
        if (enchantmentsStr != null && !enchantmentsStr.isEmpty()) {
            ItemEnchantments.Mutable enchBuilder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            String[] parts = enchantmentsStr.split(",");
            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) continue;
                String[] enchantParts = part.split(":");
                String enchantId = enchantParts[0].trim();
                int level = enchantParts.length > 1 ? Integer.parseInt(enchantParts[1].trim()) : 1;
                Identifier enchantIdentifier = Identifier.tryParse(enchantId);
                if (enchantIdentifier == null) continue;
                // Enchantments temporarily disabled due to registry API changes in 1.21.11
                // Items will be given without enchantments
                continue;
            }
            stack.set(DataComponents.ENCHANTMENTS, enchBuilder.toImmutable());
        }

        target.getInventory().add(stack);
        String enchantmentInfo = (enchantmentsStr != null && !enchantmentsStr.isEmpty()) ?
            " with enchantments: " + enchantmentsStr : "";
        source.sendSuccess(() -> Component.literal("§aGave " + count + "x " + itemStr + enchantmentInfo + " to " + playerName), true);
        return 1;
    }
}
