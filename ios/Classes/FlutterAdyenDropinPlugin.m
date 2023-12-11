#import "FlutterAdyenDropinPlugin.h"
#import <flutter_adyen_dropin-Swift.h>

@implementation FlutterAdyenDropinPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [FlutterAdyenPlugin registerWithRegistrar:registrar];
}
@end
