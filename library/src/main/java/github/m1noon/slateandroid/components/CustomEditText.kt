package github.m1noon.slateandroid.components

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.text.Spanned
import androidx.core.text.toSpanned


class CustomEditText : EditText, View.OnKeyListener {

    interface Listener {
        fun onSelectionChanged(selStart: Int, selEnd: Int)
        fun onDeleteKeyDown(): Boolean
        fun onInputFixed(s: Editable?)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        setOnKeyListener(this)
        addTextChangedListener(InputCompleteListener { listener?.onInputFixed(it) })
    }

    private var listener: Listener? = null

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        listener?.onSelectionChanged(selStart, selEnd)
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                KeyEvent.ACTION_DOWN -> {
                    when (event.keyCode) {
                        KeyEvent.KEYCODE_DEL -> {
                            return listener?.onDeleteKeyDown() ?: false
                        }
                    }
                }
            }
        }
        return false
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    private class InputCompleteListener(val listener: (s: Editable?) -> Unit) : TextWatcher {

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val fixed = s?.let { s ->
                val spanned = s.getSpans(0, s.length, Object::class.java)
                spanned.firstOrNull {
                    (s.getSpanFlags(it) and Spanned.SPAN_COMPOSING) == Spanned.SPAN_COMPOSING
                } == null
            } ?: false

            if (fixed) {
                listener(s)
                Log.i("CustomEditText", "Fixed: ")
            }
        }
    }
}