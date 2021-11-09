package com.lsy.airhockey.shape.programs

import android.content.res.Resources
import android.opengl.GLES20

/**
 * @author Xuwl
 * @date 2021/10/26
 *
 */
class ParticleShaderProgram(res: Resources) : ShaderProgram(res,
    "vshader/particle_vertex_shader.glsl",
    "fshader/particle_fragment_shader.glsl") {

    val uMatrixLocation: Int
    val uTimeLocation: Int
    val uTextureUnitLocation: Int

    val aPositionLocation: Int
    val aColorLocation: Int
    val aDirectionVectorLocation: Int
    val aParticleStartTimeLocation: Int

    init {
        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, U_MATRIX)
        uTimeLocation = GLES20.glGetUniformLocation(mProgram, U_TIME)
        uTextureUnitLocation = GLES20.glGetUniformLocation(mProgram, U_TEXTURE_UNIT)

        aPositionLocation = GLES20.glGetAttribLocation(mProgram, A_POSITION)
        aColorLocation = GLES20.glGetAttribLocation(mProgram, A_COLOR)
        aDirectionVectorLocation = GLES20.glGetAttribLocation(mProgram, A_DIRECTION_VECTOR)
        aParticleStartTimeLocation = GLES20.glGetAttribLocation(mProgram, A_PARTICLE_START_TIME)
    }

    /**
     * 设置uniform变量的参数
     */
    fun setUniforms(matrix: FloatArray, elapsedTime: Float) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glUniform1f(uTimeLocation, elapsedTime)
    }

    /**
     * 设置uniform变量的参数
     */
    fun setUniforms(matrix: FloatArray, elapsedTime: Float, textureId: Int) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glUniform1f(uTimeLocation, elapsedTime)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(uTextureUnitLocation, 0)
    }
}