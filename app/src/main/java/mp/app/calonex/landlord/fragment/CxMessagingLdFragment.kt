package mp.app.calonex.landlord.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.dialog_filter.view.*
import kotlinx.android.synthetic.main.dialog_new_msg.view.*
import kotlinx.android.synthetic.main.fragment_ld_cx_messaging.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.CxMessageTopicListCredential
import mp.app.calonex.common.apiCredentials.CxMsgUsersCredentials
import mp.app.calonex.common.apiCredentials.CxNewMsgCredentials
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.*
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.NotifyScreen
import mp.app.calonex.landlord.adapter.CustomSpinnerAdapter
import mp.app.calonex.landlord.adapter.CxMessagingListAdapter
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.landlord.response.CxMessageTopicListResponse
import mp.app.calonex.landlord.response.CxMsgUsersResponse
import mp.app.calonex.landlord.response.CxNewMsgResponse
import retrofit2.HttpException
import kotlin.collections.ArrayList

class CxMessagingLdFragment : Fragment() {
    private var profilePic: CircleImageView?=null
    var messageList = java.util.ArrayList<AppNotifications>()
    var linkRequestListt = java.util.ArrayList<AppNotifications>()
    var linkRequestListForLdReq = java.util.ArrayList<AppNotifications>()
    var alertsListt = java.util.ArrayList<AppNotifications>()
    var appNotifications = java.util.ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()
    private lateinit var appContext: Context
    private var CxMessagingListAdapter: CxMessagingListAdapter? = null
    private var searchMessage: SearchView? = null
    private var rvMessage: RecyclerView? = null
    private var layoutNewMsg: LinearLayout? = null
    private var refreshMessage: SwipeRefreshLayout? = null
    private var listMessage = ArrayList<CxMessageListData>()
    private var messageListCx = ArrayList<CxMessageListData>()
    private var imgFilter: ImageView? = null
    private var codnPriority: String? = null
    private var typeStatus: String? = null
    private var isBoth: Boolean = false
    private var btnTryAgain: Button? = null
    private var txtMsgEmpty: TextView? = null

    private var rlFilterBy: RelativeLayout? = null
    private var svFromList: androidx.appcompat.widget.SearchView? = null


    private var userList = ArrayList<String>()
    private var propertyList = ArrayList<String>()
    private var unitList = ArrayList<String>()

