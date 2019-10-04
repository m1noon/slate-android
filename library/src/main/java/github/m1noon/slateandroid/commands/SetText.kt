package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark

data class SetText(val text: String, val marks: List<Mark>? = null) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.command(
            SetTextAtRange(
                controller.getValue().selection, text, marks
            )
        )
    }
}