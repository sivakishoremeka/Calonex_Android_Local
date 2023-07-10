package mp.app.calonex.common.network

interface CallbackRxAPI<P> {
    fun onSuccess(response: P)

    fun onFailed(P:Throwable)
}