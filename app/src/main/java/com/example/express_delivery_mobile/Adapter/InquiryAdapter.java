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

import com.example.express_delivery_mobile.Model.Inquiry;
import com.example.express_delivery_mobile.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InquiryAdapter extends RecyclerView.Adapter<InquiryAdapter.ViewHolder> implements Filterable {

    private final Context context;
    private List<Inquiry> inquiries;
    private List<Inquiry> filteredInquiry;
    private String token;
    private String userRole;
    private String email;

    ProgressDialog mProgressDialog;

    public InquiryAdapter(Context context, List<Inquiry> inquiries, String token, String userRole, ProgressDialog mProgressDialog) {
        this.context = context;
        this.inquiries = inquiries;
        this.token = token;
        this.userRole = userRole;
        this.mProgressDialog = mProgressDialog;
    }

    public void setInquiries(final List<Inquiry> inquiries){
        if(this.inquiries  == null){
            this.inquiries = inquiries;
            this.filteredInquiry = inquiries;

            //Alert a change in items
            notifyItemChanged(0, filteredInquiry.size());
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
                    return InquiryAdapter.this.inquiries.get(oldItemPosition).getInquiryId() == inquiries.get(newItemPosition).getInquiryId();

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Inquiry newInquiry = InquiryAdapter.this.inquiries.get(oldItemPosition);

                    Inquiry oldInquiry = inquiries.get(newItemPosition);

                    return newInquiry.getInquiryId() == oldInquiry.getInquiryId();
                }
            });

            this.inquiries = inquiries;
            this.filteredInquiry = inquiries;
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
                    filteredInquiry = inquiries;
                }else{
                    List<Inquiry> filteredList = new ArrayList<>();
                    for(Inquiry inquiry : inquiries){
                        String searchString = charString.toLowerCase();

                        //Filter through fields and add to filtered list
                        if(String.valueOf(inquiry.getInquiryId()).contains(searchString)){
                            filteredList.add(inquiry);
                        }
                    }
                    filteredInquiry = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredInquiry;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredInquiry = (ArrayList<Inquiry>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public InquiryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inquiry_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InquiryAdapter.ViewHolder holder, int position) {
        SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = df.parse(String.valueOf(filteredInquiry.get(position).getCreatedAt()));
            df.applyPattern("dd/MM/yyyy");
            holder.date.setText(df.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.inquiryId.setText(String.valueOf(filteredInquiry.get(position).getInquiryId()));
        holder.status.setText(filteredInquiry.get(position).getStatus());
        holder.customerName.setText(filteredInquiry.get(position).getUser().getFirstName() + " " + filteredInquiry.get(position).getUser().getLastName());
    }

    @Override
    public int getItemCount() {
        if (filteredInquiry != null) return filteredInquiry.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView status, customerName, date, inquiryId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.inquiry_status);
            customerName = itemView.findViewById(R.id.customer_name);
            date = itemView.findViewById(R.id.date);
            inquiryId = itemView.findViewById(R.id.inquiry_id);
        }
    }
}
