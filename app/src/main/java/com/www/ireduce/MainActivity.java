package com.www.ireduce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {

    ImageView iv;
    Toolbar toolbar;
    EditText et_width,et_height,et_compress;
    Button btn,btn_compress;
   int REQUEST_CAMERA=1, SELECT_FILE=0;
   TextView tv_size;

   private Uri camera_uri;

   private Uri selected_uri;
   private File file;
   private File qwerty;
   private InputStream fileip;
   String exact_path;
   long size;
   Bitmap compress_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv=findViewById(R.id.iv);
//        toolbar=findViewById(R.id.toolbar);
//        et_width=findViewById(R.id.et_width);
//        et_height=findViewById(R.id.et_height);
        tv_size= findViewById(R.id.tv_size);
        btn= findViewById(R.id.btn);

//        setSupportActionBar(toolbar);

        et_compress=findViewById(R.id.et_compress);
        btn_compress=findViewById(R.id.btn_compress);



        btn.setOnClickListener(new View.OnClickListener() {
            final CharSequence[] item={"Gallery","Camera","Cancel"};
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertdialog= new AlertDialog.Builder(MainActivity.this);
                alertdialog.setTitle("Choose the Option");
                alertdialog.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                   if(item[which].equals("Camera")){
                       if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
                           ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
                       }
                       else {

//                        ContentValues values = new ContentValues();
//                           values.put(MediaStore.Images.Media.TITLE, "New Picture");
//                           values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
//                           camera_uri = getContentResolver().insert(
//                                   MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                           Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                           intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);
//                           startActivityForResult(intent, REQUEST_CAMERA);
                           Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                           startActivityForResult(intent, REQUEST_CAMERA);
                       }
                   }

                   else if(item[which].equals("Gallery")){
                       Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                       intent.setType("image/*");
                       startActivityForResult(intent.createChooser(intent,"Select File"),SELECT_FILE);
                   }

                   else if(item[which].equals("Cancel")){
                       dialog.dismiss();
                   }
                    }
                });
                alertdialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK){
            if( requestCode== REQUEST_CAMERA){

//                try {
//                   Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
//                            getContentResolver(), camera_uri);
//                    iv.setImageBitmap(thumbnail);
//                  exact_path = getRealPathFromURI(camera_uri);
//
//
//
//                    fileip= getApplicationContext().getContentResolver().openInputStream(camera_uri);
//                    size= fileip.available();
//                    tv_size.setText("Image size is: " + size / 1024 + " kb");
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
try {
    Bundle bundle = data.getExtras();
    final Bitmap bitmap = (Bitmap) bundle.get("data");
    iv.setImageBitmap(bitmap);

    tv_size.setVisibility(View.VISIBLE);
    et_compress.setVisibility(View.VISIBLE);
    btn_compress.setVisibility(View.VISIBLE);

    //for image size but not exact
    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
    byte[] imageinbyte1 = stream1.toByteArray();
    long size = imageinbyte1.length;
    tv_size.setText("Image size is: " + size / 1024 + " kb");

//    ContentValues values = new ContentValues();
//    values.put(MediaStore.Images.Media.TITLE, "New Picture");
//    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
//    camera_uri = getContentResolver().insert(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"camera","image");
}
catch (Exception e){
    e.printStackTrace();
}

            }
            else if( requestCode== SELECT_FILE){


                selected_uri= data.getData();
                iv.setImageURI(selected_uri);

                tv_size.setVisibility(View.VISIBLE);
                et_compress.setVisibility(View.VISIBLE);
                btn_compress.setVisibility(View.VISIBLE);

                // for getting exact path of the image loaded
                exact_path = getRealPathFromURI(selected_uri);

                Toast.makeText(this, exact_path, Toast.LENGTH_SHORT).show();

                // for exact file size of the image
                String schema= selected_uri.getScheme();
                if(schema.equals(ContentResolver.SCHEME_CONTENT)){
                    try{
                        fileip= getApplicationContext().getContentResolver().openInputStream(selected_uri);
                         size= fileip.available();
                        tv_size.setText("Image size is: " + size / 1024 + " kb");



                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }

                else if(schema.equals(ContentResolver.SCHEME_FILE)){
                    String path= selected_uri.getPath();
                    try{
                      file = new File(path);
                        tv_size.setText("Image size is: " + file.length() / 1024 + " KB");

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }


            }
        }


        // for crop image
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode== RESULT_OK){
                iv.setImageURI(result.getUri());
                tv_size.setVisibility(View.VISIBLE);
                et_compress.setVisibility(View.VISIBLE);
                btn_compress.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Image Updated Successfully!!", Toast.LENGTH_SHORT).show();
            }
        }
    }


