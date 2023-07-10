package mp.app.calonex.tenant.model

data class TenantDashboardUnitResponse(
    val `data`: ArrayList<TenantUnitData>,
    val responseDto: TenantDasboardResponseDto
)