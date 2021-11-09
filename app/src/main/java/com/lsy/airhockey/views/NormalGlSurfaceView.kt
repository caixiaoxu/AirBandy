package com.lsy.airhockey.views

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.lsy.airhockey.render.*

/**
 * @author Xuwl
 * @date 2021/10/20
 *
 */
class NormalGlSurfaceView(context: Context?, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {
    val mRender: BaseRenderer


    init {
        setEGLContextClientVersion(2)
//        mRender = NormalRenderer(resources)
//        mRender = MatrixRenderer(resources)
//        mRender = CoordinatesRender(resources)
//        mRender = AirHockeyRender(resources)
        mRender = ParticlesRender(resources)
        setRenderer(mRender)
//        renderMode = RENDERMODE_WHEN_DIRTY
        renderMode = RENDERMODE_CONTINUOUSLY
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        return event?.let {
//            //将触摸坐标转换为标准化设备坐标，记住Android的Y坐标是反向的
//            val normalizedX = (event.x / width.toFloat()) * 2 - 1
//            val normalizedY = -((event.y / height.toFloat()) * 2 - 1)
//
//            //事件处理
//            if (MotionEvent.ACTION_DOWN == event.action) {
//                queueEvent {
//                    (mRender as AirHockeyRender).handleTouchPress(normalizedX,
//                        normalizedY)
//                }
//            } else if (MotionEvent.ACTION_MOVE == event.action) {
//                queueEvent {
//                    (mRender as AirHockeyRender).handleTouchDrag(normalizedX, normalizedY) {
//                        requestRender()
//                    }
//                }
//            }
//            true
//        } ?: false
//    }

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean =
        event?.let {
            if (MotionEvent.ACTION_DOWN == event.action) {
                previousX = event.x
                previousY = event.y
            } else if (MotionEvent.ACTION_MOVE == event.action) {
                val deltaX = event.x - previousX
                val deltaY = event.y - previousY

                previousX = event.x
                previousY = event.y

                queueEvent {
                    (mRender as ParticlesRender).handleTouchDrag(deltaX, deltaY)
                }
            }
            true
        } ?: false

}