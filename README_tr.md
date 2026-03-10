<div align="center">

# ✦ ᴇ ɴ ꜱ ᴅ ᴀ ɪ ʀ ᴇ ✦

**Minecraft Mini-Oyunları için Premium Eklenti**  
*Ensis Tarafından Geliştirildi | [ensis.net](https://ensis.net)*

[English](README.md) | [Türkçe](README_tr.md) | [Deutsch](README_de.md) | [Français](README_fr.md) | [Español](README_es.md)

---

# EnsDaire - Professional Circle Mini-Game Plugin

EnsDaire, gelişmiş özellikleri ve tamamen özelleştirilebilir yapısıyla sunucunuza premium bir oyun deneyimi katar.

## 🌟 Öne Çıkan Özellikler

*   **GUI Odaklı Yönetim:** Tüm oyuncu ve admin işlemleri modern, konfigüre edilebilir arayüzler üzerinden yapılır.
*   **Gelişmiş Kozmetikler:** Kill efektleri, kazanma efektleri ve daha fazlasını içeren gelişmiş kozmetik sistemi.
*   **Dinamik BossBar & Actionbar:** Oyun durumunu (geri sayım, süre, sağ kalanlar) takip eden animasyonlu göstergeler.
*   **Hareketli Scoreboard:** Özelleştirilebilir, animasyonlu başlıklar ve kapsamlı bilgi tablosu.
*   **Holografik Liderlik Tablosu:** İlk 10 oyuncuyu gösteren şık, premium görünümlü liderlik tabloları.
*   **Tamamen Veri Odaklı:** GUI başlıklarından slotlara, item lore'larından materyallere kadar her şey YAML dosyalarıyla değiştirilebilir.
*   **Modern Kod Yapısı:** Gelişmiş API, NamespacedKey tabanlı item tanıma ve performans odaklı mimari.

## 🛠️ Komutlar

*   `/daire` - Ana oyun menüsünü açar (Arena Seçimi).
*   `/katil <arena>` - Belirtilen arenaya hızlıca katılır.
*   `/ayril` - Mevcut oyundan ayrılır (Durum verilerini geri yükler).
*   `/kozmetik` - Kozmetik mağazasını açar.
*   `/istatistik` - Kişisel verilerinizi gösterir.
*   `/ed admin` - Yönetim panelini açar.
*   `/ed reload` - Tüm dosyaları anında yeniler.

## ⚙️ Konfigürasyon Dosyaları

*   `config.yml`: Genel oyun ayarları, süreler ve mekanikler.
*   `guis.yml`: Tüm arayüzlerin (Menu) tasarımı, başlıkları ve itemları.
*   `cosmetics.yml`: Kozmetik kategorileri ve içerikleri.
*   `messages_tr.yml`: Tüm dildeki mesajların ve prefixlerin ayarı.

## 🚀 Kurulum

1.  Jar dosyasını `plugins` klasörüne atın.
2.  Sunucuyu başlatın ve oluşan dosyaları isteğinize göre düzenleyin.
3.  `/ed reload` komutuyla ayarlarınızı uygulayın.

---
**Ensis Geliştirme Ekibi** | [ensis.net](https://ensis.net)

[![Paper API](https://img.shields.io/badge/API-Paper%201.20.1-green.svg)](https://papermc.io/)
[![Version](https://img.shields.io/badge/Version-2.0-blue.svg)]()
[![Database](https://img.shields.io/badge/Database-SQLite-red.svg)]()


</div>

---

## ✦ ö ᴢ ᴇ ʟ ʟ ɪ ᴋ ʟ ᴇ ʀ

* **ᴅɪɴᴀᴍɪᴋ ᴀʀᴇɴᴀʟᴀʀ:** Gelişmiş, sezgisel bir oyun-içi menü aracılığıyla tamamen sınırsız arenalar oluşturun, düzenleyin ve yönetin (`/daire admin`).
* **ɢᴇʟɪşᴍɪş ᴋᴏᴢᴍᴇᴛɪᴋ ꜱɪꜱᴛᴇᴍɪ:** Sunucular için özel olarak tasarlanmış, tamamen entegre bir kozmetik marketi (`/daire market`). Öldürme Efektleri, Ok İzleri, Kazanma Efektleri ve Yürüme İzleri içerir.
* **ᴋᴀᴘꜱᴀᴍʟɪ öᴢᴇʟʟᴇşᴛɪʀᴍᴇ:** `config.yml` (jetonlar, puanlar, renkler, bloklar, shulker eşyaları) dosyasından tutun, çoklu dil sistemine kadar (`tr_TR`, `en_US`, vb.) her şey baştan aşağı düzenlenebilir.
* **ᴛᴇᴍᴇʟ ᴍᴇᴋᴀɴɪᴋʟᴇʀ:** 
    * Partikül izleme sınırlarına sahip renge dayalı hayatta kalma bölgeleri.
    * Merkezi güvenli bölgeler (`IRON_BLOCK`, `QUARTZ_BLOCK`).
    * Üst düzey eşya dağıtımı için yapılandırılabilir Shulker Kutusu alanları.
* **ᴏʏᴜɴᴄᴜ ᴠᴇʀɪꜱɪ ᴠᴇ ʀüᴛʙᴇʟᴇʀ:** K/D oranlarını, Galibiyet Yüzdelerini saklayan ve jeton kazanımıyla eş zamanlı çalışan otomatik Rütbe Atlama sistemini yürüten yerel SQLite altyapısı.
* **ʜᴏʟᴏɢʀᴀᴍ & ʙᴏꜱꜱʙᴀʀ ᴇɴᴛᴇɢʀᴀꜱʏᴏɴᴜ:** Yerleşik `TextDisplay` tabanlı en iyiler sıralaması (Leaderboard) ve tamamen yapılandırılabilir BossBar (Renk, Stil) desteği.
* **ꜱᴍᴀʟʟ ᴄᴀᴘꜱ ᴀʀᴀʏüᴢ:** Tüm menülerde (GUI) ve skor tablolarında standart olarak kullanılan ultra-premium Small Caps tasarımı (`ᴀʀᴇɴᴀ ꜱᴇçɪᴍɪ`, `ᴀᴅᴍɪɴ ᴘᴀɴᴇʟɪ`).

---

## ✦ ᴋ ᴏ ᴍ ᴜ ᴛ ʟ ᴀ ʀ

Tüm işlevler bütünleşik bir komut hiyerarşisi üzerinden yönetilir.

| Komut | Yetki | Açıklama |
| :--- | :--- | :--- |
| `/daire` | `Yok` | Ana Katılım/Arena Seçim menüsüne erişir. |
| `/katil` | `Yok` | Hızlıca durumu uygun olan arenaya katılır. |
| `/ayril` | `Yok` | Arenadan ayrılır ve bir önceki konuma/duruma geri döner. |
| `/daire istatistik` | `Yok` | Bireysel savaş istatistiklerini gösterir. |
| `/daire market` | `Yok` | Kapsamlı Kozmetik arayüzünü açar. |
| `/daire admin` | `ensdaire.admin` | Evrensel Yönetim Paneli'ne (GUI) erişir. |
| `/ed admin create <id>` | `ensdaire.admin` | Yeni, bağımsız bir arena ortamı oluşturur. |
| `/ed reload` | `ensdaire.admin` | Konfigürasyonları ve dil dosyalarını önbellekten yeniler. |

---

## ✦ ᴋ ᴜ ʀ ᴜ ʟ ᴜ ᴍ

1. **ᴅᴇᴘʟᴏʏᴍᴇɴᴛ:** `EnsDaire-2.0.jar` dosyasını `/plugins` klasörüne kopyalayın.
2. **ᴅᴇᴘᴇɴᴅᴇɴᴄɪᴇꜱ:** Sunucunuzda `PlaceholderAPI` ve `Vault` eklentilerinin yüklü olduğundan emin olun.
3. **ɪɴɪᴛɪᴀʟɪᴢᴀᴛɪᴏɴ:** Konfigürasyon dosyalarını oluşturması için Minecraft sunucusunu yeniden başlatın.
4. **ᴀʀᴇɴᴀ ʜᴀᴢɪʀʟᴀᴍᴀ:** 
    * `/ed admin create <ArenaAdı>` komutunu girin.
    * `/daire admin` -> `ᴀʀᴇɴᴀ ʏöɴᴇᴛɪᴍɪ` menüsünden yeni oluşturduğunuz arenaya tıklayın.
    * Lobi, İzleyici spawn noktalarını ve renkli kapsül doğma noktalarını belirleyin.
    * Arena modül durumunu AÇIK hale getirin.
