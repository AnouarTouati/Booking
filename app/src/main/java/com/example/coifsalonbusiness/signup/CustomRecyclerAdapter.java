package com.example.coifsalonbusiness.signup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.coifsalonbusiness.R;

import java.util.ArrayList;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {
    ArrayList<String> errorsMessagesList =new ArrayList<>();

    public CustomRecyclerAdapter(ArrayList<String> errorsMessagesList) {
        this.errorsMessagesList = errorsMessagesList;
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
     viewHolder.errorTextView.setText(errorsMessagesList.get(i));
    }

    @Override
    public int getItemCount() {
        return errorsMessagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView errorTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
          errorTextView =itemView.findViewById(R.id.ErrorTextView_SignUpActivity);
        }
    }
}
