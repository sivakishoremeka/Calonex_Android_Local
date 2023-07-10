package mp.app.calonex.landlord.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_all_application_details.*

import mp.app.calonex.R
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.common.apiCredentials.*
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.adapter.ApplicantTenantAdapter
import mp.app.calonex.landlord.dashboard.DashboardLdFragment
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.*
import mp.app.calonex.tenant.model.LeaseTenantInfo
import mp.app.calonex.tenant.response.FindApiResponse

class AllApplicantDetailsActivity : AppCompatActivity() {

    var applicantList = ArrayList<LeaseTenantInfo>()
    var userDetailResponse = UserDetail()
    var listUserImages = ArrayList<FetchDocumentModel>()
    lateinit var appContext: Context
    var appNotifications = ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var messageList = ArrayList<AppNotifications>()
    var adapter: ApplicantTenantAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_application_details)
        appContext = this@AllApplicantDetailsActivity

        iv_back.setOnClickListener {
            onBackPressed()
        }

        setData()
        fetchImages()
        getUserInfo()
        getapplicantTenantList()
        getNotificationList()
    }

    private fun getUserInfo() {
        //Create retrofit Service

        val credentials = UserDetailCredential()

        credentials.userId = Kotpref.userId
        val userDetailService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<UserDetailResponse> =
            userDetailService.getUserDetail(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<UserDetailResponse> {
            override fun onSuccess(userDetail: UserDetailResponse) {
                userDetailResponse = userDetail.data!!
                setUserInfo()
            }

            override fun onFailed(t: Throwable) {
                // show error
                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }
        })
    }

    private fun setUserInfo() {

        var profileImg: String = ""
        val customPb: CircularProgressDrawable = Util.customProgress(this)
        for (item in listUserImages) {

            if (item.uploadFileType.equals(getString(R.string.key_profile_pic))) {
                profileImg = item.fileName
                break
            }
        }
        Log.e("imageUrl", "" + getString(R.string.base_url_img2) + profileImg)

        if (!profileImg.isNullOrEmpty()) {

            Kotpref.profile_image = getString(R.string.base_url_img2) + profileImg

            Log.e("imageUrl", "" + R.string.base_url_img2 + profileImg)
            Glide.with(appContext)
                .load(getString(R.string.base_url_img2) + profileImg)
                .placeholder(customPb)
                .into(profile_pic!!)
        }
    }


    private fun fetchImages() {
        if (NetworkConnection.isNetworkConnected(appContext)) {

            val credentials = FetchDocumentCredential()
            credentials.userId = Kotpref.userId

            val signatureApi: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<FetchDocumentResponse> =
                signatureApi.fetchDocument(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto!!.responseDescription.toString())

                    if (it.responseDto!!.responseCode.equals(200) && it.data != null) {
                        listUserImages = it.data!!

                    } else {
                        Toast.makeText(appContext, it.responseDto!!.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())

                        e.message?.let {
                            Toast.makeText(appContext, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getNotificationList() {
        layout_lp_notify!!.visibility = View.VISIBLE
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<AppNotificationResponse> =
            notifyListService.getAppNotifications(credentials) //Test API Key 3600248I

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AppNotificationResponse> {
            override fun onSuccess(response: AppNotificationResponse) {
                Log.e("onSuccess", response.responseCode.toString())

                if (!response.data?.isEmpty()!!) {
                    appNotifications = response.data!!
                    DashboardLdFragment.linkRequestList.clear()
                    DashboardLdFragment.alertsList.clear()
                    messageList.clear()

                    for (i in 0..appNotifications.size - 1) {
                        val gson = Gson()

                        if (appNotifications[i].activityType == "1") {
                            val objectList = gson.fromJson(
                                appNotifications[i].notificationData,
                                NotificationLinkRequestModel::class.java
                            )
                            mNotificationLinkRequest = objectList
                            DashboardLdFragment.linkRequestList.add(appNotifications[i])

                            Log.e(
                                "NOTI DATA 1",
                                DashboardLdFragment.linkRequestList.size.toString()
                            )
                        } else if (appNotifications[i].activityType == "2") {
                            DashboardLdFragment.alertsList.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "3") {
                            messageList.add(appNotifications[i])
                        }
                    }
                }
                Kotpref.notifyCount =
                    (DashboardLdFragment.linkRequestList.size + DashboardLdFragment.alertsList.size).toString()
                txt_lp_notify!!.text = Kotpref.notifyCount
                //(activity as HomeActivityCx?)!!.addBadgeNew(messageList.size.toString())

                layout_lp_notify!!.visibility = View.GONE
                this@AllApplicantDetailsActivity.getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txt_lp_notify!!.visibility = View.GONE

                } else {
                    txt_lp_notify!!.visibility = View.VISIBLE

                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                layout_lp_notify!!.visibility = View.GONE
                this@AllApplicantDetailsActivity!!.getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                txt_lp_notify!!.text =
                    (DashboardLdFragment.linkRequestList.size + DashboardLdFragment.alertsList.size).toString()

                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txt_lp_notify!!.visibility = View.GONE

                } else {
                    txt_lp_notify!!.visibility = View.VISIBLE

                }
            }
        })

    }

    private fun getapplicantTenantList() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            val credential = TenantFindApiCredentials()
            credential.userId = Kotpref.userId
            //credential.propertyId = Kotpref.propertyId
            credential.userRole = Kotpref.userRole
            var propertyListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java);
            val apiCall: Observable<FindApiResponse> =
                propertyListService.find(credential)
            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<FindApiResponse> {
                    override fun onSuccess(response: FindApiResponse) {

                        /*
                            APPLICANTS
                            TENANT_SIGNATURE_PENDING = "15"
                            TENANT_SIGNATURE_IN_PROGRESS = "16";
                            MODIFIED_LANDLORD_APPROVAL_PENDING = "17";
                            LANDLORD_SIGNATURE_PENDING = "18";

                            Tenants
                            FINALIZED = "19";
                        * */
                        for (i in 0 until response.data!!.size) {
                            if (!response.data!!.get(i).leaseStatus.equals("19", true)) {
                                applicantList.add(response.data!!.get(i))
                            }
                        }

                        setPropertyList(applicantList, "applicant")

                    }

                    override fun onFailed(throwable: Throwable) {
                        // show error
                        Log.e("onFailure", throwable.toString())

                    }
                })

        } else {
            Log.e("network", "Please check the network for get property list")
            Toast.makeText(appContext, "please check your Internet connection", Toast.LENGTH_SHORT)
                .show();
        }
    }

    private fun setPropertyList(listResponse: ArrayList<LeaseTenantInfo>, type: String) {

        applicanttenant_list?.layoutManager = LinearLayoutManager(appContext)
        if (listResponse.size == 1) {
            adapter = ApplicantTenantAdapter(
                appContext,

                listResponse as ArrayList<LeaseTenantInfo>,
                type
            )
        } else {

            adapter = ApplicantTenantAdapter(
                appContext,
                listResponse as ArrayList<LeaseTenantInfo>,
                type
            )
        }
        applicanttenant_list!!.adapter = adapter

    }

    private fun setData() {
        setPropertyList(applicantList, "applicant")

        layout_lp_notify!!.setOnClickListener {
            Util.navigationNext(this, NotifyScreenAgent::class.java)
        }

    }

    override fun onBackPressed() {
        finish()
    }

}