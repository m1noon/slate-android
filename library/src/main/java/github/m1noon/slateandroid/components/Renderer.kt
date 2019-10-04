package github.m1noon.slateandroid.components

import android.content.Context
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.*

class Renderer(
    val context: Context
) {

    val blockRenderer: BlockRenderer = DefaultBlockRenderer
    val inlineRenderer: InlineRenderer = DefaultInlineRenderer
    val markRenderer: MarkRenderer = DefaultMarkRenderer
    val componentMap: MutableMap<String, Component> = mutableMapOf()

    /**
     * Render document from scratch.
     */
    fun renderDocument(controller: IController, document: Document): List<Component> {
        val list: MutableList<Component> = mutableListOf()
        document.nodes.forEach { n ->
            when (n) {
                is BlockNode -> {
                    n.getBlockRenderingData(listOf())
                        .map {
                            val component = createNewBlockComponent(controller, it)
                            componentMap.put(it.key, component)
                            component
                        }.forEach {
                            list.add(it)
                        }
                }
            }
        }
        return list.toList()
    }

    fun render(controller: IController, document: Document): List<Component> {

        return document.nodes.map { it as BlockNode }.flatMap { it.getBlockRenderingData() }
            .map { data ->
                // get or create component
                componentMap.get(data.key)
                    ?.also {
                        blockRenderer.rerenderLeafComponent(
                            context,
                            controller,
                            data,
                            renderInlineOrTextNodes(data.nodes),
                            it
                        ).let { leaf ->
                            // TODO bind block data to branch correctly
                            data.parents.map { parent ->
                                blockRenderer.wrapBranchComponent(
                                    context,
                                    controller,
                                    parent.type,
                                    leaf
                                )
                            }
                        }
                    } ?: createNewBlockComponent(controller, data)
            }
        // TODO bind selection
        // TODO remove non exist node
    }

    private fun createNewBlockComponent(
        controller: IController,
        data: BlockNode.BlockRenderingData
    ): Component {
        // create
        val leaf = blockRenderer.createLeafComponent(
            context,
            controller,
            data,
            renderInlineOrTextNodes(data.nodes)
        )

        val wrappedComponent = data.parents
            .fold(leaf) { acc, parent ->
                blockRenderer.wrapBranchComponent(context, controller, parent.type, acc)
            }

        // put to map
        componentMap.put(data.key, wrappedComponent)

        return wrappedComponent
    }

    private fun renderInlineOrTextNodes(nodes: List<Node>): String {
        return nodes.joinToString(separator = "") { renderInlineOrTextNode(it) }
    }

    private fun renderInlineOrTextNode(node: Node): String {
        return when (node.objectType) {
            ObjectType.Inline -> {
                val childText = node.nodes?.map {
                    renderInlineOrTextNode(it)
                }?.joinToString() ?: ""
                inlineRenderer(childText, InlineNode(node))
            }
            ObjectType.Text -> renderTextNode(node)
            else -> ""
        }
    }

    private fun renderTextNode(node: Node): String {
        return node.marks.orEmpty().fold(node.text ?: "") { acc, mark -> markRenderer(acc, mark) }
    }
}
