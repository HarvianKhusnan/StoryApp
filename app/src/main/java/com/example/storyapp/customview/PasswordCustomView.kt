package com.example.storyapp.customview

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.R
import org.w3c.dom.Text

class PasswordCustomView : AppCompatEditText {

    private val messageError = MutableLiveData<String>()
    private val errorHiding = MutableLiveData<Boolean>()
    private lateinit var icon: Drawable

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context,attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }
    override fun onDraw(canvas: Canvas?){
        super.onDraw(canvas)
        hint = context.getString(R.string.hint_string)
    }

    private fun init(){
        icon = ContextCompat.getDrawable(context, R.drawable.baseline_key_24) as Drawable
        onShow(icon)
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(text.toString().length < 8){
                    error = resources.getString(R.string.password_length)
                }else{
                    error = null
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun onShow(icon: Drawable){
        ButtonDrawables(startOfText = icon)
    }
    private fun ButtonDrawables(
        startOfText: Drawable? = null,
        topOfText: Drawable? = null,
        endOfText: Drawable? = null,
        bottomOfText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfText,
            topOfText,
            endOfText,
            bottomOfText
        )
    }

     fun validateData(activity: Activity, closeMessage: () -> Unit, setError: (message: String) -> Unit){
        errorHiding.observe(activity as LifecycleOwner) {
            closeMessage()
        }
        messageError.observe(activity as LifecycleOwner){
            setError(it)
        }
    }


}