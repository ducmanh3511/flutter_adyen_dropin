package app.adyen.flutter_adyen_dropin

import android.app.Activity
import androidx.annotation.NonNull
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.core.Environment
import com.adyen.checkout.core.internal.util.LocaleUtil
import com.adyen.checkout.dropin.DropIn
import com.adyen.checkout.dropin.DropInConfiguration
import com.adyen.checkout.sessions.core.CheckoutSessionProvider
import com.adyen.checkout.sessions.core.CheckoutSessionResult
import com.adyen.checkout.sessions.core.SessionModel
import io.flutter.embedding.android.FlutterFragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar

/** FlutterAdyenDropinPlugin */
class FlutterAdyenDropinPlugin: FlutterPlugin, MethodCallHandler, FlutterDropInCallback {
  private lateinit var channel : MethodChannel
  private var flutterResult: Result? = null

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_adyen_dropin")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "openDropIn") {
      flutterResult = result
      CoroutineScope(Dispatchers.IO).launch {
        AdyenSetup.activity?.let {
          dropIn(call, it)
        } ?: result.error(
            "1",
            "Activity not set",
            "Call AdyenCheckout.setActivity(this) on MainActivity.onCreate()"
        )
      }
    }
    else {
      result.notImplemented()
    }
  }

  private suspend fun dropIn(call: MethodCall, activity: FlutterFragmentActivity) {
    val sessionInfo = call.argument<Map<String, Any>>("sessionInfo") ?: emptyMap()
    val clientKey = call.argument<String>("clientKey") ?: ""
    val environment = call.argument<String>("environment") ?: "TEST"
    val countryCode = call.argument<String>("countryCode") ?: "ko-KR"

    if (clientKey.isEmpty()) {
        flutterResult?.error(
            "2",
            "clientKey is Empty",
            "Please input a clientKey"
        )
        return
    }

    val env = when (environment) {
      "LIVE_US" -> Environment.UNITED_STATES
      "LIVE_AUSTRALIA" -> Environment.AUSTRALIA
      "LIVE_EUROPE" -> Environment.EUROPE
      "LIVE_INDIA" -> Environment.INDIA
      "LIVE_APSE" -> Environment.APSE
      else -> Environment.TEST
    }
      val locale = Locale.forLanguageTag(countryCode)

    val cardConfiguration = CardConfiguration.Builder(
        locale,
        env,
        clientKey
    ).setHolderNameRequired(true).build()

    val dropInConfiguration = DropInConfiguration.Builder(
        locale,
        env,
        clientKey
    ).addCardConfiguration(cardConfiguration).build()

    val sessionModel = SessionModel.SERIALIZER.deserialize(JSONObject(sessionInfo))

    when (val result =
        CheckoutSessionProvider.createSession(sessionModel, dropInConfiguration, null)) {
        is CheckoutSessionResult.Success -> {
          AdyenSetup.addDropInListener(this)
          AdyenSetup.dropInLauncher?.let {
              DropIn.startPayment(
                  activity,
                  it,
                  result.checkoutSession,
                  dropInConfiguration,
              )
          } ?: flutterResult?.error(
                  "1",
                  "dropInLauncher not set",
                  "Call AdyenCheckout.setLauncherActivity(this) on MainActivity.onCreate()"
              )
              return
        }

        is CheckoutSessionResult.Error -> {
          run {
            Log.e("FlutterAdyen", result.exception.message.toString())
          }
        }
    }
    }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

    override fun onCancelledByUser() {
        flutterResult?.success("PAYMENT_CANCELED")
    }
    override fun onError(reason: String?) {
        flutterResult?.success(reason)
    }

    override fun onFinished(result: String) {
        flutterResult?.success(result)
    }
}

internal interface FlutterDropInCallback {
    fun onCancelledByUser()
    fun onError(reason: String?)
    fun onFinished(result: String)
}
