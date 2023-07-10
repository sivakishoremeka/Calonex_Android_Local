package mp.app.calonex.tenant.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_tenant_rating.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.GetRatingCredential
import mp.app.calonex.common.apiCredentials.SubmitRatingCredential
import mp.app.calonex.common.network.*
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.tenant.response.SubmitRatingResponse

class TenantRatingActivity : CxBaseActivity2() {

    private var headerBack: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_rating)

        headerBack = findViewById(R.id.header_back)

        getRating()

        actionComponent()

    }

    private fun getRating() {


        pb_rating.visibility = View.VISIBLE
        val credentialRating = GetRatingCredential()

        credentialRating.fromTenantId = Kotpref.userId
        credentialRating.landlordId = Kotpref.landlordId
        credentialRating.propertyId = Kotpref.propertyId
        credentialRating.unitId = Kotpref.unitId


        val getRatingService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<SubmitRatingResponse> =
            getRatingService.getRating(credentialRating)

        RxAPICallHelper().call(apiCall, object : RxAPICallback<SubmitRatingResponse> {
            override fun onSuccess(response: SubmitRatingResponse) {
                pb_rating.visibility = View.GONE

                if (response.data != null) {
                    var unitRating = response.data!!.unitRating
                    var landlordRating = response.data!!.landlordRating
                    var propertyRating = response.data!!.propertyRating

                    rating_building.rating = propertyRating.toFloat()
                    rating_landlord.rating = landlordRating.toFloat()
                    rating_unit.rating = unitRating.toFloat()

                }


            }

            override fun onFailed(t: Throwable) {
                pb_rating.visibility = View.GONE
            }
        })


    }


    private fun actionComponent() {
        headerBack?.setOnClickListener {
            super.onBackPressed()
        }

        rating_submit.setOnClickListener {

            pb_rating.visibility = View.VISIBLE

            val credentialRating = SubmitRatingCredential()

            credentialRating.fromTenantId = Kotpref.userId
            credentialRating.landlordId = Kotpref.landlordId
            credentialRating.landlordRating = rating_landlord.rating.toString()
            credentialRating.propertyId = Kotpref.propertyId
            credentialRating.propertyRating = rating_building.rating.toString()
            credentialRating.unitId = Kotpref.unitId
            credentialRating.unitRating = rating_unit.rating.toString()


            val ratingService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<SubmitRatingResponse> =
                ratingService.submitRating(credentialRating) //Test API Key

            RxAPICallHelper().call(apiCall, object : RxAPICallback<SubmitRatingResponse> {
                override fun onSuccess(response: SubmitRatingResponse) {
                    pb_rating.visibility = View.GONE
                    val builder = AlertDialog.Builder(this@TenantRatingActivity)

                    builder.setTitle("Success")

                    // Display a message on alert dialog
                    builder.setMessage("Thanks for your feedback")

                    // Set a positive button and its click listener on alert dialog
                    builder.setPositiveButton("Ok") { dialog, which ->
                        onBackPressed()
                    }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                }

                override fun onFailed(t: Throwable) {
                    pb_rating.visibility = View.GONE
                }
            })


        }


    }
}
