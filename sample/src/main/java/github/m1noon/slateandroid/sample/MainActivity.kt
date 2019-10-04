package github.m1noon.slateandroid.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import github.m1noon.slateandroid.Editor
import github.m1noon.slateandroid.models.Value
import github.m1noon.slateandroid.sample.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editor: Editor = findViewById(R.id.editor)

        editor.updateValue(Value.Default)
    }
}
