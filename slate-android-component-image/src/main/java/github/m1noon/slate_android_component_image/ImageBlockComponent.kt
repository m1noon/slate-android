package github.m1noon.slate_android_component_image

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import github.m1noon.slate_android_component_image.databinding.ViewImageBlockBinding
import github.m1noon.slateandroid.components.BlockComponent
import github.m1noon.slateandroid.controllers.IController
import github.m1noon.slateandroid.models.BlockNode

class ImageBlockComponent(
    context: Context,
    val controller: IController,
    private var data: BlockNode.BlockRenderingData,
    private var sync: Boolean = false
) : BlockComponent {

    private val binding: ViewImageBlockBinding =
        ViewImageBlockBinding.inflate(LayoutInflater.from(context))

    override fun bindBlockData(data: BlockNode.BlockRenderingData) {
        data.data?.let {
            if (it is ImageBlockData) {
                Glide.with(binding.imageView)
                    .load(it.url)
                    .into(binding.imageView)
            }
        }
    }

    override fun view(): View {
        return binding.root
    }

    override fun data(): BlockNode.BlockRenderingData {
        return this.data
    }

    override fun setSyncState(start: Boolean) {
        /* Do nothing */
    }

    override fun applyTextAppearance(styleRes: Int) {
        /* Do nothing */
    }

    override fun bindText(text: String, forceUpdate: Boolean) {
        /* Do nothing */
    }

    override fun syncSelection() {
        // FIXME should set selection?
        /* Do nothing */
    }
}