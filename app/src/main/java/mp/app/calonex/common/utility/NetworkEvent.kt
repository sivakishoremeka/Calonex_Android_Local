package mp.app.calonex.common.utility

import android.os.Handler
import android.os.Looper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject

object NetworkEvent {



    /*
     * Step 2: Create a PublishSubject instance
     * which we use to publish events to all
     * registered subscribers in the app.
     */
    private var subject: PublishSubject<NetworkState>? = null


    /*
     * Step 2: Create a method to fetch the Subject
     * or create it if it's not already in memory.
     */
    private fun getSubject(): PublishSubject<NetworkState> {
        if (subject == null) {
            subject = PublishSubject.create()
            subject!!.subscribeOn(AndroidSchedulers.mainThread())
        }
        return subject as PublishSubject<NetworkState>
    }

    /*
     * Step 3: Create a CompositeDisposable map.
     * We use CompositeDisposable to maintain the list
     * of subscriptions in a pool. And also to so that
     * we can dispose them all at once.
     */
    private val compositeDisposableMap = HashMap<Any, CompositeDisposable>()


    /*
     * Step 3: Create a method to fetch the CompositeDisposable Map
     * or create it if it's not already in memory.
     * We pass a key (in this case our key is the Activity or fragment
     * instance).
     */
    private fun getCompositeSubscription(anyObject: Any): CompositeDisposable {
        var compositeSubscription: CompositeDisposable? = compositeDisposableMap[anyObject]
        if (compositeSubscription == null) {
            compositeSubscription = CompositeDisposable()
            compositeDisposableMap[anyObject] = compositeSubscription
        }
        return compositeSubscription
    }


    /*
     * Step 4: Use this method to Publish the NetworkState
     * to all the specified subscribers of the subject.
     */
    fun publish(networkState: NetworkState) {
        Handler(Looper.getMainLooper())
            .post { getSubject().onNext(networkState) }
    }


    /*
     * Step 4: Use this method to subscribe to the specified subject
     * and listen for updates on that subject.
     * Pass in an object (in this case the activity or fragment instance)
     * to associate the registration, so that we can unsubscribe later.
     */
    fun register(lifecycle: Any, action: Consumer<NetworkState>) {
        val disposable = getSubject().subscribe(action)
        getCompositeSubscription(lifecycle).add(disposable)
    }


    /*
     * Step 4: Use this method to Unregister this particular object
     * from the bus, removing all subscriptions.
     * This should be called in order to avoid memory leaks
     */
    fun unregister(lifecycle: Any) {
        val compositeSubscription = compositeDisposableMap.remove(lifecycle)
        compositeSubscription?.dispose()
    }
}