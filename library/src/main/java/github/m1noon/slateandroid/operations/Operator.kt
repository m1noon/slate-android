package github.m1noon.slateandroid.operations

import github.m1noon.slateandroid.models.Value

class Operator {
    fun applyOperation(value: Value, operation: Operation): Value {
        return when (operation) {
            is Operation.AddMark -> {
                value.addMark(operation.path, operation.mark)
            }
            is Operation.InsertNode -> {
                value.insertNode(operation.path, operation.node)
            }
            is Operation.InsertText -> {
                value.insertText(operation.path, operation.offset, operation.text)
            }
            is Operation.RemoveMark -> {
                value.removeMark(operation.path, operation.mark)
            }
            is Operation.RemoveNode -> {
                value.removeNode(operation.path)
            }
            is Operation.RemoveText -> {
                value.removeText(operation.path, operation.offset, operation.text)
            }
            is Operation.ReplaceText -> {
                value.replaceText(
                    operation.path,
                    operation.offset,
                    operation.length,
                    operation.text,
                    operation.marks
                )
            }
            is Operation.SetSelection -> {
                value.setSelection(operation.selection)
            }
            is Operation.SplitNode -> {
                value.splitNode(operation.path, operation.position, operation.property)
            }
            is Operation.MergeNode -> {
                value.mergeNode(operation.from, operation.to)
            }
            else -> value
        }
    }

    fun invert(operation: Operation): Operation {
        return operation
    }
}