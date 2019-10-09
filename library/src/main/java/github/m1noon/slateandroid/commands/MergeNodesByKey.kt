package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.operations.Operation
import github.m1noon.slateandroid.utils.decrementPath

data class MergeNodesByKey(val key: String) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()
        val fromPath = value.document.getPathByKey(key)
        val toPath = fromPath.decrementPath()

        controller.withoutNormalizing { c ->
            c.applyOperation(Operation.MergeNode(fromPath, toPath))
        }
    }
}
