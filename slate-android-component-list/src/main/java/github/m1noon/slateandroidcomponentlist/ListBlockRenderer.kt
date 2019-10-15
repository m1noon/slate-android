package github.m1noon.slateandroidcomponentlist

import github.m1noon.slateandroid.R
import github.m1noon.slateandroid.components.BaseTextBlockRenderer
import github.m1noon.slateandroid.components.Component
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.BlockNode

class ListBlockRenderer : BaseTextBlockRenderer() {
    companion object {
        const val UL_BULLET = "\u2022"
    }

    override fun getTextStyle(type: BlockNode.Type): Int? {
        return when (type) {
            ListBlockNodeType.LI -> R.style.TextAppearance_SlateAndroid_Paragraph
            else -> super.getTextStyle(type)
        }
    }

    override fun getIndicatorText(
        type: BlockNode.Type,
        controller: IController,
        component: Component
    ): String? {
        return when (type) {
            ListBlockNodeType.UL -> UL_BULLET
            ListBlockNodeType.OL -> "${controller.getValue().document.getPathByKey(component.data().key).last() + 1}."
            ListBlockNodeType.LI -> ""
            else -> super.getIndicatorText(type, controller, component)
        }
    }
}