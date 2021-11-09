package com.lsy.airhockey.render

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.Matrix
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.lsy.airhockey.R
import com.lsy.airhockey.shape.*
import com.lsy.airhockey.shape.programs.HeightmapShaderProgram
import com.lsy.airhockey.shape.programs.ParticleShaderProgram
import com.lsy.airhockey.shape.programs.SkyboxShaderProgram
import com.lsy.airhockey.utils.MatrixUtil
import com.lsy.airhockey.utils.TextureUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author Xuwl
 * @date 2021/10/26
 *
 */
class ParticlesRender(res: Resources) : BaseRenderer(res) {

    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewMatrixForSkybox = FloatArray(16)
//    private val viewProjectionMatrix = FloatArray(16)

    private val modelViewMatrix = FloatArray(16)
    private val it_modelViewMatrix = FloatArray(16)

    private val tempMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private lateinit var particleProgram: ParticleShaderProgram
    private lateinit var particleSystem: ParticleSystem
    private lateinit var redParticleShooter: ParticleShooter
    private lateinit var greenParticleShooter: ParticleShooter
    private lateinit var blueParticleShooter: ParticleShooter
    private var globalStartTime: Long = 0L
    private var particleTexture: Int = 0

    private lateinit var skyboxShaderProgram: SkyboxShaderProgram
    private lateinit var skyBox: SkyBox
    private var skyboxTexture: Int = 0

    private lateinit var heightmapProgram: HeightmapShaderProgram
    private lateinit var heightmap: Heightmap

    private val vectorToLight = floatArrayOf(0.30f, 0.35f, -0.89f, 0f)
    private val pointLightPositions = floatArrayOf(
        -1f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        1f, 1f, 0f, 1f)
    private val pointLightColors = floatArrayOf(
        1.00f, 0.20f, 0.02f,
        0.02f, 0.25f, 0.02f,
        0.02f, 0.20f, 1.00f)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        particleProgram = ParticleShaderProgram(res)
        particleSystem = ParticleSystem(10000)
        globalStartTime = System.nanoTime()

        particleTexture = TextureUtil.loadTexture(res, R.drawable.particle_texture)

        val angleVarianceInDegresss = 5f
        val speedVariance = 1f

        val particleDirection = Geometry.Vector(0f, 0.5f, 0f)
        redParticleShooter =
            ParticleShooter(Geometry.Point(-1f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegresss,
                speedVariance)
        greenParticleShooter =
            ParticleShooter(Geometry.Point(0f, 0f, 0f),
                particleDirection,
                Color.rgb(25, 255, 25),
                angleVarianceInDegresss,
                speedVariance)
        blueParticleShooter =
            ParticleShooter(Geometry.Point(1f, 0f, 0f),
                particleDirection,
                Color.rgb(5, 50, 255),
                angleVarianceInDegresss,
                speedVariance)

        skyboxShaderProgram = SkyboxShaderProgram(res)
        skyBox = SkyBox()
        skyboxTexture = TextureUtil.loadCubeMap(res, intArrayOf(
            R.drawable.left, R.drawable.right,
            R.drawable.bottom, R.drawable.top,
            R.drawable.front, R.drawable.back
        ))
//        skyboxTexture = TextureUtil.loadCubeMap(res, intArrayOf(
//            R.drawable.night_left, R.drawable.night_right,
//            R.drawable.night_bottom, R.drawable.night_top,
//            R.drawable.night_front, R.drawable.night_back
//        ))

        heightmapProgram = HeightmapShaderProgram(res)
//        val bitmap = BitmapFactory.decodeResource(res, R.drawable.heightmap,
//            BitmapFactory.Options().also { it.inScaled = false })
        heightmap = Heightmap(res.getDrawable(R.drawable.heightmap).toBitmap())
    }

    override fun initMatrix(width: Int, height: Int) {
        super.initMatrix(width, height)
        val aspectRatio = width.toFloat() / height.toFloat()
        MatrixUtil.perspectiveM(mProjectionMatrix, 45f, aspectRatio, 1f, 100f)
        updateViewMatrices()

//        Matrix.setIdentityM(viewMatrix, 0)
//        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f)
//
//        Matrix.multiplyMM(viewProjectionMatrix, 0, mProjectionMatrix, 0, viewMatrix, 0)
    }

