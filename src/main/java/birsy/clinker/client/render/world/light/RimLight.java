package birsy.clinker.client.render.world.light;

import birsy.clinker.core.registry.ClinkerLightTypes;
import foundry.veil.api.client.registry.LightTypeRegistry;
import foundry.veil.api.client.render.light.PointLight;
import net.minecraft.client.Camera;
import org.joml.Vector3fc;

public class RimLight extends PointLight {
    @Override
    public LightTypeRegistry.LightType<?> getType() {
        return ClinkerLightTypes.RIM.get();
    }

    @Override
    public RimLight setColor(float red, float green, float blue) {
        return (RimLight) super.setColor(red, green, blue);
    }

    @Override
    public RimLight setColor(Vector3fc color) {
        return (RimLight) super.setColor(color);
    }

    @Override
    public RimLight setBrightness(float brightness) {
        return (RimLight) super.setBrightness(brightness);
    }

    @Override
    public RimLight setPosition(double x, double y, double z) {
        return (RimLight) super.setPosition(x, y, z);
    }

    @Override
    public RimLight setRadius(float radius) {
        return (RimLight) super.setRadius(radius);
    }

    @Override
    public RimLight setTo(Camera camera) {
        return (RimLight) super.setTo(camera);
    }

    @Override
    public RimLight clone() {
        return (RimLight) new RimLight()
                .setPosition(this.position)
                .setColor(this.color)
                .setRadius(this.radius)
                .setBrightness(this.brightness);
    }
}
