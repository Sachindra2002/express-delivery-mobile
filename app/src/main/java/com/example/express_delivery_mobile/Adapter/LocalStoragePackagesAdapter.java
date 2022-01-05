package com.example.express_delivery_mobile.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.express_delivery_mobile.Model.LocalStoragePackages;
import com.example.express_delivery_mobile.R;

import java.util.List;

public class LocalStoragePackagesAdapter extends RecyclerView.Adapter<LocalStoragePackagesAdapter.ViewHolder> {
    private Context context;
    private List<LocalStoragePackages> packages;
    private List<LocalStoragePackages> filteredPackages;
    private String token;
    private String userRole;

    private ProgressDialog mProgressDialog;

    public LocalStoragePackagesAdapter(Context context, List<LocalStoragePackages> packages, ProgressDialog mProgressDialog) {
        this.context = context;
        this.packages = packages;
        this.mProgressDialog = mProgressDialog;
    }

    public void setPackages(final List<LocalStoragePackages> packages) {
        if (this.packages == null) {
            this.packages = packages;
            this.filteredPackages = packages;
            //Alert a change in item
            notifyItemChanged(0, filteredPackages.size());
        }
        //if updating items (previously)
        else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return LocalStoragePackagesAdapter.this.packages.size();
                }

                @Override
                public int getNewListSize() {
                    return packages.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return LocalStoragePackagesAdapter.this.packages.get(oldItemPosition).getMailId() == packages.get(newItemPosition).getMailId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    LocalStoragePackages newLocalStoragePackage = LocalStoragePackagesAdapter.this.packages.get(oldItemPosition);

                    LocalStoragePackages oldLocalStoragePackage = packages.get(newItemPosition);

                    return newLocalStoragePackage.getMailId() == oldLocalStoragePackage.getMailId();
                }
            });

            this.packages = packages;
            this.filteredPackages = packages;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public LocalStoragePackagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.local_storage_packages_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalStoragePackagesAdapter.ViewHolder holder, int position) {
        holder.status.setText(filteredPackages.get(position).getPackageStatus());
        holder.customerName.setText(filteredPackages.get(position).getCustomerName());
        holder.customerEmail.setText(filteredPackages.get(position).getCustomerEmail());
        holder.address.setText(filteredPackages.get(position).getPickUpAddress());
        holder.type.setText(filteredPackages.get(position).getParcelType());
        holder.weight.setText(filteredPackages.get(position).getWeight() + "KG");
        holder.paymentMethod.setText(filteredPackages.get(position).getPaymentMethod());
        holder.totalCost.setText(filteredPackages.get(position).getTotalCost());
    }

    @Override
    public int getItemCount() {
        if (filteredPackages != null) return filteredPackages.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView status, customerName, customerEmail, address, type, weight, paymentMethod, totalCost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.transport_status);
            customerName = itemView.findViewById(R.id.customer_name);
            customerEmail = itemView.findViewById(R.id.customer_email);
            address = itemView.findViewById(R.id.address);
            type = itemView.findViewById(R.id.type);
            weight = itemView.findViewById(R.id.weight);
            paymentMethod = itemView.findViewById(R.id.payment_method);
            totalCost = itemView.findViewById(R.id.total_cost);
        }
    }
}
