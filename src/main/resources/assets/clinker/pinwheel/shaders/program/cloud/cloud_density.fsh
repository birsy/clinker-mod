
uniform sampler2D NoiseSampler;
uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

float smin( float a, float b, float k )
{
    k *= 16.0/3.0;
    float h = max( k-abs(a-b), 0.0 )/k;
    return min(a,b) - h*h*h*(4.0-h)*k*(1.0/16.0);
}

float pingpong(float a, float b) {
	if (b == 0.0) {
		return 0.0f;
	} else {
		return abs(fract((a - b) / (b * 2.0)) * b * 2.0 - b);
	}
}   

void main() {
	float time = GameTime * 0.2;

	vec3 noiseA = texture(NoiseSampler, 
		texCoord - time * vec2(0.005, 0.01) * 0.8).rgb;
	vec3 noiseB = texture(NoiseSampler, 
		texCoord + time * vec2(0.005, 0.01) * 0.8 + 0.005).rgb;
	float bubbles = max(noiseA.b, noiseB.b);
		
	vec2 offset = vec2(noiseA.r, noiseB.r) * -0.6 + time * vec2(-0.01, 0.005) * 2;
	offset += bubbles * 0.05;
	vec3 noiseC = texture(NoiseSampler, texCoord * 4 + offset).rgb;
	
	float final = mix(noiseC.b, noiseC.g, smoothstep(0, 1, noiseA.r * noiseA.r * noiseA.r));
	final = mix(final, noiseC.r, smoothstep(0, 1, noiseB.r) * noiseB.r);
	
	float ridgesAndWiggles = mix(noiseC.g, noiseC.b, noiseC.r);
	
	final = bubbles + (ridgesAndWiggles * 2 - 1) * 0.5;
	final = smoothstep(-0.5, 1.5, final);
    fragColor = vec4(final, 0, 0, 1.0);

}
































































































































































































































