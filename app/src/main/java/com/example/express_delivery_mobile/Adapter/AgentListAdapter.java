package com.example.express_delivery_mobile.Adapter;

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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.express_delivery_mobile.AddAgentActivity;
import com.example.express_delivery_mobile.AdminActivity;
import com.example.express_delivery_mobile.AgentActivity;
import com.example.express_delivery_mobile.AgentListActivity;
import com.example.express_delivery_mobile.Model.ServiceCentre;
import com.example.express_delivery_mobile.Model.User;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.Service.AdminClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgentListAdapter extends RecyclerView.Adapter<AgentListAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<User> agents;
    private List<User> filteredAgents;
    private String token;
    private String userRole;
    private String email;

    //Dropdown attributes
    private List<String> centers = new ArrayList<>();
    private List<Integer> center_ids = new ArrayList<>();
    private boolean centersLoaded;
    private Spinner spinner;

    private ProgressDialog mProgressDialog;

    AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

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
        holder.center.setText(filteredAgents.get(position).getServiceCentre().getCentre());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAgent(filteredAgents.get(position));
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editAgentDetails(filteredAgents.get(position));
                return false;
            }
        });
    }

    private void editAgentDetails(User user) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Edit " + user.getFirstName() + " " + user.getLastName() + " details ?");

        //When "center" button is clicked
        builder.setPositiveButton("Service Center", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleChangeCenter(user);
            }
        });

        //When "Remove" button is clicked
        builder.setNegativeButton("Remove Agent", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleRemoveAgent(user);
            }
        });

        builder.show();
    }

    private void handleChangeCenter(User user) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogTheme);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.update_center_dialog, null);
        mBuilder.setTitle("Change service center fot " + user.getFirstName() + " " + user.getLastName());
        spinner = (Spinner) v.findViewById(R.id.center_spinner);
        //Show progress
        mProgressDialog.setMessage("Setting up form...");
        mProgressDialog.show();

        setupCenterDropdown();

        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleUpdateCenter(user);
            }
        });

        mBuilder.setView(v);
        AlertDialog dialog = mBuilder.create();
        dialog.show();


    }

    private void handleUpdateCenter(User user) {
        String _center = spinner.getSelectedItem().toString();

        ServiceCentre serviceCentre = new ServiceCentre();
        serviceCentre.setCentreId(center_ids.get(centers.indexOf(_center)));
        user.setServiceCentre(serviceCentre);

        Call<ResponseBody> call = adminClient.updateCenterAgent(token, user);

        //Show progress
        mProgressDialog.setMessage("Updating service center...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                //200 status code
                if (response.code() == 200) {
                    Toast.makeText(context, "Center updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, AgentListActivity.class);
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

    private void handleRemoveAgent(User user) {
        Call<ResponseBody> call = adminClient.deleteAgent(token, user);

        //Show progress
        mProgressDialog.setMessage("Removing Agent...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();

                //200 status code
                if (response.code() == 200) {
                    Toast.makeText(context, "Agent removed successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, AgentListActivity.class);
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

    private void callAgent(User user) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Call " + user.getFirstName() + " " + user.getLastName() + " ?");

        //When "Call" button is clicked
        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                handleCallAgent(user);
            }
        });

        builder.show();
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

    private void handleCallAgent(User user) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + user.getPhoneNumber()));
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if (filteredAgents != null) return filteredAgents.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView agentName, agentTelephone, agentEmail, center;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            agentName = itemView.findViewById(R.id.agentName);
            agentTelephone = itemView.findViewById(R.id.agentTelephone);
            agentEmail = itemView.findViewById(R.id.agentEmail);
            center = itemView.findViewById(R.id.center_name);
        }
    }
}
