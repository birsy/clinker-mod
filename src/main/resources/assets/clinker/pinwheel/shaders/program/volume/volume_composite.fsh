#include veil:fog
#include veil:deferred_utils

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;

uniform sampler2D VolumetricSampler;
uniform sampler2D VolumetricDepthSampler;

uniform vec2 OutSize;

in vec2 texCoord;

out vec4 fragColor;

const float ScalingFactor = 0.25;
const int KernelRadius = 1;

void main() {
    vec4 sceneColor = texture(DiffuseSampler0, texCoord);
    float sceneDepth = depthSampleToWorldDepth(texture(DiffuseDepthSampler, texCoord).r);

    // horrible horrible bilateral filtering
    vec2 volumeTexelSize = (1 / OutSize) / ScalingFactor;
	float minimumDepthDifference = 10000000.0;
	vec2 minOffsetCoordinates = vec2(0);
	for (int x = -KernelRadius; x <= KernelRadius; ++x) {
        for (int y = -KernelRadius; y <= KernelRadius; ++y) {
            vec2 offsetCoordinates = vec2(x, y) * volumeTexelSize + texCoord;
            
            float depth = depthSampleToWorldDepth(texture(VolumetricDepthSampler, offsetCoordinates).r);
            float depthDifference = abs(depth - sceneDepth);
            
            if (depthDifference < minimumDepthDifference) {
            	minimumDepthDifference = depthDifference;
            	minOffsetCoordinates = offsetCoordinates;
            }
        }
    }
    
    vec4 volumeColor = texture(VolumetricSampler, minOffsetCoordinates);
    sceneColor *= sceneColor;
    volumeColor *= volumeColor;
    fragColor = sqrt(sceneColor * (1.0 - volumeColor.a) + volumeColor);
    //float volumeDepth = depthSampleToWorldDepth(texture(VolumetricDepthSampler, texCoord).r);
    //fragColor = mix(fragColor, vec4(1, 0, 0, 1), abs(sceneDepth - volumeDepth));
}












































































































