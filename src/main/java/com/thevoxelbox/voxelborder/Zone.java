package com.thevoxelbox.voxelborder;

import com.thevoxelbox.voxelborder.util.VoxelAdminUtil;

import java.io.PrintWriter;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 *
 * @author Voxel
 */
public class Zone {

    private String name;
    private String world;
    private int lowx;
    private int lowz;
    private int highx;
    private int highz;
    private int lowinx;
    private int lowinz;
    private int highinx;
    private int highinz;
    private int[] in;
    private int[] out;
    private int[] inTP;
    private int[] outTP;
    private String inmessage;
    private String indeny;
    private String outmessage;
    private String outdeny;
    private String inTPmessage;
    private String inTPdeny;
    private String outTPmessage;
    private String outTPdeny;
    private ArrayList<Integer> temp;

    public Zone(String zoneName) {
        name = zoneName;
    }

    public void save(PrintWriter pw) {
        pw.println("Border:");
        write(pw, "name: " + name);
        write(pw, "world: " + world);
        write(pw, "highx: " + highx);
        write(pw, "highz: " + highz);
        write(pw, "lowx: " + lowx);
        write(pw, "lowz: " + lowz);
        write(pw, "inmsg: " + inmessage);
        write(pw, "indeny: " + indeny);
        write(pw, "outmsg: " + outmessage);
        write(pw, "outdeny: " + outdeny);
        write(pw, "inTPmsg: " + inTPmessage);
        write(pw, "inTPdeny: " + inTPdeny);
        write(pw, "outTPmsg: " + outTPmessage);
        write(pw, "outTPdeny: " + outTPdeny);
        write(pw, "in:");
        write(pw, in);
        write(pw, "out:");
        write(pw, out);
        write(pw, "inTP:");
        write(pw, inTP);
        write(pw, "outTP:");
        write(pw, outTP);
    }

    private void write(PrintWriter pw, String str) {
        pw.println("\t" + str);
    }

    private void write(PrintWriter pw, int[] ar) {
        if (ar == null || ar.length == 0) {
            write(pw, "\t*");
            return;
        }
        for (int _i = 0; _i < ar.length; _i++) {
            write(pw, "\t" + VoxelAdminUtil.getListName(ar[_i]));
        }
    }

    public void read(String _param, String value) {
        _param = _param.trim();
        value = value.trim();
        if (_param.equals("name")) {
            name = value;
        } else if (_param.equals("world")) {
            world = value;
        } else if (_param.equals("in")) {
            if (temp == null) {
                temp = new ArrayList<Integer>();
            }
            if (value.equals("*")) {
                in = null;
                return;
            }
            temp.add(VoxelAdminUtil.getListID(value));
        } else if (_param.equals("out")) {
            if (temp == null) {
                temp = new ArrayList<Integer>();
            } else {
                if (temp.isEmpty()) {
                    in = null;
                } else {
                    in = new int[temp.size()];
                    for (int _i = 0; _i < temp.size(); _i++) {
                        in[_i] = temp.get(_i);
                    }
                }
                temp.clear();
            }
            if (value.equals("*")) {
                out = null;
                return;
            }
            temp.add(VoxelAdminUtil.getListID(value));
        } else if (_param.equals("inTP")) {
            if (temp == null) {
                temp = new ArrayList<Integer>();
            } else {
                if (temp.isEmpty()) {
                    out = null;
                } else {
                    out = new int[temp.size()];
                    for (int _i = 0; _i < temp.size(); _i++) {
                        out[_i] = temp.get(_i);
                    }
                }
                temp.clear();
            }
            if (value.equals("*")) {
                inTP = null;
                return;
            }
            temp.add(VoxelAdminUtil.getListID(value));
        } else if (_param.equals("outTP")) {
            if (temp == null) {
                temp = new ArrayList<Integer>();
            } else {
                if (temp.isEmpty()) {
                    inTP = null;
                } else {
                    inTP = new int[temp.size()];
                    for (int _i = 0; _i < temp.size(); _i++) {
                        inTP[_i] = temp.get(_i);
                    }
                }
                temp.clear();
            }
            if (value.equals("*")) {
                outTP = null;
                return;
            }
            temp.add(VoxelAdminUtil.getListID(value));
        } else if (_param.equals("highx")) {
            highx = Integer.parseInt(value);
        } else if (_param.equals("highz")) {
            highz = Integer.parseInt(value);
        } else if (_param.equals("lowx")) {
            lowx = Integer.parseInt(value);
        } else if (_param.equals("lowz")) {
            lowz = Integer.parseInt(value);
        } else if (_param.equals("inmsg")) {
            inmessage = (value.equals("null") ? null : value);
        } else if (_param.equals("indeny")) {
            indeny = (value.equals("null") ? null : value);
        } else if (_param.equals("outmsg")) {
            outmessage = (value.equals("null") ? null : value);
        } else if (_param.equals("outdeny")) {
            outdeny = (value.equals("null") ? null : value);
        } else if (_param.equals("inTPmsg")) {
            inTPmessage = (value.equals("null") ? null : value);
        } else if (_param.equals("inTPdeny")) {
            inTPdeny = (value.equals("null") ? null : value);
        } else if (_param.equals("outTPmsg")) {
            outTPmessage = (value.equals("null") ? null : value);
        } else if (_param.equals("outTPdeny")) {
            outTPdeny = (value.equals("null") ? null : value);
        }
    }

