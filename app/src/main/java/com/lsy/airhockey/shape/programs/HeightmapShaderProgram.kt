package com.lsy.airhockey.shape.programs

import android.content.res.Resources
import android.opengl.GLES20
import android.opengl.GLUtils
import com.lsy.airhockey.shape.Geometry

/**
 * @author Xuwl
 * @date 2021/10/27
 *
 */
class HeightmapShaderProgram(res: Resources) : ShaderProgram(res,
    "vshader/heightmap_vertex_shader.glsl",
    "fshader/heightmap_fragment_shader.glsl") {

    val uMVMatrixLocation: Int
    val uIT_MVMatrixLocation: Int
    val uMVPMatrixLocation: Int
    val uVectorToLightLocation: Int
    val uPointLightPositionsLocation: Int
    val uPointLightColorsLocation: Int

    val aPositionLocation: Int
    val aNormalLocation: Int

    init {
        uMVMatrixLocation = GLES20.glGetUniformLocation(mProgram, U_MV_MATRIX)
        uIT_MVMatrixLocation = GLES20.glGetUniformLocation(mProgram, U_IT_MV_MATRIX)
        uMVPMatrixLocation = GLES20.glGetUniformLocation(mProgram, U_MVP_MATRIX)
        uPointLightPositionsLocation =
            GLES20.glGetUniformLocation(mProgram, U_POINT_LIGHT_POSITIONS)
        uPointLightColorsLocation = GLES20.glGetUniformLocation(mProgram, U_POINT_LIGHT_COLORS)
        uVectorToLightLocation = GLES20.glGetUniformLocation(mProgram, U_VECTOR_TO_LIGHT)

        aPositionLocation = GLES20.glGetAttribLocation(mProgram, A_POSITION)
        aNormalLocation = GLES20.glGetAttribLocation(mProgram, A_NORMAL)
    }

//    /**
//     * 设置uniform变量的参数
//     */
//    fun setUniforms(matrix: FloatArray) {
//        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
//    }
//
//    /**
//     * 设置uniform变量的参数
//     */
//    fun setUniforms(matrix: FloatArray, vectorToLight: Geometry.Vector) {
//        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
//        GLES20.glUniform3f(uVectorToLightLocation,
//            vectorToLight.x, vectorToLight.y, vectorToLight.z)
//    }

    /**
     * 设置uniform变量的参数
     */
    fun setUniforms(
        mvMatrix: FloatArray,
        it_mvMatrix: FloatArray,
        mvpMatrix: FloatArray,
        vectorToDirectionalLight: FloatArray,
        pointLightPositions: FloatArray,
        pointLightColors: FloatArray,
    ) {
        GLES20.glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0)
        GLES20.glUniformMatrix4fv(uIT_MVMatrixLocation, 1, false, it_mvMatrix, 0)
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0)

        GLES20.glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0)
        GLES20.glUniform4fv(uPointLightPositionsLocation, 3, pointLightPositions, 0)
        GLES20.glUniform3fv(uPointLightColorsLocation, 3, pointLightColors, 0)
    }
}