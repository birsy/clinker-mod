uniform sampler2D CloudsSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    fragColor = texture(CloudsSampler, texCoord);
}
