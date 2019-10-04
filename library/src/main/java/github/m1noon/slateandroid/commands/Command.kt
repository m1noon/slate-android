package github.m1noon.slateandroid.commands

import github.m1noon.slateandroid.controllers.IController

interface Command

interface FunctionCommand : Command {
    fun execute(controller: IController)
}
