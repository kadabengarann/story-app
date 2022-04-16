package com.kadabengaran.storyapp.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.kadabengaran.storyapp.R

class MyEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var mClearIconButtonImage: Drawable
    var isEmailMode: Boolean = true

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
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        mClearIconButtonImage =
            ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24) as Drawable
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        isSingleLine = true
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()
            }

            override fun afterTextChanged(s: Editable) {
                validate()
            }
        })
        setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (check())
                    hideClearButton()
            } else if (check()) {
                error = null
                showClearButton()
            }
        }
    }

    private fun showClearButton() {
        setCompoundDrawablesWithIntrinsicBounds(
            null, null,
            mClearIconButtonImage, null
        )
    }

    private fun hideClearButton() {
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }

    private fun clearText() {
        when {
            text != null -> text?.clear()
        }
    }

    private fun validate() {
        if (isEmailMode) {
            when {
                isEmpty() -> {
                    error = context.getString(R.string.email_empty)
                }
                isInvalidEmail() ->
                    error = context.getString(R.string.email_invalid)
            }
        } else {
            when {
                isEmpty() ->
                    error = context.getString(R.string.input_empty)
            }
        }

    }

    private fun isInvalidEmail(): Boolean {
        return !Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }

    private fun isEmpty(): Boolean {
        return text.isNullOrEmpty()
    }

    fun check(): Boolean {
        return !(isEmpty() || isInvalidEmail())
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val toggleButtonStart: Float
            val toggleButtonEnd: Float
            var isToggleButtonClicked = false
            when (layoutDirection) {
                View.LAYOUT_DIRECTION_RTL -> {
                    toggleButtonEnd =
                        (mClearIconButtonImage.intrinsicWidth + paddingStart).toFloat()
                    when {
                        event.x < toggleButtonEnd -> isToggleButtonClicked = true
                    }
                }
                else -> {
                    toggleButtonStart =
                        (width - paddingEnd - mClearIconButtonImage.intrinsicWidth).toFloat()
                    when {
                        event.x > toggleButtonStart -> isToggleButtonClicked = true
                    }
                }
            }
            return if (isToggleButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (error == null)
                            showClearButton()
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (error == null)
                            clearText()
                        true
                    }
                    else -> false
                }
            } else false

        }
        return false
    }
}