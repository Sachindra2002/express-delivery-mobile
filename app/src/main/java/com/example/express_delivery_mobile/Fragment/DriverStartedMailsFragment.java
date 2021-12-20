package com.example.express_delivery_mobile.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.express_delivery_mobile.Adapter.DriverAcceptedMailAdapter;
import com.example.express_delivery_mobile.Model.Mail;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.Service.DriverClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverStartedMailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverStartedMailsFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Mail> mails;
    private DriverAcceptedMailAdapter driverAcceptedMailAdapter;
    private String token;

    private DriverClient driverClient = RetrofitClientInstance.getRetrofitInstance().create(DriverClient.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DriverStartedMailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverStartedMailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverStartedMailsFragment newInstance(String param1, String param2) {
        DriverStartedMailsFragment fragment = new DriverStartedMailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_started_mails, container, false);
        mProgressDialog = new ProgressDialog(getContext());

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup mail list
        mails = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        driverAcceptedMailAdapter = new DriverAcceptedMailAdapter(getContext(), mails, token, "driver", mProgressDialog);
        recyclerView.setAdapter(driverAcceptedMailAdapter);

        // SetOnRefreshListener on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getAllStartedMails();
            }
        });

        getAllStartedMails();
        return view;
    }

    private void getAllStartedMails() {
        Call<List<Mail>> call = driverClient.getAllStartedMails(token);

        //Show Progress
        mProgressDialog.setMessage("Loading Packages..");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Mail>>() {
            @Override
            public void onResponse(@NonNull Call<List<Mail>> call, @NonNull Response<List<Mail>> response) {
                mails = response.body();
                System.out.println(response);
                System.out.println(response.body());
                //Handle null pointer errors
                if (mails != null) {
                    driverAcceptedMailAdapter.setMails(mails);
                } else {
                    Toast.makeText(getContext(), "Something went wrong" + response.toString(), Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {
                Toast.makeText(getContext(), "Something went Wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }
}