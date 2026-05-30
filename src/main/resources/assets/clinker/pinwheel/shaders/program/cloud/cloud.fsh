#version 150
#include veil:space_helper

uniform vec2 ScreenResolution;

uniform vec3 CameraPos;

uniform sampler2D NoiseSampler;
uniform sampler2D CloudDensitySampler;
uniform sampler2D DiffuseDepthSampler;

uniform vec3 SkyCol;
uniform vec3 FogCol;

in vec3 position;
in vec4 vertexColor;
in vec2 uv;
in float dist;

out vec4 fragColor;

void main() {
	vec2 normalizedUV = uv * 2.0 - 1.0;
	float distanceFade = smoothstep(mix(0.95, 0.5, vertexColor.a), 0, length(normalizedUV));
	float closeFade = smoothstep(0, 30, dist);
	
	vec2 screenCoords = gl_FragCoord.xy / ScreenResolution;
	float sceneDistance = texelFetch(DiffuseDepthSampler, ivec2(gl_FragCoord.xy), 0).r;
	sceneDistance = length(screenToViewSpace(screenCoords, sceneDistance).xyz);
	float geometryDistance = length(screenToViewSpace(screenCoords, gl_FragCoord.z).xyz);
	float distanceThroughGeometry = sceneDistance - geometryDistance;
	float nearGeometryFade = smoothstep(0, 10, distanceThroughGeometry);
	
	vec2 cloudUV = (position.xz + CameraPos.xz);
	cloudUV = (floor(cloudUV/2)*2) / 512.0;
	cloudUV *= 0.3;

	float density = texture(CloudDensitySampler, cloudUV).r;
	
	float alpha = vertexColor.a - (1 - density) * 0.9;
	alpha *= 3;
	alpha = smoothstep(0.0, 1.0, alpha);
	alpha *= distanceFade * closeFade * nearGeometryFade;

	vec3 color = mix(FogCol * (1.0 + density * 0.4), mix(FogCol, SkyCol, 0.4), vertexColor.a);
    fragColor = vec4(color, alpha);
}



















































































































































