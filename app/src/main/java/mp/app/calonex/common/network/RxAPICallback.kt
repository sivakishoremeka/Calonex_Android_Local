package mp.app.calonex.common.network

interface RxAPICallback<P> {
    fun onSuccess(response: P)

    fun onFailed(throwable: Throwable)

}