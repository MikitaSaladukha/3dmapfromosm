attribute vec4 a_Position;
uniform mat4 matViewProj;

void main(){
    gl_Position = matViewProj * a_Position;
}