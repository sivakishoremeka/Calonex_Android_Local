package mp.app.calonex.landlord.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_upload_photo.view.*
import mp.app.calonex.R

class TenantImageListAdapter(
    var context: Context,
    var list: ArrayList<String>,
    var imgFull: ImageView?,
    var txtTag: TextView?
) : RecyclerView.Adapter<TenantImageListAdapter.ViewHolder>() {


    lateinit var tasks: ArrayList<Uri>
    lateinit var mItemClickListener: TenantImageListAdapter.MyAdapterListener
    var selectPosition: Int = 0
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context)
            .load(list[position])
            .into(holder.imgPic)


//        holder.imgPic.setImageURI(list[position])
        if (imgFull != null) {
            Glide.with(context)
                .load(list[position])
                .into(imgFull!!)
        }
        holder.imgPic.setOnClickListener(View.OnClickListener {
//            selectPosition = position
//            imgFull?.setImageURI(list[selectPosition])

//            FullImageDialogFragment(context).customDialog(list[position])

        })
        holder.ibDel.setOnClickListener(View.OnClickListener {
            list.removeAt(position)

            if (position == selectPosition) {
                imgFull?.setImageURI(null)
            }
            if (list.size == 0) {
                if (txtTag != null) {
                    txtTag?.visibility = View.VISIBLE
                }
            }
            notifyDataSetChanged()
        })

    }

    fun setData(tasks: ArrayList<Uri>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPic = itemView.iv_photo!!
        val ibDel = itemView.ib_del!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_upload_photo, parent, false)
        return ViewHolder(v)
    }

    fun setOnItemClick(itemClickListener: MyAdapterListener): Unit {
        this.mItemClickListener = itemClickListener
    }

    interface MyAdapterListener {
        fun onItemViewClick(webUrl: String, position: Int)
    }
}