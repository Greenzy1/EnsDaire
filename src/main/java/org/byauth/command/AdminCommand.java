package org.byauth.command;

import org.byauth.ByCircleGame;
import org.byauth.controller.ArenaController;
import org.byauth.game.Arena;
import org.byauth.game.Team;
import org.byauth.utils.VictoryEffects;
import org.byauth.manager.PlayerDataManager;
import org.byauth.utils.SettingsManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class AdminCommand implements CommandExecutor {

    private final ByCircleGame plugin;
    private final ArenaController arenaController;
    private final SettingsManager settings;
    private final PlayerDataManager playerDataManager;

    public AdminCommand(ByCircleGame plugin) {
        this.plugin = plugin;
        this.arenaController = plugin.getArenaController();
        this.settings = plugin.getSettingsManager();
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ensdaire.admin")) {
            sender.sendMessage(ChatColor.RED + "Yetkiniz yok!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "/ensdaire setlobby <arena>");
            sender.sendMessage(ChatColor.YELLOW + "/ensdaire setcenter <arena>");
            sender.sendMessage(ChatColor.YELLOW + "/ensdaire givepoints <player> <amount>");
            return true;
        }

        if (args[0].equalsIgnoreCase("setlobby")) {
            if (!(sender instanceof Player))
                return true;
            Player p = (Player) sender;
            if (args.length < 2)
                return true;
            Arena arena = arenaController.getOrCreateArena(args[1]);
            arena.setLobbyLocation(p.getLocation());
            settings.saveArenaLocation(args[1], "lobby", p.getLocation());
            arenaController.buildLobby(arena);
            p.sendMessage(ChatColor.GREEN + "Lobi ayarlandı!");
        } else if (args[0].equalsIgnoreCase("setcenter")) {
            if (!(sender instanceof Player))
                return true;
            Player p = (Player) sender;
            if (args.length < 2)
                return true;
            Arena arena = arenaController.getOrCreateArena(args[1]);
            arena.setCenterLocation(p.getLocation());
            settings.saveArenaLocation(args[1], "center", p.getLocation());
            arena.getArenaManager().generateArena(p.getLocation(), Arrays.asList(Team.values()));
            p.sendMessage(ChatColor.GREEN + "Arena merkezi ayarlandı ve oluşturuldu!");
        } else if (args[0].equalsIgnoreCase("setleaderboard")) {
            if (!(sender instanceof Player))
                return true;
            Player p = (Player) sender;
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "/ensdaire setleaderboard <points/kills/wins>");
                return true;
            }
            String type = args[1].toLowerCase();
            plugin.getHologramManager().createLeaderboard(p.getLocation(), type);
            p.sendMessage(ChatColor.GREEN + type.toUpperCase() + " sıralaması oluşturuldu!");
        }

        return true;
    }
}
