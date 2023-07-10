package mp.app.calonex.agent.model

import com.google.gson.annotations.SerializedName

data class AgentGetPropertiesCredential(
    @SerializedName("primaryContactId") var primaryContactId: String="",
    )

/*
{"primaryContactId":"3089"}
 */