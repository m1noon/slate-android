package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.NodeProperty
import github.m1noon.slateandroid.operations.Operation

data class SplitNodeByPath(
    val path: List<Int>,
    val position: Int,
    val property: NodeProperty? = null,
    val skipUpdate: Boolean = false
) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.applyOperation(
            Operation.SplitNode(
                path,
                position,
                property,
                skipUpdate
            )
        )
    }
}