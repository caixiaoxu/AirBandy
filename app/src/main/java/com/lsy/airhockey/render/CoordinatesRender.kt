package com.lsy.airhockey.render

import android.content.res.Resources
import android.opengl.Matrix
import com.lsy.airhockey.R
import com.lsy.airhockey.shape.Mallet
import com.lsy.airhockey.shape.Table4
import com.lsy.airhockey.shape.programs.ColorShaderProgram
import com.lsy.airhockey.shape.programs.TextureShaderProgram
import com.lsy.airhockey.utils.MatrixUtil
import com.lsy.airhockey.utils.TextureUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 纹理Renderer
 * @author Xuwl
 * @date 2021/10/20
 */
class CoordinatesRender(res: Resources) : BaseRenderer(res) {
    private lateinit var mTable: Table4
    private lateinit var mMallet: Mallet

    private lateinit var mTextureProgram: TextureShaderProgram
    private lateinit var mColorProgram: ColorShaderProgram

    private var mTexture: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        mTable = Table4()
        mMallet = Mallet()

        mTextureProgram = TextureShaderProgram(res)
        mColorProgram = ColorShaderProgram(res)

        mTexture = TextureUtil.loadTexture(res, R.drawable.air_hockey_surface)
    }

    /**
     * 重写矩阵
     */
    override fun initMatrix(width: Int, height: Int) {
        val aspectRatio = width.toFloat() / height.toFloat()
        MatrixUtil.perspectiveM(mProjectionMatrix, 45f, aspectRatio, 1f, 10f)

        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f)
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)

        val temp = FloatArray(16)
        Matrix.multiplyMM(temp, 0, mProjectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, mProjectionMatrix, 0, temp.size)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        //绘制桌面纹理
        mTable.run {
            bindData(mTextureProgram.also { program ->
                program.useProgram()
                program.setUniforms(mProjectionMatrix, mTexture)
            })
            draw()
        }

        //绘制圆心
        mMallet.run {
            bindData(mColorProgram.also { program ->
                program.useProgram()
                program.setUniforms(mProjectionMatrix)
            })
            draw()
        }
    }
}