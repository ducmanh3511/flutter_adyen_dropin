//  Created by Joseph Nguyễn on 07/12/2023.
//

import Flutter
import Adyen

public class FlutterAdyenPlugin: NSObject, FlutterPlugin {

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "flutter_adyen_dropin", binaryMessenger: registrar.messenger())
        let instance = FlutterAdyenPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    var mResult: FlutterResult?
    var topController: UIViewController?
    var dropInComponent: DropInComponent?
    var session: AdyenSession?

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard call.method.elementsEqual("openDropIn") else { return }

        let arguments = call.arguments as? [String: Any]
        let sessionData = arguments?["sessionData"] as? String
        let clientKey = arguments?["clientKey"] as? String
        let currency = arguments?["currency"] as? String
        let value = arguments?["value"] as? Int
        let sessionId = arguments?["sessionId"] as? String
        let environment = arguments?["environment"] as? String
        let countryCode = String((arguments?["countryCode"] as? String)?.split(separator: "-").last ?? "KR")
        mResult = result

        var env = Environment.test
        if(environment == "LIVE_UNITED_STATES") {
            env = Environment.liveUnitedStates
        } else if (environment == "LIVE_AUSTRALIA"){
            env = Environment.liveAustralia
        } else if (environment == "LIVE_EUROPE"){
            env = Environment.liveEurope
        } else if (environment == "LIVE_INDIA") {
            env = Environment.liveIndia
        } else if (environment == "LIVE_APSE") {
            env = Environment.liveApse
        }

        guard let clientKey = clientKey, let value =  value, let currency = currency, let sessionId = sessionId, let sessionData = sessionData else { 
            result("missing data")
            return 
        }

        let apiContext = try! APIContext(environment: env, clientKey: clientKey)
        let amount = Amount(value: value, currencyCode: currency)
        let payment = Payment(amount: amount, countryCode: countryCode)
        let adyenContext = AdyenContext(apiContext: apiContext, payment: payment)
        
        let configuration = AdyenSession.Configuration(sessionIdentifier: sessionId, initialSessionData: sessionData, context: adyenContext)
        
        AdyenSession.initialize(with: configuration, delegate: self, presentationDelegate: self) { [weak self] result in
            switch result {
                case let .success(session):
                    self?.session = session
                    let dropInConfiguration = DropInComponent.Configuration()
                    dropInConfiguration.card.showsHolderNameField = true
                    let dropInComponent = DropInComponent(paymentMethods: session.sessionContext.paymentMethods,
                                                        context: adyenContext,
                                                        configuration: dropInConfiguration)
                    self?.dropInComponent = dropInComponent

                    guard let dropInComponent = self?.dropInComponent, let session = self?.session else { return }
                    dropInComponent.delegate = session
                    dropInComponent.partialPaymentDelegate = session
                    if var topController = UIApplication.shared.keyWindow?.rootViewController {
                        self?.topController = topController
                        while let presentedViewController = topController.presentedViewController{
                            topController = presentedViewController
                        }
                        topController.present(dropInComponent.viewController, animated: true)
                    }
                    break
                case let .failure(error):
                    self?.mResult?(error.localizedDescription)
            }
        }
    }
}

extension FlutterAdyenPlugin: AdyenSessionDelegate, PresentationDelegate {
    public func present(component: Adyen.PresentableComponent) {
    }
    
    public func didComplete(with result: Adyen.AdyenSessionResult, component: Adyen.Component, session: Adyen.AdyenSession) {
        component.stopLoadingIfNeeded()
        self.topController?.dismiss(animated: true)
        self.mResult?(result.resultCode.rawValue)
    }
    
    public func didFail(with error: Error, from component: Adyen.Component, session: Adyen.AdyenSession) {
        self.topController?.dismiss(animated: true)
        if case ComponentError.cancelled = error {
            self.mResult?("PAYMENT_CANCELED")
        } else {
            self.mResult?(error.localizedDescription)
        }
    }
    
    public func didOpenExternalApplication(component: Adyen.ActionComponent, session: Adyen.AdyenSession) {
    }
}