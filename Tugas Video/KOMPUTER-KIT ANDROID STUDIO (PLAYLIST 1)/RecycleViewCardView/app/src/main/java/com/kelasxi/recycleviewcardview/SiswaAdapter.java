package com.kelasxi.recycleviewcardview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SiswaAdapter extends RecyclerView.Adapter<SiswaAdapter.ViewHolder> {
    private Context context;
    private List<Siswa> siswaList;

    // Constructor
    public SiswaAdapter(Context context, List<Siswa> siswaList) {
        this.context = context;
        this.siswaList = siswaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_siswa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Siswa siswa = siswaList.get(position);
        holder.tvNama.setText(siswa.getNama());
        holder.tvAlamat.setText(siswa.getAlamat());
        
        // Add setOnClickListener to the itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display Toast message with name and address of the clicked item
                Toast.makeText(context, "Nama: " + siswa.getNama() + "\nAlamat: " + siswa.getAlamat(), 
                              Toast.LENGTH_SHORT).show();
            }
        });
        
        // Set OnClickListener for tvMenu
        holder.tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create PopupMenu, inflating menu_option layout
                PopupMenu popupMenu = new PopupMenu(context, holder.tvMenu);
                popupMenu.getMenuInflater().inflate(R.menu.menu_option, popupMenu.getMenu());
                
                // Show the PopupMenu
                popupMenu.show();
                
                // Set OnMenuItemClickListener for the PopupMenu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Use if-else statement to handle menu item clicks based on their IDs
                        if (item.getItemId() == R.id.menu_simpan) {
                            // Display Toast message "Simpan Data [siswa.nama]"
                            Toast.makeText(context, "Simpan Data " + siswa.getNama(), Toast.LENGTH_SHORT).show();
                            return true;
                        } else if (item.getItemId() == R.id.menu_hapus) {
                            // Remove the corresponding item from siswaList
                            siswaList.remove(position);
                            // Call notifyDataSetChanged()
                            notifyDataSetChanged();
                            // Display Toast message "Sudah di Hapus"
                            Toast.makeText(context, "Sudah di Hapus", Toast.LENGTH_SHORT).show();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return siswaList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama;
        TextView tvAlamat;
        TextView tvMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            tvMenu = itemView.findViewById(R.id.tvMenu);
        }
    }
}
