package rot.user.tekno.com.rothrow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import rot.user.tekno.com.rothrow.model.ListPengambil;

/**
 * Created by Galih on 1/5/2018.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private Context context;
    private List<ListPengambil> historyModel;

    public HistoryAdapter(Context context, List<ListPengambil> historyModel) {
        this.context = context;
        this.historyModel = historyModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_history, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(context, FaqActivity.class);
//                i.putExtra("nama", faqHomeModelsList.get(position).getNamaFaq());
//                i.putExtra("nama", "apa");
//                context.startActivity(i);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return 4;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView namaPembuang;
        TextView jenisSampah;
        TextView hargaSampah;
        TextView statusSampah;
        ImageView fotoPembuang;
        public ViewHolder(View itemView) {
            super(itemView);

              namaPembuang = (TextView) itemView.findViewById(R.id.tv_nama_siswa);
              jenisSampah = (TextView) itemView.findViewById(R.id.tv_jenissampah);
              hargaSampah = (TextView) itemView.findViewById(R.id.tv_hargasampah);
              statusSampah = (TextView) itemView.findViewById(R.id.tv_statussampah);

//            namaSiswa = (TextView) itemView.findViewById(R.id.tv_nama_siswa);
//            alamatSiswa = (TextView) itemView.findViewById(R.id.tv_alamat_siswa);
            fotoPembuang = (ImageView) itemView.findViewById(R.id.iv_gambar);
        }
    }

}
