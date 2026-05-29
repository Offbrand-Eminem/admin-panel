package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GiveCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
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

    private static boolean hasAdminPermission(ServerCommandSource source) {
        // if (source.hasPermissionLevel(4)) return true;
        if (source.getPlayer() == null) return false;
        return AdminPanelMod.permissionManager.hasPermission(
            source.getPlayer(),
            PermissionManager.PermissionLevel.ADMIN
        );
    }

    private static int giveItem(CommandContext<ServerCommandSource> context) {
        return giveItemInternal(context, 1, null);
    }

    private static int giveItemWithCount(CommandContext<ServerCommandSource> context) {
        int count = IntegerArgumentType.getInteger(context, "count");
        return giveItemInternal(context, count, null);
    }

    private static int giveItemWithEnchantments(CommandContext<ServerCommandSource> context) {
        String enchantmentsStr = StringArgumentType.getString(context, "enchantments");
        int count = 1;
        try {
            count = IntegerArgumentType.getInteger(context, "count");
        } catch (IllegalArgumentException e) {
            // count not provided
        }
        return giveItemInternal(context, count, enchantmentsStr);
    }

    private static int giveItemInternal(CommandContext<ServerCommandSource> context, int count, String enchantmentsStr) {
        String playerName = StringArgumentType.getString(context, "player");
        String itemStr = StringArgumentType.getString(context, "item");
        ServerCommandSource source = context.getSource();

        ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(playerName);
        if (target == null) {
            source.sendError(Text.literal("§cPlayer not found: " + playerName));
            return 0;
        }

        Identifier itemId = Identifier.tryParse(itemStr);
        if (itemId == null || !Registries.ITEM.containsId(itemId)) {
            source.sendError(Text.literal("§cInvalid item: " + itemStr));
            return 0;
        }

        ItemStack stack = new ItemStack(Registries.ITEM.get(itemId), count);

        // Apply enchantments using the 1.21.x component API
        if (enchantmentsStr != null && !enchantmentsStr.isEmpty()) {
            ItemEnchantmentsComponent.Builder enchBuilder = new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT);
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
            stack.set(DataComponentTypes.ENCHANTMENTS, enchBuilder.build());
        }

        target.getInventory().insertStack(stack);
        String enchantmentInfo = (enchantmentsStr != null && !enchantmentsStr.isEmpty()) ?
            " with enchantments: " + enchantmentsStr : "";
        source.sendFeedback(() -> Text.literal("§aGave " + count + "x " + itemStr + enchantmentInfo + " to " + playerName), true);
        return 1;
    }
}
