package github.m1noon.slateandroid.components

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.EditText

class CustomEditText : EditText, View.OnKeyListener {

    interface Listener {
        fun onSelectionChanged(selStart: Int, selEnd: Int)
        fun onDeleteKeyDown(): Boolean
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
}