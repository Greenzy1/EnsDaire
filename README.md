<div align="center">

# ✦ ᴇ ɴ ꜱ ᴅ ᴀ ɪ ʀ ᴇ ✦

**A Premium Minimum Viable Product for Minecraft Mini-Games**  
*Developed by Ensis | [ensis.net](https://ensis.net)*

[English](README.md) | [Türkçe](README_tr.md) | [Deutsch](README_de.md) | [Français](README_fr.md) | [Español](README_es.md)

---

**ᴇɴꜱᴅᴀɪʀᴇ** is a highly professional, fully customizable, and meticulously designed Minecraft mini-game plugin. Inspired by popular "Color Islands" and "Zone Wars" mechanics, players must survive by standing on blocks matching their assigned colors while navigating dynamic gameplay elements, modifiers, and intense PvP.

[![Paper API](https://img.shields.io/badge/API-Paper%201.20.1-green.svg)](https://papermc.io/)
[![Version](https://img.shields.io/badge/Version-1.0.0--MEGA-blue.svg)]()
[![Database](https://img.shields.io/badge/Database-SQLite-red.svg)]()

> *Designed with absolute structural integrity. Zero code comments. A to Z customization.*

</div>

---

## ✦ ꜰ ᴇ ᴀ ᴛ ᴜ ʀ ᴇ ꜱ

* **ᴅʏɴᴀᴍɪᴄ ᴀʀᴇɴᴀꜱ:** Create, edit, and manage infinite arenas entirely through an advanced, intuitive in-game GUI (`/daire admin`).
* **ᴀᴅᴠᴀɴᴄᴇᴅ ᴄᴏꜱᴍᴇᴛɪᴄꜱ ꜱʏꜱᴛᴇᴍ:** A fully integrated cosmetic market (`/daire market`) specifically designed for high-end server monetization. Includes Kill Effects, Projectile Trails, Win Sequences, and Movement Trails.
* **ᴄᴏᴍᴘʀᴇʜᴇɴꜱɪᴠᴇ ᴄᴜꜱᴛᴏᴍɪᴢᴀᴛɪᴏɴ:** Everything from `config.yml` (tokens, points, colors, blocks, shulker loot) to `languages/` (tr_TR, en_US, de_DE, fr_FR, es_ES) is entirely configurable.
* **ᴄᴏʀᴇ ᴍᴇᴄʜᴀɴɪᴄꜱ:** 
    * Color-based survival zones with particle tracking bounds.
    * Central safe territories (`IRON_BLOCK`, `QUARTZ_BLOCK`).
    * Configurable central Shulker Box spawns for high-tier loot distribution.
* **ᴘʟᴀʏᴇʀ ᴅᴀᴛᴀ & ʀᴀɴᴋꜱ:** Native SQLite database architecture storing precise gameplay statistics, K/D ratios, Win Rates, and an automatic Rank-Up continuum based on token acquisition.
* **ʜᴏʟᴏɢʀᴀᴍ & ʙᴏꜱꜱʙᴀʀ ɪɴᴛᴇɢʀᴀᴛɪᴏɴ:** Native `TextDisplay` top-point leaderboards and fully customizable BossBar implementations (Color, Style, Update Intervals).
* **ꜱᴍᴀʟʟ ᴄᴀᴘꜱ ᴜɪ:** An ultra-premium aesthetic applied universally across all GUIs and scoreboards (`ᴀʀᴇɴᴀ ꜱᴇçɪᴍɪ`, `ᴀᴅᴍɪɴ ᴘᴀɴᴇʟɪ`).

---

## ✦ ᴄ ᴏ ᴍ ᴍ ᴀ ɴ ᴅ ꜱ

All functionalities are governed by a unified command hierarchy structure.

| Command | Permission | Description |
| :--- | :--- | :--- |
| `/daire` | `None` | Access the primary Join/Arena Selection GUI. |
| `/katil` | `None` | Instantly join the fastest available arena instance. |
| `/ayril` | `None` | Disconnect and revert to the previous state/location. |
| `/daire istatistik` | `None` | Display personal combat metrics and telemetry. |
| `/daire market` | `None` | Open the comprehensive Cosmetics interface. |
| `/daire admin` | `ensdaire.admin` | Access the universal Master Control Panel (GUI). |
| `/ed admin create <id>` | `ensdaire.admin` | Provision a new, discrete arena instance. |
| `/ed reload` | `ensdaire.admin` | Flush memory and reload dynamic file systems (`config.yml`, langs). |

---

## ✦ ꜱ ᴇ ᴛ ᴜ ᴘ

1. **ᴅᴇᴘʟᴏʏᴍᴇɴᴛ:** Place the `EnsDaire-1.0.0.jar` into the `/plugins` directory.
2. **ᴅᴇᴘᴇɴᴅᴇɴᴄɪᴇꜱ:** Ensure `PlaceholderAPI` and `Vault` are installed on the relative host environment.
3. **ɪɴɪᴛɪᴀʟɪᴢᴀᴛɪᴏɴ:** Restart the Minecraft server to generate configuration structures.
4. **ᴀʀᴇɴᴀ ᴘʀᴏᴠɪꜱɪᴏɴɪɴɢ:** 
    * Execute `/ed admin create <ArenaID>`.
    * Launch `/daire admin` -> `ᴀʀᴇɴᴀ ʏöɴᴇᴛɪᴍɪ` -> Click the generated arena.
    * Define Lobby, Spectator boundaries, and specific Color Capsule intersections.
    * Enable the arena module status.

---

## ✦ ᴄ ᴏ ɴ ꜰ ɪ ɢ ᴜ ʀ ᴀ ᴛ ɪ ᴏ ɴ

`EnsDaire` utilizes an extensively structured Yaml configuration schema. Below is a macro-view of the integration capabilities defined in `config.yml`:

* **`settings.language`:** Define localized string variables (`tr_TR`, `en_US`, etc.).
* **`messages.prefix`:** Customize the global chat prefix structure.
* **`tokens` & `points`:** Precision calibration for all game events (Win, Kil, First-Blood, Round-Survive).
* **`shulker.loot`:** Modify global chest generation tables relative to the arena module.
* **`colors`:** Bind physical `Material` states to dynamic `CircleColor` enums.
* **`bossbar` / `scoreboard` / `actionbar`:** Toggle and customize dimensional HUD elements.
* **`ranks`:** Define arbitrary progression ranks scaling dynamically with token acquisition.

---

## ✦ ᴘ ʟ ᴀ ᴄ ᴇ ʜ ᴏ ʟ ᴅ ᴇ ʀ ꜱ

Integrating external variables with PlaceholderAPI expands functionality across your relative Bungee/Velocity networks.

* `%ensdaire_jeton%` — Current Token balance.
* `%ensdaire_puan%` — Active Point accumulation.
* `%ensdaire_kazanma%` — Total round victories.
* `%ensdaire_kd%` — Kill/Death ratio composite score.
* `%ensdaire_rutbe%` — Current progression Rank Title.
* `%ensdaire_toplam_oyuncu%` — Cumulative active personnel inside operational arenas.
* `%ensdaire_aktif_arena%` — Total RUNNING state arenas.
* `%ensdaire_top_isim_<1-10>%` — Leaderboard telemetry (Names).
* `%ensdaire_top_token_<1-10>%` — Leaderboard telemetry (Tokens).

---
<div align="center">
  <i>EnsDaire Core Engine | Structural Integrity Standard Compliant | No Arbitrary Code Comments</i>
</div>
