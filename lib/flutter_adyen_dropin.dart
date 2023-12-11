import 'dart:async';

import 'package:flutter/services.dart';

class FlutterAdyenDropIn {
  static const MethodChannel _channel = MethodChannel('flutter_adyen_dropin');

  static Future<String> openDropIn({
    required String sessionData,
    required String clientKey,
    required String currency,
    required int value,
    required String sessionId,

    ///Example: en-US
    required String countryCode,

    ///Data from request API /sessions [required for Android]
    required Map<String, dynamic> sessionInfo,

    ///['TEST'] for test environment ['LIVE_EUROPE', 'LIVE_UNITED_STATES', 'LIVE_AUSTRALIA', 'LIVE_INDIA', 'LIVE_APSE']
    String environment = 'TEST',
  }) async {
    Map<String, dynamic> args = {};
    args.putIfAbsent('sessionData', () => sessionData);
    args.putIfAbsent('clientKey', () => clientKey);
    args.putIfAbsent('currency', () => currency);
    args.putIfAbsent('clientKey', () => clientKey);
    args.putIfAbsent('value', () => value);
    args.putIfAbsent('sessionId', () => sessionId);
    args.putIfAbsent('countryCode', () => countryCode);
    args.putIfAbsent('environment', () => environment);
    args.putIfAbsent('sessionInfo', () => sessionInfo);

    final String response = await _channel.invokeMethod('openDropIn', args);
    return response;
  }
}
