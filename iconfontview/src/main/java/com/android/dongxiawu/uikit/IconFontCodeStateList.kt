package com.android.dongxiawu.uikit

import android.content.res.Resources
import android.util.AttributeSet
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author wudongxia
 */
class IconFontCodeStateList : ComplexString {
    /**
     * 状态二维数组
     */
    private lateinit var states: Array<IntArray>

    /**
     * 不同状态的code列表
     */
    private lateinit var codes: Array<String>
    private var defaultCode: String? = null

    private constructor() {
        // Not publicly instantiable.
    }

    constructor(states: Array<IntArray>, codes: Array<String>) {
        this.states = states
        this.codes = codes
        onCodeChanged()
    }

    /**
     * Fill in this object based on the contents of an XML "selector" element.
     */
    @Throws(XmlPullParserException::class, IOException::class)
    private fun inflate(parser: XmlPullParser, attrs: AttributeSet) {
        val innerDepth = parser.depth + 1
        var defaultCode: String? = null
        val stateSpecList = ArrayList<IntArray>()
        val codeList= ArrayList<String>()
        var listSize = 0

        var depth: Int = innerDepth
        var type: Int
        while (parser.next().also { type = it } != XmlPullParser.END_DOCUMENT
            && (parser.depth.also { depth = it } >= innerDepth || type != XmlPullParser.END_TAG)) {
            if (type != XmlPullParser.START_TAG || depth > innerDepth || parser.name != TAG_ITEM) {
                continue
            }
            val a = Resources.getSystem().obtainAttributes(attrs, R.styleable.IconFontCodeStateListItem)
            val baseCode = a.getString(R.styleable.IconFontCodeStateListItem_code) ?: DEFAULT_CODE
            a.recycle()

            // Parse all unrecognized attributes as state specifiers.
            var j = 0
            val numAttrs = attrs.attributeCount
            var stateSpec = IntArray(numAttrs)
            for (i in 0 until numAttrs) {
                val stateResId = attrs.getAttributeNameResource(i)
                stateSpec[j++] =
                    if (attrs.getAttributeBooleanValue(i, false)) stateResId else -stateResId
            }
            stateSpec = StateSet.trimStateSet(stateSpec, j)
            if (listSize == 0 || stateSpec.isEmpty()) {
                defaultCode = baseCode
            }
            codeList.add(baseCode)
            stateSpecList.add(stateSpec)
            listSize++
        }
        this.defaultCode = defaultCode
        codes = Array(listSize) {i: Int -> codeList[i] }
        states = Array(listSize) {i -> stateSpecList[i] }
        onCodeChanged()
    }

    override fun isStateful(): Boolean {
        return states.isNotEmpty() && states[0].isNotEmpty()
    }

    fun hasFocusStateSpecified(): Boolean {
        return StateSet.containsAttribute(states, StateSet.VALUE_STATE_FOCUSED)
    }

    fun getCodeForState(stateSet: IntArray, defaultCode: String): String {
        for (i in states.indices) {
            val stateSpec = states[i]
            if (StateSet.stateSetMatches(stateSpec, stateSet)) {
                return codes[i]
            }
        }
        return defaultCode
    }

    /**
     * Returns whether the specified state is referenced in any of the state
     * specs contained within this ColorStateList.
     *
     *
     * Any reference, either positive or negative {ex. ~R.attr.state_enabled},
     * will cause this method to return `true`. Wildcards are not counted
     * as references.
     *
     * @param state the state to search for
     * @return `true` if the state if referenced, `false` otherwise
     * @hide Use only as directed. For internal use only.
     */
    fun hasState(state: Int): Boolean {
        val stateSpecs = states
        val specCount = stateSpecs.size
        for (specIndex in 0 until specCount) {
            val states = stateSpecs[specIndex]
            val stateCount = states.size
            for (stateIndex in 0 until stateCount) {
                if (states[stateIndex] == state || states[stateIndex] == state.inv()) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Updates the default code.
     */
    private fun onCodeChanged() {
        var defaultCode: String? = null
        val states = states
        val codes = codes
        val n = states.size
        if (n > 0) {
            defaultCode = codes[0]
            for (i in n - 1 downTo 1) {
                if (states[i].isEmpty()) {
                    defaultCode = codes[i]
                    break
                }
            }
        }
        this.defaultCode = defaultCode
    }

    override fun getDefaultString(): String? {
        return defaultCode
    }

    companion object {
        private const val TAG = "IconFontCodeStateList"
        private val EMPTY = arrayOf(IntArray(0))
        private const val DEFAULT_CODE = ""
        private const val TAG_SELECTOR = "selector"
        private const val TAG_ITEM = "item"


        /**
         * @return A ColorStateList containing a single color.
         */
        fun valueOf(code: String): IconFontCodeStateList {
            return IconFontCodeStateList(EMPTY, arrayOf(code))
        }

        @Throws(XmlPullParserException::class, IOException::class)
        fun createFromXml(parser: XmlPullParser): IconFontCodeStateList {
            val attrs = Xml.asAttributeSet(parser)
            var type: Int
            while (parser.next().also { type = it } != XmlPullParser.START_TAG
                && type != XmlPullParser.END_DOCUMENT
            ) {
                // Seek parser to start tag.
            }
            if (type != XmlPullParser.START_TAG) {
                throw XmlPullParserException("No start tag found")
            }
            return createFromXmlInner(parser, attrs)
        }

        @Throws(XmlPullParserException::class, IOException::class)
        private fun createFromXmlInner(parser: XmlPullParser, attrs: AttributeSet): IconFontCodeStateList {
            val name = parser.name
            if (name != TAG_SELECTOR) {
                throw XmlPullParserException("${parser.positionDescription}: invalid icon font code state list tag $name")
            }
            val iconFontCodeStateList = IconFontCodeStateList()
            iconFontCodeStateList.inflate(parser, attrs)
            return iconFontCodeStateList
        }
    }
}