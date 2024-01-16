package app.adyen.flutter_adyen_dropin

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import com.adyen.checkout.dropin.DropIn
import com.adyen.checkout.dropin.SessionDropInCallback
import com.adyen.checkout.dropin.SessionDropInResult
import com.adyen.checkout.dropin.internal.ui.model.SessionDropInResultContractParams
import io.flutter.embedding.android.FlutterFragmentActivity
import java.lang.ref.WeakReference

object AdyenSetup {
    private val dropInCallback = DropInCallbackListener()
    internal var dropInLauncher: ActivityResultLauncher<SessionDropInResultContractParams>? = null
    internal var activity: FlutterFragmentActivity? = null

    @JvmStatic
    internal fun addDropInListener(callback: FlutterDropInCallback) {
        dropInCallback.callback = WeakReference(callback)
    }

    /**
     * Persist a reference to Activity that will present DropIn or Component
     * @param activity  parent activity for DropIn or Component
     */
    @JvmStatic
    fun setLauncherActivity(activity: ActivityResultCaller) {
        dropInLauncher = DropIn.registerForDropInResult(
            activity, dropInCallback
        )
    }

    @JvmStatic
    fun setActivity(activity: FlutterFragmentActivity) {
        this.activity = activity
    }
}

private class DropInCallbackListener : SessionDropInCallback {

    var callback: WeakReference<FlutterDropInCallback> =
        WeakReference(null)

    override fun onDropInResult(sessionDropInResult: SessionDropInResult?) {
        if (sessionDropInResult == null ) return
        callback.get()?.let {
            when (sessionDropInResult) {
                is SessionDropInResult.CancelledByUser -> it.onCancelledByUser()
                is SessionDropInResult.Error -> it.onError(sessionDropInResult.reason)
                is SessionDropInResult.Finished -> sessionDropInResult.result.resultCode?.let { result ->
                    it.onFinished(
                        result
                    )
                }
            }
        }
    }
}