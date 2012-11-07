package com.thevoxelbox.voxelborder;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

/**
 *
 * @author Voxel
 */
public class BorderVehicle implements Listener {

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (event.getVehicle().getPassenger() instanceof Player) {
            Player player = ((Player) event.getVehicle().getPassenger());
            if (BorderListener.zones.containsKey(event.getTo().getWorld().getUID())) {
                for (Zone z : BorderListener.zones.get(event.getTo().getWorld().getUID())) {
                    if (z.deny(event.getFrom(), event.getTo(), player)) {
                        Entity e = event.getVehicle().getPassenger();
                        event.getVehicle().eject();
                        e.teleport(event.getFrom(), TeleportCause.PLUGIN);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            Player player = ((Player) event.getEntered());
            if (BorderListener.zones.containsKey(event.getEntered().getWorld().getUID())) {
                for (Zone z : BorderListener.zones.get(event.getEntered().getWorld().getUID())) {
                    Location f = event.getEntered().getLocation();
                    Location t = event.getVehicle().getLocation();
                    if (z.deny(f, t, player)) {
                        event.getVehicle().eject();
                        event.getEntered().teleport(f, TeleportCause.PLUGIN);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
