#version 430
#veil:buffer veil:camera Camera
#include clinker:cloud_layer

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 TexCoord;

layout(std430) buffer LayerInstancePositions { ivec2 positions[]; };

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec4 SkyColor;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform vec3 DisplacementDirection;
uniform ivec2 PlayerCloudCell;
uniform vec2 PlayerCloudCellOffset;
uniform int CloudCellSize;
uniform float CloudHeight;
uniform int InstanceCount;
uniform float AlphaMultiplier;

out vec2 texCoord;
out vec4 vertexColor;
out float billboardRadius;
out float distance;
out float centerDistance;
out vec4 billboardRandom;

// https://www.shadertoy.com/view/4djSRW
vec4 hash42(vec2 p) {
    vec4 p4 = fract(vec4(p.xyxy) * vec4(.1031, .1030, .0973, .1099));
    p4 += dot(p4, p4.wzxy+33.33);
    return fract((p4.xxyz+p4.yzzw)*p4.zywx);
}

float planeDist(vec3 P, vec3 N, vec3 O) {
    return dot(N, P) - dot(N, O);
}

void main() {
    int gId = gl_InstanceID;

    ivec2 gridPos = positions[gId];
    ivec2 worldGridPos = gridPos + PlayerCloudCell;

    vec4 randoms = hash42(vec2(worldGridPos % 128));

    vec3 center = vec3(float(gridPos.x) * CloudCellSize, CloudHeight, float(gridPos.y) * CloudCellSize);

    // random xz offset
    vec3 randomOffset = vec3(randoms.x, 0.0, randoms.z) * 2.0 - 1.0;
    randomOffset = randomOffset * randomOffset * sign(randomOffset) * CloudCellSize;
    center += randomOffset;

    float brightness, alpha, baseOffset, displacement;
    sampleCloud(worldGridPos, randomOffset.xz, PlayerCloudCell, PlayerCloudCellOffset, CloudCellSize, FogEnd, 1.0,
                brightness, alpha, baseOffset, displacement);

    vec3 undisplacedCenter = center + baseOffset * DisplacementDirection;
    center = undisplacedCenter + displacement * DisplacementDirection;

    float radius = mix(7.0, 13.0, randoms.w);

    vec3 billboardOffset = TexCoord.x * radius * Camera.IViewMat[0].xyz + TexCoord.y * radius * Camera.IViewMat[1].xyz;
    vec3 worldPos = center + billboardOffset;

    vec4 viewPos = ModelViewMat * vec4(worldPos, 1.0);
    gl_Position  = ProjMat * viewPos;
    
    float offset = planeDist(undisplacedCenter, -DisplacementDirection, worldPos);
    float brightnessFromOffset = smoothstep(0.0, 1.0, offset / (maximumDisplacement + length((1.0 * radius * Camera.IViewMat[1].xyz).y)));

    float cloudBrightness = mix(brightness, brightnessFromOffset, 0.3);

    vec3 cloudColor = cloudColor(SkyColor.rgb, FogColor.rgb, cloudBrightness);
    float cloudAlpha = alpha * smoothstep(0.0, radius, -viewPos.z) * AlphaMultiplier;

    texCoord = TexCoord;
    vertexColor = vec4(cloudColor, cloudAlpha);
    billboardRadius = radius;
    distance = length(viewPos.xyz);
    centerDistance = -viewPos.z;
    billboardRandom = randoms;
}