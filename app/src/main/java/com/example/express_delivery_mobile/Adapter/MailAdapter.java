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

import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.Service.MailClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

public class MailAdapter extends RecyclerView.Adapter<MailAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Mail> mails;
    private List<Mail> filteredMails;
    private String token;
    private String userRole;
    private String username;

    private ProgressDialog mProgressDialog;

    //Mail Retrofit client
    MailClient mailClient = RetrofitClientInstance.getRetrofitInstance().create(MailClient.class);

    public MailAdapter(Context context, List<Mail> mails, String token, String userRole, ProgressDialog mProgressDialog) {
        this.context = context;
        this.mails = mails;
        this.token = token;
        this.userRole = userRole;
        this.mProgressDialog = mProgressDialog;
    }

    public void setMails(final List<Mail> mails){
        if(this.mails == null){
            this.mails = mails;
            this.filteredMails = mails;

            //Alert a change in items
            notifyItemChanged(0, filteredMails.size());
        }
        //If updating items (previously not null)
        else{
            final DiffUtil.DiffResult result= DiffUtil.calculateDiff(new DiffUtil.Callback() {
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

                    Mail oldMail= mails.get(newItemPosition);

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
                if(charString.isEmpty()){
                    filteredMails = mails;
                } else {
                    List<Mail> filteredList = new ArrayList<>();
                    for(Mail mail :  mails){
                        String searchString = charString.toLowerCase();

                        //Filter through fields and add to filtered list
                        if(mail.getDescription().contains(searchString) || String.valueOf(mail.getMailId()).contains(charString)){
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
    public void onBindViewHolder(@NonNull MailAdapter.ViewHolder holder, final int position) {
        holder.senderName.setText(String.format(filteredMails.get(position).getReceiverFirstName() + " " + filteredMails.get(position).getReceiverLastName()));
        holder.status.setText(filteredMails.get(position).getStatus());
        holder.description.setText(filteredMails.get(position).getDescription());
        holder.type.setText(filteredMails.get(position).getParcelType());
        holder.weight.setText(filteredMails.get(position).getWeight() + "KG");

        holder.itemView.setOnClickListener(view -> {

        });

    }

    @Override
    public int getItemCount() {
        if (filteredMails != null) return filteredMails.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, status, description, type, weight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            senderName = itemView.findViewById(R.id.senderName);
            status = itemView.findViewById(R.id.status);
            description = itemView.findViewById(R.id.description);
            type = itemView.findViewById(R.id.type);
            weight = itemView.findViewById(R.id.weight);
        }
    }
}
