package com.dongxiawu.uikit

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Nullable
import androidx.annotation.StyleableRes
import com.android.dongxiawu.uikit.R
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/**
 * 
 * @date 2020/10/27
 * @author wudongxia
 */
class IconFontView: View {

    private var mIconFontCodeStateList: IconFontCodeStateList
    private var mColorStateList: ColorStateList
    
    private lateinit var mPaint: Paint

    /**
     * 当前View的字体库，支持每一个IconFontView字体库不一样
     */
    private var mIconFontTypeface: Typeface? = null

    constructor(context: Context): this(context, null)
    constructor(context: Context, @Nullable attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initDefaultTypeface(context)
        val a = context.obtainStyledAttributes(attrs,
            R.styleable.IconFontView
        )
        mColorStateList = a.getColorStateList(R.styleable.IconFontView_icon_font_color)
            ?: ColorStateList.valueOf(a.getColor(
                R.styleable.IconFontView_icon_font_color,
                DEFAULT_COLOR
            ))
        mIconFontCodeStateList = a.getIconFontCodeStateList(R.styleable.IconFontView_icon_font_code)
            ?: IconFontCodeStateList.valueOf(a.getString(R.styleable.IconFontView_icon_font_code) ?: DEFAULT_CODE)
        a.recycle()
    }

    fun setColorStateList(colorStateList: ColorStateList) {
        this.mColorStateList = colorStateList
        invalidate()
    }

    fun setColor(@ColorInt color: Int) {
        this.mColorStateList = ColorStateList.valueOf(color)
        invalidate()
    }
    fun setIconFontCodeStateList(iconFontCodeStateList: IconFontCodeStateList) {
        this.mIconFontCodeStateList = iconFontCodeStateList
            invalidate()
    }

    fun setIconFontCode(code: String) {
        this.mIconFontCodeStateList = IconFontCodeStateList.valueOf(code)
        invalidate()
    }

    fun setIconFontTypeface(typeface: Typeface) {
        this.mIconFontTypeface = typeface
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = this.getPaint()
        paint.color = this.mColorStateList.getColorForState(drawableState,
            DEFAULT_COLOR
        )
        val iconFontCode = mIconFontCodeStateList.getCodeForState(drawableState,
            DEFAULT_CODE
        )
        val left = paddingLeft
        val right = width - paddingRight
        val top = paddingTop
        val bottom = height - paddingBottom
        val width = right - left
        val height = bottom - top
        paint.textSize = width.coerceAtMost(height).toFloat()
        val centerX = (left + right).toFloat()/2
        val centerY = (top + bottom).toFloat()/2
        val baseLineY = centerY - paint.fontMetrics.top/2 - paint.fontMetrics.bottom/2
        canvas.drawText(iconFontCode, centerX, baseLineY, this.mPaint)
    }

    private fun getPaint(): Paint {
        if (this::mPaint.isInitialized.not()) {
            this.mPaint = Paint()
            this.mPaint.isAntiAlias = true
            this.mPaint.textAlign = Paint.Align.CENTER
        }
        val typeface = if (mIconFontTypeface == null) {
            sIconFontTypeface
        } else { mIconFontTypeface }
        this.mPaint.typeface = typeface
        return this.mPaint
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (mColorStateList.isStateful
            || mIconFontCodeStateList.isStateful()) {
            invalidate()
        }
    }

    companion object {
        private const val TAG = "IconFontView"
        private const val DEFAULT_COLOR = Color.BLACK
        private const val DEFAULT_CODE = ""
        private lateinit var sIconFontTypeface: Typeface
        private const val DEFAULT_TYPEFACE_FILE = "4B9C87F8981C89E7134F151D95C.ttf"

        private fun initDefaultTypeface(context: Context) {
            if (Companion::sIconFontTypeface.isInitialized.not()) {
                setDefaultTypeface(
                    Typeface.createFromAsset(
                        context.applicationContext.assets,
                        DEFAULT_TYPEFACE_FILE
                    )
                )
            } else {
                Log.d(TAG, "default typeface has been set")
            }
        }

        /**
         * 设置默认字体库，支持外部手动设置字体库
         * 外部设置后可能需要手动刷新已经显示的 IconFontView
         */
        @JvmStatic
        fun setDefaultTypeface(typeface: Typeface) {
            sIconFontTypeface = typeface
        }
    }
}

private fun TypedArray.getIconFontCodeStateList(@StyleableRes index: Int) : IconFontCodeStateList? {
    try {
        val id: Int = getResourceId(index, 0)
        if (id != 0) {
            val parser: XmlPullParser = resources.getXml(id)
            return IconFontCodeStateList.createFromXml(parser)
        }
    } catch (e: XmlPullParserException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}