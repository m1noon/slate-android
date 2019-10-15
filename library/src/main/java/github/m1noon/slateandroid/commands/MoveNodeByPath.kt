package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.operations.Operation

/**
 * Move a node by [path] to a new parent by [newParentPath] and [newIndex].
 */
data class MoveNodeByPath(val path: List<Int>, val newParentPath: List<Int>, val newIndex: Int) :
    FunctionCommand {
    override fun execute(controller: IController) {
        if (path == newParentPath) {
            return
        }

        val newPath = newParentPath.plus(newIndex)
        if (path == newPath) {
            return
        }

        controller.applyOperation(
            Operation.MoveNode(path, newParentPath, newIndex)
        )
    }
}