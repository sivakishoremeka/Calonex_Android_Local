package mp.app.calonex.landlord.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.dialog_resolution_msg.view.*
import kotlinx.android.synthetic.main.layout_update_mobile.view.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.CxMessageConversationListCredential
import mp.app.calonex.common.apiCredentials.CxMessageSendNewMessageCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.adapter.ChatDetailsListAdapter
import mp.app.calonex.landlord.model.CxMessageConversationData
import mp.app.calonex.landlord.response.CxMessageConversationListResponse
import mp.app.calonex.landlord.response.CxMessageSendNewMessageResponse
import java.util.*

class ChatActivity : CxBaseActivity2() {//AppCompatActivity()

    private var messageId: String? = ""
    private var frmUsr: String? = ""
    private var frmUsrId: String? = ""
    private var rvChat: RecyclerView? = null
    private var chatList = ArrayList<CxMessageConversationData>()
    private var chatDetailsListAdapter: ChatDetailsListAdapter? = null
    private var editMsg: EditText? = null
    private var imgSendMsg: ImageView? = null
    private var ivBack: ImageView? = null
    private var txtResMsg: TextView? = null
    private var txtMsgFrom: TextView? = null
    private var isResMsg: Boolean? = false
    private var propertyAdd: String? = ""
    private var unitNo: String? = ""

