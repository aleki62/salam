package com.parmissmarthome.parmis_smart_home;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.parmissmarthome.parmis_smart_home.db.mainmenudb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class addaction extends AppCompatActivity {

    private static final int REQUEST_PICK_CODE = 1, REQUEST_CROP_ICON= 10;
    public static final int actionLamp=0, actionCurtain=1, actionTV=2;

    private String selectedImagePath;
    private int RemoteCode;
    String remoteKey;

    private int ifToggle=0;
    Long editID;
    int saveme;
    private ImageView img;
//    private ToggleButton btnlamp, btncurtian;
    private Camera camera;
    private int cameraId = 0;
    mainmenudb db;
    private Bitmap bitmap;

    Gallery galleryaction;
    Integer[] imageIDs = {
            R.drawable.lamp,
            R.drawable.curtian,
            R.drawable.tv
    };
    private int selaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addaction);

        db=new mainmenudb(this);
        img= (ImageView) findViewById(R.id.imgLamp);

        if(MainActivity.isClient)
            saveme=1; else saveme=0;
        editID= Long.valueOf(-1);
        Bundle b = getIntent().getExtras();
        if (b!=null) {
            editID = b.getLong("ID", -1);
            if (editID != -1) {
                saveme= b.getInt("saveme");
                Log.d(MainActivity.Tag, "ورود به ویرایش");
//            db.insertrecord(txtn.getText().toString(), MainActivity.infoMe.andMacAddress, RemoteCode, txtg.getText().toString(), selectedImagePath, ifToggle, 0, 0, 0);
                Cursor cursor = db.query("t1." + mainmenudb.MainMenu_ID + "=" + editID, null);
                EditText txtn = (EditText) findViewById(R.id.txtnamelamp);
                EditText txtg = (EditText) findViewById(R.id.txtgroupkeys);

                txtn.setText(cursor.getString(cursor.getColumnIndex(mainmenudb.MainMenu_Name)));
                txtg.setText(db.getGroupName(cursor.getLong(cursor.getColumnIndex(mainmenudb.MainMenu_Parent)),
                                             cursor.getString(cursor.getColumnIndex(mainmenudb.MainMenu_Server))));

                remoteKey = cursor.getString(cursor.getColumnIndex(mainmenudb.MainMenu_RemoteKey));
                RemoteCode = cursor.getInt(cursor.getColumnIndex(mainmenudb.MainMenu_RemoteCode));
                selectedImagePath = cursor.getString(cursor.getColumnIndex(mainmenudb.MainMenu_Image));
                if (selectedImagePath!= null)
                    img.setImageURI(Uri.parse(selectedImagePath));
                ifToggle = cursor.getInt(cursor.getColumnIndex(mainmenudb.MainMenu_Vaz));
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AutoCompleteTextView atxt= (AutoCompleteTextView) findViewById(R.id.txtgroupkeys);
        atxt.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, db.querygroup(null, null)));

        galleryaction = (Gallery) findViewById(R.id.glrselaction);
        galleryaction.setAdapter(new ImageAdapter(this));
        galleryaction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getBaseContext(), "pic" + (position + 1) + " selected",
//                        Toast.LENGTH_SHORT).show();
                selaction = position;
                if (selaction == actionTV)
                    ifToggle = script.ScriptTv;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
//            setMeCode();
        });
/*
        btncurtian= (ToggleButton) findViewById(R.id.btnselAddCurtian);
        btncurtian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnlamp.setChecked(false);
                btncurtian.setChecked(true);
            }
        });
        btnlamp = (ToggleButton) findViewById(R.id.btnselAddLamp);
        btnlamp.setChecked(true);
        btnlamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnlamp.setChecked(true);
                btncurtian.setChecked(false);
            }
        });*/
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                    .show();
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
            }
        }
        setMeCode();
    }
    public void pickimage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_PICK_CODE);
    }
    private void setMeCode(){
        remoteKey= MainActivity.infoMe.andMacAddress;
        RemoteCode= db.getRemotecode();
        switch (selaction){
            case actionLamp:
                ifToggle= script.ScriptOFF;
                break;
            case actionCurtain:
                ifToggle= script.ScriptCurtianAndroid;
                break;
            case actionTV:
                ifToggle= script.ScriptTv;
                break;
        }
        if (RemoteCode==-1)
            Toast.makeText(this, "کد خالی وجود ندارد...", Toast.LENGTH_LONG).show();
    }
    public void onSetMeCode(View view) {
        setMeCode();
    }
    public void onSetRemoteCode(View view){
        RemoteCode= -1;
        switch (selaction){
            case actionLamp:
                ifToggle= script.ScriptToggle;
                break;
            case actionCurtain:
                ifToggle= script.ScriptCurtianToggle;
                break;
            case actionTV:
                ifToggle= script.ScriptTv;
                break;
        }

        MainActivity.progressBar = new ProgressDialog(view.getContext());
        MainActivity.progressBar.setCancelable(true);
        MainActivity.progressBar.setMessage("دریافت کد از ریموت ...");
        MainActivity.progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        MainActivity.progressBar.setProgress(0);
        MainActivity.progressBar.setMax(100);
        MainActivity.progressBar.show();
        Log.d(MainActivity.Tag, "::دریافت کد از ریموت");
        MainActivity.mTcpClient.sendMessage("*GETRF*" + (char) 0xc1);
    }
    static final int REQUEST_IMAGE_CAPTURE = 3;

    public void takeimage(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        selectedImagePath = "file:" + image.getAbsolutePath();
        Log.d(MainActivity.Tag, selectedImagePath + " فایل ");
        return image;
    }

    //    public String getPath(Uri uri) {
        /*String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);*/
