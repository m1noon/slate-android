package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Node
import github.m1noon.slateandroid.operations.Operation

/**
 * Insert a [node] at [index] in a node by [path].
 */
data class InsertNodeByPath(
    val path: List<Int>,
    val index: Int,
    val node: Node
) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.applyOperation(Operation.InsertNode(path.plus(index), node))
    }
}
