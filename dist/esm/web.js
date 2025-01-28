import { WebPlugin } from '@capacitor/core';
export class CapacitorMlkitDocumentScannerWeb extends WebPlugin {
    captureScan(_options) {
        // TODO: Implement the document scanning logic in the future
        throw new Error('Method not implemented.');
    }
    async echo(options) {
        console.log('ECHO', options);
        return options;
    }
}
//# sourceMappingURL=web.js.map