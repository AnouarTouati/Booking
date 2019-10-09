package com.example.bookingapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class CustomRecyclerViewAdapterSignUpErrors extends RecyclerView.Adapter<CustomRecyclerViewAdapterSignUpErrors.ViewHolder> {
    ArrayList<String> ErrorsMessagesList=new ArrayList<>();

    public CustomRecyclerViewAdapterSignUpErrors(ArrayList<String> errorsMessagesList) {
        ErrorsMessagesList = errorsMessagesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_error_item, viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
     viewHolder.ErrorTextView.setText(ErrorsMessagesList.get(i));
    }

    @Override
    public int getItemCount() {
        return ErrorsMessagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView ErrorTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
          ErrorTextView=itemView.findViewById(R.id.ErrorTextView_SignUpActivity);
        }
    }
}
