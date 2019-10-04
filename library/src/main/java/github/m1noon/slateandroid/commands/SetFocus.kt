package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Point

data class SetFocus(val p: Point) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.updateValue { it.copy(selection = it.selection.setFocus(p)) }
    }
}