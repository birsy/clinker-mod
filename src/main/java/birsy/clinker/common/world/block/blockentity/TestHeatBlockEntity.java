package birsy.clinker.common.world.block.blockentity;

import birsy.clinker.common.world.level.heatnetwork.HeatConnection;
import birsy.clinker.common.world.level.heatnetwork.HeatNode;
import birsy.clinker.common.world.level.heatnetwork.HeatSocket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class TestHeatBlockEntity extends BlockEntity {
    public static HeatSocket socketA, socketB;

    public final HeatNode heatNode;

    public TestHeatBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, Function<TestHeatBlockEntity, HeatNode> node) {
        super(pType, pPos, pBlockState);
        heatNode = node.apply(this);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TestHeatBlockEntity entity) {
        if (entity.heatNode instanceof HeatNode.Updatable updatable) updatable.update();
    }

    public void onBreak() {
        for (HeatSocket socket : this.heatNode.sockets) {
            if (socket.connected()) {
                boolean isA = socket.connection().a == socket;
                socket.connection().sever(!isA, isA);
            }
        }
    }

    public HeatSocket getSocketForDirection(Direction direction) {
        return heatNode.sockets[direction.ordinal()];
    }

    public void rightClick(Direction face) {
        if (socketA == null) {
            socketA = this.getSocketForDirection(face);
        } else if (socketB == null && socketA.node != this.heatNode) {
            socketB = this.getSocketForDirection(face);
        } else if (socketB != null) {
            HeatConnection newConnection = new HeatConnection(socketA, socketB, 1.0F, 1.0F);
            socketA = null;
            socketB = null;
        }
    }
}
