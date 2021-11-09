package com.lsy.airhockey.render

import android.content.res.Resources
import android.opengl.Matrix
import com.lsy.airhockey.R
import com.lsy.airhockey.shape.Geometry
import com.lsy.airhockey.shape.Mallet1
import com.lsy.airhockey.shape.Puck
import com.lsy.airhockey.shape.Table4
import com.lsy.airhockey.shape.programs.ColorShaderProgram
import com.lsy.airhockey.shape.programs.TextureShaderProgram
import com.lsy.airhockey.utils.LogUtil
import com.lsy.airhockey.utils.MatrixUtil
import com.lsy.airhockey.utils.TextureUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min

/**
 * 纹理Renderer
 * @author Xuwl
 * @date 2021/10/20
 */
class AirHockeyRender(res: Resources) : BaseRenderer(res) {
    private val leftBound = -0.5f
    private val rightBound = 0.5f
    private val farBound = -0.8f
    private val nearBound = 0.8f

    private lateinit var mTable: Table4
    private lateinit var mMallet: Mallet1
    private lateinit var mPuck: Puck

    private lateinit var mTextureProgram: TextureShaderProgram
    private lateinit var mColorProgram: ColorShaderProgram

    private var mTexture: Int = 0

    private lateinit var previousBlueMalletPosition: Geometry.Point
    private lateinit var puckPosition: Geometry.Point
    private lateinit var puckVector: Geometry.Vector

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        mTable = Table4()
        mMallet = Mallet1(0.08f, 0.15f, 32)
        mPuck = Puck(0.06f, 0.02f, 32)

        mTextureProgram = TextureShaderProgram(res)
        mColorProgram = ColorShaderProgram(res)

        mTexture = TextureUtil.loadTexture(res, R.drawable.air_hockey_surface)

        blueMallectPosition = Geometry.Point(0f, mMallet.height / 2f, 0.4f)
        puckPosition = Geometry.Point(0f, mPuck.height / 2, 0f)
        puckVector = Geometry.Vector(0f, 0f, 0f)
    }

    //视角矩阵
    protected val viewMatrix = FloatArray(16)
    protected val viewProjectionMatrix = FloatArray(16)
    protected val modelViewProjectionMatrix = FloatArray(16)

    protected val invertedViewProjectionMatrix = FloatArray(16)

    /**
     * 重写矩阵
     */
    override fun initMatrix(width: Int, height: Int) {
        val aspectRatio = width.toFloat() / height.toFloat()
        MatrixUtil.perspectiveM(mProjectionMatrix, 45f, aspectRatio, 1f, 10f)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        Matrix.multiplyMM(viewProjectionMatrix, 0, mProjectionMatrix, 0, viewMatrix, 0)
        //反转矩阵
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0)

        //绘制桌面纹理
        positionTableInScene()
        mTextureProgram.useProgram()
        mTextureProgram.setUniforms(modelViewProjectionMatrix, mTexture)
        mTable.bindData(mTextureProgram)
        mTable.draw()

        //绘制木槌
        positionObjectInScene(0f, mMallet.height / 2f, -0.4f)
        mColorProgram.useProgram()
        mColorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f)
        mMallet.bindData(mColorProgram)
        mMallet.draw()

//        positionObjectInScene(0f, mMallet.height / 2f, 0.4f)
        positionObjectInScene(blueMallectPosition.x,
            blueMallectPosition.y, blueMallectPosition.z)
        mColorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        mMallet.draw()

        //冰球
        //修改冰球的位置
        puckPosition = puckPosition.translate(puckVector)
        //左右边界,到边界就反向向量
        if (puckPosition.x < leftBound + mPuck.radius || puckPosition.x > rightBound - mPuck.radius) {
            puckVector = Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z)
            //撞击减速
            puckVector = puckVector.scale(0.9f)
        }
        //前后边界,到边界就反向向量
        if (puckPosition.z < farBound + mPuck.radius || puckPosition.z > nearBound - mPuck.radius) {
            puckVector = Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z)
            puckVector = puckVector.scale(0.9f)
        }
        //过滤边界
        puckPosition = Geometry.Point(
            clamp(puckPosition.x, leftBound + mPuck.radius, rightBound - mPuck.radius),
            puckPosition.y,
            clamp(puckPosition.z, farBound + mPuck.radius, nearBound - mPuck.radius))

        //摩擦减速
        puckVector = puckVector.scale(0.99f)

