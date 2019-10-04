package github.m1noon.slateandroid.components

import android.view.View
import androidx.annotation.StyleRes
import github.m1noon.slateandroid.models.BlockNode

interface Component {
    fun view(): View
    fun data(): BlockNode.BlockRenderingData
    fun setSyncState(start: Boolean)
    fun applyTextAppearance(@StyleRes styleRes: Int)
    fun bindText(text: String)

    /**
     * set up selection of [EditText] by current value of controller
     */
    fun syncSelection()
}