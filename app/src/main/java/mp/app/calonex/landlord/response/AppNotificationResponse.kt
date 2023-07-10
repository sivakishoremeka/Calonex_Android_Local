package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.AppNotifications

data class AppNotificationResponse
    (var responseCode: Int?, var exceptionCode: Int?, var responseDescription: String?, var data: ArrayList<AppNotifications>?)
