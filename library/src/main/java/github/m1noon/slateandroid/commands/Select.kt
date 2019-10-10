package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.Selection
import github.m1noon.slateandroid.operations.Operation

data class Select(val selection: Selection, val skipUpdate: Boolean = false) : FunctionCommand {
    override fun execute(controller: IController) {
        val value = controller.getValue()
        var next = value.document.resolveSelection(selection)

        if (next == value.selection) {
            return
        }

        controller.applyOperation(Operation.SetSelection(next, skipUpdate))
    }
}