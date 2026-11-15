package net.rpgz.util;

import java.util.Optional;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CorpseBox {

    public static boolean isCorpse(Entity entity) {
        return entity instanceof Mob mobEntity && mobEntity.isDeadOrDying() && mobEntity.deathTime >= 20;
    }

    public static AABB getBoundingBox(Mob mobEntity) {
        AABB localBox = getLocalBox(mobEntity);
        double minX = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        for (double x : new double[] { localBox.minX, localBox.maxX }) {
            for (double z : new double[] { localBox.minZ, localBox.maxZ }) {
                Vec3 corner = toWorld(mobEntity, new Vec3(x, 0.0D, z));
                minX = Math.min(minX, corner.x());
                minZ = Math.min(minZ, corner.z());
                maxX = Math.max(maxX, corner.x());
                maxZ = Math.max(maxZ, corner.z());
            }
        }
        return new AABB(minX, mobEntity.getY() + localBox.minY, minZ, maxX, mobEntity.getY() + localBox.maxY, maxZ);
    }

    public static Optional<Vec3> raycast(Mob mobEntity, Vec3 start, Vec3 end) {
        return getLocalBox(mobEntity).clip(toLocal(mobEntity, start), toLocal(mobEntity, end)).map(localHit -> toWorld(mobEntity, localHit));
    }

    private static AABB getLocalBox(Mob mobEntity) {
        EntityDimensions dimensions = mobEntity.getDimensions(Pose.STANDING);
        double width = dimensions.width();
        double height = dimensions.height();
        if (width / height > 1.395D) {
            return new AABB(-width / 2.0D, 0.0D, -width / 2.0D, width / 2.0D, height, width / 2.0D);
        }
        return new AABB(0.0D, -width / 4.0D, -width / 2.0D, height, width * 0.75D, width / 2.0D);
    }

    private static Vec3 toLocal(Mob mobEntity, Vec3 pos) {
        float yaw = mobEntity.yBodyRot * Mth.DEG_TO_RAD;
        double cos = Mth.cos(yaw);
        double sin = Mth.sin(yaw);
        double x = pos.x() - mobEntity.getX();
        double z = pos.z() - mobEntity.getZ();
        return new Vec3(x * cos + z * sin, pos.y() - mobEntity.getY(), -x * sin + z * cos);
    }

    private static Vec3 toWorld(Mob mobEntity, Vec3 localPos) {
        float yaw = mobEntity.yBodyRot * Mth.DEG_TO_RAD;
        double cos = Mth.cos(yaw);
        double sin = Mth.sin(yaw);
        return new Vec3(mobEntity.getX() + localPos.x() * cos - localPos.z() * sin, mobEntity.getY() + localPos.y(), mobEntity.getZ() + localPos.x() * sin + localPos.z() * cos);
    }

}
