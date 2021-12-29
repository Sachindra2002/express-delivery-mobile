package com.example.express_delivery_mobile.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.express_delivery_mobile.AgentActivity;
import com.example.express_delivery_mobile.Model.Documents;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.Service.AgentClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Tag;

public class DriverDocumentsAdapter extends RecyclerView.Adapter<DriverDocumentsAdapter.ViewHolder> {
    private static final String TAG = "LOG";
    private static final int MY_PERMISSION_REQUEST = 100;

    private Context context;
    private List<Documents> documents;
    private String token;
    private String userRole;
    private String email;

    private ProgressDialog mProgressDialog;

    private AgentClient agentClient = RetrofitClientInstance.getRetrofitInstance().create(AgentClient.class);

    public DriverDocumentsAdapter(Context context, List<Documents> documents, String token, String userRole, String email, ProgressDialog mProgressDialog) {
        this.context = context;
        this.documents = documents;
        this.token = token;
        this.userRole = userRole;
        this.email = email;
        this.mProgressDialog = mProgressDialog;
    }

    @NonNull
    @Override
    public DriverDocumentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.driver_documents_row, parent, false);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverDocumentsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.fileName.setText(documents.get(position).getDescription());
        holder.fileSize.setText(documents.get(position).getFileSize().toString() + " KB");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                builder.setMessage("Download : " + documents.get(position).getDescription() + " ?");

                //When "Accept" button is clicked
                builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        handleDownload(documents.get(position).getFileName());
                    }
                });

                //When cancel button is clicked
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.show();
            }
        });
    }

    private void handleDownload(String fileName) {
        Call<ResponseBody> call = agentClient.downloadDriverDocument(token, fileName);

        //Show progress
        mProgressDialog.setMessage("Downloading...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Successfully accepted
                if (response.code() == 200) {
                    Toast.makeText(context, "successfully downloaded", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "server contacted and has file");
                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(), fileName);
                    Log.d(TAG, "file download was a success? " + writtenToDisk);
                    mProgressDialog.dismiss();
                } else {
                    try {
                        //Capture and display specific messages
                        JSONObject object = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            // todo change the file location/name according to your needs
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public int getItemCount() {
        if (documents != null)
            return documents.size();

        return 0;
    }

    public void setDocuments(final List<Documents> documents) {
        this.documents = documents;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName, fileSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
        }
    }
}
