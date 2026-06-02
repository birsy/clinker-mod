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

const float layerOffset = 10.0;
const float maximumDisplacement = 10.0;
void sampleCloud(ivec2 cloudCenterCellPos, vec2 cloudCenterCellOffset, ivec2 cameraCloudCellPos, vec2 cameraCloudCellOffset, int cloudCellSize, float renderDist, float holeSizeOffset,
                 out float brightness, out float alpha, out float baseOffset, out float displacement) {
    int textureSize = 8000;
    vec4 cloudTexture = texture(CloudDensitySampler, (mod(cloudCenterCellPos * cloudCellSize, textureSize) + cloudCenterCellOffset) / float(textureSize));
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
        float holeDist = length(localCenter - localHolePos.xy);
        float holeInfluence = holeDist / holeRadius;

        float thisHoleOffset = 1.0 - clamp((holeInfluence - 1.0) / 0.5, 0.0, 1.0);
        thisHoleOffset *= thisHoleOffset * thisHoleOffset;
        float thisHoleAlpha = smoothstep(holeRadius - float(cloudCellSize), holeRadius + float(cloudCellSize), holeDist * holeSizeOffset);

        // beacon
        if (hole.type == 2) {
            float beaconSine = sin(holeDist / 15.0 - GameTime * 1000.0) * 0.5 + 0.5;
            beaconSine *= smoothstep(8.0, 1.0, holeInfluence);
            thisHoleOffset = 1.0 - ((1.0 - thisHoleOffset) * (1.0 - (beaconSine * 0.4)));
        }

        holeAlpha = min(holeAlpha, thisHoleAlpha);
        holeOffset = max(holeOffset, thisHoleOffset);
    }

    baseOffset = layerOffset + (horizontalDistance / renderDist) * 15.0;
    displacement = (baseOffset + 5) * -holeOffset +
                   (cloudDensity * 2.0 - 1.0) * maximumDisplacement;
    brightness = cloudDensity * (1.0 - (holeOffset * 0.5));
    alpha = smoothstep(renderDist, renderDist * 0.3, horizontalDistance) * holeAlpha;
}

vec3 cloudColor(vec3 skyColor, vec3 fogColor, float cloudBrightness) {
    vec3 cloudColor = mix(skyColor, fogColor * 1.1, smoothstep(0.0, 1.0, smoothstep(0.0, 1.0, cloudBrightness)));
    cloudColor = mix(cloudColor, fogColor.rgb * 1.5, smoothstep(0.6, 1.0, cloudBrightness));
    cloudColor = mix(cloudColor, skyColor.rgb * 0.7, smoothstep(1.0, 0.0, cloudBrightness));
    return cloudColor;
}