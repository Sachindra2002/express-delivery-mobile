package com.example.express_delivery_mobile.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.express_delivery_mobile.AgentDriverProfileActivity;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.TrackPackageActivity;

import java.util.ArrayList;
import java.util.List;

public class DriverListAdapter extends RecyclerView.Adapter<DriverListAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<User> drivers;
    private List<User> filteredDrivers;
    private String token;
    private String userRole;
    private String email;

    private ProgressDialog mProgressDialog;

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
                        if (user.getEmail().contains(searchString)) {
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

        }
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
