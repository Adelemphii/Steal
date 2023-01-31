package tech.adelemphii.steal.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import tech.adelemphii.steal.Steal;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class BlockClickListener implements Listener {

    private final Steal plugin;
    public BlockClickListener(Steal plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        if(clickedBlock == null) {
            return;
        }

        if(plugin.getStealManager().isStealing(player.getUniqueId())) {
            event.setCancelled(true);

            if(cooldowns.containsKey(player.getUniqueId())) {
                long secondsLeft = ((cooldowns.get(player.getUniqueId()) / 1000) + 10) - (System.currentTimeMillis() / 1000);
                if(secondsLeft > 0) {
                    player.sendMessage(Component.text("You must wait " + secondsLeft + " seconds before stealing again!").color(NamedTextColor.RED));
                    return;
                }
            }

            if(player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                player.sendMessage(Component.text("You must have an empty hand to steal!").color(NamedTextColor.RED));
                return;
            }

            UUID target = plugin.getStealManager().getTarget(player.getUniqueId());
            OfflinePlayer targetPlayer = plugin.getServer().getOfflinePlayer(target);
            if(targetPlayer.getName() == null) {
                player.sendMessage(Component.text("The specified player is not a valid player!" ).color(NamedTextColor.RED));
                return;
            }

            if(!targetPlayer.isOnline()) {
                player.sendMessage(Component.text("Player ").color(NamedTextColor.RED)
                        .append(Component.text(targetPlayer.getName()).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                        .append(Component.text(" is not online!").color(NamedTextColor.RED)));
                return;
            }

            assert(targetPlayer.getPlayer() != null);
            ItemStack stolen = steal(targetPlayer.getPlayer());
            if(stolen == null) {
                player.sendMessage(Component.text("There is nothing to steal!").color(NamedTextColor.RED));
                return;
            }

            player.getInventory().addItem(stolen);
            player.sendMessage(Component.text("You have stolen ").color(NamedTextColor.GREEN)
                    .append(Component.text(stolen.getAmount() + "x " + stolen.getType().name().toLowerCase())
                            .color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .append(Component.text(" from ").color(NamedTextColor.GREEN))
                    .append(Component.text(targetPlayer.getName()).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .append(Component.text("!").color(NamedTextColor.GREEN)));
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
            makeCooldown(player);
        }
    }

    private ItemStack steal(Player target) {
        ItemStack item;

        List<ItemStack> contents = Arrays.stream(target.getEnderChest().getContents())
                .filter(Objects::nonNull)
                .filter(itemStack -> !itemStack.getType().isAir())
                .collect(Collectors.toList());

        if(contents.size() == 0) {
            return null;
        }

        // randomly get an item from the list
        int random = ThreadLocalRandom.current().nextInt(0, contents.size());
        item = contents.get(random);

        // remove the item from the target's ender chest
        target.getEnderChest().removeItem(item);
        target.sendMessage(Component.text("...You sense something has gone missing from your ender chest.")
                .color(NamedTextColor.RED));

        return item;
    }

    private void makeCooldown(Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> cooldowns.remove(player.getUniqueId()), 20 * 10);
    }
}
