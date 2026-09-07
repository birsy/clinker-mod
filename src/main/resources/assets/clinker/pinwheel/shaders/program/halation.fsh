#include veil:fog
#include veil:space_helper
#include clinker:dither

uniform vec2 ScreenResolution;

uniform sampler2D TextureSampler;
uniform sampler2D DepthSampler;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float Radius;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
	vec4 color = texture(TextureSampler, texCoord0);
    // quantize
	float haloBrightness = color.r;//floor(color.r * 32.0) / 32.0;

    vec2 screenCoords = gl_FragCoord.xy / ScreenResolution;
	float sceneDistance = texelFetch(DepthSampler, ivec2(gl_FragCoord.xy), 0).r;
	sceneDistance = length(screenToViewSpace(screenCoords, sceneDistance).xyz);
	float geometryDistance = length(screenToViewSpace(screenCoords, gl_FragCoord.z).xyz);
	float distanceThroughGeometry = sceneDistance - geometryDistance;
	float geoProximityFade = smoothstep(0.0, Radius, distanceThroughGeometry);

    float alpha = vertexColor.a * haloBrightness * haloBrightness * geoProximityFade;
	alpha *= smoothstep(0.5, 1.5, geometryDistance);
    fragColor = vec4(mix(vertexColor.rgb, vec3(1.0), alpha * 0.25), alpha);
}


