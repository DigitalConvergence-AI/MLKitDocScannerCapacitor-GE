import { WebPlugin } from '@capacitor/core';
import type { CapacitorMlkitDocScannerPluginGE, ScanResult, StartScanOptions } from './definitions';
export declare class CapacitorMlkitDocumentScannerWeb extends WebPlugin implements CapacitorMlkitDocScannerPluginGE {
    captureScan(_options: StartScanOptions): Promise<ScanResult>;
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
}
