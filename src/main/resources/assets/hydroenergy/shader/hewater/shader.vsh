#version 330 core
// in_Position was bound to attribute index 0 and in_waterID was bound to attribute index 1
layout (location = 0) in vec3 in_Position;
layout (location = 1) in int in_waterId;

out VS_OUT {
    int waterId;
} gs_out;

void main(void) {
    gl_Position = vec4(in_Position, 1.0);
    gs_out.waterId = in_waterId;
}