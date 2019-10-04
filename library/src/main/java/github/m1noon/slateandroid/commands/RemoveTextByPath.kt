package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.operations.Operation

data class RemoveTextByPath(
    val path: List<Int>,
    val offset: Int,
    val length: Int
) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()
        val node = value.document.getNodeByPath(path)
        val text = node?.text?.substring(offset, offset + length)
        if (text.isNullOrEmpty()) {
            return
        }
        
        controller.withoutNormalizing { c ->
            // TODO do something for annotation

            c.applyOperation(Operation.RemoveText(path, offset, text))
        }
    }
}