#version 150
#include veil:fog

uniform sampler2D CloudDensitySampler;
uniform sampler2D CloudSpriteSampler;
uniform int Transparent;

uniform vec4 FogColor;
uniform float FogStart;
uniform float FogEnd;
uniform float GameTime;

in vec2 texCoord;
in vec4 vertexColor;
in float billboardRadius;
in float distance;
in float centerDistance;
in vec4 billboardRandom;

out vec4 fragColor;

void main() {
    float pixelsPerBlock = (1 / centerDistance) * 100.0;
    pixelsPerBlock = clamp(pixelsPerBlock, 3.0, 1.0);
    vec2 pixellatedTexCoord = texCoord * billboardRadius;
    // rotate it!
    float angle = billboardRandom.y * 3.141592 * 2 * 1582.4832 + GameTime * 300 * mix(0.1, 1.0, billboardRandom.w);
    pixellatedTexCoord = vec2(pixellatedTexCoord.x * cos(angle) - pixellatedTexCoord.y * sin(angle), pixellatedTexCoord.x * sin(angle) + pixellatedTexCoord.y * cos(angle));

    pixellatedTexCoord = round(pixellatedTexCoord * pixelsPerBlock) / pixelsPerBlock;
    pixellatedTexCoord /= billboardRadius;

    float rad = length(pixellatedTexCoord * billboardRadius);
    if (rad > billboardRadius) discard;
    vec4 col = vertexColor * vec4(vec3(1.0), smoothstep(billboardRadius, billboardRadius - 3.0, rad));

    col = vertexColor * texture(CloudSpriteSampler, pixellatedTexCoord * 0.5 + 0.5);

    const float transparencyThreshold = 0.9;
    if (col.a > transparencyThreshold && Transparent == 1) discard;
    else if (col.a < transparencyThreshold && Transparent != 1) discard;
    else if (col.a < 0.01) discard;

    // #veil:albedo
    vec4 albedo = col;
    fragColor = albedo;
}