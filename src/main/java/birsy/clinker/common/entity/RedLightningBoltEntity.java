package birsy.clinker.common.entity;

import birsy.clinker.client.render.world.cloud.OthershoreCloudRenderer;
import birsy.clinker.client.render.world.cloud.UpperLayerCloudRenderer;
import foundry.veil.api.client.render.light.data.PointLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RedLightningBoltEntity extends LightningBolt {
    public List<List<LightningNode>> shape = new ArrayList<>();
    public boolean clientRemoved = false;
    public LightRenderHandle<PointLightData> light;

    public RedLightningBoltEntity(EntityType<? extends LightningBolt> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if (!level().isClientSide()) return;
        if (shape == null) shape = new ArrayList<>();
        shape.clear();

        RandomSource randomSource = RandomSource.create(this.getId());

        float cloudHeight = UpperLayerCloudRenderer.UPPER_CLOUD_HEIGHT;
        float approximateSegmentHeight = 10;

        // generate the primary branch, from the bottom up
        float primaryBranchHeight = y < cloudHeight ? (float) (cloudHeight - y) : 256;
        int primaryBranchSegments = Math.round(primaryBranchHeight / approximateSegmentHeight);
        ArrayList<LightningNode> primaryBranch = new ArrayList<>(primaryBranchSegments);

        float lX = 0, lY = 0, lZ = 0;
        float primaryBranchHeightPerSegment = primaryBranchHeight / primaryBranchSegments;
        for (int i = 0; i < primaryBranchSegments; i++) {
            primaryBranch.add(
                    new LightningNode(lX, lY, lZ, i / (primaryBranchSegments - 1.0F))
            );

            lY += primaryBranchHeightPerSegment;
            lX += (float) (randomSource.nextGaussian() * 8);
            lZ += (float) (randomSource.nextGaussian() * 8);
        }
        Collections.reverse(primaryBranch);

        shape.add(primaryBranch);

        // generate the other branches from the top down
        if (primaryBranchSegments > 4) {
            int branches = randomSource.nextIntBetweenInclusive(3, 16);
            for (int i = 0; i < branches; i++) {
                ArrayList<LightningNode> branch = new ArrayList<>(primaryBranchSegments);

                // select a random node on the main branch
                float branchApproximateSegmentHeight = (float) randomSource.triangle(8, 4);
                LightningNode startingNode = primaryBranch.get(randomSource.nextInt(primaryBranchSegments - 3));
                float branchHeight = Mth.lerp(randomSource.nextFloat(), branchApproximateSegmentHeight * 2, startingNode.y);
                int branchSegments = (int) Math.ceil(branchHeight / approximateSegmentHeight);
                float branchHeightPerSegment = branchHeight / branchSegments;

                lX = startingNode.x; lY = startingNode.y; lZ = startingNode.z;
                for (int j = 0; j < branchSegments; j++) {
                    float factor = j / (float) (branchSegments - 1);

                    branch.add(
                            new LightningNode(lX, lY, lZ, (1 - factor) * startingNode.scale() * 0.5F)
                    );

                    lY -= branchHeightPerSegment;
                    lX += (float) (randomSource.nextGaussian() * 12);
                    lZ += (float) (randomSource.nextGaussian() * 12);
                }

                shape.add(branch);
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        clientRemoved = true;
        if (light != null) {
            light.close();
            light = null;
        }
    }

    @Override
    public void onClientRemoval() {
        super.onClientRemoval();
        clientRemoved = true;
        if (light != null) {
            light.close();
            light = null;
        }
    }

    // represented as an offset from entity's origin
    public record LightningNode(float x, float y, float z, float scale) {}
}
