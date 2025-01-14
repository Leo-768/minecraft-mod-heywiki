package wiki.minecraft.heywiki.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent.ClientCommandSourceStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import wiki.minecraft.heywiki.wiki.Target;
import wiki.minecraft.heywiki.wiki.WikiPage;

import static dev.architectury.event.events.client.ClientCommandRegistrationEvent.literal;
import static wiki.minecraft.heywiki.wiki.WikiPage.NO_FAMILY_EXCEPTION;

public class WhatIsThisItemCommand {
    public static final SimpleCommandExceptionType NO_ITEM_HELD = new SimpleCommandExceptionType(
            Text.translatable("heywiki.no_item_held"));
    private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

    @SuppressWarnings("UnusedReturnValue")
    public static LiteralCommandNode<ClientCommandSourceStack> register(
            CommandDispatcher<ClientCommandSourceStack> dispatcher) {
        return dispatcher.register(
                literal("whatisthisitem")
                        .executes(ctx -> {
                            if (CLIENT.player == null) return 1;
                            ItemStack stack = CLIENT.player.getInventory().getMainHandStack();
                            return openBrowserForStack(stack);
                        }).then(literal("offhand")
                                        .executes(ctx -> {
                                            if (CLIENT.player == null) return 1;
                                            ItemStack stack = CLIENT.player.getInventory().offHand.getFirst();
                                            return openBrowserForStack(stack);
                                        })));
    }

    private static int openBrowserForStack(ItemStack stack) throws CommandSyntaxException {
        var target = Target.of(stack);
        if (target == null) {
            throw NO_ITEM_HELD.create();
        }
        var page = WikiPage.fromTarget(target);
        if (page == null) {
            throw NO_FAMILY_EXCEPTION.create();
        }
        page.openInBrowser(true);
        return 0;
    }
}