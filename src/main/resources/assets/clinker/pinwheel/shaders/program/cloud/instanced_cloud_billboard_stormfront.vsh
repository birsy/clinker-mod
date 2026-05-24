#version 430
#veil:buffer veil:camera Camera

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 TexCoord;

struct CloudPosition { vec3 position; float padding0; vec3 normal; float padding1; };
layout(std430) buffer StormFrontInstancePositions {
    CloudPosition positions[];
};

uniform sampler2D CloudDensitySampler;
uniform float GameTime;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec4 SkyColor;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform int PlayerCloudCell;
uniform float PlayerCloudCellOffset;
uniform int CloudCellSize;
uniform int InstanceCount;
uniform int Transparent;
uniform float DistanceToCamera;
uniform float Fade;

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
    // if (Transparent) { gId = InstanceCount - gl_InstanceID - 1; } else { gId = gl_InstanceID; }
    int gId = gl_InstanceID * (1 - Transparent) + (InstanceCount - gl_InstanceID - 1) * Transparent;
    CloudPosition cloudPos = positions[gId];
    vec3 center = cloudPos.position;
    vec3 normal = -cloudPos.normal;

    vec3 worldPosCenter = center + vec3(PlayerCloudCell * CloudCellSize, 0.0, 0.0);

    float maxAllowedZOffset = smoothstep(20.0, 80.0, length((center + vec3(0.0, 0.0, -DistanceToCamera)).xz));

    vec4 randoms = hash42(vec2(mod(worldPosCenter.x, 128.0), worldPosCenter.y));
    vec3 randomOffset = vec3(randoms.x, randoms.y, randoms.z) * 2.0 - 1.0;
    randomOffset = randomOffset * randomOffset * sign(randomOffset) * CloudCellSize;
    center += randomOffset * maxAllowedZOffset;

    float sineOffset = sin(worldPosCenter.x * 0.04 + GameTime * 500.0);
    center += normal * sineOffset * 5.0 * maxAllowedZOffset;

    vec3 unOffsetCenter = center;

    int textureSize = 5000;
    vec4 cloudTexture = texture(CloudDensitySampler, (worldPosCenter.xy + GameTime * 5000.0 * vec2(1.0, -1.0)) / float(textureSize));
    float cloudDensity = cloudTexture.a;
    const float displacementIntensity = 30.0;
    vec3 displacementOffset = cloudDensity * normal * displacementIntensity;
    center += displacementOffset * maxAllowedZOffset;

    float radius = mix(7.0, 13.0, randoms.w);
    vec3 worldPos = center + TexCoord.x * radius * Camera.IViewMat[0].xyz + TexCoord.y * radius * Camera.IViewMat[1].xyz;
    vec4 viewPos = ModelViewMat * vec4(worldPos, 1.0);
    gl_Position  = ProjMat * viewPos;

    float offset = planeDist(unOffsetCenter, -normal, worldPos);
    float brightnessFromOffset = offset / (radius + displacementIntensity);

    float dist = length(viewPos.xyz);
    float horizontalDist = length((center - vec3(PlayerCloudCellOffset, 0, 0)).xz);

    float depthDarkness = mix(1.0, 0.3, smoothstep(120.0, 30.0, center.y));
    depthDarkness = mix(depthDarkness, 1.0, smoothstep(FogStart, FogEnd, horizontalDist));

    float cloudBrightness = mix(cloudDensity, brightnessFromOffset, 0.5);
    vec3 cloudColor = mix(SkyColor * depthDarkness, FogColor * 1.1, smoothstep(0.0, 1.0, smoothstep(0.0, 1.0, cloudBrightness))).rgb;
    cloudColor = mix(cloudColor, FogColor.rgb * 1.5, smoothstep(0.8, 1.0, cloudBrightness));
    cloudColor = mix(cloudColor, SkyColor.rgb * 0.5 * depthDarkness, smoothstep(1.0, 0.0, cloudBrightness));

    float cloudAlpha = smoothstep(0.0, radius, -viewPos.z) * smoothstep(FogEnd, FogEnd * 0.3, horizontalDist) * smoothstep(FogEnd, FogEnd * 0.3, horizontalDist) * Fade;

    texCoord = TexCoord;
    vertexColor = vec4(cloudColor, cloudAlpha);
    billboardRadius = radius;
    distance = dist;
    centerDistance = -viewPos.z;
    billboardRandom = randoms;
}