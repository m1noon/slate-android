package github.m1noon.slateandroid.controllers

import github.m1noon.slateandroid.plugins.schema.SchemaRule
import github.m1noon.slateandroid.plugins.schema.schemaRuleDefault

data class Config(
    val schemaRule: SchemaRule = schemaRuleDefault,
    val readOnly: Boolean = false
)
