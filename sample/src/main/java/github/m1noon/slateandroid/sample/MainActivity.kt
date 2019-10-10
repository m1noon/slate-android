package github.m1noon.slateandroid.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import github.m1noon.slateandroid.Editor
import github.m1noon.slateandroid.commands.AddMark
import github.m1noon.slateandroid.commands.ToggleMark
import github.m1noon.slateandroid.models.Mark
import github.m1noon.slateandroid.models.MarkType
import github.m1noon.slateandroid.models.Value
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
        val sizeBtn: View = findViewById(R.id.btn_size)
        val quoteBtn: View = findViewById(R.id.btn_quote)
        val listBulletBtb: View = findViewById(R.id.btn_list_bullet)
        val listNumberedBtn: View = findViewById(R.id.btn_list_numbered)
        val insertLinkBtn: View = findViewById(R.id.btn_insert_link)
        val insertPhotoBtn: View = findViewById(R.id.btn_insert_photo)

        editor.updateValue(Value.Default)
    }
}
