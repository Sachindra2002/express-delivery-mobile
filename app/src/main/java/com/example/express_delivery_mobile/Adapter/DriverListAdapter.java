package com.example.express_delivery_mobile.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.express_delivery_mobile.AddDriverActivity;
import com.example.express_delivery_mobile.AdminDriverListActivity;
import com.example.express_delivery_mobile.AdminDriverProfileActivity;
import com.example.express_delivery_mobile.AgentDriverProfileActivity;
import com.example.express_delivery_mobile.AgentListActivity;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.Service.AdminClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.TrackPackageActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverListAdapter extends RecyclerView.Adapter<DriverListAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<User> drivers;
    private List<User> filteredDrivers;
    private String token;
    private String userRole;
    private String email;

    private ProgressDialog mProgressDialog;

    AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

    public DriverListAdapter(Context context, List<User> drivers, String token, String userRole, ProgressDialog mProgressDialog) {
        this.context = context;
        this.drivers = drivers;
        this.token = token;
        this.userRole = userRole;
        this.email = email;
        this.mProgressDialog = mProgressDialog;
    }

    public void setDrivers(final List<User> drivers) {
        if (this.drivers == null) {
            this.drivers = drivers;
            this.filteredDrivers = drivers;

            //Alert a change in items
            notifyItemChanged(0, filteredDrivers.size());
        } else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return 0;
                }

                @Override
                public int getNewListSize() {
                    return 0;
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return DriverListAdapter.this.drivers.get(oldItemPosition).getEmail() == drivers.get(newItemPosition).getEmail();

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    User newUser = DriverListAdapter.this.drivers.get(oldItemPosition);

                    User oldUser = drivers.get(newItemPosition);

                    return newUser.getEmail() == oldUser.getEmail();
                }
            });

            this.drivers = drivers;
            this.filteredDrivers = drivers;
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
                    filteredDrivers = drivers;
                } else {
                    List<User> filteredList = new ArrayList<>();
                    for (User user : drivers) {
                        String searchString = charString.toLowerCase();

                        //Filter through fields and add to filtered list
                        if (user.getEmail().contains(searchString) || String.valueOf(user.getDriverDetail().getDriverId()).contains(searchString)) {
                            filteredList.add(user);
                        }
                    }
                    filteredDrivers = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredDrivers;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredDrivers = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public DriverListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.driver_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (userRole.equalsIgnoreCase("agent")) {
            holder.driverStatus.setText(filteredDrivers.get(position).getDriverDetail().getStatus());
            holder.driverName.setText(filteredDrivers.get(position).getFirstName() + " " + filteredDrivers.get(position).getLastName());
            holder.driverTelephone.setText(filteredDrivers.get(position).getPhoneNumber());
            holder.driverAddress.setText(filteredDrivers.get(position).getDriverDetail().getAddress());
            holder.driverNic.setText(filteredDrivers.get(position).getDriverDetail().getNic());
            holder.driverEmail.setText(filteredDrivers.get(position).getEmail());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AgentDriverProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("driver_name", filteredDrivers.get(position).getFirstName() + " " + filteredDrivers.get(position).getLastName());
                    intent.putExtra("driver_telephone", filteredDrivers.get(position).getPhoneNumber());
                    intent.putExtra("driver_dob", filteredDrivers.get(position).getDriverDetail().getDob());
                    intent.putExtra("driver_address", filteredDrivers.get(position).getDriverDetail().getAddress());
                    intent.putExtra("driver_email", filteredDrivers.get(position).getEmail());
                    intent.putExtra("driver_city", filteredDrivers.get(position).getLocation());
                    intent.putExtra("driver_nic", filteredDrivers.get(position).getDriverDetail().getNic());
                    intent.putExtra("vehicle_number", filteredDrivers.get(position).getDriverDetail().getVehicle().getVehicleNumber());
                    intent.putExtra("vehicle_type", filteredDrivers.get(position).getDriverDetail().getVehicle().getVehicleType());
                    intent.putExtra("center", filteredDrivers.get(position).getServiceCentre().getCentre());
                    intent.putExtra("center_address", filteredDrivers.get(position).getServiceCentre().getAddress());
                    intent.putExtra("driver_id", filteredDrivers.get(position).getDriverDetail().getDriverId());
                    context.startActivity(intent);
                }
            });
        } else if (userRole.equalsIgnoreCase("admin")) {
            holder.driverStatus.setText(filteredDrivers.get(position).getDriverDetail().getStatus());
            holder.driverName.setText(filteredDrivers.get(position).getFirstName() + " " + filteredDrivers.get(position).getLastName());
            holder.driverTelephone.setText(filteredDrivers.get(position).getPhoneNumber());
            holder.driverAddress.setText(filteredDrivers.get(position).getDriverDetail().getAddress());
            holder.driverNic.setText(filteredDrivers.get(position).getDriverDetail().getNic());
            holder.driverEmail.setText(filteredDrivers.get(position).getEmail());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AdminDriverProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    if (filteredDrivers.get(position).getDriverDetail().getVehicle() != null) {
                        intent.putExtra("driver_name", filteredDrivers.get(position).getFirstName() + " " + filteredDrivers.get(position).getLastName());
                        intent.putExtra("driver_telephone", filteredDrivers.get(position).getPhoneNumber());
                        intent.putExtra("driver_dob", filteredDrivers.get(position).getDriverDetail().getDob());
                        intent.putExtra("driver_address", filteredDrivers.get(position).getDriverDetail().getAddress());
                        intent.putExtra("driver_email", filteredDrivers.get(position).getEmail());
                        intent.putExtra("driver_city", filteredDrivers.get(position).getLocation());
                        intent.putExtra("driver_nic", filteredDrivers.get(position).getDriverDetail().getNic());
                        intent.putExtra("vehicle_number", filteredDrivers.get(position).getDriverDetail().getVehicle().getVehicleNumber());
                        intent.putExtra("vehicle_type", filteredDrivers.get(position).getDriverDetail().getVehicle().getVehicleType());
                        intent.putExtra("center", filteredDrivers.get(position).getServiceCentre().getCentre());
                        intent.putExtra("center_address", filteredDrivers.get(position).getServiceCentre().getAddress());
                        intent.putExtra("driver_id", filteredDrivers.get(position).getDriverDetail().getDriverId());
                    } else {
                        intent.putExtra("driver_name", filteredDrivers.get(position).getFirstName() + " " + filteredDrivers.get(position).getLastName());
                        intent.putExtra("driver_telephone", filteredDrivers.get(position).getPhoneNumber());
                        intent.putExtra("driver_dob", filteredDrivers.get(position).getDriverDetail().getDob());
                        intent.putExtra("driver_address", filteredDrivers.get(position).getDriverDetail().getAddress());
                        intent.putExtra("driver_email", filteredDrivers.get(position).getEmail());
                        intent.putExtra("driver_city", filteredDrivers.get(position).getLocation());
                        intent.putExtra("driver_nic", filteredDrivers.get(position).getDriverDetail().getNic());
                        intent.putExtra("center", filteredDrivers.get(position).getServiceCentre().getCentre());
                        intent.putExtra("center_address", filteredDrivers.get(position).getServiceCentre().getAddress());
                        intent.putExtra("driver_id", filteredDrivers.get(position).getDriverDetail().getDriverId());
                    }

                    context.startActivity(intent);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    handleRemoveDriver(filteredDrivers.get(position));
                    return false;
                }
            });
        }
    }

    private void handleRemoveDriver(User user) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Remove " + user.getFirstName() + " " + user.getLastName());

        //When "Remove" button is clicked
        builder.setNegativeButton("Remove Driver", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeDriver(user);
            }
        });

        builder.show();
    }

    private void removeDriver(User user) {
        Call<ResponseBody> call = adminClient.deleteDriver(token, user);

        //Show progress
        mProgressDialog.setMessage("Removing Driver...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();

                //200 status code
                if (response.code() == 200) {
                    Toast.makeText(context, "Driver removed successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, AdminDriverListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                } else {
                    try {
                        // Capture an display specific messages
                        JSONObject obj = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(context, "Something! went wrong" + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (filteredDrivers != null) return filteredDrivers.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView driverName, driverTelephone, driverAddress, driverStatus, driverNic, driverEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.driverName);
            driverTelephone = itemView.findViewById(R.id.driverTelephone);
            driverAddress = itemView.findViewById(R.id.driverAddress);
            driverStatus = itemView.findViewById(R.id.driver_status);
            driverNic = itemView.findViewById(R.id.driverNic);
            driverEmail = itemView.findViewById(R.id.driverEmail);
        }
    }
}
