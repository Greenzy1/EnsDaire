package net.ensdaireplugin.ensdaire.commands;

import net.ensdaireplugin.ensdaire.EnsDaire;
import net.ensdaireplugin.ensdaire.arena.Arena;
import net.ensdaireplugin.ensdaire.game.GameState;
import net.ensdaireplugin.ensdaire.gui.ArenaSelectGui;
import net.ensdaireplugin.ensdaire.gui.StatsGui;
import net.ensdaireplugin.ensdaire.utils.Msg;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnsDaireCommand implements CommandExecutor, TabCompleter {
    private final EnsDaire plugin;

    public EnsDaireCommand(EnsDaire plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        String pref = Msg.get(plugin, "prefix");
        if (args.length == 0) { sendHelp(sender, pref); return true; }

        String sub = args[0].toLowerCase();
        
        if (sub.equalsIgnoreCase(plugin.getLanguageManager().getRaw("commands.join"))) {
            requirePlayer(sender, () -> {
                if (args.length >= 2) joinByName((Player) sender, args[1], pref);
                else ArenaSelectGui.open(plugin, (Player) sender);
            });
            return true;
        }

        if (sub.equalsIgnoreCase(plugin.getLanguageManager().getRaw("commands.leave"))) {
            requirePlayer(sender, () -> {
                Player p = (Player) sender;
                Arena a = plugin.getArenaManager().getByPlayer(p.getUniqueId());
                if (a == null) { p.sendMessage(pref + "§cBir oyunda değilsin."); return; }
                a.removePlayer(p);
            });
            return true;
        }

        if (sub.equalsIgnoreCase(plugin.getLanguageManager().getRaw("commands.stats"))) {
            requirePlayer(sender, () -> StatsGui.open(plugin, (Player) sender));
            return true;
        }

        if (sub.equalsIgnoreCase(plugin.getLanguageManager().getRaw("commands.admin"))) {
            if (!sender.hasPermission("ensdaire.admin")) { sender.sendMessage(pref + Msg.get(plugin, "no-permission")); return true; }
            handleAdmin(sender, args, pref);
            return true;
        }

        if (sub.equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("ensdaire.admin")) { sender.sendMessage(pref + Msg.get(plugin, "no-permission")); return true; }
            plugin.reloadConfig();
            plugin.getLanguageManager().loadLanguages();
            plugin.getMenuManager().loadMenus();
            sender.sendMessage(pref + "§aDosyalar yenilendi.");
            return true;
        }

        sendHelp(sender, pref);
        return true;
    }

    private void handleAdmin(CommandSender sender, String[] args, String pref) {
        if (args.length < 2) { sendAdminHelp(sender, pref); return; }
        String action = args[1].toLowerCase();
        
        switch (action) {
            case "create" -> {
                if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin create <isim>"); return; }
                plugin.getArenaManager().createArena(args[2]);
                sender.sendMessage(pref + "§aArena oluşturuldu: " + args[2]);
            }
            case "gui", "menu" -> {
                if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin gui <isim>"); return; }
                requirePlayer(sender, () -> net.ensdaireplugin.ensdaire.gui.AdminGui.open(plugin, (Player) sender, args[2]));
            }
            case "start" -> {
                if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin start <isim>"); return; }
                Arena a = plugin.getArenaManager().get(args[2]);
                if (a != null) a.startGame();
            }
            default -> sendAdminHelp(sender, pref);
        }
    }

    private void joinByName(Player p, String name, String pref) {
        Arena a = plugin.getArenaManager().get(name);
        if (a == null) { p.sendMessage(pref + "§cArena bulunamadı."); return; }
        a.addPlayer(p);
    }

    private void requirePlayer(CommandSender s, Runnable action) {
        if (s instanceof Player) action.run();
        else s.sendMessage("§cBu komut sadece oyuncular içindir.");
    }

    private void sendHelp(CommandSender s, String p) {
        s.sendMessage("§8§m══════§b EnsDaire §8§m══════");
        s.sendMessage(" §e/ed " + plugin.getLanguageManager().getRaw("commands.join") + " §8- §7Menüyü açar.");
        s.sendMessage(" §e/ed " + plugin.getLanguageManager().getRaw("commands.leave") + " §8- §7Oyundan çıkar.");
        s.sendMessage(" §e/ed " + plugin.getLanguageManager().getRaw("commands.stats") + " §8- §7İstatistiklerini görürsün.");
        s.sendMessage("§8§m══════════════════════");
    }

    private void sendAdminHelp(CommandSender s, String p) {
        s.sendMessage("§8§m══════§c Admin Komutları §8§m══════");
        s.sendMessage(" §c/ed admin gui <arena> §8- §7Görsel yönetici paneli.");
        s.sendMessage(" §c/ed admin create <isim> §8- §7Yeni arena oluşturur.");
        s.sendMessage(" §c/ed admin start <arena> §8- §7Oyunu hemen başlatır.");
        s.sendMessage(" §c/ed reload §8- §7Dosyaları yeniler.");
        s.sendMessage("§8§m══════════════════════════");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add(plugin.getLanguageManager().getRaw("commands.join"));
            list.add(plugin.getLanguageManager().getRaw("commands.leave"));
            list.add(plugin.getLanguageManager().getRaw("commands.stats"));
            if (sender.hasPermission("ensdaire.admin")) {
                list.add(plugin.getLanguageManager().getRaw("commands.admin"));
                list.add("reload");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase(plugin.getLanguageManager().getRaw("commands.admin"))) {
            list.add("create"); list.add("gui"); list.add("start");
        }
        return list;
    }
}
