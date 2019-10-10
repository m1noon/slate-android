package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark

data class AddMark(val mark: Mark) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()

        if (value.selection.isExpanded()) {
            controller.command(AddMarkAtRange(value.selection, mark))
        } else {
            val marks = if (value.selection.marks.isNotEmpty()) {
                value.selection.marks
            } else {
                value.document.getActiveMarksAtRange(value.selection)
            }
            // skip if already applied
            if (marks.contains(mark)) return

            controller.command(
                Select(
                    value.selection.copy(marks = marks.plus(mark))
                )
            )
        }
    }
}