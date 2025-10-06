package birsy.clinker.client.render.world.light;

import birsy.clinker.core.registry.ClinkerLightTypes;
import foundry.veil.api.client.registry.LightTypeRegistry;
import foundry.veil.api.client.render.light.data.PointLightData;
import net.minecraft.client.Camera;
import org.joml.Vector3fc;

public class GreaseLightData extends PointLightData {
    @Override
    public LightTypeRegistry.LightType<?> getType() {
        return ClinkerLightTypes.GREASE.get();
    }

    @Override
    public GreaseLightData setColor(float red, float green, float blue) {
        return (GreaseLightData) super.setColor(red, green, blue);
    }

    @Override
    public GreaseLightData setColor(Vector3fc color) {
        return (GreaseLightData) super.setColor(color);
    }

    @Override
    public GreaseLightData setBrightness(float brightness) {
        return (GreaseLightData) super.setBrightness(brightness);
    }

    @Override
    public GreaseLightData setPosition(double x, double y, double z) {
        return (GreaseLightData) super.setPosition(x, y, z);
    }

    @Override
    public GreaseLightData setRadius(float radius) {
        return (GreaseLightData) super.setRadius(radius);
    }

    @Override
    public GreaseLightData setTo(Camera camera) {
        return (GreaseLightData) super.setTo(camera);
    }

    @Override
    public GreaseLightData clone() {
        return (GreaseLightData) new GreaseLightData()
                .setPosition(this.position)
                .setColor(this.color)
                .setRadius(this.radius)
                .setBrightness(this.brightness);
    }
}
