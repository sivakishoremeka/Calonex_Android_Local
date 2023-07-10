package mp.app.calonex.landlord.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_upload_photo.view.*
import mp.app.calonex.R

class ImageBitmapAdapter (var context: Context,
var list: ArrayList<Bitmap>
) : RecyclerView.Adapter<ImageBitmapAdapter.ViewHolder>() {


    lateinit var tasks: ArrayList<Bitmap>

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.imgPic.setImageBitmap(list[position])

        holder.ibDel.setOnClickListener(View.OnClickListener {
            list.removeAt(position)


            notifyDataSetChanged()
        })

    }

    fun setData(tasks: ArrayList<Bitmap>) {
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


}