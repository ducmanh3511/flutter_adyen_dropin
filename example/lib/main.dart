import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_adyen_dropin/flutter_adyen_dropin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: CupertinoButton(
              child: const Text("Drop In"),
              onPressed: () {
                FlutterAdyenDropIn.openDropIn(
                  sessionData: "<YOUR-SESSION-DATA>",
                  clientKey: "<YOUR-CLIENT-KEY>",
                  currency: "USD",
                  value: 1000,
                  sessionId: "<YOUR-SESSION-ID>",
                  countryCode: "en-US",
                  sessionInfo: {"FIELD": "<YOUR-SESSION-INFO>"},
                );
              }),
        ),
      ),
    );
  }
}
