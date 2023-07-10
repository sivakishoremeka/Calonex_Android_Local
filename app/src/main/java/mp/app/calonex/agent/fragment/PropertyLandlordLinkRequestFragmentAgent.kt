package mp.app.calonex.agent.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mp.app.calonex.R
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.agent.adapter.NotificationLandlordLinkRequestAdapterAgent
import mp.app.calonex.landlord.model.AppNotifications

class PropertyLandlordLinkRequestFragmentAgent : Fragment() {

    lateinit var appContext: Context
    private var recyclerView: RecyclerView? = null
    private var pbLink: ProgressBar? = null
    private var txtNotifyEmpty: TextView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_property_link_request, container, false)
        recyclerView = rootView.findViewById<View>(R.id.rv_link_request) as RecyclerView
        pbLink = rootView.findViewById(R.id.pb_link)
        txtNotifyEmpty = rootView.findViewById(R.id.txt_notify_empty)

       /* if(Kotpref.loginRole.equals("broker"))
        {*/
            setNotificationList(NotifyScreenAgent.linkRequestListForLdReq)
        /*} else {
            setNotificationList(NotifyScreenAgent.linkRequestList)
        }*/

       //getNotifyList()
        //Log.d("PropertyLint", "onCreateView: agent property list"+linkRequestList.toString())
        return rootView
    }

    fun setNotificationList(listResponse: List<AppNotifications>) {
        if (listResponse.size > 0) {
            recyclerView!!.visibility = View.VISIBLE
            txtNotifyEmpty!!.visibility = View.GONE
            recyclerView?.layoutManager = LinearLayoutManager(appContext)

            val adapter = NotificationLandlordLinkRequestAdapterAgent(appContext
                ,
                activity,
                pbLink!!,
                txtNotifyEmpty!!,
                listResponse
            )
            recyclerView?.adapter = adapter
        } else {
            recyclerView!!.visibility = View.GONE
            txtNotifyEmpty!!.visibility = View.VISIBLE
        }
    }

}