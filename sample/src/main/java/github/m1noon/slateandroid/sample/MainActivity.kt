package github.m1noon.slateandroid.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import github.m1noon.slateandroid.Editor
import github.m1noon.slateandroid.commands.AddMark
import github.m1noon.slateandroid.commands.SetBlocks
import github.m1noon.slateandroid.commands.SetNodeByKey
import github.m1noon.slateandroid.commands.ToggleMark
import github.m1noon.slateandroid.models.*
import github.m1noon.slateandroid.sample.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editor: Editor = findViewById(R.id.editor)
        findViewById<View>(R.id.btn_bold).setOnClickListener {
            editor.command(ToggleMark(Mark(type = MarkType.BOLD)))
        }
        findViewById<View>(R.id.btn_italic).setOnClickListener {
            editor.command(ToggleMark(Mark(type = MarkType.ITALIC)))
        }
        findViewById<View>(R.id.btn_size).setOnClickListener {
            editor.getValue().startBlock()?.let { block ->
                val type: BlockNode.Type
                when (block.type) {
                    BlockNodeType.HEADING_1 -> type = BlockNodeType.HEADING_2
                    BlockNodeType.HEADING_2 -> type = BlockNodeType.HEADING_3
                    BlockNodeType.HEADING_3 -> type = BlockNodeType.PARAGRAPH
                    else -> type = BlockNodeType.HEADING_1
                }
                editor.command(SetNodeByKey(block.key, NodeProperty(type = type)))
            }
        }
        val quoteBtn: View = findViewById(R.id.btn_quote)
        val listBulletBtb: View = findViewById(R.id.btn_list_bullet)
        val listNumberedBtn: View = findViewById(R.id.btn_list_numbered)
        val insertLinkBtn: View = findViewById(R.id.btn_insert_link)
        val insertPhotoBtn: View = findViewById(R.id.btn_insert_photo)

        editor.updateValue(Value.Default)
    }
}
