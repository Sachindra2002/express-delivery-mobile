package com.example.express_delivery_mobile.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.express_delivery_mobile.Model.Disputes;
import com.example.express_delivery_mobile.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisputesAdapter extends RecyclerView.Adapter<DisputesAdapter.ViewHolder> implements Filterable {

    private  Context context;
    private List<Disputes> disputes;
    private List<Disputes> filteredDisputes;
    private String token;
    private String userRole;
    private String email;

    ProgressDialog mProgressDialog;

    public DisputesAdapter(Context context, List<Disputes> disputes, String token, String userRole, ProgressDialog mProgressDialog) {
        this.context = context;
        this.disputes = disputes;
        this.token = token;
        this.userRole = userRole;
        this.mProgressDialog = mProgressDialog;
    }

    public void setDisputes(final List<Disputes> disputes){
        if(this.disputes  == null){
            this.disputes = disputes;
            this.filteredDisputes = disputes;

            //Alert a change in items
            notifyItemChanged(0, filteredDisputes.size());
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
                    return DisputesAdapter.this.disputes.get(oldItemPosition).getDisputeId() == disputes.get(newItemPosition).getDisputeId();

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Disputes newDisputes = DisputesAdapter.this.disputes.get(oldItemPosition);

                    Disputes oldDisputes = disputes.get(newItemPosition);

                    return newDisputes.getDisputeId() == oldDisputes.getDisputeId();
                }
            });

            this.disputes = disputes;
            this.filteredDisputes = disputes;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if(charString.isEmpty()){
                    filteredDisputes = disputes;
                }else{
                    List<Disputes> filteredList = new ArrayList<>();
                    for(Disputes dispute : disputes){
                        String searchString = charString.toLowerCase();

                        //Filter through fields and add to filtered list
                        if(String.valueOf(dispute.getDisputeId()).contains(searchString)){
                            filteredList.add(dispute);
                        }
                    }
                    filteredDisputes = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredDisputes;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredDisputes = (ArrayList<Disputes>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public DisputesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dispute_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisputesAdapter.ViewHolder holder, int position) {
        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(filteredDisputes.get(position).getCreatedAt());
        holder.date.setText(date);
        holder.inquiryId.setText(String.valueOf(filteredDisputes.get(position).getDisputeId()));
        holder.mailId.setText(String.valueOf(filteredDisputes.get(position).getMail().getMailId()));
        holder.status.setText(filteredDisputes.get(position).getStatus());
        holder.customerName.setText(filteredDisputes.get(position).getMail().getUser().getFirstName() + " " + filteredDisputes.get(position).getMail().getUser().getLastName());

    }

    @Override
    public int getItemCount() {
        if (filteredDisputes != null) return filteredDisputes.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView status, customerName, date, inquiryId, mailId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.inquiry_status);
            customerName = itemView.findViewById(R.id.customer_name);
            date = itemView.findViewById(R.id.date);
            inquiryId = itemView.findViewById(R.id.inquiry_id);
            mailId = itemView.findViewById(R.id.mail_id);
        }
    }
}
