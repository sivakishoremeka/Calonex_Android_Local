package mp.app.calonex.landlord.adapter

import android.content.Context
import android.content.Intent
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
import mp.app.calonex.tenant.activity.WebViewJava
import mp.app.calonex.tenant.fragment.FullImageDialogFragment

class ImgListAdapter(
    var context: Context,
    var list: ArrayList<Uri>,
    var imgFull: ImageView?,
    var txtTag: TextView?
) : RecyclerView.Adapter<ImgListAdapter.ViewHolder>() {


    lateinit var tasks: ArrayList<Uri>
    lateinit var mItemClickListener: ImgListAdapter.MyAdapterListener
    var selectPosition: Int = 0
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var fileFrom: String = ""
        var fileType: String = ""
        //TODO: write something more in condition, only s3 not efficient

        if (list[position].toString().contains("s3.us")) {
            fileFrom = "Server"
            fileType =
                list[position].toString().substring(list[position].toString().lastIndexOf("."))
        } else {
            fileFrom = "Internal"
        }


        if (fileType.contains("pdf")) {
            Glide.with(context)
                .load(context.resources.getDrawable(R.drawable.logo))
                .into(holder.imgPic)
        } else {
            Glide.with(context)
                .load(list[position])
                .into(holder.imgPic)
        }

        try {
            if (imgFull != null) {
                imgFull?.setImageURI(list[position])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.imgPic.setOnClickListener(View.OnClickListener {

            if (fileFrom.contains("server", true) && fileType.contains("pdf", true)) {
                val intent = Intent(context, WebViewJava::class.java)
                intent.putExtra("url", list[position].toString())
                context.startActivity(intent)

            } else {
                FullImageDialogFragment(context).customDialog(list[position])
            }


        })
        holder.ibDel.setOnClickListener(View.OnClickListener {
            removeItem(holder.getAdapterPosition())
        })
    }

    fun removeItem(position: Int) {
        //selectPosition = if (position <= 0) 0 else position - 1
        selectPosition = position

        list.removeAt(selectPosition)
        //mArrayUri.clear()
        //mArrayUri = list
        notifyItemRemoved(selectPosition)
        notifyItemRangeChanged(selectPosition, list.size)

        //notifyDataSetChanged()
        //

        if (position == selectPosition) {
            imgFull?.setImageURI(null)
        }

        if (list.size == 0) {
            if (txtTag != null) {
                txtTag?.visibility = View.VISIBLE
            }
        }
    }

    /*fun setData(tasks: ArrayList<Uri>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }*/


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