    fun updateViewMatrices() {
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)
        System.arraycopy(viewMatrix, 0, viewMatrixForSkybox, 0, viewMatrix.size)

        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f)
    }

    fun updateMvpMatrix() {
//        Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
//        Matrix.multiplyMM(modelViewProjectionMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0)

        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.invertM(tempMatrix, 0, modelViewMatrix, 0)
        Matrix.transposeM(it_modelViewMatrix, 0, tempMatrix, 0)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, mProjectionMatrix, 0, modelViewMatrix, 0)
    }

    fun updateMvpMatrixForSkybox() {
        Matrix.multiplyMM(tempMatrix, 0, viewMatrixForSkybox, 0, modelMatrix, 0)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, mProjectionMatrix, 0, tempMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
//        super.onDrawFrame(gl)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        drawHeightmap()
        drawSkybox()
        drawParticles()
    }

    private fun drawHeightmap() {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.scaleM(modelMatrix, 0, 100f, 10f, 100f)
        updateMvpMatrix()
        heightmapProgram.useProgram()
//        heightmapProgram.setUniforms(modelViewProjectionMatrix, vectorToLight)

        val vectorToLightInEyeSpace = FloatArray(4)
        val pointPositionsInEyeSpace = FloatArray(12)
        Matrix.multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix, 0, vectorToLight, 0)
        Matrix.multiplyMV(pointPositionsInEyeSpace, 0, viewMatrix, 0, pointLightPositions, 0)
        Matrix.multiplyMV(pointPositionsInEyeSpace, 4, viewMatrix, 0, pointLightPositions, 4)
        Matrix.multiplyMV(pointPositionsInEyeSpace, 8, viewMatrix, 0, pointLightPositions, 8)
        heightmapProgram.setUniforms(modelViewMatrix, it_modelViewMatrix, modelViewProjectionMatrix,
            vectorToLightInEyeSpace, pointPositionsInEyeSpace, pointLightColors)

        heightmap.bindData(heightmapProgram)
        heightmap.draw()
    }

    private fun drawSkybox() {
        Matrix.setIdentityM(modelMatrix, 0)
        updateMvpMatrixForSkybox()
//        Matrix.setIdentityM(viewMatrix, 0)
//        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
//        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)
//        Matrix.multiplyMM(viewProjectionMatrix, 0, mProjectionMatrix, 0, viewMatrix, 0)

        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        skyboxShaderProgram.useProgram()
        skyboxShaderProgram.setUniforms(modelViewProjectionMatrix, skyboxTexture)
        skyBox.bindData(skyboxShaderProgram)
        skyBox.draw()
        GLES20.glDepthFunc(GLES20.GL_LESS)
    }

    private fun drawParticles() {
        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f
        redParticleShooter.addParticles(particleSystem, currentTime, 5)
        greenParticleShooter.addParticles(particleSystem, currentTime, 5)
        blueParticleShooter.addParticles(particleSystem, currentTime, 5)

        Matrix.setIdentityM(modelMatrix, 0)
        updateMvpMatrix()
//        Matrix.setIdentityM(viewMatrix, 0)
//        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
//        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)
//        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f)
//        Matrix.multiplyMM(viewProjectionMatrix, 0, mProjectionMatrix, 0, viewMatrix, 0)

        GLES20.glDepthMask(false)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE)

        particleProgram.useProgram()
        particleProgram.setUniforms(modelViewProjectionMatrix, currentTime, particleTexture)
        particleSystem.bindData(particleProgram)
        particleSystem.draw()

        GLES20.glDisable(GLES20.GL_BLEND)
        GLES20.glDepthMask(true)
    }

    private var xRotation: Float = 0f
    private var yRotation: Float = 0f

    /**
     * 处理拖拽事件
     * @param deltaX x差
     * @param deltaY y差
     */
    fun handleTouchDrag(deltaX: Float, deltaY: Float) {
        xRotation += deltaX / 16f
        yRotation += deltaY / 16f

        if (yRotation < -90) {
            yRotation = -90f
        } else if (yRotation > 90) {
            yRotation = 90f
        }
        updateViewMatrices()
    }
}