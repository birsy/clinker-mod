#version 150

uniform sampler2D Sampler0;

uniform vec4 FogColor;
uniform vec4 SkyColor1;
uniform vec4 SkyColor2;
uniform vec2 WindOffset;
uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float map(float value, float min1, float max1, float min2, float max2) {
    value = clamp(value, min(min1, max1), max(min1, max1));
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}

void main() {
    vec4 texture = texture(Sampler0, texCoord0*0.35 + WindOffset);
    float heightFactor = 1.0 - vertexColor.a;
//    if (heightFactor > 0.5) {
//        heightFactor = (1.0 - heightFactor) * 2;
//    } else {
//        heightFactor = heightFactor * 2;
//    }
    //heightFactor = smoothstep(0.0, 1.0, heightFactor);
    float cloudFactor = clamp(map(texture.b * texture.b, 0.0, 1.0, -0.5, 1.0), 0.0, 1.0);
    // todo: this sucks ass
    //if (heightFactor < cloudFactor) discard;
    float alpha = map(heightFactor, cloudFactor, 1.0, 0.0, 1.0) * cloudFactor;

    //vec4 color = vec4(FogColor.rgb + ((texture.rgb*2.0)-1.0)*vertexColor.r, alpha);

    float factor = 1 - (vertexColor.b * vertexColor.b);
    vec4 color = mix(SkyColor2, SkyColor1, (1 - factor) * 0.8);
    color = mix(FogColor, color, smoothstep(0, 0.8, 1 - factor));
    color = vec4(color.rgb, alpha);

    fragColor = color * ColorModulator;
}
