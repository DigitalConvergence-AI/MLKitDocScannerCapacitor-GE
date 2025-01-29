import Foundation
import Capacitor
import UIKit

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorMlkitDocScannerPluginGEV1)
public class CapacitorMlkitDocScannerPluginGEV1: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "CapacitorMlkitDocScannerPluginGEV1"
    public let jsName = "CapacitorMlkitDocScannerGEV1"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = CapacitorMlkitDocScannerGEV1()
    
    
//will be inplemented once google release MLKIT vision for iOS
//https://developers.google.com/ml-kit/vision/doc-scanner/iOS

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
}
