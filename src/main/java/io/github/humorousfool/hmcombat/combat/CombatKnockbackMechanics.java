package io.github.humorousfool.hmcombat.combat;

import io.github.humorousfool.hmcombat.config.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * @author Kernitus, HumorousFool
 */

public class CombatKnockbackMechanics implements Listener
{
    private final double knockbackHorizontal = Config.KnockbackHorizontal;
    private final double knockbackVertical = Config.KnockbackVertical;
    private final double knockbackVerticalLimit = Config.KnockbackVerticalLimit;
    private final double knockbackExtraHorizontal = Config.KnockbackExtraHorizontal;
    private final double knockbackExtraVertical = Config.KnockbackExtraVertical;

    Random random = new Random();
    private final HashMap<UUID, Vector> playerKnockbackHashMap = new HashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerKnockbackHashMap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerVelocityEvent(PlayerVelocityEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        if (!playerKnockbackHashMap.containsKey(uuid)) return;
        event.setVelocity(playerKnockbackHashMap.get(uuid));
        playerKnockbackHashMap.remove(uuid);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if(event.isCancelled()) return;

        //Sprint Crits
        if(Config.EnableSprintCrits && event.getDamager().getType() == EntityType.PLAYER && event.getEntity() instanceof LivingEntity)
        {
            if(((Player) event.getDamager()).isSprinting() && event.getDamager().getVelocity().getY() < -0.0784000015258789)
            {
                event.setDamage(event.getDamage() * (1.5 + (random.nextDouble() / 4)));
                Location loc = event.getEntity().getLocation();
                loc.setY(loc.getY() + 0.5D);
                event.getEntity().getWorld().spawnParticle(Particle.CRIT, loc, 5, 0.1, 0, 0.1, 0.2);
                event.getEntity().getWorld().playSound(loc, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1f, 1f);
            }
        }

        //Old Knockback
        if (!(event.getDamager() instanceof final LivingEntity attacker)) return;

        if (!(event.getEntity() instanceof final Player victim)) return;

        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) > 0) return;

        // Figure out base knockback direction
        double d0 = attacker.getLocation().getX() - victim.getLocation().getX();
        double d1;

        for (d1 = attacker.getLocation().getZ() - victim.getLocation().getZ();
             d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
            d0 = (Math.random() - Math.random()) * 0.01D;
        }

        final double magnitude = Math.sqrt(d0 * d0 + d1 * d1);

        // Get player knockback before any friction is applied
        final Vector playerVelocity = victim.getVelocity();

        // Apply friction, then add base knockback
        playerVelocity.setX((playerVelocity.getX() / 2) - (d0 / magnitude * knockbackHorizontal));
        playerVelocity.setY((playerVelocity.getY() / 2) + knockbackVertical);
        playerVelocity.setZ((playerVelocity.getZ() / 2) - (d1 / magnitude * knockbackHorizontal));

        // Calculate bonus knockback for sprinting or knockback enchantment levels
        final EntityEquipment equipment = attacker.getEquipment();
        if (equipment != null) {
            final ItemStack heldItem = equipment.getItemInMainHand().getType() == Material.AIR ?
                    equipment.getItemInOffHand() : equipment.getItemInMainHand();

            int bonusKnockback = heldItem.getEnchantmentLevel(Enchantment.KNOCKBACK);
            if (attacker instanceof Player && ((Player) attacker).isSprinting()) ++bonusKnockback;

            if (playerVelocity.getY() > knockbackVerticalLimit) playerVelocity.setY(knockbackVerticalLimit);

            if (bonusKnockback > 0) { // Apply bonus knockback
                playerVelocity.add(new Vector((-Math.sin(attacker.getLocation().getYaw() * 3.1415927F / 180.0F) *
                        (float) bonusKnockback * knockbackExtraHorizontal), knockbackExtraVertical,
                        Math.cos(attacker.getLocation().getYaw() * 3.1415927F / 180.0F) *
                                (float) bonusKnockback * knockbackExtraHorizontal));
            }
        }
        // Allow netherite to affect the horizontal knockback. Each piece of armour yields 10% resistance
        final double resistance = 1 - victim.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue();
        playerVelocity.multiply(new Vector(resistance, 1, resistance));

        // Knockback is sent immediately in 1.8+, there is no reason to send packets manually
        playerKnockbackHashMap.put(victim.getUniqueId(), playerVelocity);
    }
}
