package com.jspark.android.bbsbasic;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jspark.android.bbsbasic.domain.Memo;
import com.jspark.android.bbsbasic.interfaces.ListInterface;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    View view;
    Context context = null;
    RecyclerView recyclerView;
    ListAdapter listAdapter;
    Button btnPlus, btnSelect;
    boolean state = true;

    private List<Memo> datas = new ArrayList<>();

    ListInterface listInterface = null;

    public static List<Integer> deleteList = new ArrayList<>();

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
                if(btnPlus.getText().toString().equals("+")) {
                    listInterface.goDetail();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("삭제");
                    builder.setMessage("체크한 메모를 모두 삭제하시겠습니까?");
                    builder.setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(deleteList.size()!=0) {
                                for(int item : deleteList) {
                                    try {
                                        listInterface.delete(item);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                                setMultiSelectMode(false);
                            }
                            deleteList.clear();
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteList.clear();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
        btnSelect = (Button)view.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state==true) {
                    setMultiSelectMode(true);
                } else {
                    setMultiSelectMode(false);
                }
            }
        });
    }

    private void setMultiSelectMode(boolean state) {
        if(state==true) {
            listAdapter = new ListAdapter(context, datas, false);
            recyclerView.setAdapter(listAdapter);
            btnSelect.setText(R.string.back);
            btnPlus.setText("-");
            this.state = false;
        } else {
            listAdapter = new ListAdapter(context, datas, true);
            recyclerView.setAdapter(listAdapter);
            btnSelect.setText(R.string.edit);
            btnPlus.setText("+");
            deleteList.clear();
            this.state = true;
        }
    }

    public void refresh() {
        listAdapter = new ListAdapter(context, datas);
        recyclerView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }
}
