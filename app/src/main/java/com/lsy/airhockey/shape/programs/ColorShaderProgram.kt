package com.lsy.airhockey.shape.programs

import android.content.res.Resources
import android.opengl.GLES20

class ColorShaderProgram(
    res: Resources
) : ShaderProgram(
    res,
//    "vshader/matrix_vertex_shader.glsl",
//    "fshader/matrix_fragment_shader.glsl"
    "vshader/matrix_vertex_shader1.glsl",
    "fshader/matrix_fragment_shader1.glsl"
) {
    val uMatrixLocation: Int
    val uColorLocation: Int

    val aPositionLocation: Int
//    val aColorLocation: Int

    init {
        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, U_MATRIX)
        uColorLocation = GLES20.glGetUniformLocation(mProgram, U_COLOR)

        aPositionLocation = GLES20.glGetAttribLocation(mProgram, A_POSITION)
//        aColorLocation = GLES20.glGetAttribLocation(mProgram, A_COLOR)
    }

    /**
     * 设置Uniform参数
     * @param matrix 矩阵
     * @param textureId 纹理ID
     */
    fun setUniforms(matrix: FloatArray) {
        //矩阵参数
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }

    /**
     * 设置Uniform参数
     * @param matrix 矩阵
     * @param textureId 纹理ID
     */
    fun setUniforms(matrix: FloatArray, r: Float, g: Float, b: Float) {
        //矩阵参数
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glUniform4f(uColorLocation, r, g, b, 1f)
    }
}