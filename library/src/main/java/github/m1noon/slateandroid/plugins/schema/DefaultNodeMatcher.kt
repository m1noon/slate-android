package github.m1noon.slateandroid.plugins.schema

import github.m1noon.slateandroid.models.Node
import github.m1noon.slateandroid.models.ObjectType

data class DefaultNodeMatcher(
    val objectTypes: Set<ObjectType>? = null,
    val first: List<SchemaNodeMatcher>? = null
) : SchemaNodeMatcher {

    constructor(objectType: ObjectType) : this(objectTypes = setOf(objectType))

    constructor(objectType: ObjectType, first: SchemaNodeMatcher) : this(
        objectTypes = setOf(objectType),
        first = listOf(first)
    )

    override fun invoke(node: Node): Boolean {
        // check objectType
        if (objectTypes?.contains(node.objectType) == false) {
            return false
        }

        // check first
        if (first != null) {
            val firstOk = first.firstOrNull { first ->
                node.nodes?.firstOrNull()?.let { first(it) } ?: false
            } != null
            if (!firstOk) {
                return false
            }
        }

        return true
    }
}
