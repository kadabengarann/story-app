package com.kadabengaran.storyapp.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.kadabengaran.storyapp.R

class MySecondaryButton : AppCompatButton {
    private var enabledBackground: Drawable? = null
    private var disabledBackground: Drawable? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = when {
            isEnabled -> enabledBackground
            else -> disabledBackground
        }
        gravity = Gravity.CENTER
    }

    private fun init() {
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_secondary)
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.action_button_disabled)
    }
}