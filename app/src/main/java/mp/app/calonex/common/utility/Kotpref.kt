package mp.app.calonex.common.utility

import com.chibatching.kotpref.KotprefModel

object Kotpref: KotprefModel() {

    var emailId by stringPref()
    var password by stringPref()
    var accessToken by stringPref()
    var firebaseToken by stringPref()
    var userId by stringPref()
    var profile_image by stringPref()
    var subscriptionActive by booleanPref()
    var userRole by stringPref()
    var loginRole by stringPref()
    var agentLicenceNo by stringPref()
    var exactRole by stringPref()
    var isMsgClick by booleanPref()
    var bankAccountVerified by booleanPref()
    var bankAdded by booleanPref()

    var setupComplete by booleanPref()
    var isCardUseToPay by booleanPref()
    var stripeToken by stringPref()

    var rentcxStripeToken by stringPref()
    var propertyIdNew by longPref()


    var isLogin by booleanPref()
    var isAddPropertyFromLd by booleanPref()
    var propertyId by stringPref()
    var tenantId by stringPref()
    var payDay by stringPref()
    var isFingerPrint by booleanPref()
    var isNotifyRefresh by booleanPref()
    var isRegisterConfirm by booleanPref()
    var dateRegistration by stringPref()
    var isPrimaryAddress by booleanPref()
    var isLeaseHistory by booleanPref()
    var planSelected by stringPref()
    var propertyImageIndex by intPref()
    var tenantFirstName by stringPref()
    var tenantLastName by stringPref()
    var phone by stringPref()
    var address by stringPref()
    var userName by stringPref()
    var notifyCount by stringPref()

    var cxUser by stringPref()
    var cxToUser by stringPref()

    var leaseSigned by booleanPref()
    var tenantDetailsModified by booleanPref()
    var bankInfoAdded by booleanPref()

    var leaseId by stringPref()
    var unitNumber by stringPref()
    var unitId by stringPref()
    var landlordId by stringPref()

    var authProvider by stringPref()
    var socialToken by nullableStringPref()

    var linkBrokerLicenceNo by nullableStringPref()

    var calMonToAdd by intPref()

    var invoiceId by stringPref()

}