package com.example.express_delivery_mobile.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.express_delivery_mobile.AgentActivity;
import com.example.express_delivery_mobile.DriverAcceptedMailsActivity;
import com.example.express_delivery_mobile.DriverActivity;
import com.example.express_delivery_mobile.Model.DriverDetail;
import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.Model.ServiceCentre;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.Service.AdminClient;
import com.example.express_delivery_mobile.Service.DriverClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.ViewPackageAdminActivity;
import com.example.express_delivery_mobile.ViewPackageDriverActivity;
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
    private boolean centersLoaded;
    private Spinner spinner;

    //Dropdown attributes
    private List<String> centers = new ArrayList<>();
    private List<Integer> center_ids = new ArrayList<>();

    //Driver Retrofit client
    DriverClient driverClient = RetrofitClientInstance.getRetrofitInstance().create(DriverClient.class);
    AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DriverAcceptedMailAdapter.ViewHolder holder, final int position) {
        if (filteredMails.get(position).getStatus().equalsIgnoreCase("Driver Accepted")) {
            if (filteredMails.get(position).getTransportationStatus().contains("Pick Up")) {
                holder.name.setText(filteredMails.get(position).getUser().getFirstName() + " " + mails.get(position).getUser().getLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getPickupAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
                holder.paymentMethod.setText(filteredMails.get(position).getPaymentMethod());
                holder.totalPrice.setText(filteredMails.get(position).getTotalCost());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startDelivery(filteredMails.get(position).getMailId());

                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Intent intent = new Intent(context, ViewPackageAdminActivity.class);
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
                        return false;
                    }
                });

            } else if (filteredMails.get(position).getTransportationStatus().contains("Drop Off")) {
                holder.name.setText(filteredMails.get(position).getReceiverFirstName() + " " + mails.get(position).getReceiverLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getReceiverAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
                holder.paymentMethod.setText(filteredMails.get(position).getPaymentMethod());
                holder.totalPrice.setText(filteredMails.get(position).getTotalCost());

            }
        } else if (filteredMails.get(position).getStatus().equalsIgnoreCase("Delivery Started")) {
            if (filteredMails.get(position).getTransportationStatus().contains("Pick Up")) {
                holder.name.setText(filteredMails.get(position).getUser().getFirstName() + " " + mails.get(position).getUser().getLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getPickupAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
                holder.paymentMethod.setText(filteredMails.get(position).getPaymentMethod());
                holder.totalPrice.setText(filteredMails.get(position).getTotalCost());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pickupDelivery(filteredMails.get(holder.getAdapterPosition()));
                        notifyItemChanged(0, filteredMails.size());
                    }
                });

            } else if (filteredMails.get(position).getTransportationStatus().contains("Drop Off")) {
                holder.name.setText(filteredMails.get(position).getReceiverFirstName() + " " + mails.get(position).getReceiverLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getReceiverAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
                holder.paymentMethod.setText(filteredMails.get(position).getPaymentMethod());
                holder.totalPrice.setText(filteredMails.get(position).getTotalCost());

            }
        } else if (filteredMails.get(position).getStatus().equalsIgnoreCase("Package picked up")) {
            if (filteredMails.get(position).getTransportationStatus().contains("Pick Up")) {
                holder.name.setText(filteredMails.get(position).getUser().getFirstName() + " " + mails.get(position).getUser().getLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getPickupAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
                holder.paymentMethod.setText(filteredMails.get(position).getPaymentMethod());
                holder.totalPrice.setText(filteredMails.get(position).getTotalCost());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        transitPackage(filteredMails.get(holder.getAdapterPosition()));
                        notifyItemChanged(0, filteredMails.size());
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        callCustomer(filteredMails.get(position));
                        return false;
                    }
                });

            } else if (filteredMails.get(position).getTransportationStatus().contains("Drop Off")) {
                holder.name.setText(filteredMails.get(position).getReceiverFirstName() + " " + mails.get(position).getReceiverLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getReceiverAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
                holder.paymentMethod.setText(filteredMails.get(position).getPaymentMethod());
                holder.totalPrice.setText(filteredMails.get(position).getTotalCost());

            }
        } else if (filteredMails.get(position).getStatus().equalsIgnoreCase("In Transit")) {
            if (filteredMails.get(position).getTransportationStatus().contains("Drop Off")) {
                holder.name.setText(filteredMails.get(position).getReceiverFirstName() + " " + mails.get(position).getReceiverLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getReceiverAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
                holder.paymentMethod.setText(filteredMails.get(position).getPaymentMethod());
                holder.totalPrice.setText(filteredMails.get(position).getTotalCost());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleOutForDelivery(filteredMails.get(position));
                        notifyItemChanged(0, filteredMails.size());
                    }
                });
            }
        } else if (filteredMails.get(position).getStatus().equalsIgnoreCase("Out for Delivery")) {
            if (filteredMails.get(position).getTransportationStatus().contains("Drop Off")) {
                holder.name.setText(filteredMails.get(position).getReceiverFirstName() + " " + mails.get(position).getReceiverLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getReceiverAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
                holder.paymentMethod.setText(filteredMails.get(position).getPaymentMethod());
                holder.totalPrice.setText(filteredMails.get(position).getTotalCost());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleConfirmDelivery(filteredMails.get(position));
                        notifyItemChanged(0, filteredMails.size());
                    }
                });
            }
        } else if (filteredMails.get(position).getStatus().equalsIgnoreCase("Delivered")) {
            if (filteredMails.get(position).getTransportationStatus().contains("Drop Off")) {
                holder.name.setText(filteredMails.get(position).getReceiverFirstName() + " " + mails.get(position).getReceiverLastName());
                holder.transportationStatus.setText(filteredMails.get(position).getTransportationStatus());
                holder.address.setText(filteredMails.get(position).getReceiverAddress());
                holder.type.setText(filteredMails.get(position).getParcelType());
                holder.weight.setText(filteredMails.get(position).getWeight() + "KG");
                holder.paymentMethod.setText(filteredMails.get(position).getPaymentMethod());
                holder.totalPrice.setText(filteredMails.get(position).getTotalCost());
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

    }

    private void handleConfirmDelivery(Mail mail) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Package " + mail.getMailId());

        //When "Start" button is clicked
        builder.setPositiveButton("Confirm Delivered", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleConfirmDelivered(mail);
            }
        });

        //When cancel button is clicked
        builder.setNeutralButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleCallCustomer2(mail);
            }
        });

        builder.show();
    }

    private void handleOutForDelivery(Mail mail) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Package " + mail.getMailId());

        //When "Start" button is clicked
        builder.setPositiveButton("Start Delivery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startOutForDelivery(mail);
            }
        });

        //When cancel button is clicked
        builder.setNeutralButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleCallCustomer2(mail);
            }
        });

        builder.show();

    }

    private void startOutForDelivery(Mail mail) {

        Mail newMail = new Mail();
        newMail.setMailId(mail.getMailId());

        Call<ResponseBody> call = driverClient.startDelivery(token, newMail);

        //Show progress
        mProgressDialog.setMessage("Starting delivery...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Successfully accepted
                if (response.code() == 200) {
                    mProgressDialog.dismiss();
                    Toast.makeText(context, "successfully started delivery", Toast.LENGTH_SHORT).show();
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
                mProgressDialog.dismiss();
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void callCustomer(Mail mail) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Package " + mail.getMailId());


        //When cancel button is clicked
        builder.setNegativeButton("Call Customer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleCallCustomer2(mail);
            }
        });

        builder.show();
    }

    private void transitPackage(Mail mail) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Package " + mail.getMailId());

        //When "Start" button is clicked
        builder.setPositiveButton("Transit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleTransitPackage(mail);
            }
        });

        //When cancel button is clicked
        builder.setNeutralButton("Confirm Delivered", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleConfirmDelivered(mail);
            }
        });

        builder.show();
    }

    private void handleConfirmDelivered(Mail mail) {
        Call<ResponseBody> call = driverClient.confirmPackageDelivered(token, mail.getMailId());

        //Show progress
        mProgressDialog.setMessage("Confirming delivery...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Successfully accepted
                if (response.code() == 200) {
                    mProgressDialog.dismiss();
                    Toast.makeText(context, "Successfully Delivered", Toast.LENGTH_SHORT).show();
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
                mProgressDialog.dismiss();
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void handleCallCustomer2(Mail mail) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mail.getReceiverPhoneNumber()));
        context.startActivity(intent);
    }

    private void handleTransitPackage(Mail mail) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.change_center_dialog, null);
        mBuilder.setTitle("Transit package " + mail.getMailId());
        spinner = (Spinner) v.findViewById(R.id.center_spinner);
        //Show progress
        mProgressDialog.setMessage("Setting up form...");
        mProgressDialog.show();

        setupCenterDropdown();

        mBuilder.setPositiveButton("Drop Package", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleDropPackage(mail);
            }
        });
        mBuilder.setView(v);
        AlertDialog dialog = mBuilder.create();
        dialog.show();

    }

    private void handleDropPackage(Mail mail) {
        String _centerName = spinner.getSelectedItem().toString();
        int centerId = center_ids.get(centers.indexOf(_centerName));

        ServiceCentre serviceCentre = new ServiceCentre();
        serviceCentre.setCentreId(centerId);
        Mail newMail = new Mail();
        newMail.setMailId(mail.getMailId());
        newMail.setServiceCentre(serviceCentre);

        Call<ResponseBody> call = driverClient.transitPackage(token, newMail);

        //Show progress
        mProgressDialog.setMessage("Dropping package...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();

                //200 status code
                if (response.code() == 200) {
                    Toast.makeText(context, "successfully transited", Toast.LENGTH_SHORT).show();
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

    private void pickupDelivery(Mail mail) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Package " + mail.getMailId());

        //When "Start" button is clicked
        builder.setPositiveButton("Confirm Pick up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleConfirmPickup(mail.getMailId());
            }
        });

        //When cancel button is clicked
        builder.setNegativeButton("Call Customer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleCallCustomer(mail);
            }
        });

        builder.show();
    }

    private void handleCallCustomer(Mail mail) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mail.getUser().getPhoneNumber()));
        context.startActivity(intent);
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
                    mProgressDialog.dismiss();
                    Toast.makeText(context, "successfully picked up", Toast.LENGTH_SHORT).show();
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
                mProgressDialog.dismiss();
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

    private void setupCenterDropdown() {
        Call<List<ServiceCentre>> call = adminClient.getServiceCenters(token);

        call.enqueue(new Callback<List<ServiceCentre>>() {
            @Override
            public void onResponse(Call<List<ServiceCentre>> call, Response<List<ServiceCentre>> response) {
                List<ServiceCentre> serviceCentreList = response.body();
                if (serviceCentreList != null) {

                    //Configure drop down
                    for (ServiceCentre serviceCentre : serviceCentreList) {
                        centers.add(serviceCentre.getCentre());
                        center_ids.add(serviceCentre.getCentreId());
                    }

                    //Set Adapter for dropdown
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, centers);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    centersLoaded = true;

                    if (centersLoaded) mProgressDialog.dismiss();

                } else {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<ServiceCentre>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
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
        TextView name, transportationStatus, address, type, weight, paymentMethod, totalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.senderName);
            transportationStatus = itemView.findViewById(R.id.transport_status);
            address = itemView.findViewById(R.id.address);
            type = itemView.findViewById(R.id.type);
            weight = itemView.findViewById(R.id.weight);
            paymentMethod = itemView.findViewById(R.id.payment_method);
            totalPrice = itemView.findViewById(R.id.total_price);
        }
    }
}