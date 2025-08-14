package com.dunn.demo.utils

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Executes [block] with a typed version of the View's layoutParams and reassigns the
 * layoutParams with the updated version.
 *
 * @see View.getLayoutParams
 * @see View.setLayoutParams
 **/
@JvmName("updateLayoutParamsTyped")
public inline fun <reified T : ViewGroup.LayoutParams> View.updateLayoutParams(
    block: T.() -> Unit
) {
    val params = layoutParams as T
    block(params)
    layoutParams = params
}

inline val <T : ViewGroup> T.wrapContent: Int
    get() = ViewGroup.LayoutParams.WRAP_CONTENT

inline val <T : ViewGroup> T.matchParent: Int
    get() = ViewGroup.LayoutParams.MATCH_PARENT

inline val <T : ConstraintLayout> T.matchConstraint: Int
    get() = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT

inline val ConstraintLayout.LayoutParams.parentId: Int
    get() = ConstraintLayout.LayoutParams.PARENT_ID

inline val <T : ConstraintLayout> T.unSet: Int
    get() = ConstraintLayout.LayoutParams.UNSET

 fun View.setDebounceClickListener(
    debounceTime: Long = 1000L,
     action: () -> Unit
) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (System.currentTimeMillis() - lastClickTime >= debounceTime) {
                lastClickTime = System.currentTimeMillis()
                action()
            }
        }
    })
}