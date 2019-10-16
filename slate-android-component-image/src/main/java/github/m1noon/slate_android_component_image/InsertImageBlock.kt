package github.m1noon.slate_android_component_image

import github.m1noon.slateandroid.commands.FunctionCommand
import github.m1noon.slateandroid.commands.InsertNodeByPath
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.utils.*

data class InsertImageBlock(val url: String) : FunctionCommand {
    override fun execute(controller: IController) {
        controller.withoutNormalizing { c ->
            val value = c.getValue()
            value.startBlock()?.let { startBlock ->
                val startBlockPath = value.document.getPathByKey(startBlock.key)
                val imagePath =
                    if (startBlock.getTextString().isEmpty()) startBlockPath else startBlockPath.incrementPath()
                val lastTextPath =
                    value.document.getLastText()?.key?.let { value.document.getPathByKey(it) }
                val shouldInsertText =
                    lastTextPath?.isBeforePath(imagePath) ?: true
                c.command(
                    InsertNodeByPath(
                        imagePath.lift(),
                        imagePath.last(),
                        newImageBlockNode(url)
                    )
                )
                if (shouldInsertText) {
                    c.command(
                        InsertNodeByPath(
                            imagePath.lift(),
                            imagePath.last() + 1,
                            newTextBlockNode()
                        )
                    )
                }
            }
        }
    }
}