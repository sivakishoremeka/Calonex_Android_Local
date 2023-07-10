package mp.app.calonex.agent.model

data class OnboardPropertyAgent(
    val propertyId: Long = 0,
    val userCatalogID: String="",
    val address: String="",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val phone: String = "",
    val numberOfUnits: String = "",
    val description: String = "",
    val brokerOrAgentLiscenceID: String = "",
    val primaryContact: String = "",
    val broker: String = "",
    val brokerPhone: String = "",
    val brokerEmailID: String = "",
    val propertyStatusID: String = "",
    val unitsOccupied: Long = 0,
    val linkedByBrokerAgent: Boolean= false,
    val primaryContactId: String = "",
    val landLordName: String = "",
    val brokerAgentPropertyStatus: String = "",
    val landlordFullName: String = "",
    val onBoardedBy: String = "",
    val allowTenantOnboard: String = "",
    val tenantApproved: Long = 0,
    val unitsVacants: Long = 0,
    val tenantSubmitted: Long = 0,
    val availableNumberOfUnit: Long = 0,
    val linkedByBrokerAgentOnAdd: Boolean = false,
    val showAllowPropertyAccess: String = ""
    )

/*
{
      "propertyId": 135,
      "userCatalogID": "3025",
      "address": "address_full_match",
      "city": "Anchorage",
      "state": "Alaska",
      "zipCode": "99501",
      "phone": "9876543210",
      "numberOfUnits": "1",
      "brokerOrAgentLiscenceID": "98765432111",
      "primaryContact": "Calonex Test.Broker",
      "broker": "Calonex Test.Broker",
      "brokerPhone": "4664664646",
      "brokerEmailID": "cx.broker@yopmail.com",
      "propertyStatusID": "1",
      "unitsOccupied": 0,
      "primaryContactId": "2",
      "brokerAgentPropertyStatus": "ACTIVE",
      "unitsVacants": 0,
      "landlordFullName": "Test Test.Test",
      "onBoardedBy": "2",
      "availableNumberOfUnit": 0,
      "linkedByBrokerAgent": true,
      "linkedByBrokerAgentOnAdd": false

      "showAllowPropertyAccess": "true",
    }
 */