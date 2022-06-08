package com.pzx.canteen.scan

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import com.pzx.canteen.R

/**
 * File Name : FinderView
 * Created by : PanZX on 2020/03/14
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 扫码框
 */
class FinderView : View {
    private var finderMaskPaint: Paint? = null
    private var measureedWidth = 0
    private var measureedHeight = 0

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(leftRect, finderMaskPaint!!)
        canvas.drawRect(topRect, finderMaskPaint!!)
        canvas.drawRect(rightRect, finderMaskPaint!!)
        canvas.drawRect(bottomRect, finderMaskPaint!!)

        //画框
        roi_box!!.bounds = middleRect
        roi_box!!.draw(canvas)

    }

    private val topRect = Rect()
    private val bottomRect = Rect()
    private val rightRect = Rect()
    private val leftRect = Rect()
    private val middleRect = Rect()
    private var roi_box: Drawable? = null
    private fun init(context: Context) {
        finderMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        finderMaskPaint!!.color = context.resources.getColor(R.color.finder_mask)
        roi_box = context.resources.getDrawable(R.drawable.roi_box)
        //scan_line = context.getResources().getDrawable(R.drawable.scan_line);
    }
    //////////////新增该方法//////////////////////
    /**
     * 根据图片size求出矩形框在图片所在位置，tip：相机旋转90度以后，拍摄的图片是横着的，所有传递参数时，做了交换
     * @param w
     * @param h
     * @return
     */
    fun getScanImageRect(w: Int, h: Int): Rect {
        //先求出实际矩形
        val rect = Rect()
        val tempw = w / measureedWidth.toFloat()
        val temph = h / measureedHeight.toFloat()
        rect.left = (middleRect.left * tempw).toInt()
        rect.right = (middleRect.right * tempw).toInt()
        rect.top = (middleRect.top * temph).toInt()
        rect.bottom = (middleRect.bottom * temph).toInt()
        return rect
    }

    ////////////////////////////////////
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureedWidth = MeasureSpec.getSize(widthMeasureSpec)
        measureedHeight = MeasureSpec.getSize(heightMeasureSpec)
        val borderWidth = measureedWidth / 2 + 160
        middleRect[(measureedWidth - borderWidth) / 2, (measureedHeight - borderWidth) / 2, (measureedWidth - borderWidth) / 2 + borderWidth] = (measureedHeight - borderWidth) / 2 + borderWidth
        //	lineRect.set(middleRect);
        //	lineRect.bottom = lineRect.top + lineHeight;
        leftRect[0, middleRect.top, middleRect.left] = middleRect.bottom
        topRect[0, 0, measureedWidth] = middleRect.top
        rightRect[middleRect.right, middleRect.top, measureedWidth] = middleRect.bottom
        bottomRect[0, middleRect.bottom, measureedWidth] = measureedHeight
    }
}