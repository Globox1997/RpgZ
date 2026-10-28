package net.rpgz.util;

import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CorpseBox {

    public static boolean isCorpse(Entity entity) {
        return entity instanceof MobEntity mobEntity && mobEntity.isDead() && mobEntity.deathTime >= 20;
    }

    public static Box getBoundingBox(MobEntity mobEntity) {
        Box localBox = getLocalBox(mobEntity);
        double minX = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        for (double x : new double[] { localBox.minX, localBox.maxX }) {
            for (double z : new double[] { localBox.minZ, localBox.maxZ }) {
                Vec3d corner = toWorld(mobEntity, new Vec3d(x, 0.0D, z));
                minX = Math.min(minX, corner.getX());
                minZ = Math.min(minZ, corner.getZ());
                maxX = Math.max(maxX, corner.getX());
                maxZ = Math.max(maxZ, corner.getZ());
            }
        }
        return new Box(minX, mobEntity.getY() + localBox.minY, minZ, maxX, mobEntity.getY() + localBox.maxY, maxZ);
    }

    public static Optional<Vec3d> raycast(MobEntity mobEntity, Vec3d start, Vec3d end) {
        return getLocalBox(mobEntity).raycast(toLocal(mobEntity, start), toLocal(mobEntity, end)).map(localHit -> toWorld(mobEntity, localHit));
    }

    private static Box getLocalBox(MobEntity mobEntity) {
        EntityDimensions dimensions = mobEntity.getDimensions(EntityPose.STANDING);
        double width = dimensions.width();
        double height = dimensions.height();
        if (width / height > 1.395D) {
            return new Box(-width / 2.0D, 0.0D, -width / 2.0D, width / 2.0D, height, width / 2.0D);
        }
        return new Box(0.0D, -width / 4.0D, -width / 2.0D, height, width * 0.75D, width / 2.0D);
    }

    private static Vec3d toLocal(MobEntity mobEntity, Vec3d pos) {
        float yaw = mobEntity.bodyYaw * MathHelper.RADIANS_PER_DEGREE;
        double cos = MathHelper.cos(yaw);
        double sin = MathHelper.sin(yaw);
        double x = pos.getX() - mobEntity.getX();
        double z = pos.getZ() - mobEntity.getZ();
        return new Vec3d(x * cos + z * sin, pos.getY() - mobEntity.getY(), -x * sin + z * cos);
    }

    private static Vec3d toWorld(MobEntity mobEntity, Vec3d localPos) {
        float yaw = mobEntity.bodyYaw * MathHelper.RADIANS_PER_DEGREE;
        double cos = MathHelper.cos(yaw);
        double sin = MathHelper.sin(yaw);
        return new Vec3d(mobEntity.getX() + localPos.getX() * cos - localPos.getZ() * sin, mobEntity.getY() + localPos.getY(), mobEntity.getZ() + localPos.getX() * sin + localPos.getZ() * cos);
    }

}
