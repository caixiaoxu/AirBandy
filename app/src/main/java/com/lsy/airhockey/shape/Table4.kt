package com.lsy.airhockey.shape

import android.opengl.GLES20
import com.lsy.airhockey.shape.programs.TextureShaderProgram

/**
 * 曲球棍的板子（第四版，纹理）
 * @author Xuwl
 * @date 2021/10/20
 *
 */
class Table4 : Shape1() {
    private val POSITION_COMPONENT_COUNT = 2
    private val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
    private val STRIDE: Int =
        (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Float.SIZE_BYTES

    override fun createVerticesArr(): FloatArray = floatArrayOf(
        //面板
        0f, 0f, 0.5f, 0.5f,
        -0.5f, -0.8f, 0f, 0.9f,
        0.5f, -0.8f, 1f, 0.9f,
        0.5f, 0.8f, 1f, 0.1f,
        -0.5f, 0.8f, 0f, 0.1f,
        -0.5f, -0.8f, 0f, 0.9f
    )

    /**
     * 绑定数据
     * @param textureProgram 纹理程序
     */
    fun bindData(textureProgram: TextureShaderProgram) {
        setVertexAttribPointer(
            0, textureProgram.aPositionLocation, POSITION_COMPONENT_COUNT, STRIDE
        )
        setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            textureProgram.aTextureCoordinatesLocation,
            TEXTURE_COORDINATES_COMPONENT_COUNT,
            STRIDE
        )
    }

    override fun draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6)
    }
}