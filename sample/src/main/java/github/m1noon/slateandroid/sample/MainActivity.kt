package github.m1noon.slateandroid.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import github.m1noon.slate_android_component_image.*
import github.m1noon.slateandroid.Editor
import github.m1noon.slateandroid.commands.*
import github.m1noon.slateandroid.components.BlockRenderers
import github.m1noon.slateandroid.components.Renderer
import github.m1noon.slateandroid.controllers.Config
import github.m1noon.slateandroid.models.*
import github.m1noon.slateandroid.operations.Operation
import github.m1noon.slateandroid.plugins.schema.SchemaRule
import github.m1noon.slateandroid.plugins.schema.coreSchemaRules
import github.m1noon.slateandroid.utils.incrementPath
import github.m1noon.slateandroid.utils.isBeforePath
import github.m1noon.slateandroid.utils.lift
import github.m1noon.slateandroid.utils.newTextBlockNode
import github.m1noon.slateandroidcomponentlist.ListBlockNodeType
import github.m1noon.slateandroidcomponentlist.ListBlockRenderer
import github.m1noon.slateandroidcomponentlist.UnwrapListItemByKey
import github.m1noon.slateandroidcomponentlist.listSchemaRules
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editor: Editor = findViewById(R.id.editor)
        editor.setup(
            Config(
                schemaRule = SchemaRule(
                    coreSchemaRules + listSchemaRules + imageSchemaRules
                )
            )
        )
        editor.renderer = Renderer(
            this,
            blockRenderer = BlockRenderers(
                renderers = mapOf(
                    ListBlockNodeType.LI to ListBlockRenderer(),
                    ListBlockNodeType.OL to ListBlockRenderer(),
                    ListBlockNodeType.UL to ListBlockRenderer(),
                    ImageBlockNodeType to ImageBlockRenderer()
                )
            )
        )
        // Bold
        findViewById<View>(R.id.btn_bold).setOnClickListener {
            editor.command(ToggleMark(Mark(type = MarkType.BOLD)))
        }
        // Italic
        findViewById<View>(R.id.btn_italic).setOnClickListener {
            editor.command(ToggleMark(Mark(type = MarkType.ITALIC)))
        }
        //
        findViewById<View>(R.id.btn_size).setOnClickListener {
            editor.getValue().startBlock()?.let { block ->
                val type: BlockNode.Type
                when (block.type) {
                    BlockNodeType.HEADING_1 -> type = BlockNodeType.HEADING_2
                    BlockNodeType.HEADING_2 -> type = BlockNodeType.HEADING_3
                    BlockNodeType.HEADING_3 -> type = BlockNodeType.PARAGRAPH
                    else -> type = BlockNodeType.HEADING_1
                }
                var path = editor.getValue().document.getPathByKey(block.key)
                while (path.size > 1) {
                    editor.command(UnwrapBlockByKey(block.key))
                    path = editor.getValue().document.getPathByKey(block.key)
                }
                editor.command(SetNodeByKey(block.key, NodeProperty(type = type)))
            }
        }
        val quoteBtn: View = findViewById(R.id.btn_quote)
        findViewById<View>(R.id.btn_list_bullet).setOnClickListener {
            val value = editor.getValue()
            value.startBlock()?.let { block ->
                val blockPath = value.document.getPathByKey(block.key)
                when (block.type) {
                    ListBlockNodeType.LI -> {
                        value.document.getNodeByPath(blockPath.lift())?.let { parentBlock ->
                            if (parentBlock.type == ListBlockNodeType.UL) {
                                // unwrap UL
                                editor.command(UnwrapListItemByKey(block.key))
                            } else {
                                // set parent to UL
                                editor.command(
                                    SetNodeByPath(
                                        blockPath.lift(),
                                        NodeProperty(type = ListBlockNodeType.UL)
                                    )
                                )
                            }
                        }
                    }
                    else -> {
                        // wrap UL
                        editor.command(
                            WrapBlockByPath(
                                blockPath,
                                BlockNode(
                                    key = UUID.randomUUID().toString(),
                                    type = ListBlockNodeType.UL
                                )
                            )
                        )
                    }
                }
            }
        }
        findViewById<View>(R.id.btn_list_numbered).setOnClickListener {
            val value = editor.getValue()
            value.startBlock()?.let { block ->
                val blockPath = value.document.getPathByKey(block.key)
                when (block.type) {
                    ListBlockNodeType.LI -> {
                        value.document.getNodeByPath(blockPath.lift())?.let { parentBlock ->
                            if (parentBlock.type == ListBlockNodeType.OL) {
                                // unwrap OL
                                editor.command(UnwrapListItemByKey(block.key))
                            } else {
                                // set parent to OL
                                editor.command(
                                    SetNodeByPath(
                                        blockPath.lift(),
                                        NodeProperty(type = ListBlockNodeType.OL)
                                    )
                                )
                            }
                        }
                    }
                    else -> {
                        // wrap OL
                        editor.command(
                            WrapBlockByPath(
                                blockPath,
                                BlockNode(
                                    key = UUID.randomUUID().toString(),
                                    type = ListBlockNodeType.OL
                                )
                            )
                        )
                    }
                }
            }
        }
        val insertLinkBtn: View = findViewById(R.id.btn_insert_link)

        // Image
        findViewById<View>(R.id.btn_insert_photo).setOnClickListener {
            val url =
                "https://lh6.googleusercontent.com/-y5Z-qlqw3I4/AAAAAAAAAAI/AAAAAAAAAzE/-Jvz1UN0rZ8/photo.jpg"
            editor.command(InsertImageBlock(url))
        }

        editor.updateValue(Value.Default)
    }
}
