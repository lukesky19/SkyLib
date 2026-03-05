/*
    SkyLib is a library that contains shared code for all of my plugins.
    Copyright (c) 2024 lukeskywlker19

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */
package com.github.lukesky19.skylib.plugin.command.arguments;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * This class creates the deserialize command argument for the skylib command.
 */
public class DeserializeArgument {
    /**
     * Default Constructor.
     */
    public DeserializeArgument() {}

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the deserialize command argument for the /skylib command.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} for the deserialize command argument for the /skylib command.
     */
    public @NonNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("deserialize")
                .requires(ctx -> ctx.getSender().hasPermission("skylib.commands.skylib.deserialize"))
                .then(Commands.argument("content", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            String message = ctx.getArgument("content", String.class);

                            Component component;
                            if(sender instanceof Player player) {
                                component = AdventureUtil.deserialize(player, message);
                            } else {
                                component = AdventureUtil.deserialize(message);
                            }

                            sender.sendMessage(Component.text("Deserialized Content:"));
                            sender.sendMessage(component);

                            return 1;
                        })
                ).build();
    }
}
