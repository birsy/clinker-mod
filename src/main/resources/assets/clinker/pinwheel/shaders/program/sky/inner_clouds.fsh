#version 430
#veil:buffer veil:camera Camera
#include clinker:cloud_layer
#include clinker:dither

uniform vec4 SkyColor;
uniform vec4 FogColor;
uniform float FogEnd;
uniform vec2 CloudHeight;
uniform vec2 CloudScale;

in vec3 pos;
in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec3 worldPos = pos + vec3(Camera.CameraPosition.x, 0, Camera.CameraPosition.z);
    float brightness, alpha, baseOffset, displacement;
    sampleCloud(ivec2(0), worldPos.xz, ivec2(0), vec2(0.0), 1, FogEnd, 0.0, brightness, alpha, baseOffset, displacement);

    float range = 50.0;
    float density = texture(CloudDensitySampler, texCoord * vec2(10.0, 1.0) * 0.1 + vertexColor.g * 100.0).a;
    float cloudHeight = 250.0 + displacement;
    cloudHeight = mix(cloudHeight, CloudHeight.x + range, vertexColor.g * vertexColor.g);
    float maxY = cloudHeight + range;
    float minY = cloudHeight - range;

    if (worldPos.y < minY) discard;
    vec3 cloudColor = cloudColor(SkyColor.rgb * 1.2, FogColor.rgb * 0.9, brightness + (density * 2.0 - 1.0) * 0.1);
    cloudColor = mix(cloudColor, SkyColor.rgb * 0.8, smoothstep(0.0, 1.0, vertexColor.g));
    float cloudAlpha = smoothstep(minY, maxY, worldPos.y);
    float additionFactor = 1.0 - (abs(cloudAlpha - 0.5) * 2.0);
    cloudAlpha += (density * 2.0 - 1.0) * 3.0 * additionFactor;

    cloudAlpha = dither(int(gl_FragCoord.x), int(gl_FragCoord.y), cloudAlpha);
    if (cloudAlpha < 0.01) discard;
    fragColor = vec4(cloudColor, cloudAlpha);
}