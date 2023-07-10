package mp.app.calonex.tenant.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import mp.app.calonex.R


class CxChoosePhotoSourceFragment : Fragment() {

    private var choose_photo_camera: View? = null
    private var choose_photo_gallery: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_choose_photo_source, container, false)
        initComponents(rootView)
        return rootView
    }

    private fun initComponents(viewRoot: View) {
        choose_photo_camera = viewRoot.findViewById(R.id.choose_photo_camera)
        choose_photo_gallery = viewRoot.findViewById(R.id.choose_photo_gallery)
        actionComponent()

    }

    private fun actionComponent() {

        choose_photo_camera?.setOnClickListener {

        }

        choose_photo_gallery?.setOnClickListener {

        }
    }

}