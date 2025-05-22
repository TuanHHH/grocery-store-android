package com.hp.grocerystore.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hp.grocerystore.R;
import com.hp.grocerystore.view.activity.OrderActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        LinearLayout pendingOrder = view.findViewById(R.id.pendingOrder);
        LinearLayout indeliveryOrder = view.findViewById(R.id.indeliveryOrder);
        LinearLayout successOrder = view.findViewById(R.id.successOrder);
        LinearLayout cancelOrder = view.findViewById(R.id.cancelOrder);

        View.OnClickListener listener = v -> {
            int orderStatus = 0;
            if (v.getId() == R.id.pendingOrder) orderStatus = 0;
            else if (v.getId() == R.id.indeliveryOrder) orderStatus = 1;
            else if (v.getId() == R.id.successOrder) orderStatus = 2;
            else if (v.getId() == R.id.cancelOrder) orderStatus = 3;

            Intent intent = new Intent(getActivity(), OrderActivity.class);
            intent.putExtra("orderStatus", orderStatus);
            startActivity(intent);
        };

        pendingOrder.setOnClickListener(listener);
        indeliveryOrder.setOnClickListener(listener);
        successOrder.setOnClickListener(listener);
        cancelOrder.setOnClickListener(listener);
//        return inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }
}