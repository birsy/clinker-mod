#version 430
#veil:buffer veil:camera Camera
#include clinker:cloud_layer

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 TexCoord;
layout(location = 2) in vec4 Color;

layout(std430) buffer CloudChunkPositions {
    ivec2 cloudChunkPositions[];
};

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec4 SkyColor;

uniform vec3 DisplacementDirection;
uniform ivec2 CameraChunk;
uniform vec2 CameraChunkOffset;
uniform int ChunkSize;
uniform int CellSize;
uniform float CloudHeight;
uniform float AlphaMultiplier;

uniform int LODLevel;
uniform vec2 LODThreshold;

out vec2 texCoord;
out vec4 vertexColor;
out float billboardRadius;
out float distance;
out float centerDistance;
out vec4 billboardRandom;

float map(float value, float min1, float max1, float min2, float max2) {
    return clamp(min2 + (value - min1) * (max2 - min2) / (max1 - min1), min2, max2);
}
vec4 hash42(vec2 p) {
    vec4 p4 = fract(vec4(p.xyxy) * vec4(.1031, .1030, .0973, .1099));
    p4 += dot(p4, p4.wzxy+33.33);
    return fract((p4.xxyz+p4.yzzw)*p4.zywx);
}
float planeDist(vec3 P, vec3 N, vec3 O) {
    return dot(N, P) - dot(N, O);
}

void main() {
    float absoluteChunkSize = ChunkSize * CellSize;
    ivec2 chunkPos = cloudChunkPositions[gl_InstanceID];
    vec2 chunkOffset = vec2(float(chunkPos.x), float(chunkPos.y)) * absoluteChunkSize;

    vec3 chunkSpaceCenter = Position;
    ivec2 cellPos = ivec2(int(floor(chunkSpaceCenter.x)), int(floor(chunkSpaceCenter.z))) / CellSize + (chunkPos + CameraChunk) * ChunkSize;
    vec3 cameraSpaceCenter = chunkSpaceCenter + vec3(chunkOffset.x, CloudHeight, chunkOffset.y);

    vec4 randoms = hash42(vec2(cellPos % 128));

    vec3 randomOffset = vec3(randoms.x, 0.0, randoms.z) * 2.0 - 1.0;
    randomOffset = randomOffset * randomOffset * sign(randomOffset) * CellSize;
    cameraSpaceCenter += randomOffset;

    ivec2 cameraChunkCell = ivec2(floor(CameraChunkOffset.xy / CellSize));
    ivec2 cameraCell = CameraChunk * ChunkSize + cameraChunkCell;
    vec2 cameraCellOffset = CameraChunkOffset - vec2(cameraChunkCell) * CellSize;
    float brightness, alpha, baseOffset, displacement;
    sampleCloud(cellPos, randomOffset.xz, cameraCell, cameraCellOffset, CellSize, FogEnd, 1.0, brightness, alpha, baseOffset, displacement);

    cameraSpaceCenter += baseOffset * DisplacementDirection;
    vec3 undisplacedCenter = cameraSpaceCenter;
    cameraSpaceCenter += displacement * DisplacementDirection;

    float horizontalDistance = length(cameraSpaceCenter.xz - CameraChunkOffset);

    // radius is scaled by LOD factor, near the edges
    // such that the LODs transition smoothly into each other!
    float lodStartFactor = map(horizontalDistance, LODThreshold.x - absoluteChunkSize, LODThreshold.x, 0.0, 1.0);
    float lodEndFactor = map(horizontalDistance, LODThreshold.y - absoluteChunkSize - CellSize, LODThreshold.y, 0.0, 1.0);
    float prevLodScale = float(1 << max(LODLevel - 1, 0));
    float lodScale = float(1 << (LODLevel));
    float nextLodScale = float(1 << (LODLevel + 1));
    float scaleMultiplier = mix(
        mix(mix(prevLodScale, lodScale, lodStartFactor), 0.0, lodEndFactor),
        mix(lodScale, nextLodScale, lodEndFactor), Color.r
    );
    float radius = mix(8.0, 13.0, randoms.w) * 0.8 * scaleMultiplier;

    vec3 billboardOffset = TexCoord.x * radius * Camera.IViewMat[0].xyz + TexCoord.y * radius * Camera.IViewMat[1].xyz;
    vec3 vertexPos = cameraSpaceCenter + billboardOffset;
    vec4 viewPos = ModelViewMat * vec4(vertexPos, 1.0);
    gl_Position  = ProjMat * viewPos;

    float offset = planeDist(undisplacedCenter, -DisplacementDirection, cameraSpaceCenter);
    float brightnessFromOffset = smoothstep(0.0, 1.0, offset / (maximumDisplacement + length((1.0 * radius * Camera.IViewMat[1].xyz).y)));
    float cloudBrightness = mix(brightness, brightnessFromOffset, 0.5);
    vec3 cloudColor = cloudColor(SkyColor.rgb, FogColor.rgb, cloudBrightness);
    float cloudAlpha = alpha * smoothstep(0.0, radius, -viewPos.z) * AlphaMultiplier;

    texCoord = TexCoord;
    vertexColor = vec4(cloudColor, cloudAlpha);
    billboardRadius = radius;
    distance = length(viewPos.xyz);
    centerDistance = -viewPos.z;
    billboardRandom = randoms;
}