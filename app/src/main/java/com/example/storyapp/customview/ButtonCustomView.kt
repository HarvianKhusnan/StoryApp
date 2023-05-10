package com.example.storyapp.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.storyapp.R

class ButtonCustomView : AppCompatButton {
    private lateinit var backgroundEnabled : Drawable
    private lateinit var backgroundDisabled : Drawable
    private var color : Int = 0
    private var outlined = false

    constructor(context: Context) : super(context){
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(attrs, defStyleAttr)
    }

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)
        background = if(isEnabled) backgroundEnabled else backgroundDisabled
        setTextColor(color)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int = 0){
        val get = context.obtainStyledAttributes(attrs, R.styleable.Button, defStyleAttr, 0)

        outlined = get.getBoolean(R.styleable.EditText_email, false)

        color = ContextCompat.getColor(
            context, if(outlined) R.color.pink_red else android.R.color.background_light
        )
        backgroundEnabled =
            ContextCompat.getDrawable(
                context, if(outlined) R.drawable.button_bg_outline else R.drawable.button_bg_pink
            ) as Drawable
        backgroundDisabled =
            ContextCompat.getDrawable(context, R.drawable.button_bg_pink_disabled) as Drawable

        get.recycle()
    }
}