//      try {
//                    // for converting uri into bitmap
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selected_uri);
//                    //for getting the size of the image
////                    ByteArrayOutputStream stream=new ByteArrayOutputStream();
////                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
////                    byte[] imageinbyte= stream.toByteArray();
////                    long length = imageinbyte.length;
////                    tv_size.setText("Image size is: " + length / 1024 + " kb");
//                }
//                catch (Exception e){
//
//                }


    //for compress button on click
    public void compress(View view){

//        file =new File(exact_path);
//
//        bitmapCompress(file,4,75);

        try {


            file = new File(exact_path);
            int a = Integer.parseInt(et_compress.getText().toString());

            //for getting the width of the image
            BitmapFactory.Options options= new BitmapFactory.Options();
            options.inJustDecodeBounds =true;
            BitmapFactory.decodeFile(String.valueOf(file),options);



      int b = 1;

            int MAX_SIZE= a*1024;
            int streamLength = (int) size;
            int compressQuality= 100;

//            ByteArrayOutputStream bmpStream =new ByteArrayOutputStream();

            while(streamLength >= MAX_SIZE){
                compressQuality -= 1;
                Log.d("test","Size: "+ streamLength);
                int width = options.outWidth;
                int height = options.outHeight;
               width= width/b;
               height= height/b;
            qwerty = new Compressor(this)
                        .setMaxWidth((width))
                        .setMaxHeight((height))
                        .setQuality(compressQuality)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFile(file);

            String file_path= qwerty.getPath();

            streamLength= (int) qwerty.length();

            //converting file to bitmap

            compress_img= BitmapFactory.decodeFile(file_path);

                b++;




                if(BuildConfig.DEBUG){
                    Log.d("test upload","Quality: "+ compressQuality);
                    Log.d("test upload","Size: "+ streamLength);
                    Log.d("test upload","Width: "+ width);
                    Log.d("test upload", "Height: "+ height);
                }



            }

            iv.setImageBitmap(compress_img);
            tv_size.setText("Image size is: " + streamLength / 1024 + " kb");
            Toast.makeText(this, "Image is compressed", Toast.LENGTH_SHORT).show();
            //for saving the image in the gallery
            MediaStore.Images.Media.insertImage(getContentResolver(), String.valueOf(qwerty), "compress.jpg", "image");

//

        }
        catch (Exception e){
            e.printStackTrace();
        }



    }
//for saving the data in the sd card

//    private  File getOutputMediaFile(){
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//                + "/Android/data/"
//                + getApplicationContext().getPackageName()
//                + "/Files");
//
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (! mediaStorageDir.exists()){
//            if (! mediaStorageDir.mkdirs()){
//                return null;
//            }
//        }
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
//        File mediaFile;
//        String mImageName="MI_"+ timeStamp +".jpg";
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
//        return mediaFile;
//    }


    //for getting exact file path which is loaded in the imageview

    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);

        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    //for toolbar item selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.crop:
                startcrop(selected_uri);
                break;
        }
        return true;
    }
    //for cropping the image
    public void startcrop(Uri uri){



        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);

    }

}