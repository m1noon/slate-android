package github.m1noon.slateandroid.utils

import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.models.MarkType
import github.m1noon.slateandroid.models.Node
import github.m1noon.slateandroid.models.TextNode
import org.junit.Test

import org.junit.Assert.*

class NodeListHelperKtTest {

    @Test
    fun equalsIgnoringText() {
        data class In(
            val a: List<Node>,
            val b: List<Node>
        )

        data class TestCase(
            val name: String,
            val input: In,
            val out: Boolean
        )

        val tests = listOf<TestCase>(
            TestCase(
                name = "equal_simple_case",
                input = In(
                    a = listOf(
                        TextNode(
                            key = "key-text-000",
                            text = "text-000"
                        )
                    ),
                    b = listOf(
                        TextNode(
                            key = "key-text-000",
                            text = "text-000"
                        )
                    )
                ),
                out = true
            ),
            TestCase(
                name = "equal_empty",
                input = In(
                    a = listOf(),
                    b = listOf()
                ),
                out = true
            ),
            TestCase(
                name = "equal_even_if_text_changed",
                input = In(
                    a = listOf(
                        TextNode(
                            key = "key-text-000",
                            text = "text-000"
                        )
                    ),
                    b = listOf(
                        TextNode(
                            key = "key-text-000",
                            text = "CHANGED_TEXT"
                        )
                    )
                ),
                out = true
            ),
            TestCase(
                name = "not_equal_key_changed",
                input = In(
                    a = listOf(
                        TextNode(
                            key = "key-text-000",
                            text = "text-000"
                        )
                    ),
                    b = listOf(
                        TextNode(
                            key = "CHANGED_KEY",
                            text = "text-000"
                        )
                    )
                ),
                out = false
            ),
            TestCase(
                name = "not_equal_marks_changed",
                input = In(
                    a = listOf(
                        TextNode(
                            key = "key-text-000",
                            text = "text-000",
                            marks = listOf()
                        )
                    ),
                    b = listOf(
                        TextNode(
                            key = "CHANGED_KEY",
                            text = "text-000",
                            marks = listOf(Mark(type = MarkType.BOLD))
                        )
                    )
                ),
                out = false
            ),
            TestCase(
                name = "not_equal_list_size_increased",
                input = In(
                    a = listOf(
                        TextNode(
                            key = "key-text-000",
                            text = "text-000"
                        )
                    ),
                    b = listOf(
                        TextNode(
                            key = "key-text-000",
                            text = "text-000"
                        ),
                        TextNode(
                            key = "key-text-000",
                            text = "text-000"
                        )
                    )
                ),
                out = false
            ),
            TestCase(
                name = "not_equal_list_size_decreased",
                input = In(
                    a = listOf(
                        TextNode(
                            key = "key-text-000",
                            text = "text-000"
                        ),
                        TextNode(
                            key = "key-text-000",
                            text = "text-000"
                        )
                    ),
                    b = listOf(
                        TextNode(
                            key = "key-text-000",
                            text = "text-000"
                        )
                    )
                ),
                out = false
            )
        )

        tests.forEach { case ->
            val result = case.input.a.equalsIgnoringText(case.input.b)

            assertEquals("result is invalid on test case '${case.name}'", case.out, result)
        }
    }
}