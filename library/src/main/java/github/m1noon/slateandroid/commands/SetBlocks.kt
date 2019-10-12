package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.NodeProperty

data class SetBlocks(val properties: NodeProperty) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()

        val blocks = value.document.getLeafBlocksAtRange(value.selection)
        if (blocks.isNotEmpty()) {
            // update by range if current selection has block
            controller.command(SetBlocksAtRange(value.selection, properties))
        } else {
            // FIXME is it ok?
            // update parent block
            value.document.getClosestBlock(value.selection.start().path)?.let { block ->
                controller.command(SetNodeByKey(block.key, properties))
            }
        }
    }
}
