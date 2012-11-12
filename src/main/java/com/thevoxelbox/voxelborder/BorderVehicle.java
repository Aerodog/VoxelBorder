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
            Player _player = ((Player) event.getVehicle().getPassenger());
            if (BorderListener.getZones().containsKey(event.getTo().getWorld().getUID())) {
                for (Zone _zone : BorderListener.getZones().get(event.getTo().getWorld().getUID())) {
                    if (_zone.deny(event.getFrom(), event.getTo(), _player)) {
                        Entity entity = event.getVehicle().getPassenger();
                        event.getVehicle().eject();
                        entity.teleport(event.getFrom(), TeleportCause.PLUGIN);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            Player _player = ((Player) event.getEntered());
            if (BorderListener.getZones().containsKey(event.getEntered().getWorld().getUID())) {
                for (Zone _z : BorderListener.getZones().get(event.getEntered().getWorld().getUID())) {
                    Location _startLoc = event.getEntered().getLocation();
                    Location _vehicleLoc = event.getVehicle().getLocation();
                    if (_z.deny(_startLoc, _vehicleLoc, _player)) {
                        event.getVehicle().eject();
                        event.getEntered().teleport(_startLoc, TeleportCause.PLUGIN);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
