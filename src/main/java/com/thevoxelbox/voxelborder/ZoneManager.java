package com.thevoxelbox.voxelborder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.gson.Gson;

/**
 * Handles all currently active zones. Also handles zone serialization.
 * 
 * @author TheCryoknight
 */
public class ZoneManager
{
    private static final File ZONE_FILE = new File("plugins/voxelborder/zones.json");

    public static File getZoneFile()
    {
        return ZoneManager.ZONE_FILE;
    }

    private final String basePerm = "voxelborder.zone.";

    private final Set<Zone> zones = new HashSet<Zone>();

    private final VoxelBorder plugin;

    public ZoneManager(final VoxelBorder plugin)
    {
        this.plugin = plugin;
        final File zoneFolder = new File("plugins/voxelborder/");
        if (!zoneFolder.exists())
        {
            zoneFolder.mkdirs();
        }
        this.readZones(ZoneManager.ZONE_FILE);
    }

    /**
     * Adds a zone to the active list
     * 
     * @param newZone
     *            Zone to add
     */
    public void addZone(final Zone newZone)
    {
        this.zones.add(newZone);
    }

    /**
     * Checks to see if a player can move from a location to another location.
     * 
     * @param player
     *            The player moving
     * @param startLoc
     *            Originating location
     * @param endLoc
     *            Ending location
     * @return True if the specified player can move
     */
    public boolean canMoveTo(final Player player, final Location startLoc, final Location endLoc)
    {
        for (final Zone zone : this.zones)
        {
            if (zone.getWorldID().equals(endLoc.getWorld().getUID()))
            {
                if (zone.inBound(endLoc))
                {
                    if (zone.inBound(startLoc))
                    {
                        return true;
                    }
                    if (player.isOp() ? true : player.hasPermission(this.basePerm + zone.getName().replaceAll(" ", "")))
                    {
                        player.sendMessage("§cNow crossing border of " + zone.getName().trim());
                        return true;
                    }
                    else
                    {
                        player.sendMessage("§cYou can not cross the border of " + zone.getName().trim());
                        return false;
                    }
                }
            }
        }
        player.sendMessage("§cYou can not access area outside of the the borders");
        return false;
    }

    public Zone getZone(final String zoneName)
    {
        for (final Zone z : this.zones)
        {
            if (z.getName().equalsIgnoreCase(zoneName))
            {
                return z;
            }
        }
        return null;
    }

    /**
     * Searches the names of all the active zones and returns a list of all the zones that start with the search term.
     * 
     * @param str
     *            the search term
     * @return A list of strings that start with the search term
     */
    public List<String> lookupZone(final String str)
    {
        final List<String> matches = new ArrayList<String>();
        for (final Zone z : this.zones)
        {
            if (z.getName().toLowerCase().startsWith(str))
            {
                matches.add(z.getName());
            }
        }
        return matches.isEmpty() ? null : matches;
    }

    public void readZones(final File zoneFile)
    {
        final Gson gson = new Gson();
        if (zoneFile.exists())
        {
            Scanner scan;
            try
            {
                scan = new Scanner(zoneFile);
            }
            catch (final Exception e)
            {
                this.plugin.getLogger().severe("Can not open config files");
                e.printStackTrace();
                return;
            }
            try
            {
                while (scan.hasNext())
                {
                    final Zone zone = gson.fromJson(scan.nextLine(), Zone.class);
                    this.zones.add(zone);
                }
            }
            catch (final Exception e)
            {
                this.plugin.getLogger().severe("Can not read config files");
                e.printStackTrace();
                return;
            }
            finally
            {
                scan.close();
            }
        }
        else
        {

        }
    }

    /**
     * Removes a zone from the active list
     * 
     * @param oldZone
     *            Zone to remove
     * @return true if successfully removed
     */
    public boolean removeZone(final Zone oldZone)
    {
        return this.zones.remove(oldZone);
    }

    public void saveZones(final File zonefile)
    {
        final Gson gson = new Gson();
        if (!zonefile.exists())
        {
            try
            {
                zonefile.createNewFile();
            }
            catch (final IOException e)
            {
                e.printStackTrace();
                return;
            }
        }
        else
        {
            if (!zonefile.delete())
            {
                this.plugin.getLogger().severe("Can not save config files");
            }
            try
            {
                zonefile.createNewFile();
            }
            catch (final IOException e)
            {
                e.printStackTrace();
                return;
            }
        }
        PrintWriter pw;
        try
        {
            pw = new PrintWriter(zonefile);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            return;
        }
        for (final Zone z : this.zones)
        {
            try
            {
                pw.print(gson.toJson(z) + "\n");
            }
            finally
            {
                pw.close();
            }
        }
    }
}