    public void finishRead() {
        if (temp.isEmpty()) {
            outTP = null;
        } else {
            outTP = new int[temp.size()];
            for (int _i = 0; _i < temp.size(); _i++) {
                outTP[_i] = temp.get(_i);
            }
        }
        temp.clear();
        temp = null;
        lowinx = lowx + 1;
        lowinz = lowz + 1;
        highinx = highx - 1;
        highinz = highz - 1;
        VoxelBorder.log.info("[VoxelBorder] Border \"" + name + "\" has been loaded!");
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public void setName(String zoneName) {
        name = zoneName;
    }

    public void setWorld(String worldName) {
        world = worldName;
    }

    public void setBound(Block b1, Block b2) {
        lowx = b1.getX() <= b2.getX() ? b1.getX() : b2.getX();
        lowz = b1.getZ() <= b2.getZ() ? b1.getZ() : b2.getZ();
        highx = b1.getX() >= b2.getX() ? b1.getX() : b2.getX();
        highz = b1.getZ() >= b2.getZ() ? b1.getZ() : b2.getZ();
        lowinx = lowx + 1;
        lowinz = lowz + 1;
        highinx = highx - 1;
        highinz = highz - 1;
    }

    public int lowX() {
        return lowx;
    }

    public int lowZ() {
        return lowz;
    }

    public int highX() {
        return highx;
    }

    public int highZ() {
        return highz;
    }

    public boolean inBound(Block b) {
        return lowx <= b.getX()
                && lowz <= b.getZ()
                && b.getX() <= highx
                && b.getZ() <= highz;
    }

    public boolean inBound(Location l) {
        return lowx <= l.getX()
                && lowz <= l.getZ()
                && l.getX() <= highx
                && l.getZ() <= highz;
    }

    public boolean inInnerBound(Location l) {
        return lowinx <= l.getX()
                && lowinz <= l.getZ()
                && l.getX() <= highinx
                && l.getZ() <= highinz;
    }

    public boolean inBound(int x, int z) {
        return lowx <= x
                && lowz <= z
                && x <= highx
                && z <= highz;
    }

    private void send(String msg, Player player) {
        if (msg == null) {
            player.sendMessage(ChatColor.GOLD + "Now crossing the border of " + name.trim());
        } else {
            player.sendMessage(msg);
        }
    }

    private void sendDeny(String msg, Player player) {
        if (msg == null) {
            player.sendMessage(ChatColor.DARK_GREEN + "You may not cross the border of " + name.trim());
        } else {
            player.sendMessage(msg);
        }
    }

    public boolean deny(Location from, Location to, Player player) {
        if (inInnerBound(from)) {
            if (inInnerBound(to)) {
                return false;
            } else {
                if (out == null) {
                    sendDeny(outdeny, player);
                    return true;
                } else {
                    String pname = player.getName();
                    for (int i : out) {
                        if (VoxelAdminUtil.contains(i, pname)) {
                            return false;
                        }
                    }
                    sendDeny(outdeny, player);
                    return true;
                }
            }
        }
        if (inBound(from)) {
            if (inBound(to)) {
                if (inInnerBound(to)) {
                    return false;
                } else {
                    if (out == null) {
                        sendDeny(outdeny, player);
                        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation(), TeleportCause.ENDER_PEARL);
                        return false;
                    } else {
                        String pname = player.getName();
                        for (int _i : out) {
                            if (VoxelAdminUtil.contains(_i, pname)) {
                                return false;
                            }
                        }
                        sendDeny(outdeny, player);
                        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation(), TeleportCause.ENDER_PEARL);
                        return false;
                    }
                }
            } else {
                if (out == null) {
                    sendDeny(outdeny, player);
                    return true;
                } else {
                    String _playerName = player.getName();
                    for (int _i : out) {
                        if (VoxelAdminUtil.contains(_i, _playerName)) {
                            send(outmessage, player);
                            return false;
                        }
                    }
                    sendDeny(outdeny, player);
                    return true;
                }
            }
        } else {
            if (inBound(to)) {
                if (in == null) {
                    sendDeny(indeny, player);
                    return true;
                } else {
                    String _playerName = player.getName();
                    for (int i : in) {
                        if (VoxelAdminUtil.contains(i, _playerName)) {
                            send(inmessage, player);
                            return false;
                        }
                    }
                    sendDeny(indeny, player);
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public boolean denyTP(Location from, Location to, Player player) {
        if (inBound(from)) {
            if (inBound(to)) {
                return false;
            } else {
                if (outTP == null) {
                    sendDeny(outTPdeny, player);
                    return true;
                } else {
                    String _playerName = player.getName();
                    for (int _i : outTP) {
                        if (VoxelAdminUtil.contains(_i, _playerName)) {
                            send(outTPmessage, player);
                            return false;
                        }
                    }
                    sendDeny(outTPdeny, player);
                    return true;
                }
            }
        } else {
            if (inBound(to)) {
                if (inTP == null) {
                    sendDeny(inTPdeny, player);
                    return true;
                } else {
                    String pname = player.getName();
                    for (int i : inTP) {
                        if (VoxelAdminUtil.contains(i, pname)) {
                            send(inTPmessage, player);
                            return false;
                        }
                    }
                    sendDeny(inTPdeny, player);
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public boolean denyTPfrom(Location from, Player player) {
        if (inBound(from)) {
            if (outTP == null) {
                sendDeny(outTPdeny, player);
                return true;
            } else {
                String pname = player.getName();
                for (int _i : outTP) {
                    if (VoxelAdminUtil.contains(_i, pname)) {
                        send(outTPmessage, player);
                        return false;
                    }
                }
                sendDeny(outTPdeny, player);
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean denyTPto(Location to, Player player) {
        if (inBound(to)) {
            if (inTP == null) {
                sendDeny(inTPdeny, player);
                return true;
            } else {
                String pname = player.getName();
                for (int i : inTP) {
                    if (VoxelAdminUtil.contains(i, pname)) {
                        send(inTPmessage, player);
                        return false;
                    }
                }
                sendDeny(inTPdeny, player);
                return true;
            }
        } else {
            return false;
        }
    }
}
