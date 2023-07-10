package mp.app.calonex.common.network

interface RxAPICallbackForLogin<P> {
    fun onSuccess(response: P)

    fun onFailed(throwable: Throwable, response: P)

}