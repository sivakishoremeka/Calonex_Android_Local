package mp.app.calonex.landlord.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_cx_message.view.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.firstLetterWord
import mp.app.calonex.landlord.activity.ChatActivity
import mp.app.calonex.landlord.model.CxMessageListData
import mp.app.calonex.landlord.model.CxMessageUsers


class CxMessagingListAdapter(
    var context: Context,
    var activity: FragmentActivity?,
    var list: ArrayList<CxMessageListData>
) : RecyclerView.Adapter<CxMessagingListAdapter.ViewHolder>(),
    Filterable {

    private var listMessage = ArrayList<CxMessageListData>()

    init {
        listMessage = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtMsgSub.text = listMessage[position].topic
        holder.txtMsg.text = listMessage[position].recentMsg

        try {
            val user: ArrayList<CxMessageUsers> = listMessage[position].users

            if (user[0].id.equals(Kotpref.userId)) {
                holder.txtUsr.text = firstLetterWord(user[0].name)
                if (!(user[1].name.isNullOrEmpty()))
                    holder.txtFrmUsr.text = firstLetterWord(user[1].name)

            }else {
                try {
                    if (user[1].name != null) {
                        holder.txtUsr.text = firstLetterWord(user[1].name)
                    }
                    if (user[0].name != null) {
                        holder.txtUsr.text = firstLetterWord(user[0].name)
                    }

                    holder.txtFrmUsr.text = firstLetterWord(user[0].name)
                } catch (E: Exception) {

                }
            }
        } catch (E: Exception) {

        }

        val msgPriority = listMessage[position].priority

        holder.txtMsgTime.text = Util.getDateTime(listMessage[position].timestamp.toString())
        if (msgPriority.equals("1")) {
            holder.txtMsgPrioroty.text = "Normal"
            holder.viewPriority.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorGreen
                )
            )
        } else if (msgPriority.equals("2")) {
            holder.txtMsgPrioroty.text = "Emergency"
            holder.viewPriority.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorDkYellow
                )
            )
        } else if (msgPriority.equals("3")) {
            holder.txtMsgPrioroty.text = "Urgent"
            holder.viewPriority.setBackgroundColor(Color.RED)
        }
        holder.itemView.setOnClickListener {
            // Navigate to cxMessage Detail Scree
            Kotpref.cxUser = holder.txtUsr.text.toString()
            Kotpref.cxToUser = holder.txtFrmUsr.text.toString()
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(
                context.getString(R.string.property_added),
                listMessage[holder.adapterPosition].address
            )
            intent.putExtra(
                context.getString(R.string.unit_no),
                listMessage[holder.adapterPosition].unitNumber
            )
            intent.putExtra(
                context.getString(R.string.msg_id),
                listMessage[holder.adapterPosition].messageId.toString()
            )
            intent.putExtra(
                context.getString(R.string.frm_usr),
                listMessage[holder.adapterPosition].users[1].name
            )
            intent.putExtra(
                context.getString(R.string.frm_usr_id),
                listMessage[holder.adapterPosition].users[1].id
            )
            intent.putExtra(
                context.getString(R.string.is_chat_close),
                listMessage[holder.adapterPosition].isClosed
            )
            intent.putExtra(
                context.getString(R.string.chat_priority),
                listMessage[holder.adapterPosition].priority
            )
            intent.putExtra(
                context.getString(R.string.chat_topic),
                listMessage[holder.adapterPosition].topic
            )
            context.startActivity(intent)

            activity?.overridePendingTransition(R.anim.activity_in, R.anim.activity_out)
        }
    }

    override fun getItemCount(): Int {
        return listMessage.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtMsgSub = itemView.txt_msg_sub!!
        val txtMsg = itemView.txt_msg!!
        val txtFrmUsr = itemView.txt_frm_usr!!
        val txtUsr = itemView.txt_usr!!
        val viewPriority = itemView.view_priority!!
        val txtMsgPrioroty = itemView.txt_msg_prioroty!!
        val txtMsgTime = itemView.txt_msg_time!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_cx_message, parent, false)
        return ViewHolder(v)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listMessage = list
                } else {
                    val resultList = ArrayList<CxMessageListData>()
                    for (row in list) {
                        if (row.topic.contains(charSearch, true)) {
                            resultList.add(row)
                        }
                    }
                    listMessage = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = listMessage
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                listMessage = results.values as ArrayList<CxMessageListData>
                notifyDataSetChanged()
            }
        }
    }
}