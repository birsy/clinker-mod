package birsy.clinker.common.page;

import com.mojang.serialization.MapCodec;

public interface PageElementType<T extends PageElement> {
    MapCodec<T> codec();
}
