package ru.alfomine.afmvanish.hooks;

import gloomyfolken.hooklib.asm.Hook;
import gloomyfolken.hooklib.asm.ReturnCondition;
import li.cil.oc.server.machine.Machine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.common.tileentity.TileEntityEntityDetector;
import pl.asie.computronics.reference.Config;
import pl.asie.computronics.util.RadarUtils;
import ru.alfomine.afmvanish.vanish.VanishManager;
import li.cil.oc.api.network.ComponentConnector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VanishHookContainer {
    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static int getCurrentPlayerCount(MinecraftServer anus) {
        return VanishManager.getOnlinePlayersNonVanished();
    }

    public static HashMap<String, Object> info(Entity entity, BlockPos offset, BlockPos a, ComponentConnector node) {
        HashMap<String, Object> value = new HashMap<String, Object>();

        double rangeToEntity = entity.getDistance(a.getX(), a.getY(), a.getZ());
        String name;
        if (entity instanceof EntityPlayer)
            name = ((EntityPlayer) entity).getDisplayNameString();
        else
            name = entity.getName();

        BlockPos entityLocalPosition = entity.getPosition().subtract(offset);

        value.put("name", name);
        value.put("range", rangeToEntity);
        value.put("height", entity.height);
        value.put("x", entityLocalPosition.getX());
        value.put("y", entityLocalPosition.getY());
        value.put("z", entityLocalPosition.getZ());
        node.sendToReachable("computer.signal", "entityDetect", name, rangeToEntity, entityLocalPosition.getX(), entityLocalPosition.getY(), entityLocalPosition.getZ());

        return value;
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static Map<Integer, HashMap<String, Object>> scan(TileEntityEntityDetector anus, boolean players, BlockPos offset) {
        Map<Integer, HashMap<String, Object>> output = new HashMap<>();
        int index = 1;

        int range;

        try {
            Field field = anus.getClass().getDeclaredField("range");
            field.setAccessible(true);

            range = (int) field.get(anus);
        } catch (Exception e) {
            e.printStackTrace();

            return output;
        }

        for (Entity entity : anus.getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(anus.getPos(), anus.getPos()).grow(range))) {
            if (players && entity instanceof EntityPlayer) {
                if (VanishManager.tablistVisibleList.contains(((EntityPlayer) entity).getDisplayNameString()))
                    output.put(index++, info(entity, offset, anus.getPos(), anus.node));
            } else if (!players && !(entity instanceof EntityPlayer)) {
                output.put(index++, info(entity, offset, anus.getPos(), anus.node));
            }
        }

        return output;
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static Set<Map<String, Object>> getEntities(RadarUtils anus, World world, double xCoord, double yCoord, double zCoord, AxisAlignedBB bounds, Class<? extends EntityLivingBase> eClass) {
        Set<Map<String, Object>> entities = new HashSet<Map<String, Object>>();
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(eClass, bounds)) {
            if (eClass == EntityPlayer.class && !VanishManager.tablistVisibleList.contains(((EntityPlayer) entity).getDisplayNameString())) {
                continue;
            }

            double dx = entity.posX - xCoord;
            double dy = entity.posY - yCoord;
            double dz = entity.posZ - zCoord;
            if (Math.sqrt(dx * dx + dy * dy + dz * dz) < Config.RADAR_RANGE) {
                Map<String, Object> entry = new HashMap<String, Object>();
                entry.put("name", entity.getName());
                if (!Config.RADAR_ONLY_DISTANCE) {
                    entry.put("x", (int) dx);
                    entry.put("y", (int) dy);
                    entry.put("z", (int) dz);
                }
                entry.put("distance", Math.sqrt(dx * dx + dy * dy + dz * dz));
                entities.add(entry);
            }
        }
        return entities;
    }

    @Hook(returnCondition = ReturnCondition.ALWAYS)
    public static void addUser(Machine anus, String name) throws Exception {
        if (anus.li$cil$oc$server$machine$Machine$$_users().size() >= li.cil.oc.Settings.get().maxUsers()) {
            throw new Exception("too many users");
        } else if (anus.li$cil$oc$server$machine$Machine$$_users().contains(name)) {
            throw new Exception("user exists");
        } else if (name.length() > li.cil.oc.Settings.get().maxUsernameLength()) {
            throw new Exception("username too long");
        } else if (VanishManager.tablistVisibleList.contains(name)) {
            synchronized (anus.li$cil$oc$server$machine$Machine$$_users()) {
                anus.li$cil$oc$server$machine$Machine$$_users().$plus$eq(name);

                try {
                    Method method = anus.getClass().getDeclaredMethod("usersChanged_$eq", boolean.class);
                    method.setAccessible(true);
                    method.invoke(anus, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            throw new Exception("player must be online (beu!)");
        }
    }
}
