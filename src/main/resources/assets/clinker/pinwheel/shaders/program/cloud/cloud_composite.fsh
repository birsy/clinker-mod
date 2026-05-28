#include veil:deferred_utils

uniform sampler2D CloudsSampler;
uniform sampler2D CloudsResolutionDepthSampler;
uniform sampler2D MainSampler;
uniform sampler2D MainDepthSampler;
uniform vec2 ScreenSize;

in vec2 texCoord;

out vec4 fragColor;

const float ScalingFactor = 0.5;
const int KernelRadius = 1;

float depthSampleToWorldDepth(float depthSample) {
    float f = depthSample * 2.0 - 1.0;
    return 2.0 * VeilCamera.NearPlane * VeilCamera.FarPlane / (VeilCamera.FarPlane + VeilCamera.NearPlane - f * (VeilCamera.FarPlane - VeilCamera.NearPlane));
}

void main() {
    float sceneDepth = depthSampleToWorldDepth(texture(MainDepthSampler, texCoord).r);

    vec2 volumeTexelSize = (1.0 / ScreenSize) / ScalingFactor;
    float minimumDepthDifference = 1e9;
    vec2 minimumDepthDifferenceCoordinates = vec2(0.0);
    for (int x = -KernelRadius; x <= KernelRadius; ++x) {
        for (int y = -KernelRadius; y <= KernelRadius; ++y) {
            vec2 coordinates = vec2(x, y) * volumeTexelSize + texCoord;

            float depth = depthSampleToWorldDepth(texture(CloudsResolutionDepthSampler, coordinates).r);
            float depthDifference = abs(depth - sceneDepth);

            if (depthDifference < minimumDepthDifference) {
                minimumDepthDifference = depthDifference;
                minimumDepthDifferenceCoordinates = coordinates;
            }
        }
    }
    if (minimumDepthDifference > VeilCamera.FarPlane * 0.9 || minimumDepthDifference < 0.5) minimumDepthDifferenceCoordinates = texCoord;

    vec4 mainColor = texture(MainSampler, texCoord);
    vec4 cloudColor = texture(CloudsSampler, minimumDepthDifferenceCoordinates);
    vec3 color = mix(sqrt(mainColor.rgb), sqrt(cloudColor.rgb), cloudColor.a);
    color = color * color;
    fragColor = vec4(color, 1.0);
}