    private var dialogNewMsg: Dialog? = null
    private var userResponseList = ArrayList<CxMsgUsers>()
    private var layoutCxNotify: RelativeLayout? = null
    private var txtCxNotify: TextView? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_ld_cx_messaging, container, false)
        initComponents(rootView)
        return rootView
    }

    private fun initComponents(viewRoot: View) {
        profilePic=viewRoot.findViewById(R.id.profile_pic)
        Glide.with(appContext)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profilePic!!)

        searchMessage = viewRoot.findViewById(R.id.search_msg)
        rvMessage = viewRoot.findViewById(R.id.rv_messages)
        layoutNewMsg = viewRoot.findViewById(R.id.layout_new_msg)
        refreshMessage = viewRoot.findViewById(R.id.refresh_message)
        imgFilter = viewRoot.findViewById(R.id.img_filter)
        btnTryAgain = viewRoot.findViewById(R.id.btn_try_again)
        txtMsgEmpty = viewRoot.findViewById(R.id.txt_msg_empty)
        layoutCxNotify = viewRoot.findViewById(R.id.layout_lp_notify)
        txtCxNotify = viewRoot.findViewById(R.id.txt_lp_notify)

        rlFilterBy = viewRoot.findViewById(R.id.rl_filter_by)
        svFromList = viewRoot.findViewById(R.id.sv_from_list)

        val searchTextViewId: Int = searchMessage!!.getContext().getResources()
            .getIdentifier("android:id/search_src_text", null, null)
        val searchText = searchMessage!!.findViewById(searchTextViewId) as EditText
        searchText.setTextColor(Color.WHITE)

        val dividerItemDecoration =
            DividerItemDecoration(rvMessage!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(requireActivity(), R.drawable.item_list_divider)!!
        )


        rvMessage!!.addItemDecoration(dividerItemDecoration)

        //** Set the colors of the Pull To Refresh View
        refreshMessage?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorPrimary
            )
        )
        refreshMessage?.setColorSchemeColors(Color.WHITE)


        actionComponent()

    }

    override fun onStart() {
        super.onStart()

        if (Kotpref.notifyCount != null && !Kotpref.notifyCount.isEmpty() && Integer.parseInt(
                Kotpref.notifyCount
            ) > 0
        ) {
            txtCxNotify!!.text = Kotpref.notifyCount
            txtCxNotify!!.visibility = View.VISIBLE
        } else {
            txtCxNotify!!.visibility = View.GONE
        }


    }

    override fun onResume() {
        super.onResume()
        getMsgList()
        getUserList()
        getNotificationList()

    }
    private fun getNotificationList() {

        requireActivity().getWindow().setFlags(
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

                    linkRequestListt.clear()
                    alertsListt.clear()
                    messageList.clear()
                    for (i in 0..appNotifications.size - 1) {
                        val gson = Gson()

                        if (appNotifications[i].activityType == "1") {
                            val objectList = gson.fromJson(
                                appNotifications[i].notificationData,
                                NotificationLinkRequestModel::class.java
                            )
                            mNotificationLinkRequest = objectList
                            linkRequestListt.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "2") {
                            alertsListt.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "3") {
                            messageList.add(appNotifications[i])
                        }
                    }
                }

                Kotpref.notifyCount = (linkRequestListt.size + alertsListt.size).toString()
                txtCxNotify!!.text = Kotpref.notifyCount
                Log.e("onSuccess", "Notification Count in CX-Messaging "+Kotpref.notifyCount)


                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txtCxNotify!!.visibility = View.GONE

                } else {
                    txtCxNotify!!.visibility = View.VISIBLE

                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())

                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                txtCxNotify!!.text = (linkRequestListt.size + alertsListt.size).toString()
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txtCxNotify!!.visibility = View.GONE

                } else {
                    txtCxNotify!!.visibility = View.VISIBLE

                }
            }
        })
    }


    private fun actionComponent() {
        searchMessage!!.setOnClickListener {
            searchMessage!!.isIconified = false
        }

        svFromList!!.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty() && CxMessagingListAdapter != null) {
                    CxMessagingListAdapter!!.filter.filter(newText)
                } else if (CxMessagingListAdapter != null) {
                    CxMessagingListAdapter!!.filter.filter("")
                }
                return true
            }

        })


        searchMessage!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty() && CxMessagingListAdapter != null) {
                    CxMessagingListAdapter!!.filter.filter(newText)
                } else {
                    if (CxMessagingListAdapter != null)
                        CxMessagingListAdapter!!.filter.filter("")
                }
                return true
            }
        })
        layoutCxNotify!!.setOnClickListener {
            Util.navigationNext(requireActivity(), NotifyScreen::class.java)
        }
        refreshMessage?.setOnRefreshListener {
            getMsgList()
            refreshMessage?.isRefreshing = false
        }

        layoutNewMsg!!.setOnClickListener {
            dialognewMsg()
        }

        imgFilter!!.setOnClickListener {
            dialogFilter()
        }
        rlFilterBy!!.setOnClickListener {
            dialogFilter()
        }
        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility = View.GONE
            getUserList()
            getMsgList()
        }
    }

    private fun getMsgList() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_msg?.visibility = View.VISIBLE
            val credentials = CxMessageTopicListCredential()

            credentials.userCatalogID = Kotpref.userId
            val topicListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<CxMessageTopicListResponse> =
                topicListService.getTopicList(credentials)

            RxAPICallHelper().call(apiCall, object : RxAPICallback<CxMessageTopicListResponse> {
                override fun onSuccess(cxMessageListResponse: CxMessageTopicListResponse) {
//                Log.e("onSuccess", cxMessageListResponse.responseDto.responseCode.toString())
                    if (!cxMessageListResponse.data?.isEmpty()!!) {
                        try {
                            setMsgList(cxMessageListResponse.data!!)
                        }catch (E:Exception){
                           Log.e("error occur","while set the msg")
                        }

                    } else {

                        txtMsgEmpty?.visibility = View.VISIBLE

                    }
                    pb_msg?.visibility = View.GONE
                }

                override fun onFailed(t: Throwable) {
                    // show error
                    Log.e("onFailure", t.toString())
                    btnTryAgain?.visibility = View.VISIBLE
                    pb_msg?.visibility = View.GONE
                    val exception: HttpException = t as HttpException
                    try {
                        if (exception.code() == 500) {
                            Toast.makeText(
                                appContext,
                                getString(R.string.error_server),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (exception.code().equals(405)) {
                            Toast.makeText(
                                appContext,
                                getString(R.string.error_network),
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(
                                appContext,
                                getString(R.string.error_something),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            appContext,
                            getString(R.string.error_something),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }


    }

    private fun getUserList() {

        val msgCredentials = CxMsgUsersCredentials()


        msgCredentials.userCatalogId = Kotpref.userId
        msgCredentials.role = Kotpref.userRole
        val userListService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<CxMsgUsersResponse> =
            userListService.getMsgUsers(msgCredentials)

        RxAPICallHelper().call(apiCall, object : RxAPICallback<CxMsgUsersResponse> {
            override fun onSuccess(response: CxMsgUsersResponse) {
                if (!response.data?.isEmpty()!!) {
                    userResponseList = response.data as ArrayList<CxMsgUsers>
                    userList.clear()
                    propertyList.clear()
                    unitList.clear()
                    userList.add("Select User")
                    propertyList.add("Select Property")
                    val addList = mutableListOf<String>()
                    val usersList = mutableListOf<String>()
                    for (item in userResponseList) {
                        if (!item.userName.isNullOrEmpty()) {
                            usersList.add(item.userName + item.role.replace("CX", ""))
                            addList.add(item.propertyAddress)
                        }
                    }

                    propertyList.addAll(ArrayList<String>(addList.toSet().toList()))
                    userList.addAll(ArrayList<String>(usersList.toSet().toList()))
                }

            }

            override fun onFailed(t: Throwable) {
                // show error
                Log.e("onFailure", t.toString())

            }
        })


    }

    private fun setMsgList(listResponse: ArrayList<CxMessageListData>) {

        listMessage = listResponse
        val list: MutableList<CxMessageListData>? = listResponse.toMutableList()
        listMessage = ArrayList(list!!.reversed())
        messageListCx = listMessage
        rvMessage?.layoutManager = LinearLayoutManager(appContext)
        setAdapter(listMessage)


    }


    private fun setAdapter(msgList: ArrayList<CxMessageListData>) {

        CxMessagingListAdapter = CxMessagingListAdapter(appContext, activity, msgList!!)
        rvMessage?.adapter = CxMessagingListAdapter

    }

    private fun dialognewMsg() {
        dialogNewMsg = Dialog(appContext, R.style.DialogSlideBottomFullScreen)
        var spinnerUnitAdapter: CustomSpinnerAdapter? = null
        var spinnerPropertyAdapter = CustomSpinnerAdapter(appContext, propertyList)


        var listUnit = ArrayList<String>()
        var listUser = ArrayList<String>()
        var propertyValue: String? = null
        var unitValue: String? = null
        var priorityValue: String? = null
        var topicValue: String? = null
        var msgValue: String? = null
        var userValue: String? = null
        var listPriority = ArrayList<String>()
        listPriority.add("Select Priority")
        listPriority.add("Normal")
        listPriority.add("Emergency")
        listPriority.add("Urgent")
        listUser = userList
        var spinnerUserAdapter = CustomSpinnerAdapter(appContext, listUser)
        var listPriorityValue = listOf<String>("0", "1", "2", "3")
        val bottomSheetDialog = layoutInflater.inflate(R.layout.dialog_new_msg, null)
        var spinnerProperty = bottomSheetDialog.findViewById<Spinner>(R.id.spinner_cx_property)
        var spinnerCxUser = bottomSheetDialog.findViewById<Spinner>(R.id.spinner_cx_user)
        var spinnerCxUNit = bottomSheetDialog.findViewById<Spinner>(R.id.spinner_cx_unit)
        var spinnerCxPirority = bottomSheetDialog.findViewById<Spinner>(R.id.spinner_cx_priority)
        var btnMsgSend = bottomSheetDialog.findViewById<TextView>(R.id.btn_msg_send)
        var editTopic = bottomSheetDialog.findViewById<TextInputEditText>(R.id.edit_cx_topic)
        var editNewMsg = bottomSheetDialog.findViewById<TextInputEditText>(R.id.edit_new_msg)
        priorityValue = "0"

        var spinnerPriorityAdapter = CustomSpinnerAdapter(appContext, listPriority)

        spinnerProperty!!.adapter = spinnerPropertyAdapter
        spinnerCxUser!!.adapter = spinnerUserAdapter
        spinnerCxPirority!!.adapter = spinnerPriorityAdapter
        propertyValue = ""
        unitValue = ""
        spinnerProperty!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    propertyValue = propertyList.get(position)
                    listUnit = getUnitList(propertyValue!!)
                    listUser = getUsers(propertyValue!!, "")
                    spinnerUnitAdapter = CustomSpinnerAdapter(appContext, listUnit)
                    spinnerCxUNit!!.adapter = spinnerUnitAdapter
                    spinnerUserAdapter = CustomSpinnerAdapter(appContext, listUser)
                    spinnerCxUser!!.adapter = spinnerUserAdapter
                } else {
                    listUnit.add("Select Unit")
                    spinnerUnitAdapter = CustomSpinnerAdapter(appContext, listUnit)
                    spinnerCxUNit!!.adapter = spinnerUnitAdapter
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

        spinnerCxUNit!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    unitValue = listUnit.get(position)
                    listUser = getUsers(propertyValue!!, unitValue!!)
                    spinnerUserAdapter = CustomSpinnerAdapter(appContext, listUser)
                    spinnerCxUser!!.adapter = spinnerUserAdapter
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

        spinnerCxPirority!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    priorityValue = listPriorityValue.get(position)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

        spinnerCxUser!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    userValue = listUser.get(position)
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

        btnMsgSend!!.setOnClickListener {
            topicValue = editTopic!!.text.toString()
            msgValue = editNewMsg!!.text.toString()
            if (topicValue.isNullOrEmpty()) {
                editTopic?.error = getString(R.string.error_mandetory)
                editTopic?.requestFocus()
                return@setOnClickListener
            }
            if (msgValue.isNullOrEmpty()) {
                editNewMsg?.error = getString(R.string.error_mandetory)
                editNewMsg?.requestFocus()
                return@setOnClickListener
            }

            if (priorityValue.equals("0")) {
                Toast.makeText(appContext, getString(R.string.error_priority), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (userValue.isNullOrEmpty()) {
                Toast.makeText(appContext, getString(R.string.error_user), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            dialogNewMsg!!.dismiss()
            sendNewCxMsg(propertyValue, unitValue, userValue, priorityValue, topicValue, msgValue)

        }

        dialogNewMsg!!.setContentView(bottomSheetDialog)

        dialogNewMsg!!.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialogNewMsg!!.window?.setGravity(Gravity.RIGHT)


        dialogNewMsg!!.show()

        //cancel button click of custom layout
        bottomSheetDialog.btn_msg_cancel.setOnClickListener {
            //dismiss dialog
            dialogNewMsg!!.dismiss()
        }

        bottomSheetDialog.iv_back.setOnClickListener {
            //dismiss dialog
            dialogNewMsg!!.dismiss()
        }

    }

    private fun sendNewCxMsg(
        msgProperty: String?,
        msgUnit: String?,
        msgUser: String?,
        msgPriority: String?,
        newTopic: String?,
        newMsg: String?
    ) {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_msg?.visibility = View.VISIBLE
            var filterId = getMsgId(msgProperty, msgUnit, msgUser)
            val credentials = CxNewMsgCredentials()

            credentials.senderId = Kotpref.userId
            credentials.message = newMsg
            credentials.msg = newMsg
            credentials.topic = newTopic
            credentials.priority = msgPriority
            if (msgProperty.isNullOrEmpty()) {
                credentials.propertyId = msgProperty
            } else {
                credentials.propertyId = filterId.propertyId
            }

            if (msgUnit.isNullOrEmpty()) {
                credentials.unitId = msgUnit
            } else {
                credentials.unitId = filterId.unitId
            }
            credentials.receiverId = filterId.userId


            val topicListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<CxNewMsgResponse> =
                topicListService.sendCxNewMsg(credentials)

            RxAPICallHelper().call(apiCall, object : RxAPICallback<CxNewMsgResponse> {
                override fun onSuccess(cxMessageResponse: CxNewMsgResponse) {

                    if (!cxMessageResponse.data?.isEmpty()!!) {

                        getMsgList()

                    }

                }

                override fun onFailed(t: Throwable) {
                    // show error
                    Log.e("onFailure", t.toString())

                    pb_msg?.visibility = View.GONE
                    try {
                        Util.apiFailure(appContext, t)
                    } catch (e: Exception) {
                        Toast.makeText(
                            appContext,
                            getString(R.string.error_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }


    }

    private fun dialogFilter() {

        val dialog = BottomSheetDialog(appContext)
        val bottomSheetDialog = layoutInflater.inflate(R.layout.dialog_filter, null)
        //login button click of custom layout
        dialog.setContentView(bottomSheetDialog)
        dialog.show()

        if (codnPriority.equals("1")) {
            bottomSheetDialog.btn_normal?.setBackgroundResource(R.drawable.bg_circular_rectangle)
            bottomSheetDialog.btn_normal?.setTextColor(resources.getColor(R.color.colorGreenText))
            bottomSheetDialog.btn_attnsn?.setTextColor(resources.getColor(R.color.colorPrimary))
            bottomSheetDialog.btn_urgent?.setTextColor(resources.getColor(R.color.colorPrimary))
        } else if (codnPriority.equals("2")) {
            bottomSheetDialog.btn_attnsn?.setBackgroundResource(R.drawable.bg_circular_rectangle)
            bottomSheetDialog.btn_normal?.setTextColor(resources.getColor(R.color.colorPrimary))
            bottomSheetDialog.btn_attnsn?.setTextColor(resources.getColor(R.color.orange))
            bottomSheetDialog.btn_urgent?.setTextColor(resources.getColor(R.color.colorPrimary))
        } else if (codnPriority.equals("3")) {
            bottomSheetDialog.btn_urgent?.setBackgroundResource(R.drawable.bg_circular_rectangle)
            bottomSheetDialog.btn_normal?.setTextColor(resources.getColor(R.color.colorPrimary))
            bottomSheetDialog.btn_attnsn?.setTextColor(resources.getColor(R.color.colorPrimary))
            bottomSheetDialog.btn_urgent?.setTextColor(resources.getColor(R.color.colorDeepRed))
        }
        if (typeStatus.equals("false")) {
            bottomSheetDialog.btn_open?.setBackgroundResource(R.drawable.bg_circular_rectangle)
            bottomSheetDialog.btn_open?.setTextColor(resources.getColor(R.color.colorPrimaryDark2))
            bottomSheetDialog.btn_closed?.setTextColor(resources.getColor(R.color.colorAccent))
            bottomSheetDialog.btn_both?.setTextColor(resources.getColor(R.color.colorAccent))

        } else if (typeStatus.equals("true")) {
            bottomSheetDialog.btn_closed?.setBackgroundResource(R.drawable.bg_circular_rectangle)
            bottomSheetDialog.btn_open?.setTextColor(resources.getColor(R.color.colorAccent))
            bottomSheetDialog.btn_closed?.setTextColor(resources.getColor(R.color.colorPrimaryDark2))
            bottomSheetDialog.btn_both?.setTextColor(resources.getColor(R.color.colorAccent))
        }
        if (isBoth) {
            bottomSheetDialog.btn_both?.setBackgroundResource(R.drawable.bg_circular_rectangle)
            bottomSheetDialog.btn_open?.setTextColor(resources.getColor(R.color.colorAccent))
            bottomSheetDialog.btn_closed?.setTextColor(resources.getColor(R.color.colorAccent))
            bottomSheetDialog.btn_both?.setTextColor(resources.getColor(R.color.colorPrimaryDark2))
        }
        bottomSheetDialog.btn_normal?.setOnClickListener {
            //dismiss dialog
            codnPriority = "1"
            priorityFilter()
            dialog.dismiss()
        }

        bottomSheetDialog.btn_attnsn?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            codnPriority = "2"
            priorityFilter()
        }

        bottomSheetDialog.btn_urgent?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            codnPriority = "3"
            priorityFilter()
        }

        bottomSheetDialog.btn_both?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            isBoth = true
            typeStatus = null
            priorityFilter()
        }

        bottomSheetDialog.btn_open?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            isBoth = false
            typeStatus = "false"
            priorityFilter()
        }

        bottomSheetDialog.btn_closed?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            isBoth = false
            typeStatus = "true"
            priorityFilter()
        }

        bottomSheetDialog.btn_clr_filter?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            isBoth = false
            typeStatus = null
            codnPriority = null
            setAdapter(messageListCx)
        }
       /* if (codnPriority.equals("1")) {
            bottomSheetDialog.btn_normal?.setBackgroundResource(R.drawable.btn_green_round)
        } else if (codnPriority.equals("2")) {
            bottomSheetDialog.btn_attnsn?.setBackgroundResource(R.drawable.btn_yellow_round)
        } else if (codnPriority.equals("3")) {
            bottomSheetDialog.btn_urgent?.setBackgroundResource(R.drawable.btn_red_round)
        }
        if (typeStatus.equals("false")) {
            bottomSheetDialog.btn_open?.setBackgroundResource(R.drawable.btn_dk_blue_round)
        } else if (typeStatus.equals("true")) {
            bottomSheetDialog.btn_closed?.setBackgroundResource(R.drawable.btn_dk_blue_round)
        }
        if (isBoth) {
            bottomSheetDialog.btn_both?.setBackgroundResource(R.drawable.btn_dk_blue_round)
        }
        bottomSheetDialog.btn_normal?.setOnClickListener {
            //dismiss dialog

            codnPriority = "1"
            priorityFilter()

            dialog.dismiss()
        }

        bottomSheetDialog.btn_attnsn?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            codnPriority = "2"
            priorityFilter()

        }

        bottomSheetDialog.btn_urgent?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            codnPriority = "3"
            priorityFilter()

        }

        bottomSheetDialog.btn_both?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            isBoth = true
            typeStatus = null
            priorityFilter()

        }

        bottomSheetDialog.btn_open?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            isBoth = false
            typeStatus = "false"
            priorityFilter()

        }



        bottomSheetDialog.btn_closed?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            isBoth = false
            typeStatus = "true"
            priorityFilter()

        }

        bottomSheetDialog.btn_clr_filter?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            isBoth = false
            typeStatus = null
            codnPriority = null
            setAdapter(messageList)

        }
*/
    }


    private fun priorityFilter() {
        lateinit var filterList: List<CxMessageListData>
        if (!typeStatus.isNullOrEmpty() && !codnPriority.isNullOrEmpty()) {
            filterList =
                listMessage.filter { it.priority == codnPriority && it.isClosed == typeStatus }

        } else {
            if (!typeStatus.isNullOrEmpty()) {
                filterList = listMessage.filter { it.isClosed == typeStatus }
            } else if (!codnPriority.isNullOrEmpty()) {
                filterList = listMessage.filter { it.priority == codnPriority }
            } else {
                filterList = listMessage
            }
        }

        val array = arrayListOf<CxMessageListData>()
        array.addAll(filterList)
        setAdapter(array)
    }

    private fun getUnitList(value: String): ArrayList<String> {
        lateinit var filterList: List<CxMsgUsers>
        var filterUnit = ArrayList<String>()
        filterList = userResponseList.filter { it.propertyAddress == value }

        var listUnit = mutableListOf<String>()
        for (item in filterList) {
            if (!item.unitNumber.isNullOrEmpty()) {
                listUnit.add(item.unitNumber)
            }

        }
        filterUnit.add("Select Unit")
        filterUnit.addAll(ArrayList<String>(listUnit.sorted().toSet().toList()))

        return filterUnit
    }

    private fun getUsers(valueProperty: String, valueUnit: String): ArrayList<String> {
        lateinit var filterList: List<CxMsgUsers>
        var filterUser = ArrayList<String>()
        if (!valueProperty.isNullOrEmpty() && !valueUnit.isNullOrEmpty()) {

            filterList =
                userResponseList.filter { it.propertyAddress == valueProperty && it.unitNumber == valueUnit }


        } else {
            if (!valueProperty.isNullOrEmpty()) {
                filterList = userResponseList.filter { it.propertyAddress == valueProperty }
            } else if (!valueUnit.isNullOrEmpty()) {
                filterList = userResponseList.filter { it.unitNumber == valueProperty }
            } else {
                filterList = userResponseList
            }
        }


        var listUnit = mutableListOf<String>()
        for (item in filterList) {
            listUnit.add(item.userName + item.role.replace("CX", ""))
        }
        filterUser.add("Select User")
        filterUser.addAll(ArrayList<String>(listUnit.toSet().toList()))

        return filterUser
    }

    private fun getMsgId(msgProperty: String?, msgUnit: String?, msgUser: String?): CxMsgUsers {
        lateinit var filterList: List<CxMsgUsers>

        var filterId = CxMsgUsers()
        if (!msgProperty.isNullOrEmpty() && !msgUnit.isNullOrEmpty() && !msgUser.isNullOrEmpty()) {
            filterList = userResponseList.filter {
                it.propertyAddress == msgProperty && it.unitNumber == msgUnit && it.userName == msgUser.substring(
                    0,
                    msgUser.indexOf("-")
                )
            }
        } else if (!msgProperty.isNullOrEmpty() && msgUnit.isNullOrEmpty() && !msgUser.isNullOrEmpty()) {
            filterList = userResponseList.filter {
                it.propertyAddress == msgProperty && it.userName == msgUser.substring(
                    0,
                    msgUser.indexOf("-")
                )
            }
        } else if (msgProperty.isNullOrEmpty() && msgUnit.isNullOrEmpty() && !msgUser.isNullOrEmpty()) {
            filterList = userResponseList.filter {
                it.userName == msgUser.substring(
                    0,
                    msgUser.indexOf("-")
                )
            }
        }



        filterId = filterList[0]


        return filterId

    }

}