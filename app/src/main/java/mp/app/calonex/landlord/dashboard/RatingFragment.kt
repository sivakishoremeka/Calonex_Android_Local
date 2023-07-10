package mp.app.calonex.landlord.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import kotlinx.android.synthetic.main.rating_fragment.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.landlord.response.LandlordRatingResponse
import org.json.JSONException


class RatingFragment : Fragment() {
    lateinit var layoutFull: LinearLayout
    lateinit var layoutEmpty: LinearLayout
    lateinit var appContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.rating_fragment, container, false)
        layoutFull = rootView.findViewById(R.id.rating_star_parent_full) as LinearLayout
        layoutEmpty = rootView.findViewById(R.id.rating_star_parent_empty) as LinearLayout

        getRating()
        return rootView
    }

    override fun onResume() {
        super.onResume()
        getRating()
    }

    @Throws(JSONException::class)
    private fun setData() {
        for (j in 1..6) {
            val selectedImage = ImageView(activity)
            selectedImage.setLayoutParams(ViewGroup.LayoutParams(50, 50))
            selectedImage.setMaxHeight(30)
            selectedImage.setMaxWidth(30)
            selectedImage.setImageResource(R.drawable.full_img)
            layoutFull.addView(selectedImage)
        }
        for (i in 6..9) {
            val image = ImageView(activity)
            image.setLayoutParams(ViewGroup.LayoutParams(50, 50))
            image.setMaxHeight(30)
            image.setMaxWidth(30)
            image.setImageResource(R.drawable.empty_img)
            // Adds the view to the layout
            layoutFull.addView(image)
        }
    }

    private fun getRating() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val service: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<LandlordRatingResponse> =
                service.getRating(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<LandlordRatingResponse> {
                    override fun onSuccess(response: LandlordRatingResponse) {

                        if (response.data != null && !response.data!!.landlordRating.isEmpty()) {

                            val rating = response.data!!.landlordRating.toFloat()
                            rating_landlord.rating = rating
                            landlord_rating_text.text = ""+rating+"/"+"10"

//                            try {
//                                setData()
//                            } catch (e: JSONException) {
//                                e.printStackTrace()
//                            }
                        }


                    }

                    override fun onFailed(t: Throwable) {

                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            RatingFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("data", "data")
                }
            }
    }
}