/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thevoxelbox.voxelborder;

import org.bukkit.entity.EnderPearl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 *
 * @author Voxel
 */
public class BorderEntity implements Listener {

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            ((EnderPearl) event.getEntity()).teleport(((EnderPearl) event.getEntity()).getShooter().getLocation().add(0, 1, 0), TeleportCause.ENDER_PEARL);
        }
    }
}
