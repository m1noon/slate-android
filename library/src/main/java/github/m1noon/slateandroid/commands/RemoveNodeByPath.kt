package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.operations.Operation

/**
 * Remove a node by [path].
 */
data class RemoveNodeByPath(val path: List<Int>) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.applyOperation(Operation.RemoveNode(path))
    }
}
