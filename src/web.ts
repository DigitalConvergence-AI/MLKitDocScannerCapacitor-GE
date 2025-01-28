import { WebPlugin } from '@capacitor/core';

import type { CapacitorMlkitDocScannerPluginGE, ScanResult, StartScanOptions } from './definitions';

export class CapacitorMlkitDocumentScannerWeb extends WebPlugin implements CapacitorMlkitDocScannerPluginGE {
  captureScan(_options: StartScanOptions): Promise<ScanResult> {
    // TODO: Implement the document scanning logic in the future
    throw new Error('Method not implemented.');
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