//        File myFile = new File(uri.getPath());
//        return myFile.getPath();
        /*String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = uri.getPath();
        } else {
            cursor.moveToFirst()   ;
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;*/
//    }
    @SuppressLint("NewApi")
    private String getPath(Uri uri) {
        if( uri == null ) {
            return null;
        }

        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor;
        if(Build.VERSION.SDK_INT >19)
        {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, sel, new String[]{ id }, null);
        }
        else
        {
            cursor = getContentResolver().query(uri, projection, null, null, null);
        }
        String path = null;
        try
        {
            int column_index = cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index).toString();
            cursor.close();
        }
        catch(NullPointerException e) {

        }
        return path;
    }
    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(MainActivity.Tag, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        if(camera!=null)
        {
            camera.release();
            camera=null;
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_PICK_CODE && resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_CODE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
                String fn= db.getmaxcode(MainActivity.MacServer)+".jpg";
                bitmap=MainActivity.setPic(selectedImagePath, getResources().getDimensionPixelSize(R.dimen.photo_size), getResources().getDimensionPixelSize(R.dimen.photo_size), fn);
                try {
                    selectedImagePath= MainActivity.saveToInternalStorage(bitmap, fn);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e(MainActivity.Tag, "save file : " + selectedImagePath);
                img.setImageURI(Uri.parse(selectedImagePath));
//                img.setImageURI(selectedImageUri);
//                img.setImageBitmap(bitmap);
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            String fn= db.getmaxcode(MainActivity.MacServer)+".jpg";
            bitmap=MainActivity.setPic(selectedImagePath, getResources().getDimensionPixelSize(R.dimen.photo_size), getResources().getDimensionPixelSize(R.dimen.photo_size), fn);
            try {
                selectedImagePath= MainActivity.saveToInternalStorage(bitmap, fn);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(MainActivity.Tag, "save file : " + selectedImagePath);
//            img.setImageBitmap(MainActivity.setPic(selectedImagePath, getResources().getDimensionPixelSize(R.dimen.photo_size), getResources().getDimensionPixelSize(R.dimen.photo_size), db.getmaxcode()+".png"));
            img.setImageURI(Uri.parse(selectedImagePath));
        }
        if (requestCode == REQUEST_CROP_ICON && resultCode == RESULT_OK) {
            Log.e(MainActivity.Tag, "Crop Image...");
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                //The stream to write to a file or directly using the
                FileOutputStream fos = null;
                try {
                    fos= new FileOutputStream(new File("parmis.jpg"));
                    stream.writeTo(fos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addaction, menu);
        return true;
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                onclickbtnsave(null);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
//        return super.onOptionsItemSelected(item);
    }
    private void onclickbtnsave(View view){
        EditText txtn= (EditText) findViewById(R.id.txtnamelamp);
        EditText txtg= (EditText) findViewById(R.id.txtgroupkeys);

        if(txtn.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "لطفا نام را وارد کنید", Toast.LENGTH_SHORT).show();
            return;
        }
        if(txtg.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "لطفا گروه را مشخص کنید", Toast.LENGTH_SHORT).show();
            return;
        }
//        db=new mainmenudb(this);
//        Log.d(MainActivity.Tag, "ذخیره" + selectedImagePath);
        if(ifToggle==script.ScriptToggle)
            remoteKey= MainActivity.GetRF;
//            db.insertrecord(txtn.getText().toString(), MainActivity.GetRF, -1, txtg.getText().toString(), selectedImagePath, ifToggle, 0, 0, 0);
//        else
//        db.insertrecord(txtn.getText().toString(), MainActivity.infoMe.andMacAddress, RemoteCode, txtg.getText().toString(), selectedImagePath, ifToggle, 0, 0, 0);
        Log.e(MainActivity.Tag, "اضافه مردن"+ editID);
        if (editID==-1)
            db.insertrecord(txtn.getText().toString(), remoteKey, RemoteCode, txtg.getText().toString(), selectedImagePath, ifToggle, 0, 0, 0, MainActivity.MacServer);
        else
            db.updaterecord(editID, txtn.getText().toString(), remoteKey, RemoteCode, txtg.getText().toString(), selectedImagePath, ifToggle, saveme, MainActivity.MacServer);

        setResult(RESULT_OK);
        finish();
    }
    /*******************************************************************************************************/
    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;
        public ImageAdapter(Context c)
        {
            context = c;
            // sets a grey background; wraps around the images
            TypedArray a =obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();
        }
        // returns the number of images
        public int getCount() {
            return imageIDs.length;
        }
        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }
        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }
        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(imageIDs[position]);
//            imageView.setLayoutParams(new Gallery.LayoutParams(100, 100));
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
    }
}
