package com.todobom.queenscanner.Data;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.squareup.picasso.Picasso;
import com.todobom.queenscanner.FutureSavedPDF;
import com.todobom.queenscanner.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<String> dateList;
    private List<String> pdfNameList;
    private List<String> pdfNameListAll;

    private OnPDFclickListener monPDFclickListener;

    public PdfAdapter() {
    }

    public PdfAdapter(Context context, List<String> dateList, List<String> blogList2, OnPDFclickListener onPDFclickListener) {
        this.context = context;
        this.dateList = dateList;

        this.pdfNameList = blogList2;
        this.pdfNameListAll = new ArrayList<>(blogList2);

        this.monPDFclickListener = onPDFclickListener;

    }

    @NonNull
    @Override
    public PdfAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_future_save_pdf_row,parent,false);

        return new PdfAdapter.ViewHolder(view, context , monPDFclickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfAdapter.ViewHolder holder, int position) {

        holder.name.setText(pdfNameList.get(position));

        String s = dateList.get(position);
        Log.d("TAG", "onBindViewHolder: "+s);
        holder.datee.setText(""+s);

    }

    @Override
    public int getItemCount() {
        return pdfNameList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
       // run on a background thread automatically
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<String> filteredList = new ArrayList<>();

            if (charSequence.toString().isEmpty()){
                filteredList.addAll(pdfNameListAll);
            }else {
                for (String pdflist: pdfNameListAll ){
                    if (pdflist.toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredList.add(pdflist);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }
        // run on a ui thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            pdfNameList.clear();
            pdfNameList.addAll((Collection<? extends String>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name;
        public TextView datee;

        OnPDFclickListener onPDFclickListener;

        public ViewHolder(@NonNull View view, Context ctx, OnPDFclickListener onPDFclickListener) {
            super(view);

            this.onPDFclickListener = onPDFclickListener;
            context = ctx;
            name = view.findViewById(R.id.pdfName);
            datee = view.findViewById(R.id.pdfDate);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onPDFclickListener.onPdfclick(getAdapterPosition());
        }
    }

    public interface OnPDFclickListener{
        void onPdfclick(int position);
    }

}
