package github.m1noon.slateandroid.models

import junit.framework.Assert.assertEquals
import org.junit.Test

class BlockNodeTest {

    @Test
    fun getBlockRenderingData() {
    }

    @Test
    fun blockRenderingData_equalsIgnoringText() {
        data class In(
            val a: BlockNode.BlockRenderingData,
            val b: BlockNode.BlockRenderingData
        )

        data class TestCase(
            val name: String,
            val input: In,
            val out: Boolean
        ) {
        }

        val tests = listOf<TestCase>(
            TestCase(
                name = "equal_simple_case",
                input = In(
                    a = BlockNode.BlockRenderingData(
                        key = "key-000",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(),
                        data = null,
                        parents = listOf()
                    ),
                    b = BlockNode.BlockRenderingData(
                        key = "key-000",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(),
                        data = null,
                        parents = listOf()
                    )
                ),
                out = true
            ),
            TestCase(
                name = "equal_which_complex_fields_are_not_empty",
                input = In(
                    a = BlockNode.BlockRenderingData(
                        key = "key-000",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(
                            InlineNode(
                                type = InlineNodeType.A,
                                key = "key-inline-001",
                                nodes = listOf(
                                    TextNode(
                                        key = "key-text-001-000",
                                        text = "text-value-001-000"
                                    )
                                )
                            ),
                            TextNode(
                                key = "key-text-001",
                                text = "text-value-001",
                                marks = listOf(
                                    Mark(
                                        type = MarkType.BOLD,
                                        data = MarkData.Color("#001122")
                                    )
                                )
                            )
                        ),
                        data = BlockNodeData.Image(url = "block-data-000"),
                        parents = listOf(
                            BlockNode.BlockRenderingData.ParentBlock(
                                key = "parent-key-000",
                                data = BlockNodeData.Image(url = "parent-data-000"),
                                type = BlockNodeType.PARAGRAPH
                            )
                        )
                    ),
                    b = BlockNode.BlockRenderingData(
                        key = "key-000",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(
                            InlineNode(
                                type = InlineNodeType.A,
                                key = "key-inline-001",
                                nodes = listOf(
                                    TextNode(
                                        key = "key-text-001-000",
                                        text = "text-value-001-000"
                                    )
                                )
                            ),
                            TextNode(
                                key = "key-text-001",
                                text = "text-value-001",
                                marks = listOf(
                                    Mark(
                                        type = MarkType.BOLD,
                                        data = MarkData.Color("#001122")
                                    )
                                )
                            )
                        ),
                        data = BlockNodeData.Image(url = "block-data-000"),
                        parents = listOf(
                            BlockNode.BlockRenderingData.ParentBlock(
                                key = "parent-key-000",
                                data = BlockNodeData.Image(url = "parent-data-000"),
                                type = BlockNodeType.PARAGRAPH
                            )
                        )
                    )
                ),
                out = true
            ),
            TestCase(
                name = "equal_which_text_is_different",
                input = In(
                    a = BlockNode.BlockRenderingData(
                        key = "key-000",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(
                            InlineNode(
                                type = InlineNodeType.A,
                                key = "key-inline-001",
                                nodes = listOf(
                                    TextNode(
                                        key = "key-text-001-000",
                                        text = "text-value-001-000"
                                    )
                                )
                            ),
                            TextNode(
                                key = "key-text-001",
                                text = "text-value-001",
                                marks = listOf(
                                    Mark(
                                        type = MarkType.BOLD,
                                        data = MarkData.Color("#001122")
                                    )
                                )
                            )
                        ),
                        data = BlockNodeData.Image(url = "block-data-000"),
                        parents = listOf(
                            BlockNode.BlockRenderingData.ParentBlock(
                                key = "parent-key-000",
                                data = BlockNodeData.Image(url = "parent-data-000"),
                                type = BlockNodeType.PARAGRAPH
                            )
                        )
                    ),
                    b = BlockNode.BlockRenderingData(
                        key = "key-000",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(
                            InlineNode(
                                type = InlineNodeType.A,
                                key = "key-inline-001",
                                nodes = listOf(
                                    TextNode(
                                        key = "key-text-001-000",
                                        text = "text-value-001-000"
                                    )
                                )
                            ),
                            TextNode(
                                key = "key-text-001",
                                text = "CHANGED_TEXT",
                                marks = listOf(
                                    Mark(
                                        type = MarkType.BOLD,
                                        data = MarkData.Color("#001122")
                                    )
                                )
                            )
                        ),
                        data = BlockNodeData.Image(url = "block-data-000"),
                        parents = listOf(
                            BlockNode.BlockRenderingData.ParentBlock(
                                key = "parent-key-000",
                                data = BlockNodeData.Image(url = "parent-data-000"),
                                type = BlockNodeType.PARAGRAPH
                            )
                        )
                    )
                ),
                out = true
            ),
            TestCase(
                name = "not_equal_key",
                input = In(
                    a = BlockNode.BlockRenderingData(
                        key = "key-0",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(),
                        data = null,
                        parents = listOf()
                    ),
                    b = BlockNode.BlockRenderingData(
                        key = "key-1",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(),
                        data = null,
                        parents = listOf()
                    )
                ),
                out = false
            ),
            TestCase(
                name = "not_equal_index",
                input = In(
                    a = BlockNode.BlockRenderingData(
                        key = "key-0",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(),
                        data = null,
                        parents = listOf()
                    ),
                    b = BlockNode.BlockRenderingData(
                        key = "key-0",
                        index = 2,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(),
                        data = null,
                        parents = listOf()
                    )
                ),
                out = false
            ),
            TestCase(
                name = "not_equal_type",
                input = In(
                    a = BlockNode.BlockRenderingData(
                        key = "key-0",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(),
                        data = null,
                        parents = listOf()
                    ),
                    b = BlockNode.BlockRenderingData(
                        key = "key-0",
                        index = 1,
                        type = BlockNodeType.PARAGRAPH,
                        nodes = listOf(),
                        data = null,
                        parents = listOf()
                    )
                ),
                out = false
            ),
            TestCase(
                name = "not_equal_nodes_key",
                input = In(
                    a = BlockNode.BlockRenderingData(
                        key = "key-000",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(
                            TextNode(
                                key = "key-text-000",
                                text = "key-text-000",
                                marks = listOf()
                            )
                        ),
                        data = null,
                        parents = listOf()
                    ),
                    b = BlockNode.BlockRenderingData(
                        key = "key-000",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(
                            TextNode(
                                key = "KEY_CHANGED",
                                text = "key-text-000",
                                marks = listOf()
                            )
                        ),
                        data = null,
                        parents = listOf()
                    )
                ),
                out = false
            ),
            TestCase(
                name = "not_equal_nodes_type",
                input = In(
                    a = BlockNode.BlockRenderingData(
                        key = "key-000",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(
                            TextNode(
                                key = "key-text-000",
                                text = "key-text-000",
                                marks = listOf()
                            )
                        ),
                        data = null,
                        parents = listOf()
                    ),
                    b = BlockNode.BlockRenderingData(
                        key = "key-000",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(
                            InlineNode(
                                key = "KEY_CHANGED",
                                type = InlineNodeType.A
                            )
                        ),
                        data = null,
                        parents = listOf()
                    )
                ),
                out = false
            ),
            TestCase(
                name = "not_equal_nodes_size",
                input = In(
                    a = BlockNode.BlockRenderingData(
                        key = "key-000",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(
                            TextNode(
                                key = "key-text-000",
                                text = "key-text-000",
                                marks = listOf()
                            )
                        ),
                        data = null,
                        parents = listOf()
                    ),
                    b = BlockNode.BlockRenderingData(
                        key = "key-000",
                        index = 1,
                        type = BlockNodeType.HEADING_1,
                        nodes = listOf(
                            TextNode(
                                key = "key-text-000",
                                text = "key-text-000",
                                marks = listOf()
                            ),
                            TextNode(
                                key = "key-text-000",
                                text = "key-text-000",
                                marks = listOf()
                            )
                        ),
                        data = null,
                        parents = listOf()
                    )
                ),
                out = false
            ),
            TestCase(
                name = "not_equal_data",
                input = In(
                    a = BlockNode.BlockRenderingData(
                        key = "key-0",
                        index = 1,
                        type = BlockNodeType.PARAGRAPH,
                        nodes = listOf(),
                        data = BlockNodeData.Image(url = "data-000"),
                        parents = listOf()
                    ),
                    b = BlockNode.BlockRenderingData(
                        key = "key-0",
                        index = 1,
                        type = BlockNodeType.PARAGRAPH,
                        nodes = listOf(),
                        data = BlockNodeData.Image(url = "CHANGED"),
                        parents = listOf()
                    )
                ),
                out = false
            ),
            TestCase(
                name = "not_equal_parent",
                input = In(
                    a = BlockNode.BlockRenderingData(
                        key = "key-0",
                        index = 1,
                        type = BlockNodeType.PARAGRAPH,
                        nodes = listOf(),
                        data = null,
                        parents = listOf(
                            BlockNode.BlockRenderingData.ParentBlock(
                                key = "key-parent-000",
                                type = BlockNodeType.PARAGRAPH,
                                data = null
                            )
                        )
                    ),
                    b = BlockNode.BlockRenderingData(
                        key = "key-0",
                        index = 1,
                        type = BlockNodeType.PARAGRAPH,
                        nodes = listOf(),
                        data = null,
                        parents = listOf(
                            BlockNode.BlockRenderingData.ParentBlock(
                                key = "key-parent-000",
                                type = BlockNodeType.PARAGRAPH,
                                data = BlockNodeData.Image(url = "CHANGED_URL")
                            )
                        )
                    )
                ),
                out = false
            )
        )

        tests.forEach { case ->
            // execute
            val result = case.input.a.equalsIgnoringText(case.input.b)

            // check
            assertEquals("result is invalid on test case of '${case.name}'", case.out, result)
        }
    }
}