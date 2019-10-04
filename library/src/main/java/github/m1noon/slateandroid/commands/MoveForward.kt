package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.operations.Operation

data class MoveForward(val n: Int = 1) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()
        controller.applyOperation(Operation.SetSelection(value.moveForward(n).selection))
    }
}
