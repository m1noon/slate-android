package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Node
import github.m1noon.slateandroid.operations.Operation

/**
 * Insert a [node] at [index] in a node by [key].
 */
data class InsertNodeByKey(
    val key: String,
    val index: Int,
    val node: Node
) : FunctionCommand {
    override fun execute(controller: IController) {
        val path: List<Int> = controller.getValue().document.getPathByKey(key)
        controller.command(InsertNodeByPath(path, index, node))
    }
}
