package rot.user.tekno.com.rothrow;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rot.user.tekno.com.rothrow.model.ListPengambil;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView rvSiswa;
    private HistoryAdapter historyAdapter;
    private List<ListPengambil> historyModel;


    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        rvSiswa = (RecyclerView) view.findViewById(R.id.rv_siswa);
        rvSiswa.setLayoutManager(new LinearLayoutManager(getActivity()));
        historyModel = new ArrayList<>();
        historyAdapter = new HistoryAdapter(getContext(), historyModel);
        rvSiswa.setAdapter(historyAdapter);
        return view;
    }

}
