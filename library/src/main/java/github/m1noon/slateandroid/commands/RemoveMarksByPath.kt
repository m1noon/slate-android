package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.operations.Operation
import github.m1noon.slateandroid.utils.incrementPath

data class RemoveMarksByPath(
    val path: List<Int>,
    val offset: Int,
    val length: Int,
    val marks: List<Mark>
) : FunctionCommand {
    override fun execute(controller: IController) {
        if (marks.isEmpty()) return

        val value = controller.getValue()
        val node = value.document.assertNodeByPath(path)

        if (marks.intersect(node.marks.orEmpty()).isEmpty()) return

        controller.withoutNormalizing { c ->
            var path = path

            // If it ends before the end of the node, we'll need to split to create a new
            // text with different marks.
            if (offset + length < node.text.orEmpty().length) {
                controller.command(SplitNodeByPath(path, offset + length, skipUpdate = true))
            }

            // Same thing if it starts after the start. But in that case, we need to
            // update our path and offset to point to the new start.
            if (offset > 0) {
                controller.command(SplitNodeByPath(path, offset, skipUpdate = true))
                path = path.incrementPath()
            }

            marks.forEach {mark ->
                controller.applyOperation(Operation.RemoveMark(
                    path = path,
                    mark = mark
                ))
            }
        }
    }
}