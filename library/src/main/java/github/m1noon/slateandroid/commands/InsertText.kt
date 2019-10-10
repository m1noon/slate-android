package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark

data class InsertText(val text: String, val marks: List<Mark>? = null) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()
        val marks = this.marks ?: value.selection.marks.let {
            if (it.isNotEmpty()) it else value.document.getInsertMarksAtRange(value.selection).toList()
        }

        controller.withoutNormalizing { controller ->
            controller.command(InsertTextAtRange(value.selection, text, marks))

            // If the text was successfully inserted, and the selection had marks on it,
            // unset the selection's marks.
            if (value.selection.marks.isNotEmpty() && value.document != controller.getValue().document) {
                controller.command(
                    Select(
                        controller.getValue().selection.copy(marks = listOf()),
                        skipUpdate = true
                    )
                )
            }
        }
    }
}