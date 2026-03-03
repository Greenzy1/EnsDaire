# ⭕ EnsDaire (Circle Sumo) - Profesyonel Minigame Eklentisi

**EnsDaire**, oyuncuların kendilerine ayrılan renkli bir daire içinde hayatta kalmaya çalıştığı, strateji ve yetenek tabanlı bir "Daireden Son Çıkan Kazanır" (Circle Sumo) minigame eklentisidir.

---

## 🚀 Temel Oyun Mekanikleri

1.  **Başlangıç ve Kapsül Sistemi:**
    *   Oyun başladığında oyuncular havada asılı duran **Kapsüllere** ışınlanır.
    *   Round başladığında kapsüllerden aşağı bırakılırlar ve otomatik **Yavaş Düşme (Slow Falling)** efekti alırlar.
2.  **Daire (Circle) Sistemi:**
    *   Oyuncu yere indiği anda ayaklarının altında seçtiği renkte bir daire oluşur.
    *   **Kritik Kural:** Eğer dairenin dışına çıkarsan (bloğu terk edersen) anında elenirsin!
3.  **Shulker Loot ve Savaş:**
    *   Her round başında merkeze Shulkerlar doğar.
    *   Shulkerlar kırıldığında içinden rakipleri itmeye yarayan özel eşyalar (Geri Tepme Çubuğu, Ender İncisi, Yay vb.) çıkar.
4.  **Round Döngüsü:**
    *   Round bittiğinde hayatta kalanlar tekrar kapsüllere ışınlanır ve yeni bir etkinlik/round başlar.
5.  **Elenme ve Beyaz Renk:**
    *   Bir oyuncu elendiğinde, sahadaki dairesi anında **BEYAZ** betona dönüşür. Bu, o oyuncunun pasifleştiğini gösterir.

---

## ⚡ Gelişmiş Özellikler

### 🎭 Rastgele Round Etkinlikleri (Modifiers)
Her round başında oyunun kurallarını değiştiren bir etkinlik seçilir:
*   **Düşük Yerçekimi:** Herkes çok yükseğe zıplar.
*   **Hız Patlaması:** Tüm oyuncular aşırı hızlı hareket eder.
*   **Çift Jeton:** O round kazanılan tüm jetonlar 2 katı sayılır.
*   **Normal:** Klasik kurallar geçerlidir.

### 📊 İstatistik ve Rütbe Sistemi
*   **Veritabanı:** Tüm veriler SQLite (data.db) üzerinde güvenle saklanır.
*   **Kazanımlar:** Jeton (Tokens), Puan (Points), Galibiyet, Öldürme ve Ölme takibi yapılır.
*   **Otomatik Rütbe:** Oyuncular jeton kazandıkça Çaylak > Amatör > Uzman > Efsane rütbelerine otomatik yükselir.

### 🛡️ Güvenlik ve Snapshot Sistemi
*   **Envanter Koruması:** Oyuncular arenaya girdiğinde tüm eşyaları ve XP'leri kaydedilir. Maç sonunda veya çıkış yaptıklarında her şey milimetrik olarak geri verilir.
*   **Anti-Dupe:** Ölen oyuncuların item düşürmesi ve haksız kazanç engellenmiştir.
*   **Dünya Koruması:** Arena içerisinde blok kırma, koyma ve mob doğması engellenmiştir.

---

## 🖥️ Menü (GUI) Sistemi

*   **Arena Seçim Menüsü:** `/ed katil` ile açılır. Arenalar doluluk ve durumuna göre renk değiştirir (Yeşil/Sarı/Kırmızı).
*   **Takım/Renk Seçimi:** Girişte her oyuncu kendine özel bir renk seçer.
*   **Admin Menüsü:** `/ed admin gui <arena>` ile komut yazmadan arena kurulumu sağlar.
*   **İstatistik Menüsü:** `/ed stats` ile detaylı oyuncu profili görüntülenir.

---

## ⌨️ Komutlar ve Yetkiler

### Kullanıcı Komutları
*   `/ed katil [arena]` - Bir arenaya katılır veya menüyü açar. (`ensdaire.play`)
*   `/ed ayrıl` - Mevcut oyundan çıkıp lobiye döner. (`ensdaire.play`)
*   `/ed stats` - Kendi istatistiklerini görür. (`ensdaire.play`)

### Yönetici Komutları (`ensdaire.admin`)
*   `/ed admin gui <arena>` - Görsel arena yönetim panelini açar.
*   `/ed admin create <isim>` - Yeni bir arena oluşturur.
*   `/ed admin setlobby <arena>` - Lobi noktasını belirler.
*   `/ed admin addcapsule <arena>` - Bulunduğun yeri kapsül spawnı olarak ekler.
*   `/ed admin addshulker <arena>` - Shulker doğma noktası ekler.
*   `/ed reload` - Dosyaları yeniler.

---

## 🧩 PlaceholderAPI Desteği

Sunucunda şu placeholderları kullanabilirsin:
*   `%ensdaire_jeton%` - Mevcut jeton miktarı.
*   `%ensdaire_puan%` - Toplam puan.
*   `%ensdaire_kazanma%` - Toplam galibiyet sayısı.
*   `%ensdaire_kd%` - Kill/Death oranı.
*   `%ensdaire_rutbe%` - Mevcut rütbe adı.
*   `%ensdaire_top_isim_1%` - Sıralamadaki 1. kişinin ismi.
*   `%ensdaire_top_token_1%` - Sıralamadaki 1. kişinin jetonu.
*   `%ensdaire_toplam_oyuncu%` - Tüm arenalardaki aktif oyuncu sayısı.

---

## 🛠️ Kurulum

1.  `EnsDaire.jar` dosyasını `plugins/` klasörüne atın.
2.  Sunucuyu başlatın.
3.  `messages_tr.yml` üzerinden mesajları özelleştirebilirsiniz.
4.  Admin menüsünü kullanarak ilk arenanızı 1 dakika içinde kurun!

**Geliştirici:** ensis.mc
**Sürüm:** 1.0.0
**Uyumlu Sürümler:** 1.20 - 1.21.x
