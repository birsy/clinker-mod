uniform float Brightness;
uniform float Contrast;

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

out vec4 fragColor;

mat4 brightnessMatrix(float b) {
    return mat4(
		1, 0, 0, 0,
		0, 1, 0, 0,
		0, 0, 1, 0,
		b, b, b, 1
    );
}

mat4 contrastMatrix(float c) {
    float t = ( 1.0 - c ) / 2.0;
    return mat4(
		c, 0, 0, 0,
		0, c, 0, 0,
		0, 0, c, 0,
		t, t, t, 1
    );
}

void main() {
    fragColor = brightnessMatrix(Brightness) * contrastMatrix(Contrast) * texture(DiffuseSampler, texCoord);
}