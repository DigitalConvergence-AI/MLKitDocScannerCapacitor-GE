'use strict';

var core = require('@capacitor/core');

// Registrier das Plugin mit Name and Type
const CapacitorMlkitDocScannerGE = core.registerPlugin('CapacitorMlkitDocScannerGE', {
    web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.CapacitorMlkitDocumentScannerWeb()),
});

class CapacitorMlkitDocumentScannerWeb extends core.WebPlugin {
    captureScan(_options) {
        // TODO: Implement the document scanning logic in the future
        throw new Error('Method not implemented.');
    }
    async echo(options) {
        console.log('ECHO', options);
        return options;
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    CapacitorMlkitDocumentScannerWeb: CapacitorMlkitDocumentScannerWeb
});

exports.CapacitorMlkitDocScannerGE = CapacitorMlkitDocScannerGE;
//# sourceMappingURL=plugin.cjs.js.map
