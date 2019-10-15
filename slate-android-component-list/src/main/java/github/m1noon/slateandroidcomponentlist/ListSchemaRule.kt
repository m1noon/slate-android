package github.m1noon.slateandroidcomponentlist

import github.m1noon.slateandroid.commands.*
import github.m1noon.slateandroid.models.*
import github.m1noon.slateandroid.plugins.schema.*
import github.m1noon.slateandroid.utils.decrementPath
import github.m1noon.slateandroid.utils.incrementPath
import github.m1noon.slateandroid.utils.lift
import java.util.*

val listSchemaRules = listOf<SchemaRuleItem>(
    // Ensure that UL or OL block has at least one LI block
    SchemaRuleItem(
        match = DefaultNodeMatcher(
            objectType = ObjectType.Block,
            types = setOf(ListBlockNodeType.UL, ListBlockNodeType.OL)
        ),
        nodes = DefaultNodesValidator(min = 1),
        normalizer = { c, err ->
            when (err.kind) {
                SchemaError.Kind.ChildMin -> c.command(RemoveNodeByKey(err.node.key)).let { true }
                else -> false
            }
        }
    ),
    // Only allow LI blocks in UL or OL blocks
    SchemaRuleItem(
        match = DefaultNodeMatcher(
            objectType = ObjectType.Block,
            types = setOf(ListBlockNodeType.UL, ListBlockNodeType.OL)
        ),
        nodes = DefaultNodesValidator(
            objectType = ObjectType.Block,
            type = ListBlockNodeType.LI
        ),
        normalizer = { c, err ->
            when (err) {
                is SchemaError.ChildError -> {
                    val value = c.getValue()
                    val child = value.document.assertNodeByKey(err.child.key)
                    // If child is leaf block with text, convert it to 'LI', otherwise remove child node
                    if (child.isLeafBlock()) {
                        c.command(
                            SetNodeByKey(
                                child.key,
                                NodeProperty(type = ListBlockNodeType.LI)
                            )
                        ).let { true }
                    } else {
                        //   remove child node
                        val isLastChildNode = err.node.nodes.orEmpty().size == 1
                        c.command(RemoveNodeByKey(err.child.key))
                            .also {
                                if (isLastChildNode) {
                                    c.command(RemoveNodeByKey(err.node.key))
                                }
                            }
                            .let { true }
                    }
                }
                else -> false
            }
        }
    ),
    // Remove extra adjacent empty LI nodes.
    SchemaRuleItem(
        match = DefaultNodeMatcher(objectType = ObjectType.Block, type = ListBlockNodeType.LI),
        previous = { previousNode, matchNode ->
            if (previousNode.type == ListBlockNodeType.LI && previousNode.getTextString().isEmpty() && matchNode.getTextString().isEmpty()) {
                NodeValidationError(kind = SchemaError.Kind.Text)
            } else null
        },
        normalizer = { c, err ->
            when (err) {
                is SchemaError.PreviousError -> {
                    with(c) {
                        val value = getValue()
                        val path = value.document.getPathByKey(err.node.key)
                        var insertPath = path.lift().incrementPath()
                        val parent = value.document.getParent(path)
                        val index = path.last()
                        if (parent?.nodes?.size == 2) {
                            command(RemoveNodeByPath(path.lift()))
                            insertPath = insertPath.decrementPath()
                        } else {
                            command(RemoveNodeByKey(err.previous.key))
                            command(RemoveNodeByKey(err.node.key))
                            // if node is in middle of list, split the node.
                            val parentChildrenSize = parent?.nodes?.size
                            if (parentChildrenSize != null && parentChildrenSize != index && index > 1) {
                                command(SplitNodeByPath(path.lift(), index - 1))
                            }
                            if (index <= 1) {
                                insertPath = insertPath.decrementPath()
                            }
                        }
                        command(
                            InsertNodeByPath(
                                insertPath.lift(),
                                insertPath.last(),
                                BlockNode(
                                    key = UUID.randomUUID().toString(),
                                    type = BlockNodeType.PARAGRAPH,
                                    nodes = listOf(TextNode(key = UUID.randomUUID().toString()))
                                )
                            )
                        )
                    }.let { true }
                }
                else -> false
            }
        }
    ),
    // Merge adjacent OL or UL nodes with the same type.
    SchemaRuleItem(
        match = DefaultNodeMatcher(
            objectType = ObjectType.Block,
            types = setOf(ListBlockNodeType.UL, ListBlockNodeType.OL)
        ),
        next = { nextNode, matchNode ->
            if (nextNode.type == matchNode.type && nextNode.nodes.orEmpty().isNotEmpty()) {
                NodeValidationError(kind = SchemaError.Kind.Type)
            } else null
        },
        previous = { previousNode, matchNode ->
            if (previousNode.type == matchNode.type && previousNode.nodes.orEmpty().isNotEmpty()) {
                NodeValidationError(kind = SchemaError.Kind.Type)
            } else null
        },
        normalizer = { c, err ->
            when (err) {
                is SchemaError.NextError -> c.command(MergeNodesByKey(err.next.key)).let { true }
                is SchemaError.PreviousError -> c.command(MergeNodesByKey(err.node.key)).let { true }
                else -> false
            }
        }
    )
)