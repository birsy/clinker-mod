uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec4 vertColor;
in vec2 texCoord;

out vec4 fragColor;

void main() {
    fragColor = texture(Sampler0, texCoord) * vertColor * ColorModulator;
}









