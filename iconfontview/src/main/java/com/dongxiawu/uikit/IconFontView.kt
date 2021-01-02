package com.dongxiawu.uikit

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
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
import org.xmlpull.v1.XmlPullParser

/**
 * IconFontView can draw little icon from typeface
 * @date 2020/10/27
 * @author wudongxia
 */
class IconFontView : View {

    /**
     * 保存不同状态下的图标码
     */
    private var mIconFontCodeStateList: IconFontCodeStateList

    /**
     * 保存不同状态下的颜色
     */
    private var mColorStateList: ColorStateList

    /**
     * 画笔
     */
    private lateinit var mPaint: Paint

    /**
     * 当前View的字体库，支持每一个IconFontView字体库不一样
     */
    private var mIconFontTypeface: Typeface? = null


    constructor(context: Context) : this(context, null)
    constructor(context: Context, @Nullable attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {

        checkAndInitGlobalTypeface(context)
        val a = context.obtainStyledAttributes(attrs, R.styleable.IconFontView)
        mColorStateList = a.getColorStateList(R.styleable.IconFontView_icon_font_color)
            ?: ColorStateList.valueOf(
                a.getColor(R.styleable.IconFontView_icon_font_color, DEFAULT_COLOR)
            )
        mIconFontCodeStateList = a.getIconFontCodeStateList(R.styleable.IconFontView_icon_font_code)
            ?: IconFontCodeStateList.valueOf(
                a.getString(R.styleable.IconFontView_icon_font_code) ?: DEFAULT_CODE
            )
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

    /**
     * 设置字体库，支持每个[IconFontView]不一样
     */
    fun setIconFontTypeface(typeface: Typeface) {
        this.mIconFontTypeface = typeface
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = this.getPaint()
        // 设置画笔颜色
        paint.color = this.mColorStateList.getColorForState(
            drawableState,
            DEFAULT_COLOR
        )
        // 设置图标 code
        val iconFontCode = mIconFontCodeStateList.getCodeForState(
            drawableState,
            DEFAULT_CODE
        )
        // 计算位置
        val left = paddingLeft
        val right = width - paddingRight
        val top = paddingTop
        val bottom = height - paddingBottom
        val width = right - left
        val height = bottom - top
        // 计算图标大小
        paint.textSize = width.coerceAtMost(height).toFloat()
        val centerX = (left + right).toFloat() / 2
        val centerY = (top + bottom).toFloat() / 2
        val baseLineY = centerY - paint.fontMetrics.top / 2 - paint.fontMetrics.bottom / 2
        // 居中绘制
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
        } else {
            mIconFontTypeface
        }
        this.mPaint.typeface = typeface
        return this.mPaint
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (mColorStateList.isStateful || mIconFontCodeStateList.isStateful()) {
            invalidate()
        }
    }

    companion object {
        private const val TAG = "IconFontView"
        private const val DEFAULT_COLOR = Color.BLACK
        private const val DEFAULT_CODE = ""

        /**
         * 全局 IconFont，
         */
        private lateinit var sIconFontTypeface: Typeface
        private const val DEFAULT_TYPEFACE_FILE = "4B9C87F8981C89E7134F151D95C.ttf"

        /**
         * 加载默认全局字体库，默认字体库名称为 [com.dongxiawu.uikit.IconFontView.DEFAULT_TYPEFACE_FILE]
         * 如果需要自定义默认字体库的名称，则需要在使用 IconFontView 前调用 [com.dongxiawu.uikit.IconFontView.setGlobalTypeface] 手动设置字体库
         */
        private fun checkAndInitGlobalTypeface(context: Context) {
            if (Companion::sIconFontTypeface.isInitialized.not()) {
                if (isAssetsFileExist(context, DEFAULT_TYPEFACE_FILE)) {
                    setGlobalTypeface(
                        Typeface.createFromAsset(
                            context.applicationContext.assets,
                            DEFAULT_TYPEFACE_FILE
                        )
                    )
                } else {
                    throw RuntimeException(
                        "can not find typeface file named $DEFAULT_TYPEFACE_FILE" +
                                ", and typeface has not been assigned manually"
                    )
                }
            } else {
                Log.d(TAG, "default typeface has been assigned")
            }
        }

        /**
         * 判断资源文件是否存在
         */
        private fun isAssetsFileExist(context: Context, fileName: String): Boolean {
            val filePaths = context.applicationContext.assets.list("") ?: emptyArray()
            return filePaths.any { it == fileName }
        }

        /**
         *
         * 设置默认全局字体库，支持外部手动设置字体库
         * 外部设置后可能需要调用 [IconFontView.invalidate] 手动刷新已经显示的 IconFontView
         */
        @JvmStatic
        fun setGlobalTypeface(typeface: Typeface) {
            sIconFontTypeface = typeface
        }
    }
}

/**
 * TypedArray 函数扩展
 * parse [com.dongxiawu.uikit.IconFontCodeStateList] from xml file
 */
private fun TypedArray.getIconFontCodeStateList(@StyleableRes index: Int): IconFontCodeStateList? {
    try {
        val id: Int = getResourceId(index, 0)
        if (id != 0) {
            val parser: XmlPullParser = resources.getXml(id)
            return IconFontCodeStateList.createFromXml(parser)
        }
    } catch (e: Resources.NotFoundException) {
        Log.e("TypedArray", "can not find icon font code state resource", e)
    }
    return null
}