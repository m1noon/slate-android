package github.m1noon.slate_android_component_image

import android.content.Context
import github.m1noon.slateandroid.components.BlockComponent
import github.m1noon.slateandroid.components.BlockRenderer
import github.m1noon.slateandroid.components.Component
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.BlockNode

class ImageBlockRenderer : BlockRenderer {
    override fun createLeafComponent(
        context: Context,
        controller: IController,
        data: BlockNode.BlockRenderingData,
        text: String
    ): Component {
        return ImageBlockComponent(
            context,
            controller,
            data
        ).also {
            it.bindBlockData(data)
        }
    }

    override fun wrapBranchComponent(
        context: Context,
        controller: IController,
        blockType: BlockNode.Type,
        child: Component
    ): Component {
        return child
    }

    override fun rerenderLeafComponent(
        context: Context,
        controller: IController,
        data: BlockNode.BlockRenderingData,
        text: String,
        forceUpdateText: Boolean,
        component: Component
    ): Component {
        return if (component is ImageBlockComponent) {
            component.also {
                it.bindBlockData(data)
            }
        } else createLeafComponent(context, controller, data, text)
    }
}