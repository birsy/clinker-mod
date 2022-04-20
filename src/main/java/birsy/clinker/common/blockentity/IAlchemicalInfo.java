package birsy.clinker.common.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public interface IAlchemicalInfo {
    List<? extends FormattedText> getText(BlockEntity blockEntity);
    default void renderCustomElements(BlockEntity blockEntity, PoseStack mStack, int x, int y, int width, int height) {}
}
