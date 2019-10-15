package github.m1noon.slateandroid.plugins.schema

import github.m1noon.slateandroid.commands.*
import github.m1noon.slateandroid.models.*
import github.m1noon.slateandroid.utils.decrementPath
import github.m1noon.slateandroid.utils.incrementPath
import github.m1noon.slateandroid.utils.lift
import java.util.*

val coreSchemaRules = listOf<SchemaRuleItem>(
    // Only allow block nodes in documents.
    SchemaRuleItem(
        match = DefaultNodeMatcher(ObjectType.Document),
        nodes = SchemaNodesValidators(
            listOf(
                DefaultNodesValidator(objectType = ObjectType.Block)
            )
        )
    ),
    // Only allow block nodes or inline and text nodes in blocks.
    SchemaRuleItem(
        match = DefaultNodeMatcher(
            objectType = ObjectType.Block,
            first = DefaultNodeMatcher(objectType = ObjectType.Block)
        ),
        nodes = DefaultNodesValidator(objectType = ObjectType.Block)
    ),
    SchemaRuleItem(
        match = DefaultNodeMatcher(
            objectType = ObjectType.Block,
            first = DefaultNodeMatcher(objectTypes = setOf(ObjectType.Inline, ObjectType.Text))
        ),
        nodes = DefaultNodesValidator(
            validator = DefaultNodeValidator(
                objectTypes = setOf(ObjectType.Inline, ObjectType.Text)
            )
        )
    ),
    // Only allow inline and text nodes in inlines.
    SchemaRuleItem(
        match = DefaultNodeMatcher(objectType = ObjectType.Inline),
        nodes = DefaultNodesValidator(
            match = listOf(
                DefaultNodeValidator(objectType = ObjectType.Inline),
                DefaultNodeValidator(objectType = ObjectType.Text)
            )
        )
    ),
    // Ensure that block and inline nodes have at least one text child.
    SchemaRuleItem(
        match = DefaultNodeMatcher(objectTypes = setOf(ObjectType.Block, ObjectType.Inline)),
        nodes = DefaultNodesValidator(min = 1),
        normalizer = { c, err ->
            var normalized = false
            if (err.kind == SchemaError.Kind.ChildMin && err.node.nodes.orEmpty().isEmpty()) {
                c.command(
                    InsertNodeByKey(
                        err.node.key, 0, TextNode(key = UUID.randomUUID().toString())
                    )
                )
                normalized = true
            }
            normalized
        }
    ),
    // Ensure that inline nodes are surrounded by text nodes.
    SchemaRuleItem(
        match = DefaultNodeMatcher(objectType = ObjectType.Block),
        first = DefaultNodeValidator(objectTypes = setOf(ObjectType.Block, ObjectType.Text)),
        last = DefaultNodeValidator(objectTypes = setOf(ObjectType.Block, ObjectType.Text)),
        normalizer = { c, err ->
            when (err) {
                is SchemaError.FirstError -> c.command(
                    InsertNodeByKey(
                        err.node.key, 0, TextNode(key = UUID.randomUUID().toString())
                    )
                ).let { true }
                is SchemaError.LastError -> c.command(
                    InsertNodeByKey(
                        err.node.key,
                        err.node.nodes.orEmpty().size,
                        TextNode(key = UUID.randomUUID().toString())
                    )
                ).let { true }
                else -> false
            }
        }
    ),
    SchemaRuleItem(
        match = DefaultNodeMatcher(objectType = ObjectType.Inline),
        first = DefaultNodeValidator(objectTypes = setOf(ObjectType.Block, ObjectType.Text)),
        last = DefaultNodeValidator(objectTypes = setOf(ObjectType.Block, ObjectType.Text)),
        next = DefaultNodeValidator(objectTypes = setOf(ObjectType.Block, ObjectType.Text)),
        previous = DefaultNodeValidator(objectTypes = setOf(ObjectType.Block, ObjectType.Text)),
        normalizer = { c, err ->
            when (err) {
                is SchemaError.FirstError -> c.command(
                    InsertNodeByKey(
                        err.node.key, 0, TextNode(key = UUID.randomUUID().toString())
                    )
                ).let { true }
                is SchemaError.LastError -> c.command(
                    InsertNodeByKey(
                        err.node.key,
                        err.node.nodes.orEmpty().size,
                        TextNode(key = UUID.randomUUID().toString())
                    )
                ).let { true }
                is SchemaError.PreviousError -> c.getValue().document.getPathByKey(err.node.key).let { path ->
                    c.command(
                        InsertNodeByPath(
                            path.lift(),
                            path.decrementPath().last() ?: 0,
                            TextNode(key = UUID.randomUUID().toString())
                        )
                    )
                }.let { true }
                is SchemaError.NextError -> c.getValue().document.getPathByKey(err.node.key).let { path ->
                    c.command(
                        InsertNodeByPath(
                            path.lift(),
                            path.incrementPath().last() ?: 0,
                            TextNode(key = UUID.randomUUID().toString())
                        )
                    )
                }.let { true }
                else -> false
            }
        }
    ),
    // Merge adjacent text nodes with the same marks.
    SchemaRuleItem(
        match = DefaultNodeMatcher(objectType = ObjectType.Text),
        next = { nextNode, matchNode ->
            if (
                nextNode.objectType == ObjectType.Text &&
                matchNode.marks.orEmpty() == nextNode.marks.orEmpty()
            ) {
                NodeValidationError(kind = SchemaError.Kind.Mark)
            } else {
                null
            }
        },
        normalizer = { c, err ->
            if (err is SchemaError.NextError) {
                c.command(MergeNodesByKey(err.next.key))
                true
            } else {
                false
            }
        }
    ),
    // Remove extra adjacent empty text nodes.
    SchemaRuleItem(
        match = DefaultNodeMatcher(objectType = ObjectType.Text),
        next = { nextNode, matchNode ->
            if (nextNode.objectType == ObjectType.Text && nextNode.text.isNullOrEmpty()) {
                NodeValidationError(kind = SchemaError.Kind.Text)
            } else null
        },
        previous = { previousNode, matchNode ->
            if (previousNode.objectType == ObjectType.Text && previousNode.text.isNullOrEmpty()) {
                NodeValidationError(kind = SchemaError.Kind.Text)
            } else null
        },
        normalizer = { c, err ->
            when (err) {
                is SchemaError.NextError -> c.command(RemoveNodeByKey(err.next.key)).let { true }
                is SchemaError.PreviousError -> c.command(RemoveNodeByKey(err.previous.key)).let { true }
                else -> false
            }
        }
    )
)

val schemaRuleDefault = SchemaRule(
    rules = coreSchemaRules
)

