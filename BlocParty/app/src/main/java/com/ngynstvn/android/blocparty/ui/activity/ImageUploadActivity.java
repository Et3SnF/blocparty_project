package com.ngynstvn.android.blocparty.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ngynstvn.android.blocparty.BPUtils;
import com.ngynstvn.android.blocparty.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ngynstvn on 11/5/15.
 */

public class ImageUploadActivity extends AppCompatActivity {

    private static final String TAG = BPUtils.classTag(ImageUploadActivity.class);
    private static final String CLASS_TAG = CameraActivity.class.getSimpleName();

    private static final int inputLimit = 250;
    private static final int IMAGE_ERROR = 0;
    private static final int UPLOAD_ERROR = 1;

    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private Menu menu;

    private ImageView previewImage;
    private EditText captionInputBox;

    private RelativeLayout unDownloadLayout;
    private RelativeLayout downloadingLayout;
    private RelativeLayout downloadedLayout;
    private Button downloadImageBtn;
    private ProgressBar downloadImgProgBar;

    private CheckBox fbUploadCheckbox;
    private CheckBox twUploadCheckbox;
    private CheckBox igUploadCheckbox;

    private URI imageFileURI;

    // ----- Lifecycle Methods ----- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        sharedPreferences = BPUtils.newSPrefInstance(BPUtils.SN_UPLOAD_STATES);

        imageFileURI = (URI) getIntent().getSerializableExtra(BPUtils.IMAGE_URI);

        if(imageFileURI == null) {
            displayErrorDialog(IMAGE_ERROR);
            return;
        }

        setProgressBarIndeterminateVisibility(false);
        setContentView(R.layout.activity_image_upload);

        toolbar = (Toolbar) findViewById(R.id.tb_activity_image_upload);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Upload Image");

        previewImage = (ImageView) findViewById(R.id.iv_preview_image);
        captionInputBox = (EditText) findViewById(R.id.et_caption_input);
        unDownloadLayout = (RelativeLayout) findViewById(R.id.rl_not_download_image);
        downloadingLayout = (RelativeLayout) findViewById(R.id.rl_downloading_image);
        downloadedLayout = (RelativeLayout) findViewById(R.id.rl_downloaded_image);
        downloadImageBtn = (Button) findViewById(R.id.btn_download_image);
        downloadImgProgBar = (ProgressBar) findViewById(R.id.pb_download_image);
        fbUploadCheckbox = (CheckBox) findViewById(R.id.cb_fb_share_select);
        twUploadCheckbox = (CheckBox) findViewById(R.id.cb_tw_share_select);
        igUploadCheckbox = (CheckBox) findViewById(R.id.cb_ig_share_select);

        Log.v(TAG, "Loaded Uri toString(): " + imageFileURI.toString());
        Picasso.with(this).load(imageFileURI.toString()).rotate(90).fit().into(previewImage);

        captionInputBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(inputLimit)});

        downloadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage();
            }
        });

        fbUploadCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.SN_UPLOAD_STATES,
                            BPUtils.FB_UPLOAD, true);
                } else {
                    BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.SN_UPLOAD_STATES,
                            BPUtils.FB_UPLOAD, false);
                }
            }
        });

        twUploadCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.SN_UPLOAD_STATES,
                            BPUtils.TW_UPLOAD, true);
                }
                else {
                    BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.SN_UPLOAD_STATES,
                            BPUtils.TW_UPLOAD, false);
                }
            }
        });

        igUploadCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.SN_UPLOAD_STATES,
                            BPUtils.IG_UPLOAD, true);
                }
                else {
                    BPUtils.putSPrefBooleanValue(sharedPreferences, BPUtils.SN_UPLOAD_STATES,
                            BPUtils.IG_UPLOAD, false);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart() called");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState() called");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause() called");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop() called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy() called");
        super.onDestroy();
    }

    // -----   -----  -----  -----  ----- //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(TAG, "onCreateOptionsMenu() called");
        getMenuInflater().inflate(R.menu.menu_upload_image, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "onOptionsItemSelected() called");

        if(item.getItemId() == R.id.action_cancel_image_upload) {
            Log.e(TAG, "Cancel Image Upload Clicked");
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        else if(item.getItemId() == R.id.action_upload_image) {
            Log.e(TAG, "Upload Image Clicked");

            if(captionInputBox != null) {
                String input = captionInputBox.getText().toString();

                // Find a way to associate image with the caption and the social networks

                boolean canUploadToFB = sharedPreferences.getBoolean(BPUtils.FB_UPLOAD, false);
                boolean canUploadToTW = sharedPreferences.getBoolean(BPUtils.TW_UPLOAD, false);
                boolean canUploadToIG = sharedPreferences.getBoolean(BPUtils.IG_UPLOAD, false);

                if(!canUploadToFB && !canUploadToTW && !canUploadToIG) {
                    displayErrorDialog(UPLOAD_ERROR);
                    return false;
                }

                if(canUploadToFB) {
                    uploadToFacebook();
                }

                if(canUploadToTW) {
                    uploadToTwitter();
                }

                if(canUploadToIG) {
                    uploadToInstagram();
                }

            }

            BPUtils.clearSPrefTable(sharedPreferences, BPUtils.SN_UPLOAD_STATES);

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    // ----- Error Dialog Popup (Not Fragment) ----- //

    private void displayErrorDialog(final int errorCode) {

        String errorMessage = null;

        switch (errorCode) {
            case IMAGE_ERROR:
                errorMessage = "There was an error processing the photo. Try taking another picture.";
                break;
            case UPLOAD_ERROR:
                errorMessage = "There must be at least one social network selected in order " +
                        "to post this photo.";
                break;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog
                .setMessage(errorMessage)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (errorCode == IMAGE_ERROR) {
                            startActivity(new Intent(ImageUploadActivity.this, CameraActivity.class));
                        }

                        dialog.dismiss();
                    }
                });

        alertDialog.create();
    }

    /**
     *
     * Download Method
     *
     */

    private void downloadImage() {
        new DownloadImageTask().execute();
    }

    /**
     *
     * Upload Methods
     *
     */

    private void uploadToFacebook() {

    }

    private void uploadToTwitter() {

    }

    private void uploadToInstagram() {

    }

    /**
     *
     * AsyncTask classes
     *
     */

    private class DownloadImageTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            // I include everything for safety measure
            unDownloadLayout.setVisibility(View.GONE);
            downloadingLayout.setVisibility(View.VISIBLE);
            downloadedLayout.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String imageFileName = "IMAGE_BP_" + timeStamp + ".jpg";

            try {
                String uriPath = imageFileURI.getPath();
                String destinationDirectory = Environment.getExternalStorageDirectory() + "/Blocparty/";

                File storageDirectory = new File(destinationDirectory);

                if(!storageDirectory.exists()) {
                    storageDirectory.mkdir();
                }

                // Initiate input stream buffer by having a stream of bytes from a file.
                // In this case the stream of bytes from a file comes from the uri (which actually
                // opens a file using that provided path)
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(imageFileURI)));

                // Initiate output stream buffer by having a stream of bytes from a file that is
                // is going to be outputted. It needs a particular path of the desired file
                // or a File object. This line will allow the bytes to be written onto a file.

                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new
                        FileOutputStream(destinationDirectory + imageFileName));

                // Initiate the bytes array so that the BufferedInputStream could insert bytes into
                // the bytes array (from read() method) and then the BufferedOutputStream will
                // use that bytes array to write to the desired file to save.

                byte[] bytes = new byte[(int) new File(imageFileURI).length()];
                Log.v(TAG, "Bytes Length: " + bytes.length); // nearly 2 MB per picture
                int progress = 0;

                do {
                    bufferedOutputStream.write(bytes);

                    // Keep track as to how much bytes has been written out of the total bytes
                    // Set the progress
                    progress++;
                    publishProgress((int) (((float) progress / bytes.length) * 100));
                }
                while (bufferedInputStream.read(bytes) != -1);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.v(TAG, "Progress Update: " + progress[0]);
            downloadImgProgBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            unDownloadLayout.setVisibility(View.GONE);
            downloadingLayout.setVisibility(View.GONE);
            downloadedLayout.setVisibility(View.VISIBLE);
        }
    }

    private class FBUploadPostTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class TWUploadPostTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class IGUploadPostTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}

