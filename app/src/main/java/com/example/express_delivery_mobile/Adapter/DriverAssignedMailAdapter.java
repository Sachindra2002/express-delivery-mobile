package com.example.express_delivery_mobile.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.express_delivery_mobile.DriverActivity;
import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.Provider.AddPackageToLocalStorage;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.Service.DriverClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverAssignedMailAdapter extends RecyclerView.Adapter<DriverAssignedMailAdapter.ViewHolder> {
    private Context context;
    private List<Mail> mails;
    private String token;
    private String userRole;

    private ProgressDialog mProgressDialog;

    //Driver Retrofit client
    DriverClient driverClient = RetrofitClientInstance.getRetrofitInstance().create(DriverClient.class);

    public DriverAssignedMailAdapter(Context context, List<Mail> mails, String token, String userRole, ProgressDialog mProgressDialog) {
        this.context = context;
        this.mails = mails;
        this.token = token;
        this.userRole = userRole;
        this.mProgressDialog = mProgressDialog;
    }

    public void setMails(final List<Mail> mails) {
        if (this.mails == null) {
            this.mails = mails;
            //If updating items (previously not null)
        } else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return DriverAssignedMailAdapter.this.mails.size();
                }

                @Override
                public int getNewListSize() {
                    return mails.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return DriverAssignedMailAdapter.this.mails.get(oldItemPosition).getMailId() == mails.get(newItemPosition).getMailId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Mail newMail = DriverAssignedMailAdapter.this.mails.get(oldItemPosition);

                    Mail oldMail = mails.get(newItemPosition);

                    return newMail.getMailId() == oldMail.getMailId();
                }
            });

            this.mails = mails;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public DriverAssignedMailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pending_mails_driver_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverAssignedMailAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (mails.get(position).getTransportationStatus().contains("Pick Up")) {
            holder.name.setText(mails.get(position).getUser().getFirstName() + " " + mails.get(position).getUser().getLastName());
            holder.transportationStatus.setText(mails.get(position).getTransportationStatus());
            holder.address.setText(mails.get(position).getPickupAddress());
            holder.type.setText(mails.get(position).getParcelType());
            holder.weight.setText(mails.get(position).getWeight() + "KG");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptOrRejectPackage(mails.get(position).getMailId());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    bookmarkPackage(mails.get(position));
                    return false;
                }
            });
        } else if (mails.get(position).getTransportationStatus().contains("Drop Off")) {
            holder.name.setText(mails.get(position).getReceiverFirstName() + " " + mails.get(position).getReceiverLastName());
            holder.transportationStatus.setText(mails.get(position).getTransportationStatus());
            holder.address.setText(mails.get(position).getReceiverAddress());
            holder.type.setText(mails.get(position).getParcelType());
            holder.weight.setText(mails.get(position).getWeight() + "KG");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptOrRejectPackage(mails.get(position).getMailId());
                }
            });
        }

    }

    private void bookmarkPackage(Mail mail) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Add to local Storage " + mail.getMailId());

        //When "Accept" button is clicked
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addToLocalStorage(mail);
            }
        });

        builder.show();
    }

    private void addToLocalStorage(Mail mail) {
        // class to add values in the database
        ContentValues values = new ContentValues();

        // Create values
        values.put(AddPackageToLocalStorage.MAIL_ID, mail.getMailId());
        values.put(AddPackageToLocalStorage.DRIVER_ID, mail.getDriverDetail().getDriverId());
        values.put(AddPackageToLocalStorage.DRIVER_EMAIL, mail.getDriverDetail().getUser().getEmail());
        values.put(AddPackageToLocalStorage.CUSTOMER_NAME, mail.getUser().getFirstName() + " " + mail.getUser().getLastName());
        values.put(AddPackageToLocalStorage.CUSTOMER_CONTACT, mail.getUser().getPhoneNumber());
        values.put(AddPackageToLocalStorage.CUSTOMER_EMAIL, mail.getUser().getEmail());
        values.put(AddPackageToLocalStorage.TRANSPORTATION_STATUS, mail.getTransportationStatus());
        values.put(AddPackageToLocalStorage.PACKAGE_STATUS, mail.getStatus());
        values.put(AddPackageToLocalStorage.PICKUP_ADDRESS, mail.getPickupAddress());
        values.put(AddPackageToLocalStorage.DROP_OFF_ADDRESS, mail.getReceiverAddress());
        values.put(AddPackageToLocalStorage.WEIGHT, mail.getWeight());
        values.put(AddPackageToLocalStorage.PARCEL_TYPE, mail.getParcelType());
        values.put(AddPackageToLocalStorage.PAYMENT_METHOD, mail.getPaymentMethod());
        values.put(AddPackageToLocalStorage.TOTAL_COST, mail.getTotalCost());

        try {

            // inserting into database through content URI
            context.getContentResolver().insert(AddPackageToLocalStorage.CONTENT_URI, values);

            // displaying a toast message
            Toast.makeText(context, "Added to storage!", Toast.LENGTH_LONG).show();

        } catch (SQLiteException ex) {
            Toast.makeText(context, "Already Added!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return mails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, transportationStatus, address, type, weight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.senderName);
            transportationStatus = itemView.findViewById(R.id.transport_status);
            address = itemView.findViewById(R.id.address);
            type = itemView.findViewById(R.id.type);
            weight = itemView.findViewById(R.id.weight);
        }
    }

    //Method to delete lecturers
    private void acceptOrRejectPackage(final int id) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Accept or Reject Package");
        builder.setMessage("Accept or Reject package : " + id + " ?");

        //When "Accept" button is clicked
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleAccept(id);
            }
        });

        //When cancel button is clicked
        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleReject(id);
            }
        });

        builder.show();
    }

    private void handleReject(int id) {
        Call<ResponseBody> call = driverClient.rejectPackage(token, id);

        //Show progress
        mProgressDialog.setMessage("Rejecting...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Successfully accepted
                if (response.code() == 200) {
                    Toast.makeText(context, "successfully rejected", Toast.LENGTH_SHORT).show();

                    //Direct to homepage
                    Intent intent = new Intent(context, DriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                } else {
                    try {
                        //Capture and display specific messages
                        JSONObject object = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
                mProgressDialog.show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAccept(int mailId) {

        Call<ResponseBody> call = driverClient.acceptPackage(token, mailId);

        //Show progress
        mProgressDialog.setMessage("Accepting...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Successfully accepted
                if (response.code() == 200) {
                    Toast.makeText(context, "successfully accepted", Toast.LENGTH_SHORT).show();

                    //Direct to homepage
                    Intent intent = new Intent(context, DriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                } else {
                    try {
                        //Capture and display specific messages
                        JSONObject object = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, object.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
                mProgressDialog.show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
