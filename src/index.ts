import { registerPlugin } from '@capacitor/core';

import type { CapacitorMlkitDocScannerPluginGE } from './definitions';

// Registrier das Plugin mit Name and Type
const CapacitorMlkitDocScannerGE = registerPlugin<CapacitorMlkitDocScannerPluginGE>(
  'CapacitorMlkitDocScannerGE',
  {
    web: () => import('./web').then((m) => new m.CapacitorMlkitDocumentScannerWeb()),
  },
);

export * from './definitions'; // Exportiere die TypeScript-Definitionen
export { CapacitorMlkitDocScannerGE }; // Exportiere das registrierte Plugin
