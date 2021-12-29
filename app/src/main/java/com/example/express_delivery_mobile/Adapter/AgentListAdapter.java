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

import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class AgentListAdapter extends RecyclerView.Adapter<AgentListAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<User> agents;
    private List<User> filteredAgents;
    private String token;
    private String userRole;
    private String email;

    private ProgressDialog mProgressDialog;

    public AgentListAdapter(Context context, List<User> agents, String token, String userRole, ProgressDialog mProgressDialog) {
        this.context = context;
        this.agents = agents;
        this.token = token;
        this.userRole = userRole;
        this.mProgressDialog = mProgressDialog;
    }

    public void setAgents(final List<User> agents) {
        if (this.agents == null) {
            this.agents = agents;
            this.filteredAgents = agents;

            //Alert a change in items
            notifyItemChanged(0, filteredAgents.size());
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
                    return AgentListAdapter.this.agents.get(oldItemPosition).getEmail() == agents.get(newItemPosition).getEmail();

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    User newUser = AgentListAdapter.this.agents.get(oldItemPosition);

                    User oldUser = agents.get(newItemPosition);

                    return newUser.getEmail() == oldUser.getEmail();
                }
            });

            this.agents = agents;
            this.filteredAgents = agents;
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
                    filteredAgents = agents;
                } else {
                    List<User> filteredList = new ArrayList<>();
                    for (User user : agents) {
                        String searchString = charString.toLowerCase();

                        //Filter through fields and add to filtered list
                        if (user.getEmail().contains(searchString)) {
                            filteredList.add(user);
                        }
                    }
                    filteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredAgents;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredAgents = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public AgentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.agent_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgentListAdapter.ViewHolder holder, int position) {
        holder.agentName.setText(filteredAgents.get(position).getFirstName() + " " + filteredAgents.get(position).getLastName());
        holder.agentTelephone.setText(filteredAgents.get(position).getPhoneNumber());
        holder.agentEmail.setText(filteredAgents.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        if (filteredAgents != null) return filteredAgents.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView agentName, agentTelephone, agentEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            agentName = itemView.findViewById(R.id.agentName);
            agentTelephone = itemView.findViewById(R.id.agentTelephone);
            agentEmail = itemView.findViewById(R.id.agentEmail);
        }
    }
}
