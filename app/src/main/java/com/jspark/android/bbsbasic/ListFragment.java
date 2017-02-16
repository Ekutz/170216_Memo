package com.jspark.android.bbsbasic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jspark.android.bbsbasic.domain.Memo;
import com.jspark.android.bbsbasic.interfaces.ListInterface;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    View view;
    Context context = null;
    RecyclerView recyclerView;
    ListAdapter listAdapter;
    Button btnPlus;

    private List<Memo> datas = new ArrayList<>();

    ListInterface listInterface = null;

    public ListFragment() {
    }

    @SuppressWarnings("unused")
    public static ListFragment newInstance(int columnCount) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view==null) view = inflater.inflate(R.layout.layout_list, container, false);

        context = getContext();
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        listAdapter = new ListAdapter(context, datas);
        recyclerView.setAdapter(listAdapter);

        setBtnListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.listInterface = (ListInterface)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setData(List<Memo> datas) {
        this.datas = datas;
    }

    private void setBtnListener() {
        btnPlus = (Button)view.findViewById(R.id.btnPlus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listInterface.goDetail();
            }
        });
    }

    public void refresh() {
        listAdapter = new ListAdapter(context, datas);
        recyclerView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }
}
