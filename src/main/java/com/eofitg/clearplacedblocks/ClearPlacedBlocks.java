package com.eofitg.clearplacedblocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ClearPlacedBlocks extends JavaPlugin implements Listener, CommandExecutor {
    private final Set<Location> placedBlocks = new HashSet<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("clearblocks").setExecutor(this);
    }

    @Override
    public void onDisable() {
        clearPlacedBlocks(); // Clear all placed blocks before server shutdown
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        placedBlocks.add(event.getBlock().getLocation());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("clearblocks")) {
            if (!sender.hasPermission("clearblocks.use")) {
                sender.sendMessage("§cYou do not have permission to use this command!");
                return true;
            }
            clearPlacedBlocks();
            sender.sendMessage("§aClearing all placed blocks...");
            return true;
        }
        return false;
    }

    private void clearPlacedBlocks() {
        if (placedBlocks.isEmpty()) return;

        new BukkitRunnable() {
            final Iterator<Location> iterator = placedBlocks.iterator();
            int count = 0;

            @Override
            public void run() {
                int cleared = 0;
                while (iterator.hasNext() && cleared < 100) {
                    Location loc = iterator.next();
                    if (loc.getBlock().getType() != Material.AIR) {
                        loc.getBlock().setType(Material.AIR);
                        cleared++;
                    }
                    iterator.remove();
                    count++;
                }
                if (!iterator.hasNext()) {
                    Bukkit.getLogger().info("Clearing complete. Removed " + count + " blocks.");
                    cancel();
                }
            }
        }.runTaskTimer(this, 0L, 1L);
    }
}
