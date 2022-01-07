package com.example.express_delivery_mobile.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import com.example.express_delivery_mobile.AgentListActivity;
import com.example.express_delivery_mobile.Model.Vehicle;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.Service.AdminClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.example.express_delivery_mobile.VehicleListActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Vehicle> vehicles;
    private List<Vehicle> filteredVehicles;
    private String token;
    private String userRole;
    private String email;

    private ProgressDialog mProgressDialog;

    public VehicleListAdapter(Context context, List<Vehicle> vehicles, String token, String userRole, ProgressDialog mProgressDialog) {
        this.context = context;
        this.vehicles = vehicles;
        this.token = token;
        this.userRole = userRole;
        this.mProgressDialog = mProgressDialog;
    }

    AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

    public void setVehicles(final List<Vehicle> vehicles) {
        if (this.vehicles == null) {
            this.vehicles = vehicles;
            this.filteredVehicles = vehicles;

            //Alert a change in items
            notifyItemChanged(0, filteredVehicles.size());
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
                    return VehicleListAdapter.this.vehicles.get(oldItemPosition).getVehicleId() == vehicles.get(newItemPosition).getVehicleId();

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Vehicle newVehicle = VehicleListAdapter.this.vehicles.get(oldItemPosition);

                    Vehicle oldVehicle = vehicles.get(newItemPosition);

                    return newVehicle.getVehicleId() == oldVehicle.getVehicleId();
                }
            });

            this.vehicles = vehicles;
            this.filteredVehicles = vehicles;
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
                    filteredVehicles = vehicles;
                } else {
                    List<Vehicle> filteredList = new ArrayList<>();
                    for (Vehicle vehicle : vehicles) {
                        String searchString = charString.toLowerCase();

                        //Filter through fields and add to filtered list
                        if (String.valueOf(vehicle.getVehicleId()).contains(searchString)) {
                            filteredList.add(vehicle);
                        }
                    }
                    filteredVehicles = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredVehicles;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredVehicles = (ArrayList<Vehicle>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public VehicleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vehicle_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleListAdapter.ViewHolder holder, int position) {

        if (filteredVehicles.get(position).getStatus().equalsIgnoreCase("taken")) {
            holder.status.setText("Occupied");
            holder.status.setTextColor(Color.RED);
        } else if (filteredVehicles.get(position).getStatus().equalsIgnoreCase("available")) {
            holder.status.setText("Available");
            holder.status.setTextColor(Color.parseColor("#0C6E0F"));
        } else if (filteredVehicles.get(position).getStatus().equalsIgnoreCase("Blacklisted")) {
            holder.status.setText("Blacklisted");
            holder.status.setTextColor(Color.BLACK);
        }
        holder.vehicleNumber.setText(filteredVehicles.get(position).getVehicleNumber());
        holder.vehicleType.setText(filteredVehicles.get(position).getVehicleType());
        holder.vehicleId.setText(String.valueOf(filteredVehicles.get(position).getVehicleId()));
        if (filteredVehicles.get(position).getStatus().equalsIgnoreCase("taken")) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    handleEditVehicle(filteredVehicles.get(position));
                    return false;
                }
            });
        } else if (filteredVehicles.get(position).getStatus().equalsIgnoreCase("available")) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    handleEditVehicle2(filteredVehicles.get(position));
                    return false;
                }
            });
        } else if (filteredVehicles.get(position).getStatus().equalsIgnoreCase("Blacklisted")) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    handleEditVehicle3(filteredVehicles.get(position));
                    return false;
                }
            });
        }
    }

    private void handleEditVehicle3(Vehicle vehicle) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Edit " + vehicle.getVehicleNumber() + " details ?");

        //When "Remove" button is clicked
        builder.setPositiveButton("Remove Blacklist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleRemoveBlacklist(vehicle);
            }
        });

        builder.show();
    }

    private void handleRemoveBlacklist(Vehicle vehicle) {
        Call<ResponseBody> call = adminClient.removeVehicleBlacklist(token, vehicle);

        //Show progress
        mProgressDialog.setMessage("Updating vehicle...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();

                //200 status code
                if (response.code() == 200) {
                    Toast.makeText(context, "Vehicle status updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, VehicleListActivity.class);
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

    private void handleEditVehicle2(Vehicle vehicle) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Edit " + vehicle.getVehicleNumber() + " details ?");

        //When "set blacklist" button is clicked
        builder.setNegativeButton("Set Blacklist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleSetBlacklist(vehicle);
            }
        });

        //When "set unavailable" button is clicked
        builder.setPositiveButton("Set Unavailable", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleSetUnavailable(vehicle);
            }
        });

        builder.show();
    }

    private void handleSetUnavailable(Vehicle vehicle) {
        Call<ResponseBody> call = adminClient.setVehicleUnavailable(token, vehicle);

        //Show progress
        mProgressDialog.setMessage("Updating vehicle...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();

                //200 status code
                if (response.code() == 200) {
                    Toast.makeText(context, "Vehicle status updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, VehicleListActivity.class);
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

    private void handleEditVehicle(Vehicle vehicle) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Edit " + vehicle.getVehicleNumber() + " details ?");

        //When "set blacklist" button is clicked
        builder.setNegativeButton("Set Blacklist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleSetBlacklist(vehicle);
            }
        });

        //When "set available" button is clicked
        builder.setPositiveButton("Set Available", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleSetAvailable(vehicle);
            }
        });

        builder.show();
    }

    private void handleSetBlacklist(Vehicle vehicle) {
        Call<ResponseBody> call = adminClient.setVehicleBlacklist(token, vehicle);

        //Show progress
        mProgressDialog.setMessage("Updating vehicle...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();

                //200 status code
                if (response.code() == 200) {
                    Toast.makeText(context, "Vehicle status updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, VehicleListActivity.class);
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

    private void handleSetAvailable(Vehicle vehicle) {
        Call<ResponseBody> call = adminClient.setVehicleAvailable(token, vehicle);

        //Show progress
        mProgressDialog.setMessage("Updating vehicle...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();

                //200 status code
                if (response.code() == 200) {
                    Toast.makeText(context, "Vehicle status updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, VehicleListActivity.class);
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

    @Override
    public int getItemCount() {
        if (filteredVehicles != null) return filteredVehicles.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView status, vehicleNumber, vehicleType, vehicleId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.vehicle_status);
            vehicleNumber = itemView.findViewById(R.id.vehicle_number);
            vehicleType = itemView.findViewById(R.id.vehicle_type);
            vehicleId = itemView.findViewById(R.id.vehicle_id);

        }
    }
}
