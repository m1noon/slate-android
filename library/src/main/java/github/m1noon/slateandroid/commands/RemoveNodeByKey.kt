package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.operations.Operation

/**
 * Remove a node by [key].
 */
data class RemoveNodeByKey(val key: String) : FunctionCommand {
    override fun execute(controller: IController) {
        val path = controller.getValue().document.getPathByKey(key)
        controller.command(RemoveNodeByPath(path))
    }
}
