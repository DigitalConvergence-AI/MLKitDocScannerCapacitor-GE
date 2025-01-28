package ge.galaxyeagle.mlkitscanner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.annotation.NonNull;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.mlkit.vision.documentscanner.GmsDocumentScanner;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;

import java.util.ArrayList;
import java.util.List;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.graphics.pdf.PdfDocument;
import android.os.ParcelFileDescriptor;
import android.graphics.pdf.PdfRenderer;

@CapacitorPlugin(name = "CapacitorMlkitDocScannerGE")
public class CapacitorMlkitDocScannerPluginGE extends Plugin {

    private ActivityResultLauncher<IntentSenderRequest> scannerLauncher;

    @Override
    public void load() {
        super.load();
        // Nur ein kurzer Hinweis, dass das Plugin geladen wurde
        Log.d("CapacitorMlkitDocScan", "Plugin geladen.");

        scannerLauncher = getActivity().registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        PluginCall savedCall = getSavedCall();
                        if (savedCall == null) {
                            Log.e("CapacitorMlkitDocScan", "Kein gespeicherter PluginCall vorhanden.");
                            return;
                        }

                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                GmsDocumentScanningResult scanningResult = GmsDocumentScanningResult
                                        .fromActivityResultIntent(data);
                                if (scanningResult != null) {
                                    handleScanResponse(scanningResult, savedCall);
                                } else {
                                    savedCall.reject("Kein Scan-Ergebnis erhalten.");
                                }
                            } else {
                                savedCall.reject("Keine Daten vom Scanner zurückgegeben.");
                            }
                        } else {
                            savedCall.reject("Scan abgebrochen oder kein Ergebnis.");
                        }
                    }
                });
    }

    @PluginMethod
    public void captureScan(PluginCall call) {
        int pageLimit = call.getInt("pageLimit", 0);
        boolean galleryImportAllowed = call.getBoolean("galleryImportAllowed", true);
        String resultFormat = call.getString("resultFormat", "JPG").toUpperCase(Locale.ROOT);
        String scannerMode = call.getString("scannerMode", "SCANNER_MODE_FULL");

        int lowerQuality = call.getInt("lowerQuality", 35);
        String docName = call.getString("docName", "scanned_document");

        if (lowerQuality < 20) {
            lowerQuality = 20;
        } else if (lowerQuality > 100) {
            lowerQuality = 100;
        }

        GmsDocumentScannerOptions.Builder builder = new GmsDocumentScannerOptions.Builder()
                .setGalleryImportAllowed(galleryImportAllowed);

        if (pageLimit > 0) {
            builder.setPageLimit(pageLimit);
        }

      
        switch (scannerMode) {
            case "SCANNER_MODE_BASE":
                builder.setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE);
                break;
            case "SCANNER_MODE_BASE_WITH_FILTER":
                builder.setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE_WITH_FILTER);
                break;
            case "SCANNER_MODE_FULL":
            default:
                builder.setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL);
                break;
        }

        if (resultFormat.equals("JPG")) {
            builder.setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG);
        } else {
            builder.setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF);
        }

        GmsDocumentScanner scannerClient = GmsDocumentScanning.getClient(builder.build());

 
        scannerClient.getStartScanIntent(getActivity())
                .addOnSuccessListener(new OnSuccessListener<android.content.IntentSender>() {
                    @Override
                    public void onSuccess(android.content.IntentSender intentSender) {
                        saveCall(call);
                        try {
                            IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
                            scannerLauncher.launch(request);
                        } catch (Exception e) {
                            call.reject("Error starting scanner intent: " + e.getMessage(), e);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        call.reject("Error getting scanner intent " + e.getMessage(), e);
                    }
                });
    }

    private void handleScanResponse(GmsDocumentScanningResult scanningResult, PluginCall call) {
        if (scanningResult == null) {
            call.reject("Scan-Ergebnis ist null.");
            return;
        }

        JSObject resultObject = new JSObject();
        int pageCount = scanningResult.getPages().size();
        resultObject.put("pageCount", pageCount);

        String docName = call.getString("docName", "scanned_document");
        int lowerQuality = call.getInt("lowerQuality", 100);

        if (scanningResult.getPdf() != null) {
            String pdfPath = scanningResult.getPdf().getUri().toString();
            String reducedPdfPath = reducePDFResolution(pdfPath, docName + ".pdf", lowerQuality);
            reducedPdfPath = renameFileWithLiveTimestamp(reducedPdfPath, docName);
            JSObject pdfObject = new JSObject();
            pdfObject.put("pdfUri", reducedPdfPath);
            pdfObject.put("pageCount", pageCount);
            resultObject.put("pdf", pdfObject);
        }
        else {
            List<JSObject> pagesArray = new ArrayList<>();
            for (int i = 0; i < pageCount; i++) {
                String imagePath = scanningResult.getPages().get(i).getImageUri().toString();
                String targetFileName = docName + "_page_" + (i + 1) + ".jpg";
                String processedImagePath;

                if (lowerQuality < 100) {
                    processedImagePath = reduceImageQuality(imagePath, targetFileName, lowerQuality);
                } else {
                    processedImagePath = copyImageToAndroidCache(imagePath, targetFileName);
                }
                processedImagePath = renameFileWithLiveTimestamp(processedImagePath, docName + "_page_" + (i + 1));

                JSObject pageData = new JSObject();
                pageData.put("imageUri", processedImagePath);
                pagesArray.add(pageData);
            }
            resultObject.put("pages", pagesArray);
        }

        call.resolve(resultObject);
    }

  
    private String reduceImageQuality(String sourcePath, String fileName, int quality) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String reducedImagePath = null;

        try {
            File sourceFile = new File(sourcePath.replace("file://", ""));
            inputStream = getActivity().getContentResolver().openInputStream(Uri.fromFile(sourceFile));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            File cacheDir = getActivity().getCacheDir();
            File reducedImageFile = new File(cacheDir, fileName);
            outputStream = new FileOutputStream(reducedImageFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            reducedImagePath = reducedImageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e("CapacitorMlkitDocScan", "Error reducing image quality: " + e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                Log.e("CapacitorMlkitDocScan", "Error closing streams: " + e.getMessage(), e);
            }
        }
        return reducedImagePath;
    }

   // To save to Android cache..
    private String copyImageToAndroidCache(String sourcePath, String fileName) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String newPath = null;

        try {
            File sourceFile = new File(sourcePath.replace("file://", ""));
            inputStream = getActivity().getContentResolver().openInputStream(Uri.fromFile(sourceFile));

            File cacheDir = getActivity().getCacheDir();
            File newFile = new File(cacheDir, fileName);
            outputStream = new FileOutputStream(newFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            newPath = newFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e("CapacitorMlkitDocScan", "Error copying image file: " + e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                Log.e("CapacitorMlkitDocScan", "Error closing streams: " + e.getMessage(), e);
            }
        }
        return newPath;
    }

   private String renameFileWithLiveTimestamp(String filePath, String newName) {
        File file = new File(filePath);
    
        String extension = filePath.endsWith(".pdf") ? ".pdf" : ".jpg";

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String newFileName = newName + "_" + timestamp + extension;

        File renamedFile = new File(file.getParent(), newFileName);
        if (file.renameTo(renamedFile)) {
            return renamedFile.getAbsolutePath();
        } else {
            Log.e("CapacitorMlkitDocScan", "Error renaming file.");
            return filePath;
        }
    }

    private String reducePDFResolution(String sourcePdfPath, String outputPdfName, int quality) {
        String outputPdfPath = null;
        PdfDocument pdfDocument = new PdfDocument();

        try (ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(
                new File(sourcePdfPath.replace("file://", "")), ParcelFileDescriptor.MODE_READ_ONLY);
                PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor)) {

            File cacheDir = getActivity().getCacheDir();
            File outputPdfFile = new File(cacheDir, outputPdfName);

            int pageCount = pdfRenderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = pdfRenderer.openPage(i);

                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                ByteArrayOutputStream compressedBitmapStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, compressedBitmapStream);
                Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                        compressedBitmapStream.toByteArray(),
                        0,
                        compressedBitmapStream.size());

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(page.getWidth(), page.getHeight(),
                        i + 1).create();
                PdfDocument.Page newPage = pdfDocument.startPage(pageInfo);
                newPage.getCanvas().drawBitmap(compressedBitmap, 0, 0, null);
                pdfDocument.finishPage(newPage);

                page.close();
                bitmap.recycle();
                compressedBitmap.recycle();
            }

            try (OutputStream outputStream = new FileOutputStream(outputPdfFile)) {
                pdfDocument.writeTo(outputStream);
                outputPdfPath = outputPdfFile.getAbsolutePath();
            }

        } catch (IOException e) {
            Log.e("CapacitorMlkitDocScan", "Error reducing PDF resolution: " + e.getMessage(), e);
        } finally {
            pdfDocument.close();
        }

        return outputPdfPath;
    }


 
}
