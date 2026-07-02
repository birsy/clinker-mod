#version 150
#include clinker:dither

uniform sampler2D AtmosphereSampler;
uniform sampler2D NoiseSampler;

uniform vec4 SkyColor;
uniform vec4 SkyFogColor;
uniform float GameTime;

in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    const float resolution = 200.0;
    ivec2 pixel = ivec2(floor(texCoord * resolution));
    vec2 pixelUV = vec2(pixel) / resolution;

    vec2 transformedUV = pixelUV * 2.0 - 1.0;
    transformedUV *= 0.5;
    float angle = GameTime * 100.0;
    float cosA = cos(angle), sinA = sin(angle);
    mat2 rotationMat = mat2(
        cosA, sinA,
        -sinA, cosA
    );
    transformedUV = rotationMat * transformedUV;
    transformedUV = transformedUV * 0.5 + 0.5;

    vec2 wind = vec2(1.0, 0.1) * GameTime * 2.0;
    vec4 noise = texture(NoiseSampler, pixelUV * 0.5 + wind);
    float nebulae = texture(AtmosphereSampler, transformedUV + noise.b * 0.02).a;
    nebulae = quantizeAndDither(pixel.x, pixel.y, 16, nebulae);

    float atmosphereAlpha = (1.0 - exp(-vertexColor.r * 10.0)) * smoothstep(0.0, 0.05, vertexColor.r);
    float brightness = 1.0 - vertexColor.r;
    vec4 baseSkyColor = mix(SkyFogColor, SkyColor, brightness);
    fragColor = mix(baseSkyColor, SkyFogColor, nebulae) * vec4(vec3(brightness), atmosphereAlpha);
}