package own.conusradar

import android.opengl.GLES20

object riGraphicTools {

    var sp_Image: Int = 0



    /* SHADER Image
	 * /////////////
	 * This shader is for rendering 2D images straight from a texture
	 * No additional effects.
	 *
	 */
    val vs_Image = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 a_texCoords;" +
            "varying vec2 v_texCoords;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  v_texCoords = a_texCoords;" +
            "}"

    val fs_Image = "precision mediump float;" +
            "varying vec2 v_texCoords;" +
            "uniform sampler2D u_texture;" +
            "void main() {" +
            "  gl_FragColor = texture2D(u_texture, v_texCoords);" +
            "}"


    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        val shader = GLES20.glCreateShader(type)

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        // return the shader
        return shader
    }
}
