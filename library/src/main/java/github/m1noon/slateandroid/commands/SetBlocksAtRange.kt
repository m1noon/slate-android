package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.NodeProperty
import github.m1noon.slateandroid.models.Rangeable

data class SetBlocksAtRange(val range: Rangeable, val properties: NodeProperty) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()
        val blocks = value.document.getLeafBlocksAtRange(range)

        controller.withoutNormalizing { c ->
            blocks.forEach { n ->
                c.command(SetNodeByKey(n.key, properties))
            }
        }
    }
}