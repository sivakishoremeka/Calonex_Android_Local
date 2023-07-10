package mp.app.calonex.common.constant

class StatusConstant {

    companion object {
        val Submitted_By_Agent = "11"
        val Submitted_By_Broker = "12"
        val Rejected_By_Landlord = "13"
        val Rejected_By_Tenant = "14"
        val Tenant_Signature_Pending = "15"
        val Tenant_Signature_In_Progress = "16"
        val Modified_Landlord_Approval_Pending = "17"
        val Landlord_Signature_Pending = "18"
        val FINALIZED = "19"
        val NOT_STARTED = "20"
        val IN_PROGRESS = "21"
        val SIGNED = "22"
        val LEASE_TERMINATED = "23"
        val Refund_In_progress = "24"
        val REJECTED_DEADLINE_EXCEDED = "28"

        // landlord Approval
        val PENDING = "25"
        val APPROVED = "26"
        val REJECTED = "27"
        val TERMINATED_Security_Refunded = "29"
        val CANCELLED = "30"
        val REFUND_FAILED = "31"
        val SUBMITTED_FOR_RENEWAL = "32"

        fun getLeaseText(leaseID : String): String? {
            val leaseStatusMap = mapOf<String, String>(
                "11" to "Submitted by Agent",
                "12" to "Submitted by Broker",
                "13" to "Rejected by Landlord",
                "14" to "Rejected by Tenant",
                "15" to "Tenant signature pending",
                "16" to "Tenant signature in progress",
                "17" to "Modified Landlord approval pending",
                "18" to "Landlord signature pending",
                "19" to "Finalized",
                "20" to "Not started",
                "21" to "In Progress",
                "22" to "Signed",
                "23" to "Lease terminated",
                "24" to "Refund in progress",
                "25" to "Pending",
                "26" to "Approved",
                "27" to "Rejected",
                "28" to "Rejected because deadline exceeded",
                "29" to "Terminated and Security refunded",
                "30" to "Cancelled",
                "31" to "Refund failed",
                "32" to "Submitted for Renewal",
            )

            return leaseStatusMap.get(leaseID)
        }


        fun globalStatus(status: String?): String {
            if (status == IN_PROGRESS)
                return "In-Progress"
            if (status == NOT_STARTED)
                return "Not-Started"
            if (status == SIGNED)
                return "Signed"
            if (status == FINALIZED)
                return "Finalized"
            if (status == LEASE_TERMINATED)
                return "Lease Terminated"
            if (status == Refund_In_progress)
                return "Refund In-Progress"
            if (status == REJECTED_DEADLINE_EXCEDED)
                return "Rejected Deadline Exceeded"
            if (status == TERMINATED_Security_Refunded)
                return "Terminated & Security Refunded"
            if (status == CANCELLED)
                return "Cancelled"
            if (status == REFUND_FAILED)
                return "Refund Failed"
            if (status == SUBMITTED_FOR_RENEWAL)
                return ""
            if (status == Landlord_Signature_Pending)
                return "LANDLORD SIGNATURE PENDING"

            if (status == Tenant_Signature_Pending)
                return "TENANT SIGNATURE PENDING"
            if (status == REJECTED)
                return "REJECTED"
            if (status == Rejected_By_Landlord)
                return "REJECTED BY LANDLORD"

            return ""
        }

        fun leaseStatus(status: String): String {
            if (status == Rejected_By_Landlord) {
                return "Rejected by landlord"
            }
            if (status == Rejected_By_Tenant) {
                return "Rejected by tenant"
            }
            if (status == Tenant_Signature_Pending) {
                return "Tenant Signature Pending"
            }
            if (status == Tenant_Signature_In_Progress) {
                return "Tenant Signature In Progress"
            }
            if (status == Modified_Landlord_Approval_Pending) {
                return "Landlord Approval Pending"
            }
            if (status == Landlord_Signature_Pending) {
                return "Landlord Signature Pending"
            }
            if (status == LEASE_TERMINATED) {
                return "Lease Terminated"
            }
            if (status == Landlord_Signature_Pending) {
                return "Landlord Signature Pending"
            }
            if (status == Refund_In_progress) {
                return "Refund In-Progress"
            }
            if (status == REJECTED_DEADLINE_EXCEDED) {
                return "Rejected Deadline Exceeded"
            }
            if (status == TERMINATED_Security_Refunded)
                return "Terminated & Security Refunded"
            if (status == CANCELLED)
                return "Cancelled"
            if (status == REFUND_FAILED)
                return "Refund Failed"
            if (status == SUBMITTED_FOR_RENEWAL)
                return "Submitted for renewal"
            return ""
        }


        fun innerLandLordApprovalStatus(status: String?): String {
            if (status == PENDING)
                return "Pending"
            if (status == APPROVED)
                return "Approved"
            if (status == REJECTED)
                return "Rejected"
            return ""
        }

        // test
        //val mTime: Long = 5000

        // production
        val mTime: Long = 5 * 60 * 1000
    }


}