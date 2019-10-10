package github.m1noon.slateandroid.components

import android.content.Context
import github.m1noon.slateandroid.R
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.BlockNode
import github.m1noon.slateandroid.models.BlockNodeType

class TextBlockRenderer : BlockRenderer {
    override fun createLeafComponent(
        context: Context,
        controller: IController,
        data: BlockNode.BlockRenderingData,
        text: String
    ): Component {
        return TextBlockComponent(context, controller, data, false).also { c ->
            c.bindText(text)
            getTextStyle(data.type)?.let {
                c.applyTextAppearance(it)
            }
            c.setSyncState(true)
        }
    }

    override fun rerenderLeafComponent(
        context: Context,
        controller: IController,
        data: BlockNode.BlockRenderingData,
        text: String,
        forceUpdateText: Boolean,
        component: Component
    ): Component {
        if (component is TextBlockComponent) {
            return component.also { c ->
                c.setSyncState(false)
                c.bindBlockData(data)
                c.bindText(text, forceUpdateText)
                getTextStyle(data.type)?.let {
                    c.applyTextAppearance(it)
                }
                c.setSyncState(true)
            }
        } else {
            return createLeafComponent(context, controller, data, text)
        }
    }

    override fun wrapBranchComponent(
        context: Context,
        controller: IController,
        blockType: BlockNode.Type,
        child: Component
    ): Component {
        getTextStyle(blockType)?.let { child.applyTextAppearance(it) }
        return child
    }

    private fun getTextStyle(type: BlockNode.Type): Int? {
        return when (type) {
            BlockNodeType.HEADING_1 -> R.style.TextAppearance_SlateAndroid_Heading1
            else -> null
        }
    }
}