<div align="center">

# ✦ ᴇ ɴ ꜱ ᴅ ᴀ ɪ ʀ ᴇ ✦

**Minecraft Mini-Oyunları için Premium Eklenti**  
*Ensis Tarafından Geliştirildi | [ensis.net](https://ensis.net)*

[English](README.md) | [Türkçe](README_tr.md) | [Deutsch](README_de.md) | [Français](README_fr.md) | [Español](README_es.md)

---

**ᴇɴꜱᴅᴀɪʀᴇ**, son derece profesyonel, tamamen özelleştirilebilir ve titizlikle tasarlanmış bir Minecraft mini-oyun eklentisidir. Popüler "Color Islands" ve "Zone Wars" mekaniklerinden esinlenilmiştir; oyuncular, kendilerine atanan renklere uyan blokların üzerinde durarak hayatta kalmaya çalışırken dinamik oyun elementleri, değiştiriciler (modifier) ve kıyasıya PvP ile mücadele ederler.

[![Paper API](https://img.shields.io/badge/API-Paper%201.20.1-green.svg)](https://papermc.io/)
[![Version](https://img.shields.io/badge/Version-1.0.0--MEGA-blue.svg)]()
[![Database](https://img.shields.io/badge/Database-SQLite-red.svg)]()

> *Mutlak yapısal bütünlükle tasarlandı. Sıfır kod yorumu. A'dan Z'ye özelleştirme.*

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

1. **ᴅᴇᴘʟᴏʏᴍᴇɴᴛ:** `EnsDaire-1.0.0.jar` dosyasını `/plugins` klasörüne kopyalayın.
2. **ᴅᴇᴘᴇɴᴅᴇɴᴄɪᴇꜱ:** Sunucunuzda `PlaceholderAPI` ve `Vault` eklentilerinin yüklü olduğundan emin olun.
3. **ɪɴɪᴛɪᴀʟɪᴢᴀᴛɪᴏɴ:** Konfigürasyon dosyalarını oluşturması için Minecraft sunucusunu yeniden başlatın.
4. **ᴀʀᴇɴᴀ ʜᴀᴢɪʀʟᴀᴍᴀ:** 
    * `/ed admin create <ArenaAdı>` komutunu girin.
    * `/daire admin` -> `ᴀʀᴇɴᴀ ʏöɴᴇᴛɪᴍɪ` menüsünden yeni oluşturduğunuz arenaya tıklayın.
    * Lobi, İzleyici spawn noktalarını ve renkli kapsül doğma noktalarını belirleyin.
    * Arena modül durumunu AÇIK hale getirin.
