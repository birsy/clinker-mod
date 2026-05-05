#version 430
#veil:buffer veil:camera Camera

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 TexCoord;

layout(std430) buffer InstancePositions { ivec2 positions[]; };

uniform sampler2D CloudDensitySampler;
uniform float GameTime;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec4 SkyColor;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform vec3 DisplacementDirection;
uniform ivec2 PlayerCloudCell;
uniform int CloudCellSize;
uniform float CloudHeight;
uniform int InstanceCount;
uniform int Transparent;

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
void main() {
    // if (Transparent) { gId = InstanceCount - gl_InstanceID - 1; } else { gId = gl_InstanceID; }
    int gId = gl_InstanceID * (1 - Transparent) + (InstanceCount - gl_InstanceID - 1) * Transparent;

    ivec2 gridPos = positions[gId];
    ivec2 worldGridPos = gridPos + PlayerCloudCell;

    vec4 randoms = hash42(vec2(worldGridPos % 128));

    vec3 center = vec3(float(gridPos.x) * CloudCellSize, 0, float(gridPos.y) * CloudCellSize);

    vec3 randomOffset = vec3(randoms.x, 0, randoms.z) * 2 - 1;
    randomOffset = randomOffset * randomOffset * sign(randomOffset) * CloudCellSize;
    center += randomOffset;

    int textureSize = 5000;
    float cloudDensity = texture(CloudDensitySampler,
    (mod(worldGridPos * CloudCellSize, textureSize) + randomOffset.xz) / float(textureSize)).r;

    const float maxDisplacement = 10;
    vec3 displacement = DisplacementDirection * maxDisplacement * (cloudDensity * 2 - 1);
    center += vec3(0, CloudHeight, 0) + displacement;

    float radius = mix(7, 13, randoms.w);
    vec3 worldPos = center +
    TexCoord.x * radius * Camera.IViewMat[0].xyz +
    TexCoord.y * radius * Camera.IViewMat[1].xyz;

    vec4 viewPos = ModelViewMat * vec4(worldPos, 1.0);
    gl_Position  = ProjMat * viewPos;

    float dist = length(viewPos.xyz);

    float yBrightness = smoothstep(CloudHeight - (radius + maxDisplacement) * DisplacementDirection.y, CloudHeight + (radius + maxDisplacement) * DisplacementDirection.y, worldPos.y);
    float cloudBrightness = mix(cloudDensity, yBrightness, 0.5);
    vec3 cloudColor = mix(SkyColor, FogColor * 1.1, smoothstep(0, 1, smoothstep(0, 1, cloudBrightness))).rgb;
    cloudColor = mix(cloudColor, FogColor.rgb * 1.5, smoothstep(0.8, 1, cloudBrightness));
    cloudColor = mix(cloudColor, SkyColor.rgb * 0.5, smoothstep(1.0, 0, cloudBrightness));

    texCoord = TexCoord;
    vertexColor = vec4(
        cloudColor,
        smoothstep(0, radius, -viewPos.z) * smoothstep(FogEnd, FogEnd * 0.3, dist) * smoothstep(FogEnd, FogEnd * 0.3, dist)
    );
    billboardRadius = radius;
    distance = dist;
    centerDistance = -viewPos.z;
    billboardRandom = randoms;
}