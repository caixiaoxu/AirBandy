package com.lsy.airhockey.shape

import android.content.res.Resources
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 曲球棍的板子（第三版，三维：透视投影）
 * @author Xuwl
 * @date 2021/10/20
 *
 */
class Table3(res: Resources) : Shape(res) {
    private val POSITION_COMPONENT_COUNT = 2
    private val COLOR_COMPONENT_COUNT = 3
    private val STRIDE: Int = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * Float.SIZE_BYTES

    private val tableVerticesWithTriangles = floatArrayOf(
        //面板
        0f, 0f, 1f, 1f, 1f,
        -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
        0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
        0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
        -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
        -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

        //Lin1
        -0.5f, 0f, 1f, 0f, 0f,
        0.5f, 0f, 0f, 0f, 1f,

        //Mallets
        0f, -0.4f, 0f, 0f, 1f,
        0f, 0.4f, 1f, 0f, 0f
    )

    private val vertexData: FloatBuffer =
        ByteBuffer.allocateDirect(tableVerticesWithTriangles.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(tableVerticesWithTriangles)
            }
        }

    override fun loadVertexShader(): String = "vshader/matrix_vertex_shader.glsl"

    override fun loadFragmentShader(): String = "fshader/matrix_fragment_shader.glsl"

    private val aPositionLocation: Int
    private val uMatrixLocation: Int
    private val aColorLocation: Int

    init {
        uMatrixLocation = GLES20.glGetUniformLocation(mProgram, "u_Matrix")

        aPositionLocation = GLES20.glGetAttribLocation(mProgram, "a_Position").also {
            vertexData.position(0)
            GLES20.glVertexAttribPointer(it, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT,
                false, STRIDE, vertexData)
            GLES20.glEnableVertexAttribArray(it)
        }
        aColorLocation = GLES20.glGetAttribLocation(mProgram, "a_Color").also {
            vertexData.position(POSITION_COMPONENT_COUNT)
            GLES20.glVertexAttribPointer(it, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT,
                false, STRIDE, vertexData)
            GLES20.glEnableVertexAttribArray(it)
        }
    }

    override fun drawSelf(matrix: FloatArray) {
        //矩阵变换
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        //板子
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)
        //中间线
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2)

        //木槌点
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1)
    }
}