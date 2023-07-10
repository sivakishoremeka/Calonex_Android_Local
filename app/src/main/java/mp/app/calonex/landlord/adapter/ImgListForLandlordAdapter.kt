package mp.app.calonex.landlord.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_upload_photo.view.*
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.tenant.activity.WebViewJava
import mp.app.calonex.tenant.fragment.FullImageDialogFragment
import mp.app.calonex.tenant.response.ResponseDtoResponse
import okhttp3.MultipartBody

class ImgListForLandlordAdapter(
    var context: Context,
    var list: ArrayList<Uri>
) : RecyclerView.Adapter<ImgListForLandlordAdapter.ViewHolder>() {
    lateinit var tasks: ArrayList<Uri>
    lateinit var mItemClickListener: ImgListForLandlordAdapter.MyAdapterListener
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
        if (list[selectPosition].toString().contains(context.getString(R.string.base_url_img2))) {
            callFileDeleteAPI(list[selectPosition].toString().removePrefix(context.getString(R.string.base_url_img2)), selectPosition)
        }else{
            list.removeAt(selectPosition)
            //mArrayUri.clear()
            //mArrayUri = list
            notifyItemRemoved(selectPosition)
            notifyItemRangeChanged(selectPosition, list.size)
        }


    }

    private fun callFileDeleteAPI(fileUrl: String, selectPosition: Int) {
        if (NetworkConnection.isNetworkConnected(context)) {
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

            Log.e("File Delete", "Deleted File Url==> $fileUrl")
            val uploadSign: ApiInterface =
                ApiClient(context).provideService(ApiInterface::class.java)

            val apiCallUploadSign: Observable<ResponseDtoResponse> =
                uploadSign.deleteDocument(fileUrl)

            apiCallUploadSign.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({

                    if (it.responseDto?.responseCode!!.equals(200)) {

                        list.removeAt(selectPosition)
                        //mArrayUri.clear()
                        //mArrayUri = list
                        notifyItemRemoved(selectPosition)
                        notifyItemRangeChanged(selectPosition, list.size)

                    }
                },
                    { e ->

                        Log.e("error", e.message.toString())
                    })
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }

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