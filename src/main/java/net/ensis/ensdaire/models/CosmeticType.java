package net.ensis.ensdaire.models;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

public enum CosmeticType {
    // --- İZ EFEKTLERİ (TRAILS) ---
    TRAIL_FLAME("Alev İzi", "Adımlarınızda ateş dansı.", Material.BLAZE_POWDER, 2000, Category.TRAIL, Particle.FLAME),
    TRAIL_HEART("Aşk İzi", "Kalpler her yerde!", Material.POPPY, 3000, Category.TRAIL, Particle.HEART),
    TRAIL_SOUL("Ruh İzi", "Mavi ruh ateşleri.", Material.SOUL_LANTERN, 4000, Category.TRAIL, Particle.SOUL_FIRE_FLAME),
    TRAIL_CRIT("Kritik İzi", "Sürekli kritik vuruş efekti.", Material.IRON_SWORD, 1500, Category.TRAIL, Particle.CRIT),
    TRAIL_GLOW("Parlayan İz", "Büyülü tozlar.", Material.GLOWSTONE_DUST, 2500, Category.TRAIL, Particle.GLOW),

    // --- ÖLDÜRME EFEKTLERİ (KILL EFFECTS) ---
    KILL_EXPLOSION("Patlama", "Rakibiniz havaya uçar!", Material.TNT, 5000, Category.KILL, null),
    KILL_LIGHTNING("Yıldırım", "Göklerden gelen adalet.", Material.LIGHTNING_ROD, 6000, Category.KILL, null),
    KILL_BLOOD("Kan Banyosu", "Kırmızı toz partikülleri.", Material.REDSTONE, 3500, Category.KILL, null),
    KILL_GHOST("Ruhun Ayrılışı", "Beyaz bulutlar yükselir.", Material.GHAST_TEAR, 4500, Category.KILL, null),

    // --- MERMİ İZLERİ (PROJECTILE TRAILS) ---
    ARROW_SMOKE("Kara Duman", "Okların arkasında duman kalır.", Material.COAL, 2000, Category.PROJECTILE, Particle.SMOKE_NORMAL),
    ARROW_SPARK("Kıvılcım", "Elektrikli oklar!", Material.COPPER_INGOT, 3000, Category.PROJECTILE, Particle.ELECTRIC_SPARK),
    ARROW_MAGIC("Büyü İzi", "Cadı iksiri efekti.", Material.DRAGON_BREATH, 4000, Category.PROJECTILE, Particle.SPELL_WITCH),

    // --- KAZANMA DANSLARI (WIN EFFECTS) ---
    WIN_FIREWORKS("Havai Fişek Şöleni", "Gökyüzü renklenir.", Material.FIREWORK_ROCKET, 1000, Category.WIN, null),
    WIN_METEOR("Meteor Yağmuru", "Gökten ateş topları düşer.", Material.MAGMA_BLOCK, 10000, Category.WIN, null);

    private final String name;
    private final String desc;
    private final Material icon;
    private final int cost;
    private final Category category;
    private final Particle particle;

    CosmeticType(String name, String desc, Material icon, int cost, Category category, Particle particle) {
        this.name = name;
        this.desc = desc;
        this.icon = icon;
        this.cost = cost;
        this.category = category;
        this.particle = particle;
    }

    public enum Category {
        TRAIL("Yürüme İzleri"), 
        KILL("Öldürme Efektleri"), 
        PROJECTILE("Ok Efektleri"), 
        WIN("Kazanma Dansları");
        
        private final String displayName;
        Category(String dn) { this.displayName = dn; }
        public String getDisplayName() { return displayName; }
    }

    public String getName() { return name; }
    public String getDesc() { return desc; }
    public Material getIcon() { return icon; }
    public int getCost() { return cost; }
    public Category getCategory() { return category; }
    public Particle getParticle() { return particle; }
}
