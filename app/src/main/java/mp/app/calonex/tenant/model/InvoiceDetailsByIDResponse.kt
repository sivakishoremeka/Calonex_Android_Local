package mp.app.calonex.tenant.model

data class InvoiceDetailsByIDResponse(
    val `data`: InvoiceDetailsDao,
    val responseDto: SampleResponseDto
)