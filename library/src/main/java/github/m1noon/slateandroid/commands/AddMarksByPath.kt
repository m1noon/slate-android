package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.operations.Operation
import github.m1noon.slateandroid.utils.incrementPath

data class AddMarksByPath(
    val path: List<Int>,
    val offset: Int,
    val length: Int,
    val marks: List<Mark>
) : FunctionCommand {
    override fun execute(controller: IController) {
        if (marks.isEmpty()) return

        val value = controller.getValue()
        val node = value.document.assertNodeByPath(path)

        controller.withoutNormalizing { c ->
            var path = path

            // If it ends before the end of the node, we'll need to split to create a new
            // text with different marks.
            if (offset + length < node.text.orEmpty().length) {
                controller.command(SplitNodeByPath(path, offset + length))
            }

            // Same thing if it starts after the start. But in that case, we need to
            // update our path and offset to point to the new start.
            if (offset > 0) {
                controller.command(SplitNodeByPath(path, offset))
                path = path.incrementPath()
            }

            marks.forEach {
                controller.applyOperation(Operation.AddMark(path = path, mark = it))
            }
        }
    }
}