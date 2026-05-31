#version 430
#veil:buffer veil:camera Camera

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 TexCoord;

layout(std430) buffer LayerInstancePositions { ivec2 positions[]; };

struct CloudHole {
    int type;
    int x, z; // fixed point, divide by 64 to get world pos
    float radius;
};
layout(std430) buffer CloudHoles {
    int cloudHoleCount;
    CloudHole cloudHoles[];
};

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
uniform vec2 PlayerCloudCellOffset;
uniform int CloudCellSize;
uniform float CloudHeight;
uniform int InstanceCount;

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
    int gId = gl_InstanceID;

    ivec2 gridPos = positions[gId];
    ivec2 worldGridPos = gridPos + PlayerCloudCell;

    vec4 randoms = hash42(vec2(worldGridPos % 128));

    vec3 center = vec3(float(gridPos.x) * CloudCellSize, 0.0, float(gridPos.y) * CloudCellSize);

    // random xz offset
    vec3 randomOffset = vec3(randoms.x, 0.0, randoms.z) * 2.0 - 1.0;
    randomOffset = randomOffset * randomOffset * sign(randomOffset) * CloudCellSize;
    center += randomOffset;

    // vertical cloud displacement
    int textureSize = 5000;
    vec4 cloudTexture = texture(CloudDensitySampler, (mod(worldGridPos * CloudCellSize, textureSize) + randomOffset.xz) / float(textureSize));
    float cloudDensity = cloudTexture.a;
    const float maxDisplacement = 10.0;
    vec3 displacement = DisplacementDirection * maxDisplacement * (cloudDensity * 2.0 - 1.0);
    center += vec3(0.0, CloudHeight, 0.0) + displacement;

    // vertical displacement near the edges
    float horizontalDistance = length(center.xz - PlayerCloudCellOffset);
    center += clamp(horizontalDistance / FogEnd, 0.0, 1.0) * 10.0 * DisplacementDirection;

    // cloud hole stuff
    float holeAlpha = 1.0;
    float holeOffset = 0.0;
    ivec2 cameraBlockPos = PlayerCloudCell * CloudCellSize + ivec2(int(PlayerCloudCellOffset.x), int(PlayerCloudCellOffset.y));
    vec2 cameraFractPos = fract(PlayerCloudCellOffset);
    for (int i = 0; i < cloudHoleCount; i++) {
        CloudHole hole = cloudHoles[i];
        float holeRadius = hole.radius;
        // lots of stuff to derrive the distance to the current billboard :P
        ivec2 holePos = ivec2(hole.x, hole.z);
        ivec2 holeCellBlockPos = holePos / 64;
        vec2 holeCellFracPos = vec2(holePos % 64) / 64.0;
        vec2 localHolePos = (holeCellBlockPos - cameraBlockPos) + (holeCellFracPos - cameraFractPos);
        float holeDist = length((center.xz - PlayerCloudCellOffset) - localHolePos.xy);
        float holeInfluence = holeDist / holeRadius;

        float thisHoleOffset = smoothstep(2.0, 1.0, holeInfluence);
        float thisHoleAlpha = smoothstep(holeRadius - CloudCellSize, holeRadius + CloudCellSize, holeDist);

        // beacon
        if (hole.type == 2) {
            float beaconSine = sin(holeDist * 0.2 - GameTime * 1000.0) * 0.5 + 0.5;
            beaconSine *= smoothstep(8.0, 1.0, holeInfluence);
            thisHoleOffset = max(thisHoleOffset, beaconSine * 0.5);
        }

        holeAlpha = min(holeAlpha, thisHoleAlpha);
        holeOffset = max(holeOffset, thisHoleOffset);
    }
    center += -holeOffset * DisplacementDirection * 16.0;
    cloudDensity = cloudDensity * (1.0 - holeOffset * 0.5);

    float radius = mix(7.0, 13.0, randoms.w);

    vec3 worldPos = center +
    TexCoord.x * radius * Camera.IViewMat[0].xyz +
    TexCoord.y * radius * Camera.IViewMat[1].xyz;

    vec4 viewPos = ModelViewMat * vec4(worldPos, 1.0);
    gl_Position  = ProjMat * viewPos;

    float dist = length(viewPos.xyz);

    float yBrightness = smoothstep(CloudHeight - (radius + maxDisplacement) * DisplacementDirection.y, CloudHeight + (radius + maxDisplacement) * DisplacementDirection.y, worldPos.y);
    float cloudBrightness = mix(cloudDensity, yBrightness, 0.5);
    vec3 cloudColor = mix(SkyColor, FogColor * 1.1, smoothstep(0.0, 1.0, smoothstep(0.0, 1.0, cloudBrightness))).rgb;
    cloudColor = mix(cloudColor, FogColor.rgb * 1.5, smoothstep(0.8, 1.0, cloudBrightness));
    cloudColor = mix(cloudColor, SkyColor.rgb * 0.5, smoothstep(1.0, 0.0, cloudBrightness));
    float cloudAlpha = smoothstep(0.0, radius, -viewPos.z) *
                       smoothstep(FogEnd, FogEnd * 0.3, horizontalDistance) *
                       smoothstep(FogEnd, FogEnd * 0.3, horizontalDistance) *
                       holeAlpha;

    texCoord = TexCoord;
    vertexColor = vec4(cloudColor, cloudAlpha);
    billboardRadius = radius;
    distance = dist;
    centerDistance = -viewPos.z;
    billboardRandom = randoms;
}