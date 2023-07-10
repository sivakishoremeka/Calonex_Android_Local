package mp.app.calonex.landlord.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_chat.view.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.model.CxMessageConversationData

class ChatDetailsListAdapter(var context: Context, var list: ArrayList<CxMessageConversationData>) :
    RecyclerView.Adapter<ChatDetailsListAdapter.ViewHolder>() {

    private var listMessage = ArrayList<CxMessageConversationData>()

    init {
        listMessage = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtChat.text = listMessage[position].message
        holder.txtChatTime.text = Util.getDateTime(listMessage[position].time.toString())
        holder.txtFrmUsr.text = Util.firstTwo("Benjamin Ty")
        holder.txtToUsr.text = Util.firstTwo("Raw Ku")

        if (listMessage[position].byId == Kotpref.userId) {
            holder.txtFrmUsr.visibility = View.GONE
            holder.txtFrmUsr.text = Kotpref.cxUser
            holder.txtToUsr.visibility = View.GONE
            val params = RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_END)
            }
            params.marginStart = 200
            holder.cardMsg.layoutParams = params

            /*holder.layoutChat.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorLtBrown
                )
            )*/

            holder.layoutChat.setBackgroundResource(R.drawable.bg_right_chat_box)
            holder.txtChatTime.gravity = Gravity.END
        } else {
            val params = RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
            }
            params.marginEnd = 200
            holder.cardMsg.layoutParams = params

            /*holder.layoutChat.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorBlurText
                )
            )*/
            holder.layoutChat.setBackgroundResource(R.drawable.bg_chat_box_left)
            holder.txtChatTime.gravity = Gravity.START

            holder.txtToUsr.visibility = View.GONE
            holder.txtFrmUsr.text = Kotpref.cxToUser
            holder.txtFrmUsr.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return listMessage.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtChat = itemView.txt_chat!!
        val txtChatTime = itemView.txt_chat_time!!
        val cardMsg = itemView.card_msg!!
        val layoutChat = itemView.layout_chat!!
        val txtFrmUsr = itemView.txt_frm_usr!!
        val txtToUsr = itemView.txt_to_usr!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false)

        return ViewHolder(v)
    }
}