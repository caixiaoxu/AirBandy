package com.lsy.airhockey.shape.programs

import android.content.res.Resources
import android.opengl.GLES20

/**
 * @author Xuwl
 * @date 2021/10/27
 *
 */
class SkyboxShaderProgram(res: Resources) : ShaderProgram(res,
    "vshader/skybox_vertex_shader.glsl",
    "fshader/skybox_fragment_shader.glsl") {

    private val uMatrixLocation: Int
    private val uTextureUnitLocation: Int
    val aPositionLocation: Int

    init {
        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, U_MATRIX)
        uTextureUnitLocation = GLES20.glGetUniformLocation(mProgram, U_TEXTURE_UNIT)
        aPositionLocation = GLES20.glGetAttribLocation(mProgram, A_POSITION)
    }

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId)
        GLES20.glUniform1i(uTextureUnitLocation, 0)
    }
}