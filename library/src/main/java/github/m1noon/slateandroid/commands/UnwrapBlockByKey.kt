package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.BlockNode
import github.m1noon.slateandroid.models.Data

data class UnwrapBlockByKey(
    val key: String,
    val type: BlockNode.Type? = null,
    val data: Data? = null
) : FunctionCommand {
    override fun execute(controller: IController) {
        val path = controller.getValue().document.getPathByKey(key)
        controller.command(UnwrapBlockByPath(path, type, data))
    }
}
