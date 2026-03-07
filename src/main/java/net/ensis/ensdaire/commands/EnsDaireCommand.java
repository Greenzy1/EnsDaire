package net.ensis.ensdaire.commands;

import net.ensis.ensdaire.EnsDaire;
import net.ensis.ensdaire.models.Arena;
import net.ensis.ensdaire.gui.ArenaSelectGui;
import net.ensis.ensdaire.gui.StatsGui;
import net.ensis.ensdaire.gui.ArenaListGui;
import net.ensis.ensdaire.gui.CosmeticsGui;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EnsDaireCommand implements CommandExecutor, TabCompleter {
    private final EnsDaire plugin;

    public EnsDaireCommand(EnsDaire plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        String pref = plugin.getLanguageManager().getMessage("prefix");

        // /katil yönlendirmesi
        if (label.equalsIgnoreCase("katil")) {
            requirePlayer(sender, () -> {
                if (args.length >= 1) {
                    Arena a = plugin.getArenaManager().get(args[0]);
                    if (a != null) a.addPlayer((Player) sender);
                    else sender.sendMessage(pref + "§cArena bulunamadı.");
                } else ArenaSelectGui.open(plugin, (Player) sender);
            });
            return true;
        }

        // /ayril yönlendirmesi
        if (label.equalsIgnoreCase("ayril")) {
            requirePlayer(sender, () -> {
                Player p = (Player) sender;
                Arena a = plugin.getArenaManager().getByPlayer(p.getUniqueId());
                if (a == null) { p.sendMessage(pref + "§cBir oyunda değilsin."); return; }
                a.removePlayer(p);
            });
            return true;
        }

        // Argümansız /daire veya /ed
        if (args.length == 0) {
            requirePlayer(sender, () -> ArenaSelectGui.open(plugin, (Player) sender));
            return true;
        }

        String sub = args[0].toLowerCase();
        
        switch (sub) {
            case "katil" -> {
                requirePlayer(sender, () -> {
                    if (args.length >= 2) {
                        Arena a = plugin.getArenaManager().get(args[1]);
                        if (a != null) a.addPlayer((Player) sender);
                        else sender.sendMessage(pref + "§cArena bulunamadı.");
                    } else ArenaSelectGui.open(plugin, (Player) sender);
                });
            }
            case "ayril" -> {
                requirePlayer(sender, () -> {
                    Player p = (Player) sender;
                    Arena a = plugin.getArenaManager().getByPlayer(p.getUniqueId());
                    if (a == null) { p.sendMessage(pref + "§cBir oyunda değilsin."); return; }
                    a.removePlayer(p);
                });
            }
            case "istatistik" -> {
                requirePlayer(sender, () -> StatsGui.open(plugin, (Player) sender));
            }
            case "market", "kozmetik" -> {
                requirePlayer(sender, () -> CosmeticsGui.openMain(plugin, (Player) sender));
            }
            case "admin" -> {
                if (!sender.hasPermission("ensdaire.admin")) { 
                    sender.sendMessage(pref + plugin.getLanguageManager().getMessage("no-permission")); 
                    return true; 
                }
                if (args.length >= 2 && args[1].equalsIgnoreCase("create")) {
                    if (args.length < 3) { sender.sendMessage(pref + "§cKullanım: /ed admin create <isim>"); return true; }
                    plugin.getArenaManager().createArena(args[2]);
                    sender.sendMessage(pref + "§aArena oluşturuldu: §f" + args[2]);
                    return true;
                }
                requirePlayer(sender, () -> net.ensis.ensdaire.gui.AdminPanelGui.open(plugin, (Player) sender));
            }
            case "reload" -> {
                if (!sender.hasPermission("ensdaire.admin")) { 
                    sender.sendMessage(pref + plugin.getLanguageManager().getMessage("no-permission")); 
                    return true; 
                }
                plugin.reloadConfig();
                plugin.getLanguageManager().loadLanguages();
                plugin.getMenuManager().loadMenus();
                sender.sendMessage(pref + "§aDosyalar yenilendi.");
            }
            default -> {
                if (sender.hasPermission("ensdaire.admin")) sendHelp(sender);
                else requirePlayer(sender, () -> ArenaSelectGui.open(plugin, (Player) sender));
            }
        }

        return true;
    }

    private void requirePlayer(CommandSender s, Runnable action) {
        if (s instanceof Player) action.run();
        else s.sendMessage("§cBu komut sadece oyuncular içindir.");
    }

    private void sendHelp(CommandSender s) {
        s.sendMessage("§8§m══════§b EnsDaire §8§m══════");
        s.sendMessage(" §e/daire katil §8- §7Arena seçer.");
        s.sendMessage(" §e/daire ayril §8- §7Oyundan çıkar.");
        s.sendMessage(" §e/daire istatistik §8- §7Bilgilerini görürsün.");
        s.sendMessage(" §e/daire market §8- §7Kozmetik mağazası.");
        if (s.hasPermission("ensdaire.admin")) {
            s.sendMessage(" §c/ed admin §8- §7Yönetim paneli.");
            s.sendMessage(" §c/ed reload §8- §7Yenile.");
        }
        s.sendMessage("§8§m══════════════════════");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        String label = command.getName().toLowerCase();

        // /katil tab tamamlama
        if (label.equals("katil")) {
            if (args.length == 1) {
                suggestions.addAll(plugin.getArenaManager().all().stream().map(Arena::getId).collect(Collectors.toList()));
            }
        } 
        // /ayril tab tamamlama (boş)
        else if (label.equals("ayril")) {
            return Collections.emptyList();
        }
        // /daire (veya /ed) tab tamamlama
        else {
            if (args.length == 1) {
                // Oyuncular sadece katil ve ayril görecek
                suggestions.addAll(Arrays.asList("katil", "ayril"));
                
                // Adminler ekstraları görecek
                if (sender.hasPermission("ensdaire.admin")) {
                    suggestions.addAll(Arrays.asList("admin", "reload", "istatistik", "market", "kozmetik"));
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("admin") && sender.hasPermission("ensdaire.admin")) {
                    suggestions.add("create");
                } else if (args[0].equalsIgnoreCase("katil")) {
                    suggestions.addAll(plugin.getArenaManager().all().stream().map(Arena::getId).collect(Collectors.toList()));
                }
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return suggestions.stream()
                .filter(s -> s.toLowerCase().startsWith(lastArg))
                .sorted()
                .collect(Collectors.toList());
    }
}
