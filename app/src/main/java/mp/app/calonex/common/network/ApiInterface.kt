package mp.app.calonex.common.network

import io.reactivex.Observable
import mp.app.calonex.LdRegBankAdd.AddBankModel
import mp.app.calonex.LdRegBankAdd.AddBankModelResponse
import mp.app.calonex.LdRegBankAdd.CreateNewSubscriptionResponseModel
import mp.app.calonex.LdRegBankAdd.CreateSubscriptionModel
import mp.app.calonex.agent.AgentCommisionYearlyResponse
import mp.app.calonex.agent.AgentMyBookOfBusinesResponse
import mp.app.calonex.agent.AgentPropertiesAndTenantResponse
import mp.app.calonex.agent.model.*
import mp.app.calonex.agent.responce.*
import mp.app.calonex.agentRegistration.RegistrationPayloadAgentModel
import mp.app.calonex.billpay.PayInvoiceBo
import mp.app.calonex.billpay.PayInvoiceByBank
import mp.app.calonex.broker.model.InviteAgentRequestModel
import mp.app.calonex.broker.responce.BrokerAgentListResponse
import mp.app.calonex.broker.responce.BrokerFranchiseInfoResponse
import mp.app.calonex.broker.responce.BrokerInviteAgentResponse
import mp.app.calonex.broker.responce.BrokerPaymentHistoryListResponse
import mp.app.calonex.brokerRegistration.RegistrationPayloadBrokerModel
import mp.app.calonex.common.apiCredentials.*
import mp.app.calonex.common.tenant.TenantLeaseCredentials
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.landlord.response.*
import mp.app.calonex.models.broker.AssignAgentResponseDao
import mp.app.calonex.models.broker.AssignedAgentListData
import mp.app.calonex.registration.model.*
import mp.app.calonex.registration.response.*
import mp.app.calonex.rentcx.*
import mp.app.calonex.tenant.model.GeneralReportResponse
import mp.app.calonex.tenant.model.GetCreditePointsTenant
import mp.app.calonex.tenant.model.InvoiceDetailsByIDResponse
import mp.app.calonex.tenant.model.TenantDashboardUnitResponse
import mp.app.calonex.tenant.model.apiCredentials.AddBankInfoCredential
import mp.app.calonex.tenant.model.apiCredentials.LeaseAgreementActionCredential
import mp.app.calonex.tenant.model.apiCredentials.VerifyBankInfoCredential
import mp.app.calonex.tenant.model.stripe.StripeKeyResponse
import mp.app.calonex.tenant.response.*
import mp.app.calonex.tenant.response.invoiceStats.TenantInvoiceStatDao
import mp.app.calonex.utility.StripePiiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    /*@POST("users/login")
    fun login(@Body body: Credentials): Observable<User>*/

    @POST("users/login")
    fun login(@Body body: Credentials): Call<User>

    //@Headers("Accept: application/json")
    @GET("social-service/api/user/me")
    fun socialLogin(@Header("Authorization") token: String?): Observable<SocialLoginResponse>

    @POST("root-service/api/users/persistFireBaseToken")
    fun persistFireBaseToken(@Body body: FirebaseCredentials): Observable<ResponseDtoResponse>

    @POST("root-service/api/property/getPropertyListWithImgByUser")
    fun getPropertyList(@Body body: GetPropertyListApiCredential): Observable<PropertyListResponse>

    @POST("root-service/api/property/add")
    fun addProperty(@Body body: AddPropertyPostPayload): Observable<AddPropertyPostResponse>

    @POST("root-service/api/property/edit")
    fun editProperty(@Body body: AddPropertyPostPayload): Observable<AddPropertyPostResponse>

    @POST("root-service/api/property/edit")
    fun editPropertyAddBroker(@Body body: EditPropertyAddBroker): Observable<AddPropertyPostResponse>

    @POST("root-service/api/property/addUnit")
    fun addUnit(@Body body: AddUnitCredentials): Observable<AddUnitDetailResponse>

    @POST("root-service/api/property/edit/unit")
    fun editUnit(@Body body: AddUnitCredentials): Observable<AddUnitDetailResponse>

    @POST("root-service/api/property/getByPropertyId")
    fun getPropertyById(@Body body: GetPropertyByIdApiCredential): Observable<PropertyDetailResponse>

    @POST("root-service/api/property/getPropertyByLandlordId")
    fun getPropertyByLandlordId(@Body body: GetPropertyListApiCredential): Observable<PropertyListResponse>

    @POST("root-service/api/property/getAvailableUnit")
    fun getAvailableUnit(@Body body: GetAvailableUnitApiCredential): Observable<AvailableUnitResponse>


    @POST("root-service/api/users/findAgentOrBrokerByLicenseId")
    fun getBrokerAgentList(@Body body: BrokerAgentCredentials): Observable<BrokerAgentDetailResponse>

    // 1getAvailableUnit
    @POST("root-service/api/property/getBuildingTypeList")
    fun getBuildingTypeList(): Observable<BuildingTypeListResponse>

    // 2
    @POST("root-service/api/property/getBuildingFeatureList")
    fun getBuildingFeatureList(): Observable<BuildingFeatureListResponse>

    // 3
    @POST("root-service/api/property/getParkingTypeList")
    fun getParkingTypeList(): Observable<ParkingTypeListResponse>

    // 4
    @POST("root-service/api/property/getBathroomTypeList")
    fun getBathroomTypeList(): Observable<BathroomTypeListResponse>

    // 5
    @POST("root-service/api/property/getUnitFeatureList")
    fun getUnitFeatureList(): Observable<UnitFeatureListResponse>

    // 6
    @POST("root-service/api/property/getUnitTypeList")
    fun getUnitTypeList(): Observable<UnitTypeListResponse>

    // 7
    @POST("root-service/api/property/getUnitUtilities")
    fun getUnitUtilities(): Observable<UnitUtilitiesResponse>

    // 8
    @POST("root-service/api/property/getPropertyExpensesList")
    fun getPropertyExpensesList(): Observable<PropertyExpensesListResponse>

    @POST("root-service/api/editUser/userDetail")
    fun getUserDetail(@Body body: UserDetailCredential): Observable<UserDetailResponse>

    @POST("root-service/api/getNotification")
    fun getAppNotifications(@Body body: NotificationCredential): Observable<AppNotificationResponse>

    @POST("root-service/api/linkPropertyAction")
    fun propertyAction(@Body body: LinkPropertyActionCredential): Observable<LinkRequestActionResponse>


    @POST("root-service/api/brokerOrAgent/linkAgentAction")
    fun propertyActionAgentFromBroker(@Body body: LinkPropertyActionCredential): Observable<LinkRequestActionResponseNew>

    @POST("root-service/api/linkPropertyBrokerOrAgent/Action")
    fun propertyActionBA(@Body body: LinkPropertyActionCredentialLandlord): Observable<LinkRequestActionResponse>

    @POST("root-service/api/linkPropertyAction")
    fun propertyAction2(@Body body: LinkPropertyActionCredentialLandlord): Observable<LinkRequestActionResponse>

    @Multipart
    @POST("root-service/api/s3Bucket/upload")
    fun uploadImages(
        @Part("propertyId") id: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Observable<UploadImagesPostResponse>

    @Multipart
    @POST("root-service/api/s3Bucket/upload")
    fun uploadFile(
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file: MultipartBody.Part
    ): Observable<UploadFileMultipartResponse>

    @Multipart
    @POST("tenant-service/api/leaseHandshake/uploadSignature")
    fun uploadSignature(
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file: MultipartBody.Part
    ): Observable<UploadSignatureResponse>

    // Cx Messaging

    @POST("cx-messaging/api/getTopicList")
    fun getTopicList(@Body body: CxMessageTopicListCredential): Observable<CxMessageTopicListResponse>

    @POST("cx-messaging/api/getMessageById")
    fun getMessageList(@Body body: CxMessageConversationListCredential): Observable<CxMessageConversationListResponse>

    @POST("cx-messaging/api/sendNewMessages")
    fun sendNewMessage(@Body body: CxMessageSendNewMessageCredential): Observable<CxMessageSendNewMessageResponse>

    // -----------  Tenant Api's

    @POST("api/tenant/propertyAndUnitDetailInfo")
    fun propertyUnitDetails(@Body body: TenantPropertyUnitDetailsCredentials): Observable<TenantPropertyUnitDetailResponse>

    @POST("tenant-service/api/lease/addOrUpdate")
    fun tenantAddUpdate(@Body body: TenantOnboardAddUpdateCredentials): Observable<TenantPOnboardAddUpdateResponse>

    @POST("tenant-service/api/getLeaseByUserId")
    fun getLeaseByUserId(@Body body: UserDetailCredential): Observable<LeaseByUserIdResponse>

    @POST("tenant-service/api/modifyTenantData")
    fun modifyTenantData(@Body body: TenantInfoPayload): Observable<ModifyTenantDataResponse>

    @POST("tenant-service/api/leaseAgreementAction")
    fun leaseAgreementAction(@Body body: LeaseAgreementActionCredential): Observable<ModifyTenantDataResponse>

    @POST("tenant-service/api/tenentBankInfo/addOrEdit")
    fun addBankInfo(@Body body: AddBankInfoCredential): Observable<ResponseDtoResponse>

    //Verify Account
    @POST("root-service/api/subscription/verifyBankAccount")
    fun verifyBankInfo(@Body body: VerifyBankInfoCredential): Observable<AddBankInfoResponse>

    // Payment
    @POST("root-service/api/landlord/getRevenueDetails")
    fun getRevenueDetail(@Body body: LandlordRevenueDetailCredential): Observable<LandlordRevenueDetailResponse>

    // Dashboard
    @POST("root-service/api/landlord/getOccupancyDetails")
    fun getOccupancy(@Body body: LandlordPaymentHistoryCredential): Observable<LandlordOccupencyResponse>

    // Dashboard
    @POST("root-service/api/landlord/getLandlordRating")
    fun getRating(@Body body: LandlordPaymentHistoryCredential): Observable<LandlordRatingResponse>

    // Dashboard
    @POST("root-service/api/landlord/getRentpaymentDetails")
    fun getRentPaymentDetail(@Body body: LandlordPaymentHistoryCredential): Observable<LdDashboardRentPaymentResponse>

    // Dashboard
    @POST("payment-service/api/PaymentHistory/landlordPaymentHistory")
    fun landlordPaymentHistory(@Body body: LandlordPaymentHistoryCredential): Observable<LandlordPaymentHistoryListResponse>

    // Broker Dashboard
    @POST("root-service/api/broker/getAllBrokerMarketPlace")
    fun getAllBrokerMarketPlace(@Body body: LandlordPaymentHistoryCredential): Observable<BrokerMarketPlaceResponse>

    // Broker Dashboard
    @POST("root-service/api/broker/getAllProperties")
    fun getAllProperties(@Body body: LandlordPaymentHistoryCredential): Observable<BrokerDashboardAllPropertiesResponse>

    // Broker Dashboard
    @POST("root-service/api/broker/getBrokerBookOfBusiness ")
    fun getBookOfBusiness(@Body body: LandlordPaymentHistoryCredential): Observable<BrokerBookOfBusinessResponse>

    // Broker Dashboard
    @POST("root-service/api/broker/getBookOfBusinessOfAgent")
    fun getBookOfBusinessOfAgent(@Body body: LandlordPaymentHistoryCredential): Observable<BrokerBookOfBusinessOfAgentResponse>

    // Agent Commision
    @POST("root-service/api/agent/getAgentCommissionYearly")
    fun getAgentCommissionYearly(@Body body: LandlordRevenueDetailCredential): Observable<AgentCommisionYearlyResponse>

    // getSumOfAgentPropertiesAndApprovedTenant
    @POST("root-service/api/agent/getSumOfAgentPropertiesAndApprovedTenant")
    fun getSumOfAgentPropertiesAndApprovedTenant(@Body body: LandlordRevenueDetailCredential): Observable<AgentPropertiesAndTenantResponse>

    // getAgentBookOfBusiness
    @POST("root-service/api/agent/getAgentBookOfBusiness")
    fun getAgentBookOfBusiness(@Body body: LandlordRevenueDetailCredential): Observable<AgentMyBookOfBusinesResponse>


    // Tenant List
    @POST("api/getTenantList")
    fun getTenantList(@Body body: TenantListCredential): Observable<TenantListResponse>

    @POST("tenant-service/api/searchTenant")
    fun searchTenant(@Body body: TenantSeachh): Observable<SeachTenantResponse>

    // Lease Get
    @POST("api/getLease")
    fun getLease(@Body body: LeaseGetCredential): Observable<LeaseGetResponse>

    // Find
    @POST("tenant-service/api/lease/find")
    fun find(@Body body: TenantFindApiCredentials): Observable<FindApiResponse>


    // Payment History
    @POST("tenant-service/api/getTenantPaymentHistory")
    fun paymentHistory(@Body body: TenantPaymentHistoryCredentials): Observable<PaymentHistoryResponse>

    // Get Msg users
    @POST("cx-messaging/api/getMessagingUsers")
    fun getMsgUsers(@Body body: CxMsgUsersCredentials): Observable<CxMsgUsersResponse>

    // Send new msg
    @POST("cx-messaging/api/messageInit")
    fun sendCxNewMsg(@Body body: CxNewMsgCredentials): Observable<CxNewMsgResponse>

    // User registration
    @POST("root-service/api/users/registration")
    fun registerUser(@Body body: RegistrationPayload): Observable<UserRegistrationResponse>

    // User setup
    @POST("root-service/api/users/setup")
    fun setupUser(@Body body: RegistrationPayload3): Observable<UserRegistrationResponse3>

    //social login
    @POST(" root-service/api/users/registerConnectAccount")
    fun setupUserSocial(@Body body: RegistrationPayload3): Observable<UserRegistrationResponse3>

    // User registration new
    @POST("root-service/api/users/registrationNew")
    fun registerUserNew(@Body body: RegistrationPayload2): Observable<UserRegistrationResponse2>

    // Upload Files
    @POST("fileApi/uploadFiles")
    fun uploadDoc(@Body body: RequestBody): Observable<UploadDocResponsse>

    // Payment Rent Information
    @POST("payment-service/api/cx/module/getRentInformationV2")
    fun getRentInfo(@Body body: PaymentRentInfoCredential): Observable<TenantRentInfoResponse>

    @POST("payment-service/api/cx/module/payRentV2")
    fun payRent(@Body body: PayRentCredential): Observable<ResponseDtoResponse>

    @POST("api/cx/module/security")
    fun paySecurity(@Body body: PaySecurityCredential): Observable<ResponseDtoResponse>

    @POST("tenant-service/api/submitRating")
    fun submitRating(@Body body: SubmitRatingCredential): Observable<SubmitRatingResponse>

    @GET("tenant-service/api/{tenantId}")
    fun getBankInfo(@Path("tenantId") tenantId: String): Observable<GetBankInfoResponse>


    @POST("tenant-service/api/getTenantRating")
    fun getRating(@Body body: GetRatingCredential): Observable<SubmitRatingResponse>

    @POST("root-service/api/users/retrieveStripeCardInformation")
    fun getCredit(@Body body: CreditCardCredentials): Observable<GetCreditCardInfoResponse>


    // Chnge Password
    @POST("root-service/api/users/changePassword")
    fun chngPassword(@Body body: ChngPasswordCredentials): Observable<ChngePasswordResponse>

    // Lease submit or update by landlord
    @POST("tenant-service/api/lease/addOrUpdate")
    fun addOrUpdateLease(@Body body: ObLeaseTenantPayload): Observable<TenantPOnboardAddUpdateResponse>


    // Find Lease
    @POST("tenant-service/api/lease/find")
    fun findLease(@Body body: FindLeaseCredentials): Observable<FindLeaseResponse>

    //  Get Lease Signs
    @POST("tenant-service/api/lease/getSignaturesByLeaseId")
    fun getLeaseSignature(@Body body: SignatureLeaseCredentials): Observable<SignatureLeaseResponse>

    // Action on Lease submit from Brokr by landlord
    @POST("tenant-service/api/tenantRequest/action")
    fun brkrLeaseAction(@Body body: ObLeaseTenantPayload): Observable<BrkrTenantLeaseActionResponse>

    // Lease terminate by landlord
    @POST("tenant-service/api/terminateLease")
    fun leaseTermination(@Body body: ObLeaseTenantPayload): Observable<ResponseDtoResponse>

    @POST("tenant-service/api/undoTerminateLease")
    fun leaseTerminationUndo(@Body body: ObLeaseTenantPayload): Observable<ResponseDtoResponse>

    // Action on Lease submit from tenant by landlord
    @POST("tenant-service/api/lease/landlordActionOnEditByPrimarytenant")
    fun tenantLeaseAction(@Body body: ObLeaseTenantPayload): Observable<BrkrTenantLeaseActionResponse>

    // Upload Sign
    @POST("tenant-service/api/leaseHandshake/uploadSignature")
    fun uploadSignDoc(@Body body: RequestBody): Observable<ResponseDtoResponse>

    // Security Refund
    @POST("tenant-service/api/securityRefund")
    fun securityRefund(@Body body: RefundSecurityAmountModel): Observable<ResponseDtoResponse>

    // Get Subscription
    @POST("root-service/api/subscription/getSubscriptionPlan")
    fun getSubscription(@Body body: Any = Object()): Observable<SubcriptionPlanResponse>

    // Create Subscription
    @POST("root-service/api/subscription/createSubscriptionPlan")
    fun createSubscription(@Body body: SubscriptionPayload): Observable<SelectPlanResponse2>

    // Get Select Subscription
    @POST("root-service/api/subscription/getSubscriptionPlanDetails")
    fun getSubscriptionDetail(@Body body: LandlordPaymentHistoryCredential): Observable<SubscriptionDetailResponse>

    // Cancel Subscription
    @POST("root-service/api/subscription/cancelSubscriptionPlan")
    fun cancelSubscription(@Body body: LandlordPaymentHistoryCredential): Observable<ResponseDto>

    // Update Subscription
    @POST("root-service/api/subscription/updateSubscriptionPlan")
    fun updateSubscription(@Body body: UpdateSubscriptionCredential): Observable<ResponseDtoResponse>

    //Update user profile
    @POST("root-service/api/editUser/userUpdateDetail")
    fun updateUserDetail(@Body body: UpdateUserDetailCredentials): Observable<UpdateUserResponse>

    //Upload image or document
    @POST("root-service/api/s3Bucket/upload")
    fun uploadDocument(@Body body: RequestBody): Observable<ResponseDtoResponse>

    //Download image or document
    @POST("root-service/api/s3Bucket/img/fetchFiles")
    fun fetchDocument(@Body body: FetchDocumentCredential): Observable<FetchDocumentResponse>

    //Upload image or document
    @POST("root-service/api/users/forgotPassword")
    fun passwordForgot(@Body body: EmailCredential): Observable<ResponseDtoResponse>

    //Check email exist in CX or nor
    @POST("root-service/api/users/validateEmail")
    fun emailValidate(@Body body: EmailCredential): Observable<ValidateEmailResponse>

    //Undo cancel subscription /root-service/api/subscription/resubscribePlan
    @POST("root-service/api/subscription/resubscribePlan")
    fun planResubscribe(@Body body: LandlordPaymentHistoryCredential): Observable<ResponseDto>

    //Undo cancel subscription /root-service/api/subscription/resubscribePlan
    @POST("root-service/api/deleteNotification")
    fun delAlert(@Body body: LinkPropertyActionCredential): Observable<ResponseDto>

    //  Get Lease Signs
    @POST("tenant-service/api/fetchSecurityInfo")
    fun securityFetchInfo(@Body body: SignatureLeaseCredentials): Observable<SecurityFetchResponse>

    //Renew Lease - tenant-service/api/leaseRenew/addRenewLeaseReq
    @POST("tenant-service/api/leaseRenew/addRenewLeaseReq")
    fun renewLeaseRequest(@Body body: ObLeaseTenantPayload): Observable<RenewLeaseRequestResponse>

    //-----------------------
    @POST("payment-service/api/PaymentHistory/getAgentPaymentHistory")
    fun agentPaymentHistory(@Body body: LandlordPaymentHistoryCredential): Observable<AgentPaymentHistoryListResponse>

    @POST("root-service/api/property/getPropertyListWithImgByUser")
    fun getPropertyListAgent(@Body body: GetPropertyListAgentApiCredential): Observable<PropertyListResponseAgent>

    @POST("root-service/api/subscription/getAgentSubscriptionCommissionHistory")
    fun getSubscriptionList(@Body body: LandlordPaymentHistoryCredential): Observable<AgentSubscriptionListResponse>

    @POST("payment-service/api/PaymentHistory/getToBePaidBrokerPaymentHistory")
    fun brokerPaymentHistory(
        @Body body: LandlordPaymentHistoryCredential,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Observable<BrokerPaymentHistoryListResponse>

    @POST("root-service/api/brokerOrAgent/getOnboardedProperties")
    fun getOnboardList(@Body body: AgentBrokerOnboardPropertyCredential): Observable<OnboardPropertyListResponseAgent>

    @POST("root-service/api/users/registration")
    fun registerUser2(@Body body: RegistrationPayloadNew): Observable<UserRegistrationResponse>

    @POST("root-service/api/checkLandlord")
    fun checkLandlord(@Body body: EmailCredentialAgentLink): Observable<PropertyLinkResponseAgent>

    @POST("root-service/api/linkProperty")
    fun linkProperty(@Body body: FinalAgentLinkProperty): Observable<FinalPropertyLinkResponse>

    @GET("root-service/api/finance/getBook")
    fun getBookKeepingInfo(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Observable<AgentBookKeepingResponse>

    @GET("root-service/api/finance/getCategories")
    fun getBookKeepingCategories(): Observable<List<AgentBookKeepingGetCategoriesResponse>>

    @POST("root-service/api/finance/addItem")
    fun addBookkeepingItem(@Body body: BookkeepingAddAgent): Observable<AgentBookKeepingAddResponce>

    @PUT("root-service/api/finance/updateItem")
    fun updateBookkeepingItem(@Body body: BookkeepingUpdateAgent): Observable<AgentBookKeepingAddResponce>

    @POST("api/properties")
    fun getMarketplaceProperties(
        @Query("limit") limit: String,
        @Query("page") page: String
    ): Observable<MarketplacePropertiesResponse>

    @POST("tenant-service/api/getTenantPaymentHistory")
    fun getCreditPoint(@Body body: GetCreditePointsTenant): Observable<CreditPointResponse>

    @GET("api/properties/{propertyId}")
    fun getMarketplacePropertyDetails(@Path("propertyId") propertyId: String): Observable<PropertyDetailsDataResponce>

    @POST("root-service/api/editUser/userDetail")
    fun getMarketplaceUserDetails(@Body body: UserDetailCredential): Observable<UserDetailsResponce>

    @POST("api/payment/application-fees")
    fun getMarketplaceApplicationFees(@Body body: ApplicationFeesCredential): Observable<ApplicationFeesResponce>

    // Agent User setup
    @POST("root-service/api/users/setup")
    fun setupUserAgent(@Body body: RegistrationPayloadAgentModel): Observable<UserRegistrationResponse3>

    // Agent User setup
    @POST("root-service/api/users/setup")
    fun setupUserBroker(@Body body: RegistrationPayloadBrokerModel): Observable<UserRegistrationResponse3>

    @POST("root-service/api/users/registration")
    fun registerUser2(@Body body: RegistrationPayload4): Observable<UserRegistrationResponse4>

    @POST("root-service/api/subscription/getBrokerSubscriptionCommissionHistory")
    fun getBrokerSubscriptionList(@Body body: LandlordPaymentHistoryCredential): Observable<AgentSubscriptionListResponse>

    @POST("root-service/api/brokerOrAgent/getBrokerAgentList")
    fun getAgentBrokerList(@Body body: BrokerAgentsCredential): Observable<BrokerAgentListResponse>

    @POST("root-service/api/brokerOrAgent/inviteAgent")
    fun inviteAgent(@Body body: InviteAgentRequestModel): Observable<BrokerInviteAgentResponse>

    @POST("tenant-service/api/lease/addOrUpdate")
    fun marketplaceAddUpdate(@Body body: AddorUpdateRequestModel): Observable<MarketplaceAddorUpdateResponse>

    @POST("root-service/api/subscription/createSubscriptionPlan")
    fun createSubscriptionBankAdd(@Body body: AddBankModel): Observable<AddBankModelResponse>

    @POST("api/payment/make-payment")
    fun makePaymentMarketplace(@Body body: MarketplaceMakePayRequestModel): Observable<MarketplaceMakePayResponce>

    //https://api.calonex.com/api/payment-service/api/cx/module/payRentByInvoice
    @POST("payment-service/api/cx/module/payRentByInvoice")
    fun payRentByInvoice(@Body body: PayInvoiceBo): Observable<MarketplaceMakePayResponceNewDto>

    @POST("payment-service/api/cx/module/payRentByInvoice")
    fun payRentByInvoice(@Body body: PayInvoiceByBank): Observable<MarketplaceMakePayResponceNewDto>

    @POST("root-service/api/property/edit")
    fun propertyExpense(@Body body: PropertyExpensePayload): Observable<PropertyExpenseResponse>

    @POST("payment-service/api/PaymentHistory/getReceivedBrokerPaymentHistory")
    fun getBrokerPaymentHistory(
        @Body body: LandlordPaymentHistoryCredential,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Observable<BrokerPaymentHistoryListResponse>


    @POST("root-service/api/franchise/getFranchiseDetails")
    fun getFranchise(@Body body: UserDetailCredential): Observable<BrokerFranchiseInfoResponse>

    @POST("root-service/api/franchise/updateFranchiseDetails")
    fun AddDetailsFranchise(@Body body: FranchiseDetailCredential): Observable<Boolean>

    //-------------------------------
    @POST("root-service/api/property/getPropertyListWithImgByUse")
    fun getPropertyListAgent(@Body body: AgentGetPropertiesCredential): Observable<AgentPropertyDetailsListResponse>

    @POST("root-service/api/brokerOrAgent/linkAgentAction")
    fun propertyActionLinkAgent(@Body body: LinkPropertyActionAgentCredential): Observable<LinkRequestActionAgentResponse>

    /*New APIs 20-01-2023*/

    @GET("root-service/api/agent/getAssignedAgentsByProperty/{propertyId}")
    fun getPropertyWiseAgentList(@Path("propertyId") propertyId: String): Observable<AssignedAgentListData>

    @POST("root-service/api/agent/assignAgentProperty")
    fun assignPropertyWiseAgent(@Body body: AssignPropertyWiseAgents): Observable<AssignAgentResponseDao>

    @POST("root-service/api/agent/getAssignableAgentsByProperty")
    fun getAssignableAgentList(@Body body: BrokerAgentsCredential): Observable<BrokerAgentListResponse>

    @POST("root-service/api/property/edit")
    fun editPropertyDetails(@Body body: AddPropertyPostPayload): Observable<AddPropertyPostResponse>

    @POST("root-service/api/invoice/getAllbyUserId")
    fun getAllUnitsByTenant(@Body body: LandlordPaymentHistoryCredential): Observable<TenantDashboardUnitResponse>

    @POST("root-service/api/invoice/view")
    fun getInvoiceDetailsByID(@Body body: InvoiceDetailsByIDCred): Observable<InvoiceDetailsByIDResponse>

    @POST("tenant-service/api/report/generateReport")
    fun generateReport(@Body body: GeneralReportDetailsByID): Observable<GeneralReportResponse>

    @POST("root-service/api/property/getByPropertyId")
    fun getPropertyAndUnitById(@Body body: GetPropertyWithUnitDetailsCred): Observable<PropertyDetailResponse>

    // Find Lease Details
    @POST("tenant-service/api/lease/find")
    fun find(@Body body: TenantLeaseCredentials): Observable<FindApiResponse>

    @POST("v1/tokens")
    fun piiToken(
        @Header("Authorization") header: String,
        @Query("pii[id_number]") ssn: String
    ): Call<StripePiiResponse>

    @GET("root-service/api/stripeuser/getStripeKey")
    fun stripeKey(): Call<String>

    @GET("root-service/api/stripeuser/getStripePublishableKey")
    fun stripePublicKey(): Call<String>

    @POST("root-service/api/property/getPropertyByLandlordId")
    fun getPropertyByLandlordId(@Body body: GetPropertyListByEmailApiCredential): Observable<PropertyListResponse>

    //Delete image or document
    @POST("root-service/api/s3Bucket/delete")
    fun deleteDocument(@Query("fileName") fileName: String): Observable<ResponseDtoResponse>

    @GET("root-service/api/finance/getCategories")
    fun getBookKeepingCategory(
        @Query("roleName") roleName: String,
        @Query("bookKeepingType") bookKeepingType: String
    ): Observable<ArrayList<CategoryListForBookKeepingResponseItem>>

    @POST("root-service/api/invoice/getInvoiceStats")
    fun getInvoiceStats(
        @Body body: GeneralDetailsByUserID
    ): Observable<TenantInvoiceStatDao>

    @POST("root-service/api/subscription/createSubscriptionPlan")
    fun createNewSubscription(@Body body: CreateSubscriptionModel): Observable<CreateNewSubscriptionResponseModel>



}