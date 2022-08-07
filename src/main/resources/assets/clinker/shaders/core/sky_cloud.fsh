#version 150

#moj_import <noise.glsl>

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GameTime;
uniform float FogStart; //maximum UV
uniform float FogEnd; //ring distance

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

float map(float value, float min1, float max1, float min2, float max2) {
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}

void main() {
    float maxUV = FogStart;
    vec2 uv = texCoord0;
    float resolution = 200.0;

    float speed = -22.0 * (1.0 - FogEnd);
    vec2 uv1 = uv + vec2((GameTime * speed), 0);
    uv1 = ceil(uv1 * resolution) / resolution;
    vec2 uv2 = uv + vec2(maxUV, 0) + vec2((GameTime * speed), 0);
    uv2 = ceil(uv2 * resolution) / resolution;

    uv = ceil(uv * resolution) / resolution;
    float uvMix = map(1 - (uv.x / maxUV), 0.95, 1.0, 0.0, 1.0);

    float g1 = (1.0 - (uv.y * 2.0)) * 1.0;
    float g2 = 1.0 - ((1.0 - uv.y) * 2.0);
    float g3 = min(map(uv.y, 0.85, 1.0, 1.0, 0.0), 1.0);
    float gradient = min(g1, g2) * g3;
    gradient *= gradient;

    float noiseStrength = 0.2;
    float noise0 = abs(noise(vec3(uv1 * 6.0F, (FogEnd * 10.0F) + (GameTime * 64.0F))));
    float noise1 = noise0;
    if (uvMix > 0) noise1 = abs(noise(vec3(uv2 * 6.0F, (FogEnd * 10.0F) + (GameTime * 64.0F))));
    float noise = mix(noise0, noise1, uvMix);

    float value = (gradient * (1.0 - noiseStrength)) + (noise * noiseStrength);

    float a1 = 1 - vertexColor.a;
    float a2 = a1 * 0.95;
    float a3 = a1 * 0.9;
    float a = (value > a1 ? 1.0 : value > a2 ? 0.5 : value > a3 ? 0.25 : 0.0);

    //vec4 color = vec4(gradient, gradient, gradient, 1.0);
    vec4 color = vec4(1.0, 1.0, 1.0, g3 > 0.0 ? a : 0.0) * vec4(vertexColor.rgb, 1.0);
    fragColor = color * ColorModulator;
}