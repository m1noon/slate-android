package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.operations.Operation
import github.m1noon.slateandroid.utils.incrementPath

data class ReplaceMarksByPath(
    val path: List<Int>,
    val offset: Int,
    val length: Int,
    val marks: List<Mark>,
    val skipUpdate: Boolean = false
) : FunctionCommand {
    override fun execute(controller: IController) {
        val marksSet = marks.toSet()
        val value = controller.getValue()
        val node = value.document.assertNodeByPath(path)

        if (marksSet == node.marks.orEmpty().toSet()) {
            return
        }

        controller.withoutNormalizing { c ->
            var path = path

            // If it ends before the end of the node, we'll need to split to create a new
            // text with different marks.
            if (offset + length < node.text.orEmpty().length) {
                controller.command(
                    SplitNodeByPath(
                        path,
                        offset + length,
                        skipUpdate = this.skipUpdate
                    )
                )
            }

            // Same thing if it starts after the start. But in that case, we need to
            // update our path and offset to point to the new start.
            if (offset > 0) {
                controller.command(SplitNodeByPath(path, offset, skipUpdate = this.skipUpdate))
                path = path.incrementPath()
            }

            val marksToApply = marksSet.subtract(node.marks.orEmpty())
            val marksToRemove = node.marks.orEmpty().subtract(marksSet)

            marksToRemove.forEach { m ->
                controller.applyOperation(
                    Operation.RemoveMark(
                        path,
                        m,
                        skipUpdate = this.skipUpdate
                    )
                )
            }
            marksToApply.forEach { m ->
                controller.applyOperation(
                    Operation.AddMark(
                        path,
                        m,
                        skipUpdate = this.skipUpdate
                    )
                )
            }
        }
    }
}