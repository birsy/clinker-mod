#include veil:fog
#include veil:space_helper

uniform vec2 ScreenResolution;

uniform sampler2D Sampler0;
uniform sampler2D DiffuseDepthSampler;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    
    vec2 screenCoords = gl_FragCoord.xy / ScreenResolution;
	float sceneDistance = texelFetch(DiffuseDepthSampler, ivec2(gl_FragCoord.xy), 0).r;
	sceneDistance = length(screenToViewSpace(screenCoords, sceneDistance).xyz);
	float geometryDistance = length(screenToViewSpace(screenCoords, gl_FragCoord.z).xyz);
	float distanceThroughGeometry = sceneDistance - geometryDistance;
	float geoProximityFade = smoothstep(0.0, 0.8, distanceThroughGeometry);

    float alpha = 1.0 - clamp(length(color.xy * 2 - 1), 0.0, 1.0);
    alpha *= alpha * geoProximityFade * 0.25;
    alpha = clamp(alpha + color.z, 0.0, 1.0);
    
    fragColor = linear_fog(vertexColor * alpha * vertexColor.a, vertexDistance, FogStart, FogEnd, vec4(0.0));
}


