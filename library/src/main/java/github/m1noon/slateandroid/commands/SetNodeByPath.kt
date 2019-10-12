package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.NodeProperty
import github.m1noon.slateandroid.operations.Operation

data class SetNodeByPath(val path: List<Int>, val newProperties: NodeProperty) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.applyOperation(Operation.SetNode(path, newProperties))
    }
}
