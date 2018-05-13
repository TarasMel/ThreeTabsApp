package com.example.taras.threetabsapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class ArchiveTab extends Fragment {

    private static final int MY_PERMISSION = 1;
    //private static final String PATH_TO_DOWNLOAD = "http://storage.rulsmart.com/1c46/getfiles/kartinki/1254497292_tachki.zip";
    //private static final String FILE_NAME = "1254497292_tachki.zip";
    
    String fileNameUnZip;
    private ProgressDialog progressDialog;
    private Button btn_downloadArchive;
    private Button btn_unzipArchive;
    private EditText editTextLinkUploadArchive;
    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.archive_layout, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        editTextLinkUploadArchive = (EditText)getActivity().findViewById(R.id.edit_UploadArchive_ID);
        btn_downloadArchive = (Button) getActivity().findViewById(R.id.btn_UploadArchive_ID);
        btn_unzipArchive = (Button) getActivity().findViewById(R.id.btn_unzipArchive_ID);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION);
                return;
            }
        }
        buttonWork();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                    buttonWork();
                }
                else {
                    Toast.makeText(getActivity(), "Permission not granted!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void buttonWork(){
        btn_downloadArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(Environment.getExternalStorageDirectory() + "/Download/");
                try {
                    dir.mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Cannot create folder for download!", Toast.LENGTH_SHORT).show();
                }
                new DownloadFileAsync().execute(editTextLinkUploadArchive.getText().toString());
            }
        });
        btn_unzipArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File folder = new File(Environment.getExternalStorageDirectory() + "/Download/" + "unzip" +fileNameUnZip);
                try {
                    folder.mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Cannot create folder for unzip!", Toast.LENGTH_SHORT).show();
                }
                if(unzip(Environment.getExternalStorageDirectory() + "/Download/" + fileNameUnZip, Environment.getExternalStorageDirectory() + "/Download/" + "unzip" +fileNameUnZip)){
                    Toast.makeText(getActivity(), "Unzip success", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Unzip failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static Boolean unzip(String sourceFile, String destinationFolder) {
        byte[] buffer = new byte[8192];
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceFile));
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destinationFolder + File.separator + fileName);
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        }
        catch(IOException ex){
            return false;
        }
        return true;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        InputMethodManager inm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE) ;
        inm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        mListener = null;
    }

    private class DownloadFileAsync extends AsyncTask<String, Integer, String> {

        double file_size = 0;
        String file_name;

        @Override
        protected String doInBackground(String... params) {
            file_name = params[0].substring(params[0].lastIndexOf("/")+1);
            fileNameUnZip = file_name;
            try{
                InputStream inputStream = null;
                OutputStream outputStream = null;
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(params[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                        return "Server returned HTTP" + connection.getResponseCode() + " " +
                                connection.getResponseMessage();
                    }
                    int fileLength = connection.getContentLength();
                    file_size = fileLength;
                    inputStream = connection.getInputStream();
                    outputStream = new FileOutputStream((Environment.getExternalStorageDirectory()+ "/Download/" + file_name));
                    byte data[] = new byte[8196];
                    long total = 0;
                    int count;
                    while((count = inputStream.read(data)) != -1){
                        if (isCancelled()){
                            return null;
                        }
                        total += count;
                        if (fileLength > 0){
                            publishProgress((int) (total *  100/fileLength));
                        }
                        outputStream.write(data, 0, count);
                    }
                }catch (Exception e){
                    return e.toString();
                }finally {
                    try {
                        if (outputStream != null){
                            outputStream.close();
                        }
                        if (inputStream != null){
                            inputStream.close();
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            } finally {
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Downloading...");
            progressDialog.setMessage("File size: O MB");
            progressDialog.setIndeterminate(true);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(getActivity(), "Download canceled", Toast.LENGTH_SHORT).show();
                    File dir = new File ((Environment.getExternalStorageDirectory()+ "/Download/" + file_name));
                    try {
                        dir.delete();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(values[0]);
            progressDialog.setMessage("File size: " + new DecimalFormat("##.##").format(file_size / 1000000) + "MB");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s != null){
                Toast.makeText(getActivity(), "Error: " + s, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Downloaded" , Toast.LENGTH_SHORT).show();
            }
        }
    }

    interface OnFragmentInteractionListener {}
}