#extension GL_ARB_shader_storage_buffer_object : enable

#include veil:deferred_utils

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

uniform sampler2D LightTextureSampler;

layout(std430) readonly buffer VolumetricData {
    uint volumeData[16 * 16 * 16 * 2];
};

in vec2 texCoord;
out vec4 fragColor;

const vec3 VolumePosition = vec3(72, 136, -248);

// https://iquilezles.org/articles/intersectors/
vec2 boxIntersection(vec3 ro, vec3 rd, vec3 boxSize, out bool hit, out vec3 outNormal ) {
    vec3 m = 1.0/rd; // can precompute if traversing a set of aligned boxes
    vec3 n = m*ro;   // can precompute if traversing a set of aligned boxes
    vec3 k = abs(m)*boxSize;
    vec3 t1 = -n - k;
    vec3 t2 = -n + k;
    float tN = max( max( t1.x, t1.y ), t1.z );
    float tF = min( min( t2.x, t2.y ), t2.z );
	hit = !(tN > tF || tF < 0.0);
    if (!hit) return vec2(-1.0); // no intersection
    outNormal = (tN>0.0) ? step(vec3(tN), t1) : // ro ouside the box
                           step(t2, vec3(tF));  // ro inside the box
    outNormal *= -sign(rd);
    return vec2( tN, tF );
}

vec4 unpackColor(uint packedColor) {
    return vec4(packedColor >> uint(16) & uint(0xFF), packedColor >> uint(8) & uint(0xFF), packedColor & uint(0xFF), packedColor >> uint(24)) / 255.0;
}
ivec2 unpackLightmap(uint packedLightUv) {
    return ivec2((packedLightUv & uint(0xFFFF)) >> uint(4), packedLightUv >> uint(20) & uint(65535));
}

struct GasData { vec3 color; float density; ivec2 lightmapUV; };
GasData sampleData(ivec3 pos) {
    int index = (pos.x + pos.y * 16 + pos.z * 16 * 16) * 2;
    vec4 color = unpackColor(volumeData[index]);
    ivec2 lightmap = unpackLightmap(volumeData[index + 1]);
    return GasData(color.rgb, color.a, lightmap);
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    float depth = texture(DiffuseDepthSampler, texCoord).r;
	
	vec3 origin = VeilCamera.CameraPosition;
	vec3 direction = viewDirFromUv(texCoord);
	
	bool hit;
	vec3 boxNormal = vec3(1.0);
	vec2 intersection = boxIntersection(origin - VolumePosition, direction, vec3(8), hit, boxNormal);
	
	vec3 terrainPosition = viewPosFromDepth(depth, texCoord);
	
	if (length(terrainPosition) < intersection.x || !hit) {
		fragColor = color;
		return;
	}
	
	int iterations = 128;
	float rayStartDist = max(intersection.x, 0);
	float rayEndDist = min(intersection.y, length(terrainPosition));
	
	vec3 rayPos = origin + rayStartDist * direction;
	
	float stepSize = (rayEndDist - rayStartDist) / iterations;
	
	float transmittance = 1;
	vec3 volumeColor = vec3(0);
	// raymarch loop
	for (int i = 0; i < iterations; i++) {
		ivec3 voxelCoords = ivec3(floor(rayPos - VolumePosition + vec3(8)));
        GasData data = sampleData(voxelCoords);
        
        vec3 lightColor = texture(LightTextureSampler, (vec2(data.lightmapUV) + 0.5) / 16.0).rgb;
        volumeColor += data.density * stepSize * data.color * lightColor * transmittance;
        transmittance *= exp(-data.density * stepSize);
		if (transmittance < 0.01) break;
		
		rayPos += direction * stepSize;
	}

	fragColor = vec4(color.rgb * transmittance + volumeColor, 1.0);
}
