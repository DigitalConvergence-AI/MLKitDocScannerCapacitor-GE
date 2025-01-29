// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorMlkitDocScannerGE",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorMlkitDocScannerGE",
            targets: ["CapacitorMlkitDocScannerGEPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "CapacitorMlkitDocScannerGEPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/CapacitorMlkitDocScannerGEPlugin"),
        .testTarget(
            name: "CapacitorMlkitDocScannerGEPluginTests",
            dependencies: ["CapacitorMlkitDocScannerGEPlugin"],
            path: "ios/Tests/CapacitorMlkitDocScannerGEPluginTests")
    ]
)