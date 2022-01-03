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

import com.example.express_delivery_mobile.Adapter.InquiryAdapter;
import com.example.express_delivery_mobile.Model.Inquiry;
import com.example.express_delivery_mobile.R;
import com.example.express_delivery_mobile.Service.AdminClient;
import com.example.express_delivery_mobile.Service.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneralDisputesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralDisputesFragment extends Fragment {

    private ProgressDialog mProgressDialog;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Inquiry> inquiries;
    private InquiryAdapter inquiryAdapter;
    private String token;

    private AdminClient adminClient = RetrofitClientInstance.getRetrofitInstance().create(AdminClient.class);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GeneralDisputesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GeneralDisputesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralDisputesFragment newInstance(String param1, String param2) {
        GeneralDisputesFragment fragment = new GeneralDisputesFragment();
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
        View view = inflater.inflate(R.layout.fragment_general_disputes, container, false);
        mProgressDialog = new ProgressDialog(getContext());

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = "Bearer " + sharedPreferences.getString("auth_token", null);

        //Setup inquiry list
        inquiries = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        Call<List<Inquiry>> call = adminClient.getInquiries(token);

        //Show Progress
        mProgressDialog.setMessage("Loading Inquiries..");
        mProgressDialog.show();

        getAllInquiries();

        // SetOnRefreshListener on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getAllInquiries();
            }
        });

        return view;
    }

    private void getAllInquiries() {
        Call<List<Inquiry>> call = adminClient.getInquiries(token);

        //Show Progress
        mProgressDialog.setMessage("Loading Inquiries..");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Inquiry>>() {
            @Override
            public void onResponse(@NonNull Call<List<Inquiry>> call, @NonNull Response<List<Inquiry>> response) {
                inquiries = response.body();
                System.out.println(response);
                System.out.println(response.body());
                //Handle null pointer errors
                if (inquiries != null) {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    inquiryAdapter = new InquiryAdapter(getContext(), inquiries, token, "admin", mProgressDialog);
                    recyclerView.setAdapter(inquiryAdapter);
                    Collections.reverse(inquiries);
                    inquiryAdapter.setInquiries(inquiries);
                } else {
                    Toast.makeText(getContext(), "Something went wrong" + response.toString(), Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Inquiry>> call, Throwable t) {
                Toast.makeText(getContext(), "Something went Wrong!" + t.toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }
}