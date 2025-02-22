export interface StartScanOptions {
  pageLimit?: number;
  galleryImportAllowed?: boolean;
  scannerMode?: 'SCANNER_MODE_BASE' | 'SCANNER_MODE_BASE_WITH_FILTER' | 'SCANNER_MODE_FULL';
  resultFormat?: ('JPEG' | 'PDF');
}

export interface Page {
  imageUri: string;
}

export interface Pdf {
  pdfUri: string;
  pageCount: number;
}

export interface ScanResult {
  pages: Page[];
  pdf?: Pdf;
}

export interface CapacitorMlkitDocScannerPluginGE{
  captureScan(options: StartScanOptions): Promise<ScanResult>;
}
