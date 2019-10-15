package github.m1noon.slateandroid.components

import android.content.Context
import github.m1noon.slateandroid.R
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.BlockNode
import github.m1noon.slateandroid.models.BlockNodeType

abstract class BaseTextBlockRenderer : BlockRenderer {

    override fun createLeafComponent(
        context: Context,
        controller: IController,
        data: BlockNode.BlockRenderingData,
        text: String
    ): Component {
        return TextBlockComponent(context, controller, data, false).also { c ->
            c.bindText(text)
            getTextStyle(data.type)?.let { c.applyTextAppearance(it) }
            getIndicatorText(data.type, controller, c)?.let { c.bindIndicator(it) }
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
                getTextStyle(data.type)?.let { c.applyTextAppearance(it) }
                getIndicatorText(data.type, controller, component)?.let { c.bindIndicator(it) }
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
        if (child is TextBlockComponent) {
            getIndicatorText(blockType, controller, child)?.let { child.bindIndicator(it) }
        }
        return child
    }

    protected open fun getTextStyle(type: BlockNode.Type): Int? {
        return when (type) {
            BlockNodeType.PARAGRAPH -> R.style.TextAppearance_SlateAndroid_Paragraph
            else -> null
        }
    }

    protected open fun getIndicatorText(
        type: BlockNode.Type,
        controller: IController,
        component: Component
    ): String? {
        return when (type) {
            BlockNodeType.PARAGRAPH -> ""
            else -> null
        }
    }
}