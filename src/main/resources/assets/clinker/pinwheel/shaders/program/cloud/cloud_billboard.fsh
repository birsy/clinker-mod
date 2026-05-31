#version 150
#include veil:fog
#include veil:space_helper
#include clinker:dither

uniform sampler2D CloudDensitySampler;
uniform sampler2D CloudSpriteSampler;
uniform sampler2D MainDepthSampler;

uniform vec4 FogColor;
uniform float FogStart;
uniform float FogEnd;
uniform float GameTime;
uniform vec2 ScreenSize;

in vec2 texCoord;
in vec4 vertexColor;
in float billboardRadius;
in float distance;
in float centerDistance;
in vec4 billboardRandom;

out vec4 fragColor;

void main() {
    vec2 screenUv = gl_FragCoord.xy / (ScreenSize * 0.5);
    float sceneDepth = texture(MainDepthSampler, screenUv).r;
    vec4 scenePos = screenToViewSpace(screenUv, sceneDepth);
    float sceneDist = length(scenePos.xyz);

    vec2 pixellatedTexCoord = texCoord * billboardRadius;
    // rotate it!
    float angle = billboardRandom.y * 3.141592 * 2 * 1582.4832 + GameTime * 300 * mix(0.1, 1.0, billboardRandom.w);
    pixellatedTexCoord = vec2(pixellatedTexCoord.x * cos(angle) - pixellatedTexCoord.y * sin(angle), pixellatedTexCoord.x * sin(angle) + pixellatedTexCoord.y * cos(angle));

    float pixelsPerBlock = (1 / centerDistance) * 100.0;
    pixelsPerBlock = clamp(pixelsPerBlock, 1.0, 3.0);
    pixellatedTexCoord = round(pixellatedTexCoord * pixelsPerBlock) / pixelsPerBlock;
    pixellatedTexCoord /= billboardRadius;

    float rad = length(pixellatedTexCoord * billboardRadius);
    vec4 col = vertexColor;
    float spriteAlpha = texture(CloudSpriteSampler, pixellatedTexCoord * 0.5 + 0.5).a;
    col *= vec4(1.0, 1.0, 1.0, spriteAlpha);
    col.a = dither(int(gl_FragCoord.x + billboardRandom.x), int(gl_FragCoord.y), col.a);

    if (col.a < 0.01) discard;

    fragColor = col;
}