package com.github.gerzytet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlowerCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (!player.isOp()) {
            return true;
        }
        if (command.getName().equals("createflower")) {
            if (args.length != 1) {
                sender.sendMessage("Usage: /createflower <flower>");
                return true;
            }
            String flowerName = args[0];
            Material flower;
            try {
                flower = Material.valueOf(flowerName.toUpperCase());
                if (!FlowerDatabase.isValidFlower(flower)) {
                    sender.sendMessage("Invalid flower name: " + flowerName);
                    return true;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                sender.sendMessage("Invalid flower name: " + flowerName);
                return true;
            }

            Location location = player.getLocation().getBlock().getLocation();
            if (FlowerDatabase.getEntryAtLocaiton(location) != null) {
                sender.sendMessage("There is already a flower at this location");
                return true;
            }

            FlowerDatabaseEntry entry = new FlowerDatabaseEntry(location, flower);
            FlowerDatabase.addFlowerEntry(entry);

            sender.sendMessage("Successfully created flower");
            entry.location.getBlock().setType(entry.flower);
            return true;
        } else if (command.getName().equals("removeflower")) {
            Location location = player.getLocation().getBlock().getLocation();
            FlowerDatabaseEntry entry = FlowerDatabase.getEntryAtLocaiton(location);
            if (entry == null) {
                sender.sendMessage("There is no flower at this location");
                return true;
            }

            FlowerDatabase.removeFlowerEntry(entry);
            entry.location.getBlock().setType(Material.AIR);
            sender.sendMessage("Successfully removed flower");
            return false;
        } else {
            return false;
        }
    }
}
