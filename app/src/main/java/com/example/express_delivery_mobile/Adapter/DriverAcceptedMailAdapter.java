package com.example.express_delivery_mobile.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.express_delivery_mobile.DriverAcceptedMailsActivity;
import com.example.express_delivery_mobile.DriverActivity;
import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.Service.DriverClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverAcceptedMailAdapter extends RecyclerView.Adapter<DriverAcceptedMailAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Mail> mails;
    private List<Mail> filteredMails;

    private String token;
    private String userRole;

    private ProgressDialog mProgressDialog;

    //Driver Retrofit client
    DriverClient driverClient = RetrofitClientInstance.getRetrofitInstance().create(DriverClient.class);

    public DriverAcceptedMailAdapter(Context context, List<Mail> mails, String token, String userRole, ProgressDialog mProgressDialog) {
        this.context = context;
        this.mails = mails;
        this.token = token;
        this.userRole = userRole;
        this.mProgressDialog = mProgressDialog;
    }

    public void setMails(final List<Mail> mails) {
        if (this.mails == null) {
            this.mails = mails;
            this.filteredMails = mails;

            //Alert a change in items
            notifyItemChanged(0, filteredMails.size());
        }
        //If updating items (previously not null)
        else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return DriverAcceptedMailAdapter.this.mails.size();
                }

                @Override
                public int getNewListSize() {
                    return mails.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return DriverAcceptedMailAdapter.this.mails.get(oldItemPosition).getMailId() == mails.get(newItemPosition).getMailId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Mail newMail = DriverAcceptedMailAdapter.this.mails.get(oldItemPosition);

                    Mail oldMail = mails.get(newItemPosition);

                    return newMail.getMailId() == oldMail.getMailId();
                }
            });

            this.mails = mails;
            this.filteredMails = mails;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public DriverAcceptedMailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.driver_accepted_mails_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverAcceptedMailAdapter.ViewHolder holder, final int position) {
        if(filteredMails.get(position).getStatus().equalsIgnoreCase("Driver Accepted")){
            if (filteredMails.get(position).getTransportationStatus().contains("Pick Up")) {
                holder.name.setText(filteredMails.get(position).getUser().getFirstName() + " " + mails.get(position).getUser().getLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getPickupAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startDelivery(filteredMails.get(position).getMailId());
                    }
                });

            } else if (filteredMails.get(position).getTransportationStatus().contains("Drop Off")) {
                holder.name.setText(filteredMails.get(position).getReceiverFirstName() + " " + mails.get(position).getReceiverLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getReceiverAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");

            }
        }else if(filteredMails.get(position).getStatus().equalsIgnoreCase("Delivery Started")){
            if (filteredMails.get(position).getTransportationStatus().contains("Pick Up")) {
                holder.name.setText(filteredMails.get(position).getUser().getFirstName() + " " + mails.get(position).getUser().getLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getPickupAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pickupDelivery(filteredMails.get(position).getMailId());
                    }
                });

            } else if (filteredMails.get(position).getTransportationStatus().contains("Drop Off")) {
                holder.name.setText(filteredMails.get(position).getReceiverFirstName() + " " + mails.get(position).getReceiverLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getReceiverAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");

            }
        }


    }

    private void startDelivery(int mailId) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Start Delivery " + mailId);

        //When "Start" button is clicked
        builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleStart(mailId);
            }
        });

        //When cancel button is clicked
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void pickupDelivery(int mailId) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Package " + mailId);

        //When "Start" button is clicked
        builder.setPositiveButton("Confirm Pick up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleConfirmPickup(mailId);
            }
        });

        //When cancel button is clicked
        builder.setNegativeButton("Call Customer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void handleConfirmPickup(int mailId) {
        Call<ResponseBody> call = driverClient.confirmPickupPackage(token, mailId);

        //Show progress
        mProgressDialog.setMessage("Confirming pickup...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Successfully accepted
                if (response.code() == 200) {
                    Toast.makeText(context, "successfully started", Toast.LENGTH_SHORT).show();

                    //Direct to homepage
                    Intent intent = new Intent(context, DriverAcceptedMailsActivity.class);
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

    private void handleStart(int mailId) {

        Call<ResponseBody> call = driverClient.startPackage(token, mailId);

        //Show progress
        mProgressDialog.setMessage("Starting...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Successfully accepted
                if (response.code() == 200) {
                    Toast.makeText(context, "successfully started", Toast.LENGTH_SHORT).show();

                    //Direct to homepage
                    Intent intent = new Intent(context, DriverAcceptedMailsActivity.class);
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

    @Override
    public int getItemCount() {
        if (filteredMails != null) return filteredMails.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredMails = mails;
                } else {
                    List<Mail> filteredList = new ArrayList<>();
                    for (Mail mail : mails) {
                        String searchString = charString.toLowerCase();

                        //Filter through fields and add to filtered list
                        if (mail.getDescription().contains(searchString) || String.valueOf(mail.getMailId()).contains(searchString) || mail.getReceiverFirstName().contains(searchString) || mail.getTransportationStatus().contains(searchString) ||
                        mail.getUser().getFirstName().contains(searchString) || mail.getUser().getLastName().contains(searchString) ||
                        mail.getParcelType().contains(searchString)) {
                            filteredList.add(mail);
                        }
                    }
                    filteredMails = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredMails;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredMails = (ArrayList<Mail>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, transportationStatus, address, type, weight;
        Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.senderName);
            transportationStatus = itemView.findViewById(R.id.transport_status);
            address = itemView.findViewById(R.id.address);
            type = itemView.findViewById(R.id.type);
            weight = itemView.findViewById(R.id.weight);
//            button = itemView.findViewById(R.id.start_button);
        }
    }
}
