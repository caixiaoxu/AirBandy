package com.lsy.airhockey.shape.programs

import android.content.res.Resources
import android.opengl.GLES20

class TextureShaderProgram(res: Resources) : ShaderProgram(
    res, "vshader/texture_vertex_shader.glsl", "fshader/texture_fragment_shader.glsl"
) {
    val uMatrixLocation: Int
    val uTextureUnitLocation: Int

    val aPositionLocation: Int
    val aTextureCoordinatesLocation: Int

    init {
        //获取Uniform变量
        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, "u_Matrix")
        uTextureUnitLocation = GLES20.glGetUniformLocation(mProgram, "u_TextureUnit")

        //获取attribute变量
        aPositionLocation = GLES20.glGetAttribLocation(mProgram, "a_Position")
        aTextureCoordinatesLocation = GLES20.glGetAttribLocation(mProgram, "a_TextureCoordinates")
    }

    /**
     * 设置Uniform参数
     * @param matrix 矩阵
     * @param textureId 纹理ID
     */
    fun setUniforms(matrix: FloatArray, textureId: Int) {
        //矩阵参数
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        //激活纹理单位
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        //告诉纹理使用单位0
        GLES20.glUniform1i(uTextureUnitLocation, 0)
    }

}