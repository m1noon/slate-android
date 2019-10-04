package github.m1noon.slateandroid.components

import android.content.Context
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.BlockNode

interface BlockRenderer {
    fun createLeafComponent(
        context: Context,
        controller: IController,
        data: BlockNode.BlockRenderingData,
        text: String
    ): Component

    fun wrapBranchComponent(
        context: Context,
        controller: IController,
        blockType: BlockNode.Type,
        child: Component
    ): Component

    fun rerenderLeafComponent(
        context: Context,
        controller: IController,
        data: BlockNode.BlockRenderingData,
        text: String,
        component: Component
    ): Component
}


class BlockRenderers(
    val renderers: Map<BlockNode.Type, BlockRenderer> = mapOf(),
    val defaultRenderer: BlockRenderer = TextBlockRenderer()
) : BlockRenderer {

    override fun createLeafComponent(
        context: Context,
        controller: IController,
        data: BlockNode.BlockRenderingData,
        text: String
    ): Component {
        val renderer = renderers.get(data.type) ?: defaultRenderer
        return renderer.createLeafComponent(context, controller, data, text)
    }

    override fun rerenderLeafComponent(
        context: Context,
        controller: IController,
        data: BlockNode.BlockRenderingData,
        text: String,
        component: Component
    ): Component {
        val renderer = renderers.get(data.type) ?: defaultRenderer
        return renderer.rerenderLeafComponent(context, controller, data, text, component)
    }

    override fun wrapBranchComponent(
        context: Context,
        controller: IController,
        blockType: BlockNode.Type,
        child: Component
    ): Component {
        val renderer = renderers.get(blockType) ?: defaultRenderer
        return renderer.wrapBranchComponent(context, controller, blockType, child)
    }
}

val DefaultBlockRenderer = BlockRenderers()