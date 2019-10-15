package github.m1noon.slateandroid.components

import github.m1noon.slateandroid.R
import github.m1noon.slateandroid.models.BlockNode
import github.m1noon.slateandroid.models.BlockNodeType

class TextBlockRenderer : BaseTextBlockRenderer() {

    override fun getTextStyle(type: BlockNode.Type): Int? {
        return when (type) {
            BlockNodeType.HEADING_1 -> R.style.TextAppearance_SlateAndroid_Heading1
            BlockNodeType.HEADING_2 -> R.style.TextAppearance_SlateAndroid_Heading2
            BlockNodeType.HEADING_3 -> R.style.TextAppearance_SlateAndroid_Heading3
            else -> super.getTextStyle(type)
        }
    }
}