#extension GL_ARB_shader_storage_buffer_object : enable

#include veil:deferred_utils

#define ITERATIONS 64
#define RENDER_DISTANCE 8
#define SECTION_SIZE 16 * 16 * 16

#define GAS_DATA_BLOCK_SIZE SECTION_SIZE * 2

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

uniform sampler2D LightTextureSampler;

uniform sampler2D BlueNoiseSampler;

uniform int BlueNoiseOffset;

layout(std430) readonly buffer SectionToDataIndexBuffer {
    int sectionToDataIndex[RENDER_DISTANCE * RENDER_DISTANCE * RENDER_DISTANCE];
};
layout(std430) readonly buffer GasDataBuffer {
    uint gasData[(RENDER_DISTANCE * RENDER_DISTANCE * RENDER_DISTANCE + 1) * GAS_DATA_BLOCK_SIZE];
};

in vec2 texCoord;
out vec4 fragColor;

vec4 unpackColor(uint packedColor) {
    return vec4(packedColor >> uint(16) & uint(0xFF), packedColor >> uint(8) & uint(0xFF), packedColor & uint(0xFF), packedColor >> uint(24)) / 255.0;
}
ivec2 unpackLightmap(uint packedLightUv) {
    return ivec2((packedLightUv & uint(0xFFFF)) >> uint(4), packedLightUv >> uint(20) & uint(65535));
}

struct GasData { vec3 color; float density; ivec2 lightmapUV; };
GasData sampleData(vec3 pos) {
    pos -= floor(VeilCamera.CameraPosition / 16.0) * 16.0;

    ivec3 sectionPos = ivec3(floor(pos / 16.0)) + ivec3(RENDER_DISTANCE / 2);
    // return empty if we're outside the current buffer range...
    if (sectionPos.x < 0 || sectionPos.x >= RENDER_DISTANCE ||
   		sectionPos.y < 0 || sectionPos.y >= RENDER_DISTANCE ||
    	sectionPos.z < 0 || sectionPos.z >= RENDER_DISTANCE) {
        return GasData(vec3(0), 0, ivec2(0));
    }

    int sectionIndex = sectionPos.x + sectionPos.y * RENDER_DISTANCE + sectionPos.z * RENDER_DISTANCE * RENDER_DISTANCE;
    int gasDataIndexOffset = sectionToDataIndex[sectionIndex] * GAS_DATA_BLOCK_SIZE;

    ivec3 blockPos = ivec3(floor(pos) - floor(pos / 16.0) * 16.0);
    int gasDataIndex = blockPos.x + blockPos.y * 16 + blockPos.z * 16 * 16;
   
    gasDataIndex += gasDataIndexOffset;

    vec4 color = unpackColor(gasData[gasDataIndex]);
    ivec2 lightmap = unpackLightmap(gasData[gasDataIndex + SECTION_SIZE]);
	
	//return GasData(vec3(0.5), max(lightmap.x, lightmap.y) / 16.0, lightmap);
    return GasData(mix(color.rgb, vec3(0.5), 0.8), max(color.a - color.r*0.2, 0), lightmap);
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
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
        ambientLight *= ambientLight * ambientLight;
        ambientLight = max(ambientLight, 0.5);
        ambientLight = 1;
        vec2 lightUV = ((vec2(data.lightmapUV) + 0.5) / 16.0) * vec2(1, ambientLight);
        vec3 lightColor = texture(LightTextureSampler, lightUV).rgb;
       	volumeColor += data.density * stepSize * data.color * lightColor * transmittance;
        transmittance *= exp(-data.density * stepSize);
		
		if (transmittance < 0.01) break;
		rayPos += direction * stepSize;
	}

	fragColor = vec4(color.rgb * transmittance + volumeColor, 1.0);
}



















