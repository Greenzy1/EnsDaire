package org.byauth.command;

import org.byauth.EnsDaire;
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

    private final EnsDaire plugin;
    private final ArenaController arenaController;
    private final SettingsManager settings;
    private final PlayerDataManager playerDataManager;

    public AdminCommand(EnsDaire plugin) {
        this.plugin = plugin;
        this.arenaController = plugin.getArenaController();
        this.settings = plugin.getSettingsManager();
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "ʙᴜ ᴋᴏᴍᴜᴛᴜ ꜱᴀᴅᴇᴄᴇ ᴏʏᴜɴᴄᴜʟᴀʀ ᴋᴜʟʟᴀɴᴀʙɪʟɪʀ.");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("ensdaire.admin")) {
            p.sendMessage(settings.format(settings.getMessage("error.no-permission")));
            return true;
        }

        if (args.length == 0) {
            p.openInventory(plugin.getGuiManager().createInventoryFromConfig("admin_main"));
            return true;
        }

        if (args[0].equalsIgnoreCase("setup")) {
            if (args.length < 2) {
                p.sendMessage(ChatColor.RED + "ᴋᴜʟʟᴀɴɪᴍ: /ᴇɴꜱᴅᴀɪʀᴇ ꜱᴇᴛᴜᴘ <ᴀʀᴇɴᴀ_ᴀᴅɪ>");
                return true;
            }
            Arena arena = arenaController.getOrCreateArena(args[1]);
            p.openInventory(plugin.getGuiManager().createArenaEditorInventory(arena));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            settings.reload();
            p.sendMessage(settings.format(settings.getMessage("admin.reload")));
            return true;
        }

        return true;
    }
}
