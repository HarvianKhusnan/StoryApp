package com.example.storyapp.customview

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.storyapp.R

class EmailCustomView : AppCompatEditText {
    private val messageError = MutableLiveData<String>()
    private val errorHidding = MutableLiveData<Boolean>()
    private lateinit var Icon: Drawable

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = context.getString(R.string.mail_hint)
    }

    private fun init(){
        Icon = ContextCompat.getDrawable(context, R.drawable.mail_ic) as Drawable
        onShow(Icon)
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        doAfterTextChanged { text ->
            if(text?.isNullOrEmpty() == true){
                setError(context.getString(R.string.mail_msg))
            }else{
                if(!Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                    setError(context.getString(R.string.mail_msg_error))
                }else{
                    hiddingError()
                }
            }
        }
    }

    private fun onShow(icon: Drawable) {
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
    private fun hiddingError(){
        errorHidding.value = true
    }

    private fun setError(message: String){
        messageError.value = message
    }

    fun validateData(activity: Activity, hiddingError: () -> Unit, setError : (message: String) ->Unit){
        errorHidding.observe(activity as LifecycleOwner){
            hiddingError()
        }
        messageError.observe(activity as LifecycleOwner) {
            setError(it)
        }
    }

}