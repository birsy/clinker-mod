#include veil:common
#include veil:deferred_utils
#include veil:color_utilities

in vec3 lightPos;
in vec3 lightColor;
in float radius;
in float falloff;

uniform sampler2D AlbedoSampler;
uniform sampler2D NormalSampler;
uniform sampler2D MaterialSampler;
uniform sampler2D VanillaLightSampler;
uniform sampler2D DiffuseDepthSampler;

uniform vec2 ScreenSize;

out vec4 fragColor;

float attenuate_no_cusp(float distance, float radius, float falloff)
{
    float s = distance / radius;

    if (s >= 1.0){
        return 0.0;
    }

    float s2 = s * s;
    return (1 - s2) * (1 - s2) / (1 + falloff * s2);
}

void main() {
    vec2 screenUv = gl_FragCoord.xy / ScreenSize;

    // sample buffers
    vec3 normalVS = texture(NormalSampler, screenUv).xyz;
    float depth = texture(DiffuseDepthSampler, screenUv).r;
    vec3 pos = viewToWorldSpaceDirection(viewPosFromDepth(depth, screenUv));

    vec3 newLightPos = lightPos - VeilCamera.CameraPosition;

    // lighting calculation
    vec3 offset = newLightPos - pos;
    vec3 lightDirection = (VeilCamera.ViewMat * vec4(normalize(offset), 0.0)).xyz;
    float shading = dot(normalVS, lightDirection);
    shading = max(0, shading);

    // ambient light term helps give lights some softness
    float distanceFactor = 1.0 - (length(offset) / radius);
    shading = mix(shading, 1.0, distanceFactor * distanceFactor);

    float falloff = attenuate_no_cusp(length(offset), radius, falloff);

    fragColor = vec4(falloff * shading * lightColor, 1.0);
}