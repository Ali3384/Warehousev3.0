package com.example.warehousev30;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.warehousev30.databinding.ActivityMainBinding;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper = new DatabaseHelper(this);
    private ActivityMainBinding binding;
    private WarehouseDataSource dataSource;
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showCamera();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });
    //tut deystvie pri skanere
    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result ->{
       ScanOptions options = new ScanOptions();
       options.setDesiredBarcodeFormats(ScanOptions.EAN_13);
        if (result.getContents() != null) {
            // If QR code is scanned successfully, retrieve the item place from the database
            String itemId = result.getContents();
            String itemPlace = dataSource.getItemPlaceById(itemId);
            if (itemPlace != null) {
                setResult(itemPlace);
            } else {
                Toast.makeText(this, "Item place not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error scanning QR code", Toast.LENGTH_SHORT).show();
        }
    });

    // Set the result (item place) to a dialog
    private void setResult(String itemPlace) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Item Place: " + itemPlace)
                .setTitle("Result")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //tut zakanchivaetsya
    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.EAN_13);
        options.setPrompt("Zeskanuj kod");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);
        qrCodeLauncher.launch(options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initViews();

        dataSource = new WarehouseDataSource(this);
        dataSource.open(); // Open the database connection
    }

    private void copyDatabaseIfNeeded() {

        databaseHelper.copyDatabaseFromResources(this);
    }


    private void initViews() {
        binding.fab.setOnClickListener(v -> checkPermissionAndShowActivity(this));
    }

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;

    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            showCamera();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Trzeba dostep do kamery", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCamera();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close(); // Close the database connection
    }
}