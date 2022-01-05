package com.example.express_delivery_mobile.Adapter;

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
import com.example.express_delivery_mobile.Model.ServiceCentre;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.ViewServiceCenterActivity;

import java.util.ArrayList;
import java.util.List;

public class ServiceCenterListAdapter extends RecyclerView.Adapter<ServiceCenterListAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<ServiceCentre> centers;
    private List<ServiceCentre> filteredCenters;
    private String token;
    private String userRole;
    private String email;

    private ProgressDialog mProgressDialog;

    public ServiceCenterListAdapter(Context context, List<ServiceCentre> centers, String token, String userRole, ProgressDialog mProgressDialog) {
        this.context = context;
        this.centers = centers;
        this.token = token;
        this.userRole = userRole;
        this.mProgressDialog = mProgressDialog;
    }

    public void setCenters(final List<ServiceCentre> centers) {
        if (this.centers == null) {
            this.centers = centers;
            this.filteredCenters = centers;

            //Alert a change in items
            notifyItemChanged(0, filteredCenters.size());
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
                    return ServiceCenterListAdapter.this.centers.get(oldItemPosition).getCentreId() == centers.get(newItemPosition).getCentreId();

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ServiceCentre newCenter = ServiceCenterListAdapter.this.centers.get(oldItemPosition);

                    ServiceCentre oldCenter = centers.get(newItemPosition);

                    return newCenter.getCentreId() == oldCenter.getCentreId();
                }
            });

            this.centers = centers;
            this.filteredCenters = centers;
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
                    filteredCenters = centers;
                } else {
                    List<ServiceCentre> filteredList = new ArrayList<>();
                    for (ServiceCentre centre : centers) {
                        String searchString = charString.toLowerCase();

                        //Filter through fields and add to filtered list
                        if (centre.getCentre().contains(searchString)) {
                            filteredList.add(centre);
                        }
                    }
                    filteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredCenters;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredCenters = (ArrayList<ServiceCentre>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public ServiceCenterListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_center_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceCenterListAdapter.ViewHolder holder, int position) {
        holder.centerId.setText(String.valueOf(filteredCenters.get(position).getCentreId()));
        holder.centerCity.setText(filteredCenters.get(position).getCity());
        holder.center.setText(filteredCenters.get(position).getCentre());
        holder.centerAddress.setText(filteredCenters.get(position).getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewServiceCenterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("center_id", filteredCenters.get(position).getCentreId());
                intent.putExtra("center_name", filteredCenters.get(position).getCentre());
                intent.putExtra("center_city", filteredCenters.get(position).getCity());
                intent.putExtra("center_address", filteredCenters.get(position).getAddress());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (filteredCenters != null) return filteredCenters.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView centerId, center, centerAddress, centerCity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            centerId = itemView.findViewById(R.id.center_id);
            center = itemView.findViewById(R.id.center_name);
            centerAddress = itemView.findViewById(R.id.center_address);
            centerCity = itemView.findViewById(R.id.center_city);
        }
    }
}
