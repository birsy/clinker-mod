#include veil:space_helper
#include clinker:dither

uniform sampler2D CloudsSampler;
uniform sampler2D CloudsDepthSampler;
uniform sampler2D SceneSampler;
uniform sampler2D SceneDepthSampler;
uniform vec2 ScreenSize;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    float cloudsDepth = texture(CloudsDepthSampler, texCoord).r;
    float linearizedCloudDepth = length(screenToLocalSpace(texCoord, cloudsDepth));
    float sceneDepth = texture(SceneDepthSampler, texCoord).r;
    float linearizedSceneDepth = length(screenToLocalSpace(texCoord, sceneDepth));

    float depthFade = smoothstep(0.0, 15.0, linearizedSceneDepth - linearizedCloudDepth);
    depthFade = dither(int(gl_FragCoord.x), int(gl_FragCoord.y), depthFade);

    vec4 cloudColor = texture(CloudsSampler, texCoord) * vec4(1.0, 1.0, 1.0, depthFade);
    vec4 sceneColor = texture(SceneSampler, texCoord);

    fragColor = vec4(mix(sceneColor.rgb, cloudColor.rgb, cloudColor.a), cloudColor.a + sceneColor.a - (cloudColor.a * sceneColor.a));
    gl_FragDepth = mix(sceneDepth, min(sceneDepth, cloudsDepth), depthFade);
}
