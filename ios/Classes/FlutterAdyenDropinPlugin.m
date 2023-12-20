#import "FlutterAdyenDropinPlugin.h"
#import <flutter_adyen_dropin_plugin/flutter_adyen_dropin_plugin-Swift.h>

@implementation FlutterAdyenDropinPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [FlutterAdyenPlugin registerWithRegistrar:registrar];
}
@end
