uniform sampler2D CloudsSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    // todo: bilinear filtering?
    vec4 cloudSample = texture(CloudsSampler, texCoord);
    fragColor = cloudSample;
}
