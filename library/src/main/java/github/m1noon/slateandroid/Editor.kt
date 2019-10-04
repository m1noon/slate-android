package github.m1noon.slateandroid

import android.content.Context
import android.util.AttributeSet

class Editor : EditorCore {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        orientation = VERTICAL
    }
}