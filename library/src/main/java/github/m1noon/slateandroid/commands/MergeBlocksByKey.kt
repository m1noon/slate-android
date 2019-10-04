package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.operations.Operation

data class MergeBlocksByKey(val from: String, val to: String) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()
        val fromPath = value.document.getPathByKey(from)
        val toPath = value.document.getPathByKey(to)

        controller.withoutNormalizing { c ->
            c.applyOperation(Operation.MergeNode(fromPath, toPath))
        }
    }
}
