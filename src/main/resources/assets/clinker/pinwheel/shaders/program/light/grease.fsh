#include veil:common
#include veil:deferred_utils
#include veil:color_utilities
#include veil:light

in vec3 lightPos;
in vec3 lightColor;
in float radius;

uniform sampler2D NoiseSampler;
uniform sampler2D VeilDynamicAlbedoSampler;
uniform sampler2D VeilDynamicNormalSampler;
uniform sampler2D DiffuseDepthSampler;

uniform vec2 ScreenSize;
uniform float GameTime;

out vec4 fragColor;

vec4 sampleTriplanar(sampler2D texSampler, vec3 pos, vec3 normal) {
	float dotX = abs(dot(normal, vec3(1, 0, 0))) + 0.1;
	float dotY = abs(dot(normal, vec3(0, 1, 0))) + 0.05;
	float dotZ = abs(dot(normal, vec3(0, 0, 1)));
	if (dotX > dotY && dotX > dotZ) {
		return texture(NoiseSampler, pos.yz);
	} else if (dotY > dotX && dotY > dotZ) {
		return texture(NoiseSampler, pos.xz);
	} else {
		return texture(NoiseSampler, pos.xy);
	}
}

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float wave(float x) {
    float w = 1.0 - mod(x, 1.0);
    float w1 = map(w, 0.0, 0.8, 0.0, 1.0);
    float w2 = map(w, 0.8, 1.0, 1.0, 0.0);
    return smoothstep(0.0, 1.0, min(w1, w2));
}

void main() {
    vec2 screenUv = gl_FragCoord.xy / ScreenSize;

    vec4 albedoColor = texture(VeilDynamicAlbedoSampler, screenUv);
    if(albedoColor.a == 0) {
        discard;
    }

    float depth = texture(DiffuseDepthSampler, screenUv).r;
    vec3 pos = viewToWorldSpace(viewPosFromDepth(depth, screenUv));

    vec3 offset = lightPos - pos;

    vec3 normalVS = texture(VeilDynamicNormalSampler, screenUv).xyz;
    vec3 normalWS = viewToWorldSpaceDirection(normalVS);
    
	pos += 0.001;
	vec3 pixelPos = floor(pos * 16.0) / 16.0;
	
	float noiseSample = sampleTriplanar(NoiseSampler, (pixelPos * 0.052) + GameTime * 5, normalWS).r;
	float noiseSampleColor = sampleTriplanar(NoiseSampler, (pixelPos * 0.053) + GameTime * vec3(-1, 1, -1) + 100, normalWS).r;
	
	float attenuation = attenuate_no_cusp(length(offset), radius);
	vec3 color = hsv2rgb(vec3((noiseSampleColor + noiseSample) * 5, 0.5, 1));
	float downWave = wave(pixelPos.y * 0.2 + noiseSample + GameTime * 100) * 0.8 + 0.2;
	// downWave should have less effect on floors and ceilings...
	downWave = mix(downWave, noiseSample, abs(dot(normalWS, vec3(0, 1, 0))) * 0.8);
    fragColor = vec4(attenuation * downWave * albedoColor.rgb * lightColor * color, 1.0);
}

































































































