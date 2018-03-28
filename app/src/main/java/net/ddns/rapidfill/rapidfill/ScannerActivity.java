package net.ddns.rapidfill.rapidfill;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class ScannerActivity extends AppCompatActivity implements View.OnClickListener {

    //QR scanner
    SurfaceView cameraPreview;
    TextView textResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    String code;
    int width, height;
    final int RequestCameraPermissionID = 1001;
    boolean debug_ok = true;

    //Links & requests
    final String requestSumLink = "http://dunno.ddns.net/Debug/requestSum.php";
    final String requestPaymentLink = "http://dunno.ddns.net/Debug/requestServerConfirmation.php";

    //UI
    ProgressDialog loadingDialog;
    AlertDialog payDialog;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);


        cameraPreview = findViewById(R.id.cameraSurfaceView);
        textResult = findViewById(R.id.debugTextResult);

        loadingDialog = new ProgressDialog(this);


        textResult.setText(width + " " + height);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 0) {
                    textResult.post(new Runnable() {
                        @Override
                        public void run() {
                            code = qrcodes.valueAt(0).displayValue;
                            textResult.setText(code);
                            if(debug_ok) {
                                showDialog();
                                requestPaymentSum(code);
                                debug_ok = false;
                            }
                        }
                    });
                }
            }
        });

        cameraPreview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // Ensure you call it only once :
                cameraPreview.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                height = cameraPreview.getHeight();
                width = cameraPreview.getWidth();

                cameraSource = new CameraSource.Builder(ScannerActivity.this, barcodeDetector)
                        .setRequestedPreviewSize(height, width)
                        .build();

                cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ScannerActivity.this,
                                    new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                            return;
                        }
                        try {
                            cameraSource.start(cameraPreview.getHolder());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        cameraSource.stop();
                    }
                });
                // Here you can get the size :)
            }
        });


    }
    void requestPaymentSum(String code) {
        //parsare code ??
        final HashMap<String, String> paramHash;
        paramHash = new HashMap<>();
        paramHash.put("whereGetSum", code);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestSumLink,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        double sum = Double.parseDouble(response);
                        if(sum > 0) {
                            confirmPayment(sum);
                        } else {
                            displayServerResponse(false);
                        }
                        Toast.makeText(ScannerActivity.this, "A raspuns suma!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayServerResponse(false);
                Log.d("mylog", "Volley error : " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                if (paramHash == null)
                    return null;
                Map<String, String> params = new HashMap<>();
                for (String key : paramHash.keySet()) {
                    params.put(key, paramHash.get(key));
                    Log.d("mylog", "Key : " + key + " Value : " + paramHash.get(key));
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    void confirmPayment(final double amount) {
        AlertDialog.Builder payDialogAux;
        View payView;
        TextView amountTextView;
        Button confirmPaymentButton;

        payDialogAux = new AlertDialog.Builder(ScannerActivity.this);
        payView = getLayoutInflater().inflate(R.layout.pay_layout, null);
        amountTextView = (TextView) payView.findViewById(R.id.amountTextView);
        confirmPaymentButton = (Button) payView.findViewById(R.id.btnConfirmPay);
        amountTextView.setText(amount + "");
        confirmPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientConfirmation();
            }
        });
        payDialogAux.setView(payView);
        payDialog = payDialogAux.create();
        payDialog.setCancelable(false);
        payDialog.show();
        loadingDialog.dismiss();
    }

    void clientConfirmation() {


        //call api
        //select payment method for amount
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(MenuActivity.token);
        startActivityForResult(dropInRequest.getIntent(this), 1);

        //call when ok serverConfirmation(nonce)
        //else
        //call displayServerResponse(false)
    }

    void serverConfirmation(String nonce) {
        //parsare code ??
        final HashMap<String, String> paramHash;
        paramHash = new HashMap<>();
        paramHash.put("nonce", nonce);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestPaymentLink,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("ok")) {
                            displayServerResponse(true);
                        } else {
                            displayServerResponse(false);
                        }
                        Toast.makeText(ScannerActivity.this, "A raspuns serverul cu plata!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayServerResponse(false);
                Log.d("mylog", "Volley error : " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                if (paramHash == null)
                    return null;
                Map<String, String> params = new HashMap<>();
                for (String key : paramHash.keySet()) {
                    params.put(key, paramHash.get(key));
                    Log.d("mylog", "Key : " + key + " Value : " + paramHash.get(key));
                }

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(stringRequest);
    }
    void dismissDialog() {
        loadingDialog.dismiss();
    }
    void showDialog() {
        loadingDialog.setMessage("Processing...");
        loadingDialog.show();
        loadingDialog.setCancelable(false);
    }
    void displayServerResponse(boolean ok) {
        dismissDialog();
        debug_ok = true;
        textResult.setText(ok + "");
        if(ok) {
            Toast.makeText(ScannerActivity.this, "Plata efectuata!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ScannerActivity.this, "Plata nu a fost efectuata!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                // use the result to update your UI and send the payment method nonce to your server
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                String stringNonce = nonce.getNonce();
                payDialog.dismiss();
                loadingDialog.show();
                serverConfirmation(stringNonce);
                Toast.makeText(ScannerActivity.this, stringNonce, Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }
    }

    @Override
    public void onClick(View view) {

    }
}
