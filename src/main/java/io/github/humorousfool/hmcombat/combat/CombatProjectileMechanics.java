package io.github.humorousfool.hmcombat.combat;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

/**
 * @author Kernitus
 */
public class CombatProjectileMechanics implements Listener
{
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent e){
        Projectile projectile = e.getEntity();
        ProjectileSource shooter = projectile.getShooter();

        if(shooter instanceof Player player)
        {

            Vector playerDirection = player.getLocation().getDirection().normalize();
            Vector projectileDirection = projectile.getVelocity();

            // Keep original speed
            double originalMagnitude = projectileDirection.length();
            projectileDirection.normalize();

            // The following works because using rotate modifies the vector, so we must double it to undo the rotation
            // The vector is rotated around the Y axis and matched by checking only the X and Z values
            // Angles is specified in radians, where 10° = 0.17 radians
            if(!fuzzyVectorEquals(projectileDirection, playerDirection)) { // If the projectile is not going straight
                if (!fuzzyVectorEquals(projectileDirection, playerDirection.rotateAroundY(0.17))) {
                    playerDirection.rotateAroundY(-0.35);
                }
            }

            playerDirection.multiply(originalMagnitude);
            projectile.setVelocity(playerDirection);
        }
    }

    private boolean fuzzyVectorEquals(Vector a, Vector b){
        return Math.abs(a.getX() - b.getX()) < 0.1D &&
                Math.abs(a.getZ() - b.getZ()) < 0.1D;
    }
}
