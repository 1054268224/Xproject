package com.example.systemmanageruidemo.bootspeed.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.systemmanageruidemo.R;
import com.example.systemmanageruidemo.powersavemanager.adpter.PowerSaveAdapter;

import java.util.List;

public class BootAdapter extends RecyclerView.Adapter<BootAdapter.ViewHolder> {
    private List<BootItem> bootItemList;

    public BootAdapter(List<BootItem> bootItemList) {
        this.bootItemList = bootItemList;
    }

    @NonNull
    @Override
    public BootAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bootspeed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BootAdapter.ViewHolder holder, int position) {
        final BootItem bootItem = bootItemList.get(position);
        holder.imageSoft.setImageDrawable(bootItem.getImageID());
        holder.name.setText(bootItem.getName());
        holder.mSoftCheckbox.setChecked(bootItem.getTrue());

        holder.mSoftCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bootItem.setTrue(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bootItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageSoft;
        private TextView name;
        private CheckBox mSoftCheckbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSoft = itemView.findViewById(R.id.soft_image);
            name = itemView.findViewById(R.id.soft_name);
            mSoftCheckbox = itemView.findViewById(R.id.checkbox);
        }
    }
}