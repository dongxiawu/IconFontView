package com.dongxiawu.uikit

/**
 * @see [android.content.res.ComplexColor]
 * Defines an abstract class for the complex string information, like [IconFontCodeStateList]
 * @author wudongxia
 */
abstract class ComplexString {

    /**
     * @return `true`  if this ComplexString changes String based on state, `false`
     * otherwise.
     */
    open fun isStateful(): Boolean {
        return false
    }

    /**
     * @return the default String.
     */
    abstract fun getDefaultString(): String?

}