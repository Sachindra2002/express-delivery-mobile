package com.example.express_delivery_mobile.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.express_delivery_mobile.AgentAcceptedMailsActivity;
import com.example.express_delivery_mobile.AgentActivity;
import com.example.express_delivery_mobile.DriverActivity;
import com.example.express_delivery_mobile.FromYouActivity;
import com.example.express_delivery_mobile.Model.Disputes;
import com.example.express_delivery_mobile.Model.DriverDetail;
import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.Service.AgentClient;
import com.example.express_delivery_mobile.Service.MailClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.TrackPackageActivity;
import com.example.express_delivery_mobile.TransitListActivity;
import com.example.express_delivery_mobile.ViewPackageAdminActivity;
import com.example.express_delivery_mobile.ViewPackageAgentActivity;
import com.example.express_delivery_mobile.ViewPackageDriverActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Mail> mails;
    private List<Mail> filteredMails;
    private String token;
    private String userRole;
    private String email;
    private boolean driversLoaded;

    private String pickupDate;
    private String dropOffDate;
    private String _driverId;
    private int driverId;

    EditText pickDate;
    EditText dropDate;


    //Dropdown attributes
    private List<String> drivers = new ArrayList<>();
    private List<Integer> driver_ids = new ArrayList<>();

    private ProgressDialog mProgressDialog;
    private Spinner spinner;
    private EditText description;

    //Agent Retrofit client
    AgentClient agentClient = RetrofitClientInstance.getRetrofitInstance().create(AgentClient.class);
    MailClient mailClient = RetrofitClientInstance.getRetrofitInstance().create(MailClient.class);

    final Calendar myCalendar = Calendar.getInstance();

    public MailAdapter(Context context, List<Mail> mails, String token, String userRole, ProgressDialog mProgressDialog, String email) {
        this.context = context;
        this.mails = mails;
        this.token = token;
        this.userRole = userRole;
        this.mProgressDialog = mProgressDialog;
        this.email = email;
    }

    public MailAdapter(Context context, List<Mail> mails, String token, String userRole, ProgressDialog mProgressDialog) {
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
                        if (String.valueOf(mail.getMailId()).contains(charString)) {
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

        if (userRole.equalsIgnoreCase("customer")) {
            if (filteredMails.get(position).getReceiverEmail().equals(email)) {
                holder.senderName.setText(String.format(filteredMails.get(position).getUser().getFirstName() + " " + filteredMails.get(position).getUser().getLastName()));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, TrackPackageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if (filteredMails.get(position).getDriverDetail() != null) {
                            intent.putExtra("driverFirstName", filteredMails.get(position).getDriverDetail().getUser().getFirstName());
                            intent.putExtra("driverLastName", filteredMails.get(position).getDriverDetail().getUser().getLastName());
                            intent.putExtra("driverFirstName", filteredMails.get(position).getDriverDetail().getUser().getFirstName());
                            intent.putExtra("driverVehicleNumber", filteredMails.get(position).getDriverDetail().getVehicle().getVehicleNumber());
                            intent.putExtra("driverMobile", filteredMails.get(position).getDriverDetail().getUser().getPhoneNumber());
                            intent.putExtra("status", filteredMails.get(position).getStatus());
                            intent.putExtra("created_at", filteredMails.get(position).getCreatedAt().getTime());
                            intent.putExtra("trackId", filteredMails.get(position).getMailTracking().getTrackingId());
                            intent.putExtra("pickUpAddress", filteredMails.get(position).getPickupAddress());
                            intent.putExtra("dropOffAddress", filteredMails.get(position).getReceiverAddress());
                            intent.putExtra("description", filteredMails.get(position).getDescription());
                            intent.putExtra("weight", filteredMails.get(position).getWeight());
                            intent.putExtra("parcelType", filteredMails.get(position).getParcelType());
                            intent.putExtra("pieces", filteredMails.get(position).getPieces());
                        } else {
                            intent.putExtra("status", filteredMails.get(position).getStatus());
                            intent.putExtra("created_at", filteredMails.get(position).getCreatedAt().getTime());
                            intent.putExtra("trackId", filteredMails.get(position).getMailTracking().getTrackingId());
                            intent.putExtra("pickUpAddress", filteredMails.get(position).getPickupAddress());
                            intent.putExtra("dropOffAddress", filteredMails.get(position).getReceiverAddress());
                            intent.putExtra("description", filteredMails.get(position).getDescription());
                            intent.putExtra("weight", filteredMails.get(position).getWeight());
                            intent.putExtra("parcelType", filteredMails.get(position).getParcelType());
                            intent.putExtra("pieces", filteredMails.get(position).getPieces());
                        }
                        context.startActivity(intent);
                    }
                });
            } else {
                holder._senderName.setText("To");
                holder.senderName.setText(String.format(filteredMails.get(position).getReceiverFirstName() + " " + filteredMails.get(position).getReceiverLastName()));
                if (filteredMails.get(position).getStatus().equalsIgnoreCase("Processing")) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cancelOrTrackPackage(filteredMails.get(position));
                        }
                    });
                } else if (filteredMails.get(position).getStatus().equalsIgnoreCase("Delivered")) {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            trackOrOpenDispute(filteredMails.get(position));
                        }
                    });
                } else {
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, TrackPackageActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            if (filteredMails.get(position).getDriverDetail() != null) {
                                intent.putExtra("driverFirstName", filteredMails.get(position).getDriverDetail().getUser().getFirstName());
                                intent.putExtra("driverLastName", filteredMails.get(position).getDriverDetail().getUser().getLastName());
                                intent.putExtra("driverFirstName", filteredMails.get(position).getDriverDetail().getUser().getFirstName());
                                intent.putExtra("driverVehicleNumber", filteredMails.get(position).getDriverDetail().getVehicle().getVehicleNumber());
                                intent.putExtra("driverMobile", filteredMails.get(position).getDriverDetail().getUser().getPhoneNumber());
                                intent.putExtra("status", filteredMails.get(position).getStatus());
                                intent.putExtra("created_at", filteredMails.get(position).getCreatedAt().getTime());
                                intent.putExtra("trackId", filteredMails.get(position).getMailTracking().getTrackingId());
                                intent.putExtra("pickUpAddress", filteredMails.get(position).getPickupAddress());
                                intent.putExtra("dropOffAddress", filteredMails.get(position).getReceiverAddress());
                                intent.putExtra("description", filteredMails.get(position).getDescription());
                                intent.putExtra("weight", filteredMails.get(position).getWeight());
                                intent.putExtra("parcelType", filteredMails.get(position).getParcelType());
                                intent.putExtra("pieces", filteredMails.get(position).getPieces());
                            } else {
                                intent.putExtra("status", filteredMails.get(position).getStatus());
                                intent.putExtra("created_at", filteredMails.get(position).getCreatedAt().getTime());
                                intent.putExtra("trackId", filteredMails.get(position).getMailTracking().getTrackingId());
                                intent.putExtra("pickUpAddress", filteredMails.get(position).getPickupAddress());
                                intent.putExtra("dropOffAddress", filteredMails.get(position).getReceiverAddress());
                                intent.putExtra("description", filteredMails.get(position).getDescription());
                                intent.putExtra("weight", filteredMails.get(position).getWeight());
                                intent.putExtra("parcelType", filteredMails.get(position).getParcelType());
                                intent.putExtra("pieces", filteredMails.get(position).getPieces());
                            }
                            context.startActivity(intent);
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
        } else if (userRole.equalsIgnoreCase("agent")) {
            holder._senderName.setText("Customer");
            holder.senderName.setText(String.format(filteredMails.get(position).getUser().getFirstName() + " " + filteredMails.get(position).getUser().getLastName()));
            holder.status.setText(filteredMails.get(position).getStatus());
            holder.description.setText(filteredMails.get(position).getDescription());
            holder.type.setText(filteredMails.get(position).getParcelType());
            holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
            holder.status.setTextColor(Color.parseColor("#00B832"));
            if (filteredMails.get(position).getStatus().equalsIgnoreCase("Processing")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                        builder.setMessage("Accept or Reject package : " + filteredMails.get(position).getMailId() + " ?");

                        //When "Accept" button is clicked
                        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                handleAccept(filteredMails.get(position).getMailId());
                            }
                        });

                        //When cancel button is clicked
                        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleReject(filteredMails.get(position).getMailId());
                            }
                        });

                        builder.show();
                    }
                });
            } else if (filteredMails.get(position).getStatus().equalsIgnoreCase("Accepted")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = inflater.inflate(R.layout.assign_driver_dialog, null);
                        mBuilder.setTitle("Assign Driver for package " + filteredMails.get(position).getMailId());
                        spinner = (Spinner) v.findViewById(R.id.driver_spinner);
                        //Show progress
                        mProgressDialog.setMessage("Setting up form...");
                        mProgressDialog.show();

                        setupDriverDropdown();

                        dropDate = (EditText) v.findViewById(R.id.drop_date);

                        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {
                                // TODO Auto-generated method stub
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                updateLabel();
                            }

                        };

                        dropDate.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                new DatePickerDialog(context, date, myCalendar
                                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                            }
                        });

                        mBuilder.setPositiveButton("Assign Driver", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                handleAssignDriver(filteredMails.get(position).getMailId());
                            }
                        });
                        mBuilder.setView(v);
                        AlertDialog dialog = mBuilder.create();
                        dialog.show();
                    }
                });
            } else if (filteredMails.get(position).getStatus().equalsIgnoreCase("In Transit")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleChangeDriver(filteredMails.get(position));
                    }
                });
            }
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent = new Intent(context, ViewPackageAgentActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (filteredMails.get(position).getDriverDetail() != null) {
                        intent.putExtra("mail_id", filteredMails.get(position).getMailId());
                        intent.putExtra("package_description", filteredMails.get(position).getDescription());
                        intent.putExtra("created_at", filteredMails.get(position).getCreatedAt().getTime());
                        intent.putExtra("package_status", filteredMails.get(position).getStatus());
                        intent.putExtra("drop_off_date", filteredMails.get(position).getDropOffDate());
                        intent.putExtra("pick_up_date", filteredMails.get(position).getDate());
                        intent.putExtra("weight", filteredMails.get(position).getWeight());
                        intent.putExtra("parcel_type", filteredMails.get(position).getParcelType());
                        intent.putExtra("receiver_name", filteredMails.get(position).getReceiverFirstName() + " " + filteredMails.get(position).getReceiverLastName());
                        intent.putExtra("receiver_contact", filteredMails.get(position).getReceiverPhoneNumber());
                        intent.putExtra("receiver_email", filteredMails.get(position).getReceiverEmail());
                        intent.putExtra("payment_method", filteredMails.get(position).getPaymentMethod());
                        intent.putExtra("total_cost", filteredMails.get(position).getTotalCost());
                        intent.putExtra("pieces", filteredMails.get(position).getPieces());
                        intent.putExtra("pick_up_address", filteredMails.get(position).getPickupAddress());
                        intent.putExtra("drop_off_address", filteredMails.get(position).getReceiverAddress());
                        intent.putExtra("customer_name", filteredMails.get(position).getUser().getFirstName() + " " + filteredMails.get(position).getUser().getLastName());
                        intent.putExtra("customer_contact", filteredMails.get(position).getUser().getPhoneNumber());
                        intent.putExtra("center_name", filteredMails.get(position).getServiceCentre().getCentre());
                        intent.putExtra("center_address", filteredMails.get(position).getServiceCentre().getAddress());
                        intent.putExtra("driver_name", filteredMails.get(position).getDriverDetail().getUser().getFirstName() + " " + filteredMails.get(position).getDriverDetail().getUser().getLastName());
                        intent.putExtra("driver_contact", filteredMails.get(position).getDriverDetail().getUser().getPhoneNumber());
                    } else {
                        intent.putExtra("mail_id", filteredMails.get(position).getMailId());
                        intent.putExtra("package_description", filteredMails.get(position).getDescription());
                        intent.putExtra("created_at", filteredMails.get(position).getCreatedAt().getTime());
                        intent.putExtra("package_status", filteredMails.get(position).getStatus());
                        intent.putExtra("drop_off_date", filteredMails.get(position).getDropOffDate());
                        intent.putExtra("pick_up_date", filteredMails.get(position).getDate());
                        intent.putExtra("weight", filteredMails.get(position).getWeight());
                        intent.putExtra("parcel_type", filteredMails.get(position).getParcelType());
                        intent.putExtra("receiver_name", filteredMails.get(position).getReceiverFirstName() + " " + filteredMails.get(position).getReceiverLastName());
                        intent.putExtra("receiver_contact", filteredMails.get(position).getReceiverPhoneNumber());
                        intent.putExtra("receiver_email", filteredMails.get(position).getReceiverEmail());
                        intent.putExtra("payment_method", filteredMails.get(position).getPaymentMethod());
                        intent.putExtra("total_cost", filteredMails.get(position).getTotalCost());
                        intent.putExtra("pieces", filteredMails.get(position).getPieces());
                        intent.putExtra("pick_up_address", filteredMails.get(position).getPickupAddress());
                        intent.putExtra("drop_off_address", filteredMails.get(position).getReceiverAddress());
                        intent.putExtra("customer_name", filteredMails.get(position).getUser().getFirstName() + " " + filteredMails.get(position).getUser().getLastName());
                        intent.putExtra("customer_contact", filteredMails.get(position).getUser().getPhoneNumber());
                        intent.putExtra("center_name", filteredMails.get(position).getServiceCentre().getCentre());
                        intent.putExtra("center_address", filteredMails.get(position).getServiceCentre().getAddress());
                    }

                    context.startActivity(intent);
                    return false;
                }
            });

        } else if (userRole.equalsIgnoreCase("admin")) {
            holder._senderName.setText("Customer");
            holder.senderName.setText(String.format(filteredMails.get(position).getUser().getFirstName() + " " + filteredMails.get(position).getUser().getLastName()));
            holder.status.setText(filteredMails.get(position).getStatus());
            holder.description.setText(filteredMails.get(position).getDescription());
            holder.type.setText(filteredMails.get(position).getParcelType());
            holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
            holder.status.setTextColor(Color.parseColor("#00B832"));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewPackageAdminActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (filteredMails.get(position).getDriverDetail() != null) {
                        intent.putExtra("mail_id", filteredMails.get(position).getMailId());
                        intent.putExtra("package_description", filteredMails.get(position).getDescription());
                        intent.putExtra("created_at", filteredMails.get(position).getCreatedAt().getTime());
                        intent.putExtra("package_status", filteredMails.get(position).getStatus());
                        intent.putExtra("drop_off_date", filteredMails.get(position).getDropOffDate());
                        intent.putExtra("pick_up_date", filteredMails.get(position).getDate());
                        intent.putExtra("weight", filteredMails.get(position).getWeight());
                        intent.putExtra("parcel_type", filteredMails.get(position).getParcelType());
                        intent.putExtra("receiver_name", filteredMails.get(position).getReceiverFirstName() + " " + filteredMails.get(position).getReceiverLastName());
                        intent.putExtra("receiver_contact", filteredMails.get(position).getReceiverPhoneNumber());
                        intent.putExtra("receiver_email", filteredMails.get(position).getReceiverEmail());
                        intent.putExtra("payment_method", filteredMails.get(position).getPaymentMethod());
                        intent.putExtra("total_cost", filteredMails.get(position).getTotalCost());
                        intent.putExtra("pieces", filteredMails.get(position).getPieces());
                        intent.putExtra("pick_up_address", filteredMails.get(position).getPickupAddress());
                        intent.putExtra("drop_off_address", filteredMails.get(position).getReceiverAddress());
                        intent.putExtra("customer_name", filteredMails.get(position).getUser().getFirstName() + " " + filteredMails.get(position).getUser().getLastName());
                        intent.putExtra("customer_contact", filteredMails.get(position).getUser().getPhoneNumber());
                        intent.putExtra("center_name", filteredMails.get(position).getServiceCentre().getCentre());
                        intent.putExtra("center_address", filteredMails.get(position).getServiceCentre().getAddress());
                        intent.putExtra("driver_name", filteredMails.get(position).getDriverDetail().getUser().getFirstName() + " " + filteredMails.get(position).getDriverDetail().getUser().getLastName());
                        intent.putExtra("driver_contact", filteredMails.get(position).getDriverDetail().getUser().getPhoneNumber());
                    } else {
                        intent.putExtra("mail_id", filteredMails.get(position).getMailId());
                        intent.putExtra("package_description", filteredMails.get(position).getDescription());
                        intent.putExtra("created_at", filteredMails.get(position).getCreatedAt().getTime());
                        intent.putExtra("package_status", filteredMails.get(position).getStatus());
                        intent.putExtra("drop_off_date", filteredMails.get(position).getDropOffDate());
                        intent.putExtra("pick_up_date", filteredMails.get(position).getDate());
                        intent.putExtra("weight", filteredMails.get(position).getWeight());
                        intent.putExtra("parcel_type", filteredMails.get(position).getParcelType());
                        intent.putExtra("receiver_name", filteredMails.get(position).getReceiverFirstName() + " " + filteredMails.get(position).getReceiverLastName());
                        intent.putExtra("receiver_contact", filteredMails.get(position).getReceiverPhoneNumber());
                        intent.putExtra("receiver_email", filteredMails.get(position).getReceiverEmail());
                        intent.putExtra("payment_method", filteredMails.get(position).getPaymentMethod());
                        intent.putExtra("total_cost", filteredMails.get(position).getTotalCost());
                        intent.putExtra("pieces", filteredMails.get(position).getPieces());
                        intent.putExtra("pick_up_address", filteredMails.get(position).getPickupAddress());
                        intent.putExtra("drop_off_address", filteredMails.get(position).getReceiverAddress());
                        intent.putExtra("customer_name", filteredMails.get(position).getUser().getFirstName() + " " + filteredMails.get(position).getUser().getLastName());
                        intent.putExtra("customer_contact", filteredMails.get(position).getUser().getPhoneNumber());
                        intent.putExtra("center_name", filteredMails.get(position).getServiceCentre().getCentre());
                        intent.putExtra("center_address", filteredMails.get(position).getServiceCentre().getAddress());
                    }

                    context.startActivity(intent);
                }
            });

        } else if (userRole.equalsIgnoreCase("driver")) {
            holder._senderName.setText("Customer");
            holder.senderName.setText(String.format(filteredMails.get(position).getUser().getFirstName() + " " + filteredMails.get(position).getUser().getLastName()));
            holder.status.setText(filteredMails.get(position).getStatus());
            holder.description.setText(filteredMails.get(position).getDescription());
            holder.type.setText(filteredMails.get(position).getParcelType());
            holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
            holder.status.setTextColor(Color.parseColor("#00B832"));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewPackageDriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("mail_id", filteredMails.get(position).getMailId());
                    intent.putExtra("package_description", filteredMails.get(position).getDescription());
                    intent.putExtra("created_at", filteredMails.get(position).getCreatedAt().getTime());
                    intent.putExtra("package_status", filteredMails.get(position).getStatus());
                    intent.putExtra("drop_off_date", filteredMails.get(position).getDropOffDate());
                    intent.putExtra("pick_up_date", filteredMails.get(position).getDate());
                    intent.putExtra("weight", filteredMails.get(position).getWeight());
                    intent.putExtra("parcel_type", filteredMails.get(position).getParcelType());
                    intent.putExtra("receiver_name", filteredMails.get(position).getReceiverFirstName() + " " + filteredMails.get(position).getReceiverLastName());
                    intent.putExtra("receiver_contact", filteredMails.get(position).getReceiverPhoneNumber());
                    intent.putExtra("receiver_email", filteredMails.get(position).getReceiverEmail());
                    intent.putExtra("payment_method", filteredMails.get(position).getPaymentMethod());
                    intent.putExtra("total_cost", filteredMails.get(position).getTotalCost());
                    intent.putExtra("pieces", filteredMails.get(position).getPieces());
                    intent.putExtra("pick_up_address", filteredMails.get(position).getPickupAddress());
                    intent.putExtra("drop_off_address", filteredMails.get(position).getReceiverAddress());
                    intent.putExtra("customer_name", filteredMails.get(position).getUser().getFirstName() + " " + filteredMails.get(position).getUser().getLastName());
                    intent.putExtra("customer_contact", filteredMails.get(position).getUser().getPhoneNumber());
                    intent.putExtra("center_name", filteredMails.get(position).getServiceCentre().getCentre());
                    intent.putExtra("center_address", filteredMails.get(position).getServiceCentre().getAddress());
                    intent.putExtra("driver_name", filteredMails.get(position).getDriverDetail().getUser().getFirstName() + " " + filteredMails.get(position).getDriverDetail().getUser().getLastName());
                    intent.putExtra("driver_contact", filteredMails.get(position).getDriverDetail().getUser().getPhoneNumber());
                    context.startActivity(intent);
                }
            });
        }

    }

    private void trackOrOpenDispute(Mail mail) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Package ID #" + mail.getMailId());

        //When "track" button is clicked
        builder.setPositiveButton("Track", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(context, TrackPackageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (mail.getDriverDetail() != null) {
                    intent.putExtra("driverFirstName", mail.getDriverDetail().getUser().getFirstName());
                    intent.putExtra("driverLastName", mail.getDriverDetail().getUser().getLastName());
                    intent.putExtra("driverFirstName", mail.getDriverDetail().getUser().getFirstName());
                    intent.putExtra("driverVehicleNumber", mail.getDriverDetail().getVehicle().getVehicleNumber());
                    intent.putExtra("driverMobile", mail.getDriverDetail().getUser().getPhoneNumber());
                    intent.putExtra("status", mail.getStatus());
                    intent.putExtra("created_at", mail.getCreatedAt().getTime());
                    intent.putExtra("trackId", mail.getMailTracking().getTrackingId());
                    intent.putExtra("pickUpAddress", mail.getPickupAddress());
                    intent.putExtra("dropOffAddress", mail.getReceiverAddress());
                    intent.putExtra("description", mail.getDescription());
                    intent.putExtra("weight", mail.getWeight());
                    intent.putExtra("parcelType", mail.getParcelType());
                    intent.putExtra("pieces", mail.getPieces());
                } else {
                    intent.putExtra("status", mail.getStatus());
                    intent.putExtra("created_at", mail.getCreatedAt().getTime());
                    intent.putExtra("trackId", mail.getMailTracking().getTrackingId());
                    intent.putExtra("pickUpAddress", mail.getPickupAddress());
                    intent.putExtra("dropOffAddress", mail.getReceiverAddress());
                    intent.putExtra("description", mail.getDescription());
                    intent.putExtra("weight", mail.getWeight());
                    intent.putExtra("parcelType", mail.getParcelType());
                    intent.putExtra("pieces", mail.getPieces());
                }
                context.startActivity(intent);
            }
        });

        //When open dispute button is clicked
        builder.setNegativeButton("Open Dispute", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleOpenDispute(mail);
            }
        });

        builder.show();

    }

    private void handleOpenDispute(Mail mail) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.open_dispute_dialog, null);
        mBuilder.setTitle("Open Dispute");
        spinner = (Spinner) v.findViewById(R.id.dispute_reason);
        description = (EditText) v.findViewById(R.id.dispute_description);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.dispute_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        mBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openDispute(mail);
            }
        });
        mBuilder.setView(v);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    private void openDispute(Mail mail) {
        String _dispute_type = spinner.getSelectedItem().toString();
        String _description = description.getText().toString();

        Mail newMail = new Mail();
        newMail.setMailId(mail.getMailId());

        Disputes disputes = new Disputes();
        disputes.setDisputeType(_dispute_type);
        disputes.setDescription(_description);
        disputes.setMail(newMail);

        Call<ResponseBody> call = mailClient.openDispute(token, disputes);

        //Show progress
        mProgressDialog.setMessage("Opening Dispute...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Successfully accepted
                if (response.code() == 200) {
                    mProgressDialog.dismiss();
                    Toast.makeText(context, "successfully opened dispute", Toast.LENGTH_SHORT).show();

                } else {
                    mProgressDialog.dismiss();
                    Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void cancelOrTrackPackage(Mail mail) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Package ID #" + mail.getMailId());

        //When "track" button is clicked
        builder.setPositiveButton("Track", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(context, TrackPackageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (mail.getDriverDetail() != null) {
                    intent.putExtra("driverFirstName", mail.getDriverDetail().getUser().getFirstName());
                    intent.putExtra("driverLastName", mail.getDriverDetail().getUser().getLastName());
                    intent.putExtra("driverFirstName", mail.getDriverDetail().getUser().getFirstName());
                    intent.putExtra("driverVehicleNumber", mail.getDriverDetail().getVehicle().getVehicleNumber());
                    intent.putExtra("driverMobile", mail.getDriverDetail().getUser().getPhoneNumber());
                    intent.putExtra("status", mail.getStatus());
                    intent.putExtra("created_at", mail.getCreatedAt().getTime());
                    intent.putExtra("trackId", mail.getMailTracking().getTrackingId());
                    intent.putExtra("pickUpAddress", mail.getPickupAddress());
                    intent.putExtra("dropOffAddress", mail.getReceiverAddress());
                    intent.putExtra("description", mail.getDescription());
                    intent.putExtra("weight", mail.getWeight());
                    intent.putExtra("parcelType", mail.getParcelType());
                    intent.putExtra("pieces", mail.getPieces());
                } else {
                    intent.putExtra("status", mail.getStatus());
                    intent.putExtra("created_at", mail.getCreatedAt().getTime());
                    intent.putExtra("trackId", mail.getMailTracking().getTrackingId());
                    intent.putExtra("pickUpAddress", mail.getPickupAddress());
                    intent.putExtra("dropOffAddress", mail.getReceiverAddress());
                    intent.putExtra("description", mail.getDescription());
                    intent.putExtra("weight", mail.getWeight());
                    intent.putExtra("parcelType", mail.getParcelType());
                    intent.putExtra("pieces", mail.getPieces());
                }
                context.startActivity(intent);
            }
        });

        //When cancel button is clicked
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleCancelPackage(mail);
            }
        });

        builder.show();

    }

    private void handleCancelPackage(Mail mail) {
        Call<ResponseBody> call = mailClient.cancelPackage(token, mail.getMailId());

        //Show progress
        mProgressDialog.setMessage("Cancelling Request...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();

                //200 status code
                if (response.code() == 200) {
                    Toast.makeText(context, "Dispute submitted successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, FromYouActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(context, "Something! went wrong" + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleChangeDriver(Mail mail) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.change_driver_dialog, null);
        mBuilder.setTitle("Change Driver for package " + mail.getMailId());
        spinner = (Spinner) v.findViewById(R.id.driver_spinner);
        //Show progress
        mProgressDialog.setMessage("Setting up form...");
        mProgressDialog.show();

        setupDriverDropdown();

        mBuilder.setPositiveButton("Change Driver", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                changeDriver(mail);
            }
        });
        mBuilder.setView(v);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void changeDriver(Mail mail) {
        _driverId = spinner.getSelectedItem().toString();
        driverId = driver_ids.get(drivers.indexOf(_driverId));

        DriverDetail driverDetail = new DriverDetail();
        driverDetail.setDriverId(driverId);
        Mail newMail = new Mail();
        newMail.setMailId(mail.getMailId());
        newMail.setDriverDetail(driverDetail);

        Call<ResponseBody> call = agentClient.assignDriver(token, newMail);

        //Show progress
        mProgressDialog.setMessage("Changing Driver...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();

                //200 status code
                if (response.code() == 200) {
                    Toast.makeText(context, "Driver Changed Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, TransitListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(context, "Something! went wrong" + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void handleAssignDriver(int mailId) {

        _driverId = spinner.getSelectedItem().toString();
        dropOffDate = dropDate.getText().toString();
        driverId = driver_ids.get(drivers.indexOf(_driverId));

        DriverDetail driverDetail = new DriverDetail();
        driverDetail.setDriverId(driverId);
        Mail mail = new Mail();
        mail.setMailId(mailId);
        mail.setDropOffDate(dropOffDate);
        mail.setDriverDetail(driverDetail);

        Call<ResponseBody> call = agentClient.assignDriver(token, mail);

        //Show progress
        mProgressDialog.setMessage("Assigning Driver...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();

                //200 status code
                if (response.code() == 200) {
                    Toast.makeText(context, "Successfully Assigned", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, AgentAcceptedMailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(context, "Something! went wrong" + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDriverDropdown() {
        Call<List<User>> call = agentClient.getDrivers(token);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> driverList = response.body();
                if (driverList != null) {

                    //Configure drop down
                    for (User user : driverList) {
                        drivers.add(user.getFirstName() + " " + user.getLastName() + " [ " + user.getDriverDetail().getVehicle().getVehicleType() + " ]");
                        driver_ids.add(user.getDriverDetail().getDriverId());
                    }

                    //Set Adapter for dropdown
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, drivers);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    driversLoaded = true;

                    if (driversLoaded) mProgressDialog.dismiss();

                } else {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private void handleReject(int mailId) {
        Call<ResponseBody> call = agentClient.rejectPackage(token, mailId);

        //Show progress
        mProgressDialog.setMessage("Rejecting...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Successfully accepted
                if (response.code() == 200) {
                    mProgressDialog.dismiss();
                    Toast.makeText(context, "successfully rejected", Toast.LENGTH_SHORT).show();

                    //Direct to homepage
                    Intent intent = new Intent(context, AgentActivity.class);
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
                mProgressDialog.dismiss();
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAccept(int mailId) {
        Call<ResponseBody> call = agentClient.acceptPackage(token, mailId);

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
                    Intent intent = new Intent(context, AgentActivity.class);
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

    private void updateLabel() {
        String myFormat = "MM-dd-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dropDate.setText(sdf.format(myCalendar.getTime()));
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
