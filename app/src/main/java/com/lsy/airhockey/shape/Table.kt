package com.lsy.airhockey.shape

import android.content.res.Resources
import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * 曲球棍的板子（第一版，基础绘制）
 * @author Xuwl
 * @date 2021/10/20
 *
 */
class Table(res: Resources) : Shape(res) {
    protected val POSITION_COMPONENT_COUNT = 2

    private val tableVertices = floatArrayOf(
        0f, 0f,
        0f, 14f,
        9f, 14f,
        9f, 0f
    )

    private val tableVerticesWithTriangles = floatArrayOf(
        //底板
        //Triangle1
        -0.55f, -0.54f,
        0.55f, 0.54f,
        -0.55f, 0.54f,
        //Triangle2
        -0.55f, -0.54f,
        0.55f, -0.54f,
        0.55f, 0.54f,

        //面析
        //Triangle1
        -0.5f, -0.5f,
        0.5f, 0.5f,
        -0.5f, 0.5f,
        //Triangle2
        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,

        //Lin1
        -0.5f, 0f,
        0.5f, 0f,

        //Mallets
        0f, -0.25f,
        0f, 0.25f
    )

    private val vertexData: FloatBuffer =
        ByteBuffer.allocateDirect(tableVerticesWithTriangles.size * Float.SIZE_BYTES).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(tableVerticesWithTriangles)
                position(0)
            }
        }

    override fun loadVertexShader(): String = "vshader/base_vertex_shader.glsl"

    override fun loadFragmentShader(): String = "fshader/base_fragment_shader.glsl"

    private val aPositionLocation: Int
    private val uColorLocation: Int

    init {
        aPositionLocation = GLES20.glGetAttribLocation(mProgram, "a_Position").also {
            GLES20.glVertexAttribPointer(it, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, vertexData)
            GLES20.glEnableVertexAttribArray(it)
        }
        uColorLocation = GLES20.glGetUniformLocation(mProgram, "u_Color")
    }

    override fun drawSelf(matrix: FloatArray) {
        //板子
        GLES20.glUniform4f(uColorLocation, 1f, 0f, 0f, 1f)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6)
        //板子
        GLES20.glUniform4f(uColorLocation, 1f, 1f, 1f, 1f)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 6, 6)
        //中间线
        GLES20.glUniform4f(uColorLocation, 1f, 0f, 0f, 1f)
        GLES20.glDrawArrays(GLES20.GL_LINES, 12, 2)

        //木槌点
        GLES20.glUniform4f(uColorLocation, 0f, 0f, 1f, 1f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 14, 1)
        GLES20.glUniform4f(uColorLocation, 0f, 1f, 0f, 1f)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 15, 1)
    }
}