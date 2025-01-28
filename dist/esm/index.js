import { registerPlugin } from '@capacitor/core';
// Registrier das Plugin mit Name and Type
const CapacitorMlkitDocScannerGE = registerPlugin('CapacitorMlkitDocScannerGE', {
    web: () => import('./web').then((m) => new m.CapacitorMlkitDocumentScannerWeb()),
});
export * from './definitions'; // Exportiere die TypeScript-Definitionen
export { CapacitorMlkitDocScannerGE }; // Exportiere das registrierte Plugin
//# sourceMappingURL=index.js.map