    //private var isChatClosed:Boolean=false
    private var msgSend: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initComponent()
        startHandler()
    }

    private fun initComponent() {
        messageId = intent.getStringExtra(getString(R.string.msg_id))
        frmUsr = intent.getStringExtra(getString(R.string.frm_usr))
        frmUsrId = intent.getStringExtra(getString(R.string.frm_usr_id))

        val isChatClosed = intent.getStringExtra(getString(R.string.is_chat_close))
        val msgPriority = intent.getStringExtra(getString(R.string.chat_priority))

        rvChat = findViewById(R.id.rv_chat)
        editMsg = findViewById(R.id.edit_msg)
        imgSendMsg = findViewById(R.id.img_send_msg)
        ivBack = findViewById(R.id.iv_back)
        txtResMsg = findViewById(R.id.txt_res_msg)
        txtMsgFrom = findViewById(R.id.txt_msg_from)
        txt_title?.text = intent.getStringExtra(getString(R.string.chat_topic))
        txtMsgFrom!!.text = "" + intent.getStringExtra(getString(R.string.frm_usr))
        if (intent.getStringExtra(getString(R.string.property_added)).isNullOrEmpty()) {
            txt_chat_address?.visibility = View.GONE
        } else {
            txt_chat_address?.visibility = View.VISIBLE
            if (intent.getStringExtra(getString(R.string.unit_no)).isNullOrEmpty()) {
                txt_chat_address?.text = intent.getStringExtra(getString(R.string.property_added))
            } else {
                txt_chat_address?.text =
                    getString(R.string.unit_num) + intent.getStringExtra(getString(R.string.unit_no)) + ", " +
                            intent.getStringExtra(getString(R.string.property_added))
            }
        }

        if (msgPriority.equals("1")) {
            txt_chat_priority.text = "Normal"
            // txt_chat_priority.setBackgroundResource(R.drawable.btn_green_round)
        } else if (msgPriority.equals("2")) {

            txt_chat_priority.text = "Emergency"
            //  txt_chat_priority.setBackgroundResource(R.drawable.btn_yellow_round)
        } else if (msgPriority.equals("3")) {

            txt_chat_priority.text = "Urgent"
            // txt_chat_priority.setBackgroundResource(R.drawable.btn_red_round)
        }

        if (isChatClosed.equals("true")) {
            layout_msg?.visibility = View.INVISIBLE
            txt_close?.visibility = View.VISIBLE
        } else {
            layout_msg?.visibility = View.VISIBLE
            txt_close?.visibility = View.GONE
        }

        val layoutManger = LinearLayoutManager(applicationContext)
        layoutManger.reverseLayout = true
        rvChat?.layoutManager = layoutManger
        val dividerItemDecoration =
            DividerItemDecoration(rvChat!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(applicationContext, R.drawable.item_list_divider)!!
        )

        rvChat!!.addItemDecoration(dividerItemDecoration)

        actionComponent()
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            getConversation()
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }

        ivBack!!.setOnClickListener {
            onBackPressed()
        }

    }

    private fun actionComponent() {
        editMsg!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (editMsg!!.text.toString().length > 0) {
                    imgSendMsg!!.isEnabled = true
                    imgSendMsg!!.setColorFilter(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.colorPrimaryDark
                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                } else {
                    imgSendMsg!!.isEnabled = false
                    imgSendMsg!!.setColorFilter(
                        ContextCompat.getColor(
                            applicationContext,
                            android.R.color.white
                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                }
            }
        })

        imgSendMsg!!.setOnClickListener {
            if (NetworkConnection.isNetworkConnected(applicationContext)) {
                msgSend = editMsg?.text.toString().trim()
                isResMsg = false
                sendMsg()
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_network),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        txtResMsg!!.setOnClickListener {
            resolutionDialog()
        }
    }

    private fun getConversation() {
        pb_chat!!.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        var credentials = CxMessageConversationListCredential()

        credentials.messageId = messageId
        var chatListService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        var apiCall: Observable<CxMessageConversationListResponse> =
            chatListService.getMessageList(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<CxMessageConversationListResponse> {
            override fun onSuccess(messageListResponse: CxMessageConversationListResponse) {

                /* Here messageListResponse.data is a Json String
                ** Need to convert Json String in Json Array and then list of Type "CxMessageConversationData"
                */

                val gson = Gson()

                val objectList = gson.fromJson(
                    messageListResponse.data,
                    Array<CxMessageConversationData>::class.java
                )

                //  val listChat:MutableList<CxMessageConversationData>?= objectList as MutableList<CxMessageConversationData>
                chatList = ArrayList(Arrays.asList(*objectList.reversedArray()))
                Log.e("list", objectList.toString())

                editMsg!!.setText("")

                pb_chat!!.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                setChatList(chatList)
            }

            override fun onFailed(t: Throwable) {
                // show error
                pb_chat.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                Log.e("onFailure", t.toString())
            }
        })
    }

    private fun setChatList(listChat: ArrayList<CxMessageConversationData>) {
        chatDetailsListAdapter = ChatDetailsListAdapter(applicationContext, listChat)
        rvChat?.adapter = chatDetailsListAdapter

        //rvChat?.smoothScrollToPosition(listChat.count()-1)//(mdata.Count()-1)
    }

    private fun resolutionDialog() {
        //Inflate the dialog with custom view

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_resolution_msg, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this, R.style.DialogSlideBottomChooseTeam)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()

        mAlertDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        mAlertDialog.window?.setGravity(Gravity.BOTTOM)


        mDialogView.btn_res_send!!.setOnClickListener {
            if (NetworkConnection.isNetworkConnected(applicationContext)) {

                msgSend = mDialogView.edit_res_msg!!.text.toString().trim()
                if (Util.valueMandetory(applicationContext, msgSend, mDialogView.edit_res_msg!!)) {
                    // return
                } else {
                    isResMsg = true
                    mAlertDialog.dismiss()
                    sendMsg()
                    layout_msg?.visibility = View.INVISIBLE
                    txt_close?.visibility = View.VISIBLE
                }
            } else {
                mAlertDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_network),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        mDialogView.btn_close!!.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun sendMsg() {
        pb_chat.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        imgSendMsg!!.isEnabled = false
        imgSendMsg!!.setColorFilter(
            ContextCompat.getColor(
                applicationContext,
                android.R.color.white
            ), android.graphics.PorterDuff.Mode.MULTIPLY
        )

        var credential = CxMessageSendNewMessageCredential()
        credential.msg = msgSend
        credential.messageId = messageId
        credential.receiverId = frmUsrId
        credential.recentMsg = ""
        credential.senderId = Kotpref.userId
        credential.subMessage = ""
        credential.isClosed = isResMsg

        val sendMessage: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<CxMessageSendNewMessageResponse> =
            sendMessage.sendNewMessage(credential) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<CxMessageSendNewMessageResponse> {
            override fun onSuccess(sendMsgResponse: CxMessageSendNewMessageResponse) {
                getConversation()
            }

            override fun onFailed(t: Throwable) {
                pb_chat.visibility = View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                try {
                    Util.apiFailure(applicationContext, t)
                } catch (e: Exception) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.error_server),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                imgSendMsg!!.isEnabled = true
                imgSendMsg!!.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorPrimaryDark
                    ), android.graphics.PorterDuff.Mode.MULTIPLY
                )
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this@ChatActivity)
    }
}

