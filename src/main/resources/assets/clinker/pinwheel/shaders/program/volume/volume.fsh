#extension GL_ARB_shader_storage_buffer_object : enable

#include veil:deferred_utils
#include veil:fog

#define ITERATIONS 48
#define RENDER_DISTANCE 8
#define SECTION_SIZE 16 * 16 * 16

#define GAS_DATA_BLOCK_SIZE SECTION_SIZE * 2

layout(std430) readonly buffer SectionToDataIndexBuffer {
    int sectionToDataIndex[RENDER_DISTANCE * RENDER_DISTANCE * RENDER_DISTANCE];
};
layout(std430) readonly buffer GasDataBuffer {
    uint gasData[(RENDER_DISTANCE * RENDER_DISTANCE * RENDER_DISTANCE + 1) * GAS_DATA_BLOCK_SIZE];
};

uniform sampler2D DiffuseDepthSampler;
uniform sampler2D LightTextureSampler;
uniform sampler2D BlueNoiseSampler;

uniform int BlueNoiseOffset;

uniform int FogShape;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in vec2 texCoord;
out vec4 fragColor;

vec4 unpackColor(uint packedColor) {
    return vec4(packedColor >> uint(16) & uint(0xFF), packedColor >> uint(8) & uint(0xFF), packedColor & uint(0xFF), packedColor >> uint(24)) / 255.0;
}
ivec2 unpackLightmap(uint packedLightUv) {
    return ivec2((packedLightUv & uint(0xFFFF)) >> uint(4), packedLightUv >> uint(20) & uint(65535));
}

int gasDataIndex(vec3 pos) {
 	pos -= floor(VeilCamera.CameraPosition / 16.0) * 16.0;

    ivec3 sectionPos = ivec3(floor(pos / 16.0)) + ivec3(RENDER_DISTANCE / 2);
    // return empty if we're outside the current buffer range...
    if (sectionPos.x < 0 || sectionPos.x >= RENDER_DISTANCE ||
   		sectionPos.y < 0 || sectionPos.y >= RENDER_DISTANCE ||
    	sectionPos.z < 0 || sectionPos.z >= RENDER_DISTANCE) {
        return 0;
    }

    int sectionIndex = sectionPos.x + sectionPos.y * RENDER_DISTANCE + sectionPos.z * RENDER_DISTANCE * RENDER_DISTANCE;
    int gasDataIndexOffset = sectionToDataIndex[sectionIndex] * GAS_DATA_BLOCK_SIZE;

    ivec3 blockPos = ivec3(floor(pos) - floor(pos / 16.0) * 16.0);
    return (blockPos.x + blockPos.y * 16 + blockPos.z * 16 * 16) + gasDataIndexOffset;
}

vec2 getLightmap(vec3 pos) {
	return vec2(unpackLightmap(gasData[gasDataIndex(pos) + SECTION_SIZE]));
}
vec2 sampleLightmap(vec3 pos) {
	//pos -= vec3(0.5);
	vec3 floorPos = floor(pos);
	vec3 fracPos = pos - floorPos;
	floorPos += vec3(0.1);
	
	vec2 c000 = getLightmap(floorPos + vec3(0, 0, 0));
	vec2 c100 = getLightmap(floorPos + vec3(1, 0, 0));
	vec2 c010 = getLightmap(floorPos + vec3(0, 1, 0));
	vec2 c110 = getLightmap(floorPos + vec3(1, 1, 0));
	vec2 c001 = getLightmap(floorPos + vec3(0, 0, 1));
	vec2 c101 = getLightmap(floorPos + vec3(1, 0, 1));
	vec2 c011 = getLightmap(floorPos + vec3(0, 1, 1));
	vec2 c111 = getLightmap(floorPos + vec3(1, 1, 1));
	
	vec2 c00 = mix(c000, c100, fracPos.x);
	vec2 c10 = mix(c010, c110, fracPos.x);
	
	vec2 c01 = mix(c001, c101, fracPos.x);
	vec2 c11 = mix(c011, c111, fracPos.x);
	
	vec2 c0 = mix(c00, c10, fracPos.y);
	vec2 c1 = mix(c01, c11, fracPos.y);

	return (mix(c0, c1, fracPos.z) + 0.5) / 16.0;
}

struct GasData { vec3 color; float density; vec2 lightmapUV; };
GasData sampleData(vec3 pos) {
	int index = gasDataIndex(pos);
    vec4 color = unpackColor(gasData[index]);
    vec2 lightmap = vec2(0);
	if (color.a > 0.01) lightmap = sampleLightmap(pos);
	
    return GasData(mix(color.rgb, vec3(0.5), 0.8), max(color.a - color.r * 0.1, 0), lightmap);
}

void main() {
    float depth = texture(DiffuseDepthSampler, texCoord).r;
	
	vec3 origin = VeilCamera.CameraPosition;
	vec3 direction = viewDirFromUv(texCoord);
	
	vec3 terrainPosition = viewPosFromDepth(depth, texCoord);
	float raymarchDistance = min(length(terrainPosition), (RENDER_DISTANCE - 2) * 16 * 0.5);
	float stepSize = raymarchDistance / ITERATIONS;

	float blueNoise = texelFetch(BlueNoiseSampler, ivec2(gl_FragCoord.xy + BlueNoiseOffset) % 512, 0).r;
    vec3 rayPos = origin + blueNoise * stepSize * direction;
    float transmittance = 1.0;
	vec3 volumeColor = vec3(0);
	// raymarch loop
	for (int i = 0; i < ITERATIONS; i++) {
        GasData data = sampleData(rayPos);
        GasData aboveData = sampleData(rayPos + vec3(0, 4, 0));
        
        float ambientLight = smoothstep(0, 1, 1.0 - aboveData.density);
        ambientLight = max(ambientLight, 0.94);

        vec2 lightUV = data.lightmapUV * vec2(1, ambientLight);
        vec3 lightColor = texture(LightTextureSampler, lightUV).rgb;
        
        vec3 gasColor = data.color * lightColor;
        float fogDistance = fog_distance(rayPos - origin, FogShape);
        gasColor = mix(FogColor.rgb, gasColor, linear_fog_fade(fogDistance, FogStart, FogEnd));
        
       	volumeColor += data.density * stepSize * gasColor * transmittance;
        transmittance *= exp(-data.density * stepSize);
		
		if (transmittance < 0.01) break;
		rayPos += direction * stepSize;
	}
	
	gl_FragDepth = depth;
	fragColor = vec4(volumeColor, 1.0 - transmittance);
}























































