package net.eduard.api.lib.game;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Criado em 28/11/2019
 * Atualizado em 07/05/2020
 *
 * @author Eduard
 * @version 1.1
 */
public class Hologram {
    private Location location;
    private EntityArmorStand holo;
    private String text;
    private List<String> seeingHolo = new ArrayList<>();
    private static boolean debug = false;

    private static void log(String msg) {
        if (debug)
            System.out.println("[Hologram] " + msg);
    }

    public Hologram() {


    }

    public void updateAll(int distance) {

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(location.getWorld())) {
                if (player.getLocation().distance(location) <= distance) {
                    update(player);
                } else {
                    if (seeingHolo.contains(player.getName())) {
                        hide(player);

                    }
                }
            }
        }
    }

    public Hologram(Location loc) {
        setLocation(loc);
        setText("§fHolograma");
        spawn();
    }

    public String getText() {
        return text;
    }

    private void sendPacket(Player p, Packet packet) {
        CraftPlayer craftPlayer = (CraftPlayer) p;
        EntityPlayer nms = craftPlayer.getHandle();
        nms.playerConnection.sendPacket(packet);
    }

    public void show(Player p) {
        log("§aMostrando holograma1");
        seeingHolo.add(p.getName());

        PacketPlayOutSpawnEntityLiving pacote = new PacketPlayOutSpawnEntityLiving(holo);
        sendPacket(p, pacote);


    }

    public boolean exists() {
        return holo != null;
    }

    public void hide(Player p) {
        log("§cEscondendo holograma1");
        seeingHolo.remove(p.getName());
        if (exists()) {
            PacketPlayOutEntityDestroy pacote = new PacketPlayOutEntityDestroy(getHolo().getId());
            sendPacket(p, pacote);
        }


    }


    public void update(Player p) {
        // tentativas de mandar packets de atualização da entidade
        setText(getText());
        PacketPlayOutUpdateEntityNBT updateNBT = new PacketPlayOutUpdateEntityNBT(holo.getId(), holo.getNBTTag());


        PacketPlayOutEntityMetadata updateMetadata = new PacketPlayOutEntityMetadata(holo.getId(), holo.getDataWatcher(), false);


        // PacketPlayOutTitle a = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("title linha 1"));
        // PacketPlayOutTitle a = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("action bar"));
        if (seeingHolo.contains(p.getName())) {
            log("§bAtualizando holograma2");
            //sendPacket(p, pacote);
            sendPacket(p, updateMetadata);
        } else {

            show(p);
        }

    }

    public void setText(String text) {
        this.text = text;
        if (getHolo() != null) {
            holo.setCustomName(text);
            holo.setCustomNameVisible(true);
        }
    }

    private EntityArmorStand getHolo() {
        if (holo == null) {
            if (location != null) {
                World nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
                holo = new EntityArmorStand(nmsWorld, location.getX(), location.getY(), location.getZ());
                holo.setInvisible(true);
                holo.setSmall(true);
            }

        }

        return holo;
    }

    public void spawn(Location location) {
        setLocation(location);
        spawn();
    }

    public void spawn() {

        getHolo();
        setText(getText());
        // metodo que faz a entidade spawnar no servidor e ser contralada por ele
        // nmsWorld.addEntity(armorstand, CreatureSpawnEvent.SpawnReason.CUSTOM);
        updateAll(10);

    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
