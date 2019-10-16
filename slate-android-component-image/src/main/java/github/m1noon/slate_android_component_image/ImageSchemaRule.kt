package github.m1noon.slate_android_component_image

import github.m1noon.slateandroid.models.ObjectType
import github.m1noon.slateandroid.plugins.schema.DefaultNodeMatcher
import github.m1noon.slateandroid.plugins.schema.SchemaRuleItem

val imageSchemaRules: List<SchemaRuleItem> = listOf(
    SchemaRuleItem(
        match = DefaultNodeMatcher(objectType = ObjectType.Block, type = ImageBlockNodeType),
        isVoid = true
    )
)
