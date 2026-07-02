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
uniform float RenderRadius;
uniform float WindOffset;
uniform int TextureSize;

const float layerOffset = 10.0;
const float maximumDisplacement = 10.0;

void sampleCloud(ivec2 cloudCenterCellPos, vec2 cloudCenterCellOffset, ivec2 cameraCloudCellPos, vec2 cameraCloudCellOffset, int cloudCellSize, float renderDist, float holeSizeOffset,
                 out float brightness, out float alpha, out float baseOffset, out float displacement) {
    int textureSize = TextureSize;
    vec4 cloudTexture = texture(CloudDensitySampler, (mod(cloudCenterCellPos * cloudCellSize, textureSize) + cloudCenterCellOffset + vec2(0, -WindOffset)) / float(textureSize));
    float cloudDensity = cloudTexture.a;

    vec2 localCenter = (cloudCenterCellPos - cameraCloudCellPos) * cloudCellSize + cloudCenterCellOffset - cameraCloudCellOffset;
    float horizontalDistance = length(localCenter);

    // cloud hole stuff
    float holeAlpha = 1.0;
    float holeOffset = 0.0;

    ivec2 cameraBlockPos = cameraCloudCellPos * cloudCellSize + ivec2(int(cameraCloudCellOffset.x), int(cameraCloudCellOffset.y));
    vec2 cameraFractPos = fract(cameraCloudCellOffset);

    for (int i = 0; i < cloudHoleCount; i++) {
        CloudHole hole = cloudHoles[i];
        float holeRadius = hole.radius;

        // lots of stuff to derrive the distance to the current billboard :P
        ivec2 holePos = ivec2(hole.x, hole.z);
        ivec2 holeCellBlockPos = holePos / 64;
        vec2 holeCellFracPos = vec2(holePos % 64) / 64.0;
        vec2 localHolePos = (holeCellBlockPos - cameraBlockPos) + (holeCellFracPos - cameraFractPos);
        vec2 relativeHolePos = (localCenter - cloudCenterCellOffset) - localHolePos.xy;
        float holeDist = length(relativeHolePos);
        float holeInfluence = holeDist / holeRadius;

        float thisHoleOffset = smoothstep(1.5, 1.0, holeInfluence);
        float thisHoleAlpha = smoothstep(holeRadius - float(cloudCellSize), holeRadius + float(cloudCellSize), holeDist * holeSizeOffset);

        // beacon
        if (hole.type == 2) {
            float beaconSine = sin(holeDist / 10.0 - GameTime * 1000.0) * 0.5 + 0.5;
            beaconSine *= smoothstep(8.0, 1.0, holeInfluence);
            thisHoleOffset = 1.0 - ((1.0 - thisHoleOffset) * (1.0 - (beaconSine * 0.4)));
        }
        // spiral
        if (hole.type == 3) {
            float angle = atan(relativeHolePos.y, relativeHolePos.x);
            float spiral = sin(angle * 5.0 + holeDist / 10.0 - GameTime * 1000.0 + cloudDensity) * 0.5 + 0.5;
            spiral *= smoothstep(8.0, 1.0, holeInfluence);
            thisHoleOffset = 1.0 - ((1.0 - thisHoleOffset) * (1.0 - spiral));
        }

        holeAlpha = min(holeAlpha, thisHoleAlpha);
        holeOffset = max(holeOffset, thisHoleOffset);
    }

    baseOffset = layerOffset + (horizontalDistance / renderDist) * 15.0;
    displacement = (baseOffset + 5) * -holeOffset +
                   (cloudDensity * 2.0 - 1.0) * maximumDisplacement;
    brightness = mix(cloudDensity, -0.5, holeOffset);
    alpha = smoothstep(RenderRadius, RenderRadius * 0.5, horizontalDistance) * holeAlpha;
}

vec3 cloudColor(vec3 skyColor, vec3 fogColor, float cloudBrightness) {
    vec3 col = fogColor * mix(0.8, 1.3, smoothstep(0.0, 1.0, cloudBrightness));
    col = mix(col, skyColor.rgb * 0.7, smoothstep(0.5, -0.4, cloudBrightness));
    return col;
}