//        positionObjectInScene(0f, mPuck.height / 2f, 0f)
        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z)
        mColorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f)
        mPuck.bindData(mColorProgram)
        mPuck.draw()
    }

    /**
     * 桌面矩阵
     */
    private fun positionTableInScene() {
        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

    /**
     * 更新位置点
     * @param x x坐标
     * @param y y坐标
     * @param z z坐标
     */
    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

    private var malletPressed = false
    private lateinit var blueMallectPosition: Geometry.Point

    /**
     * 处理按下触摸事件
     */
    fun handleTouchPress(normalizedX: Float, normalizedY: Float) {
        LogUtil.logE("按下事件,位置:$normalizedX,$normalizedY")
        val ray = convertNormalized2DPointToRay(normalizedX, normalizedY)

        val malletBoundingSphere = Geometry.Sphere(Geometry.Point(
            blueMallectPosition.x, blueMallectPosition.y, blueMallectPosition.z),
            mMallet.height / 2f)

        malletPressed = Geometry.intersects(malletBoundingSphere, ray)
        LogUtil.logE("是否按到了木槌:$malletPressed")
    }

    /**
     * 转换屏幕2D点为三维射线
     */
    private fun convertNormalized2DPointToRay(
        normalizedX: Float, normalizedY: Float,
    ): Geometry.Ray {
        val nearPointNdc = floatArrayOf(normalizedX, normalizedY, -1f, 1f)
        val farPointNdc = floatArrayOf(normalizedX, normalizedY, 1f, 1f)

        val nearPointWorld = FloatArray(4)
        val farPointWorld = FloatArray(4)
        //通过反转矩阵得到最近和最远两个点的向量
        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0)
        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0)

        //撤销透视除法
        divideByW(nearPointWorld)
        divideByW(farPointWorld)

        //最近点
        val nearPointRay = Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2])
        //最远点
        val farPointRay = Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2])
        return Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay))
    }

    /**
     * 撤销透视除法
     * @param vector 单条向量
     */
    private fun divideByW(vector: FloatArray) {
        vector[0] /= vector[3]
        vector[1] /= vector[3]
        vector[2] /= vector[3]
    }

    /**
     * 处理拖拽触摸事件
     */
    fun handleTouchDrag(normalizedX: Float, normalizedY: Float, callback: () -> Unit) {
        LogUtil.logE("拖拽事件,位置:$normalizedX,$normalizedY")
        if (malletPressed) {
            //转换成射线
            val ray = convertNormalized2DPointToRay(normalizedX, normalizedY)
            val plane = Geometry.Plane(Geometry.Point(0f, 0f, 0f), Geometry.Vector(0f, 1f, 0f))
            //与桌面相交点
            val touchedPoint = Geometry.intersectionPoint(ray, plane)
            previousBlueMalletPosition = blueMallectPosition
            //修改新的位置
            blueMallectPosition =
                Geometry.Point(
                    clamp(touchedPoint.x, leftBound + mMallet.radius, rightBound - mMallet.radius),
                    mMallet.height / 2f,
                    clamp(touchedPoint.z, 0f + mMallet.radius, nearBound - mMallet.radius))
            val distance = Geometry.vectorBetween(blueMallectPosition, puckPosition).length()
            LogUtil.logE("与冰球的距离:$distance")
            if (distance < (mPuck.radius + mMallet.radius)) {
                LogUtil.logE("撞击冰球")
                puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMallectPosition)
            }
            callback.invoke()
        }
    }

    /**
     * 过滤边界
     * @param value 当前值
     * @param min 最小值
     * @param max 最大值
     */
    private fun clamp(value: Float, min: Float, max: Float): Float = min(max, max(value, min))
}