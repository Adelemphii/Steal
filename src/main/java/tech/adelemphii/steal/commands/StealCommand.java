package tech.adelemphii.steal.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tech.adelemphii.steal.Steal;

public class StealCommand implements CommandExecutor {

    private final Steal plugin;
    public StealCommand(Steal plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }
        Player player = (Player) sender;

        if(args.length == 0) {
            if(plugin.getStealManager().isStealing(((Player) sender).getUniqueId())) {
                plugin.getStealManager().removeSteal(player.getUniqueId());
                sender.sendMessage(Component.text("You are no longer stealing!").color(NamedTextColor.RED));
                return true;
            }

            sender.sendMessage("You must specify a player to steal from!");
            return false;
        }
        String targetName = args[0];
        Player target = sender.getServer().getPlayer(targetName);
        if(target == null) {
            sender.sendMessage(Component.text("Player " ).color(NamedTextColor.RED)
                    .append(Component.text(targetName).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .append(Component.text(" must be online!").color(NamedTextColor.RED)));
            return false;
        }

        player.sendMessage(Component.text("Click on an ender chest to steal from " + targetName + "!")
                .color(NamedTextColor.GREEN));
        plugin.getStealManager().addSteal(player.getUniqueId(), target.getUniqueId());
        return true;
    }
}
