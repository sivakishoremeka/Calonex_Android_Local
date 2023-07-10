package mp.app.calonex.tenant.response.invoiceStats


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("activeMonthRentAmount")
    var activeMonthRentAmount: Double,
    @SerializedName("activeMonthSecurityDepositAmount")
    var activeMonthSecurityDepositAmount: Double,
    @SerializedName("totalRentAmountPaid")
    var totalRentAmountPaid: Double,
    @SerializedName("totalSecurityAmountPaid")
    var totalSecurityAmountPaid: Double,
    @SerializedName("userId")
    var userId: Int
)