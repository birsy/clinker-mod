package birsy.clinker.client.render.world.light;

import birsy.clinker.core.registry.ClinkerLightTypes;
import foundry.veil.api.client.registry.LightTypeRegistry;
import foundry.veil.api.client.render.light.PointLight;
import net.minecraft.client.Camera;
import org.joml.Vector3fc;

public class GreaseLight extends PointLight {
    @Override
    public LightTypeRegistry.LightType<?> getType() {
        return ClinkerLightTypes.GREASE.get();
    }

    @Override
    public GreaseLight setColor(float red, float green, float blue) {
        return (GreaseLight) super.setColor(red, green, blue);
    }

    @Override
    public GreaseLight setColor(Vector3fc color) {
        return (GreaseLight) super.setColor(color);
    }

    @Override
    public GreaseLight setBrightness(float brightness) {
        return (GreaseLight) super.setBrightness(brightness);
    }

    @Override
    public GreaseLight setPosition(double x, double y, double z) {
        return (GreaseLight) super.setPosition(x, y, z);
    }

    @Override
    public GreaseLight setRadius(float radius) {
        return (GreaseLight) super.setRadius(radius);
    }

    @Override
    public GreaseLight setTo(Camera camera) {
        return (GreaseLight) super.setTo(camera);
    }

    @Override
    public GreaseLight clone() {
        return (GreaseLight) new GreaseLight()
                .setPosition(this.position)
                .setColor(this.color)
                .setRadius(this.radius)
                .setBrightness(this.brightness);
    }
}
