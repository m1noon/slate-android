package github.m1noon.slateandroid.plugins.schema

import github.m1noon.slateandroid.models.Node

// see https://docs.slatejs.org/slate-core/schema#errors
interface SchemaError {
    val node: Node
    val rule: SchemaRuleItem
    val kind: Kind

    interface Kind {
        object ObjectType : Kind
        object Type : Kind
        object ChildMin : Kind
        object ChildMax : Kind
        object Data : Kind
        object Text : Kind
        object Mark : Kind
    }

    data class NodeError(
        override val node: Node,
        override val rule: SchemaRuleItem,
        override val kind: Kind
    ) : SchemaError


    data class ChildError(
        override val node: Node,
        override val rule: SchemaRuleItem,
        override val kind: Kind,
        val index: Int,
        val child: Node
    ) : SchemaError

    data class FirstError(
        override val node: Node,
        override val rule: SchemaRuleItem,
        override val kind: Kind,
        val child: Node
    ) : SchemaError

    data class LastError(
        override val node: Node,
        override val rule: SchemaRuleItem,
        override val kind: Kind,
        val child: Node
    ) : SchemaError

    data class PreviousError(
        override val node: Node,
        override val rule: SchemaRuleItem,
        override val kind: Kind,
        val previous: Node
    ) : SchemaError

    data class NextError(
        override val node: Node,
        override val rule: SchemaRuleItem,
        override val kind: Kind,
        val next: Node
    ) : SchemaError
}

interface ValidationError {
    val kind: SchemaError.Kind
    fun toSchemaError(node: Node, rule: SchemaRuleItem): SchemaError
}

data class NodeValidationError(
    override val kind: SchemaError.Kind
) : ValidationError {
    override fun toSchemaError(node: Node, rule: SchemaRuleItem): SchemaError {
        return SchemaError.NodeError(node, rule, kind)
    }
}
