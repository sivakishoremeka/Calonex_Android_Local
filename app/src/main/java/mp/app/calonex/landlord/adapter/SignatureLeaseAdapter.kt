package mp.app.calonex.landlord.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_lease_sign.view.*
import mp.app.calonex.R
import mp.app.calonex.landlord.model.LeaseSignature
import mp.app.calonex.landlord.model.ObTenantPayload
import java.lang.Exception

class SignatureLeaseAdapter(
    var context: Context,
    var ldName: String,
    var listSignature: ArrayList<LeaseSignature>,
    var list: ArrayList<ObTenantPayload>
) : RecyclerView.Adapter<SignatureLeaseAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val decodedString: ByteArray =
                Base64.decode(listSignature[position].signatureData, Base64.NO_WRAP)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            holder.imgSign!!.setImageBitmap(decodedByte)
            val tenantInfo: ObTenantPayload? =
                list.find { it.userId == listSignature[position].userId }

            Log.e(
                "Lease",
                "list info==> $position == " + Gson().toJson(list)
            )

            Log.e(
                "Lease",
                "Signature info==> $position == " + Gson().toJson(tenantInfo)
            )
            Log.e(
                "Lease",
                "Signature userId==> $position == " + Gson().toJson(listSignature[position].userId)
            )
            if (tenantInfo == null) {
                holder.txtSignName.text = "LandLord"
            } else {
                holder.txtSignName.text =
                    "Tenant" //  tenantInfo?.tenantFirstName+" "+tenantInfo?.tenantLastName
            }
        } catch (e: Exception) {
            Log.e("Error Signature", e.localizedMessage)
        }
    }

    override fun getItemCount(): Int {
        return listSignature.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtSignName = itemView.txt_sign_name
        val imgSign = itemView.img_sign
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_lease_sign, parent, false)
        return ViewHolder(v)
    }

}