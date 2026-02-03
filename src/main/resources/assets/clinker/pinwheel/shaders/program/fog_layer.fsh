#include veil:fog
#include veil:space_helper

uniform vec2 ScreenResolution;

uniform float MaxFogDistance;
uniform float FogLayerHeight;
uniform float FogLayerDensity;
uniform vec4 FogLayerColor;

uniform sampler2D FogSampler;
uniform sampler2D DepthSampler;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec2 screenCoords = gl_FragCoord.xy / ScreenResolution;
    float depth = texelFetch(DepthSampler, ivec2(gl_FragCoord.xy), 0).r;
    
    float cameraY = VeilCamera.CameraPosition.y;

    vec3 scenePos = screenToLocalSpace(screenCoords, depth).xyz;
	vec3 fogPos = scenePos + vec3(0, cameraY - FogLayerHeight, 0);
	float distanceFromHeight = abs(fogPos.y);
	float fogFactor = 1.0 - exp(distanceFromHeight * -FogLayerDensity);
	fogFactor *= 1.0 - smoothstep(FogEnd * 0.9, FogEnd, length(scenePos));
	
    fragColor = vec4(FogLayerColor.rgb, FogLayerColor.a * fogFactor);
}















































































































