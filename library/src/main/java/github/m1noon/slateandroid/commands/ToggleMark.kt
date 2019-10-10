package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Mark

data class ToggleMark(val mark: Mark) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()

        val activeMarks = value.activeMarks()
        if (activeMarks.contains(mark)) {
            controller.command(RemoveMark(mark))
        } else {
            controller.command(AddMark(mark))
        }
    }
}