package com.example.express_delivery_mobile.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.express_delivery_mobile.MainActivity;
import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.Service.MailClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.TrackPackageActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Mail> mails;
    private List<Mail> filteredMails;
    private String token;
    private String userRole;
    private String username;
    private String email;

    private ProgressDialog mProgressDialog;

    //Mail Retrofit client
    MailClient mailClient = RetrofitClientInstance.getRetrofitInstance().create(MailClient.class);

    public MailAdapter(Context context, List<Mail> mails, String token, String userRole, ProgressDialog mProgressDialog, String email) {
        this.context = context;
        this.mails = mails;
        this.token = token;
        this.userRole = userRole;
        this.mProgressDialog = mProgressDialog;
        this.email = email;
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
                    return MailAdapter.this.mails.size();
                }

                @Override
                public int getNewListSize() {
                    return mails.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return MailAdapter.this.mails.get(oldItemPosition).getMailId() == mails.get(newItemPosition).getMailId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Mail newMail = MailAdapter.this.mails.get(oldItemPosition);

                    Mail oldMail = mails.get(newItemPosition);

                    return newMail.getMailId() == oldMail.getMailId();
                }
            });

            this.mails = mails;
            this.filteredMails = mails;
            result.dispatchUpdatesTo(this);
        }
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
                        if (mail.getDescription().contains(searchString) || String.valueOf(mail.getMailId()).contains(charString)) {
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

    @NonNull
    @Override
    public MailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.upcoming_mail_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MailAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (filteredMails.get(position).getReceiverEmail().equals(email)) {
            holder.senderName.setText(String.format(filteredMails.get(position).getUser().getFirstName() + " " + filteredMails.get(position).getUser().getLastName()));
            if (filteredMails.get(position).getStatus().contains("Driver Accepted")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, TrackPackageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("status", filteredMails.get(position).getStatus());
                        intent.putExtra("created_at", filteredMails.get(position).getCreatedAt().getTime());
                        intent.putExtra("trackId", filteredMails.get(position).getMailTracking().getTrackingId());
                        intent.putExtra("pickUpAddress", filteredMails.get(position).getPickupAddress());
                        intent.putExtra("dropOffAddress", filteredMails.get(position).getReceiverAddress());
                        intent.putExtra("description", filteredMails.get(position).getDescription());
                        intent.putExtra("weight", filteredMails.get(position).getWeight());
                        intent.putExtra("parcelType", filteredMails.get(position).getParcelType());
                        intent.putExtra("pieces", filteredMails.get(position).getPieces());
                        intent.putExtra("driverFirstName",filteredMails.get(position).getDriverDetail().getUser().getFirstName());
                        intent.putExtra("driverLastName",filteredMails.get(position).getDriverDetail().getUser().getLastName());
                        intent.putExtra("driverFirstName",filteredMails.get(position).getDriverDetail().getUser().getFirstName());
                        intent.putExtra("driverVehicleNumber",filteredMails.get(position).getDriverDetail().getVehicle().getVehicleNumber());
                        intent.putExtra("driverMobile",filteredMails.get(position).getDriverDetail().getUser().getPhoneNumber());
                        context.startActivity(intent);
                    }
                });
            }
        } else {
            holder._senderName.setText("To");
            holder.senderName.setText(String.format(filteredMails.get(position).getReceiverFirstName() + " " + filteredMails.get(position).getReceiverLastName()));
            if (filteredMails.get(position).getStatus().contains("Processing")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        packageOptions(filteredMails.get(position).getMailId());
                    }
                });
            } else if (filteredMails.get(position).getStatus().contains("Driver Accepted")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        packageOptions2(filteredMails.get(position).getMailId());
                    }
                });
            } else if (filteredMails.get(position).getStatus().contains("Accepted")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        packageOptions2(filteredMails.get(position).getMailId());
                    }
                });
            }
        }
        holder.status.setText(filteredMails.get(position).getStatus());
        holder.description.setText(filteredMails.get(position).getDescription());
        holder.type.setText(filteredMails.get(position).getParcelType());
        holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
        if (filteredMails.get(position).getStatus().contains("Processing")) {
            holder.status.setTextColor(Color.parseColor("#00B832"));
        } else if (filteredMails.get(position).getStatus().contains("Accepted")) {
            holder.status.setTextColor(Color.parseColor("#00B832"));
        } else if (filteredMails.get(position).getStatus().contains("Cancelled")) {
            holder.status.setTextColor(Color.parseColor("#F41F1F"));
        } else if (filteredMails.get(position).getStatus().contains("Assigned")) {
            holder.status.setTextColor(Color.parseColor("#0C6E0F"));
        } else if (filteredMails.get(position).getStatus().contains("Rejected")) {
            holder.status.setTextColor(Color.parseColor("#F41F1F"));
        }

    }

    private void packageOptions2(int mailId) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Package : " + mailId);

        //When "Track" button is clicked
        builder.setPositiveButton("Track", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                handleAccept(id);
            }
        });


        builder.show();
    }

    private void packageOptions(int mailId) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Package : " + mailId);

        //When "Track" button is clicked
        builder.setPositiveButton("Track", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                handleAccept(id);
            }
        });

        //When "Cancel" button is clicked
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public int getItemCount() {
        if (filteredMails != null) return filteredMails.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, status, description, type, weight, _senderName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            senderName = itemView.findViewById(R.id.senderName);
            status = itemView.findViewById(R.id.status);
            description = itemView.findViewById(R.id.description);
            type = itemView.findViewById(R.id.type);
            weight = itemView.findViewById(R.id.weight);
            _senderName = itemView.findViewById(R.id._senderName);
        }
    }
}
