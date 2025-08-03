package com.kelasxi.sqlitedatabase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ViewHolder> {
    
    private Context context;
    private List<Barang> barangList;
    
    // Constructor
    public BarangAdapter(Context context, List<Barang> barangList) {
        this.context = context;
        this.barangList = barangList;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item_barang.xml layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_barang, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data from barangList to TextViews in ViewHolder
        try {
            if (barangList != null && position < barangList.size()) {
                Barang barang = barangList.get(position);
                if (barang != null) {
                    // Bind data to TextViews (tvBarang, tvStok, tvHarga)
                    holder.tvBarang.setText(barang.getBarang() != null ? barang.getBarang() : "");
                    holder.tvStok.setText(barang.getStock() != null ? barang.getStock() : "0");
                    holder.tvHarga.setText(barang.getHarga() != null ? barang.getHarga() : "0");
                    // tvMenu now shows the menu icon (⋮), not the ID
                    
                    // Set OnClickListener for TVMenu (TextView representing the menu icon)
                    holder.tvMenu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Instantiate a PopupMenu object, passing context and TVMenu
                            PopupMenu popupMenu = new PopupMenu(context, holder.tvMenu);
                            
                            // Inflate the menu_item.xml layout into the PopupMenu
                            popupMenu.inflate(R.menu.menu_item);
                            
                            // Set OnMenuItemClickListener for the PopupMenu
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    // Use if-else to handle clicks on menu items
                                    int itemId = item.getItemId();
                                    if (itemId == R.id.ubah) {
                                        // Show update dialog for Quick Edit menu item
                                        showUpdateDialog(barang);
                                        return true;
                                    } else if (itemId == R.id.edit_form) {
                                        // Edit in main form
                                        ((MainActivity) context).populateFormForUpdate(
                                            barang.getIdbarang(), 
                                            barang.getBarang(), 
                                            barang.getStock(), 
                                            barang.getHarga()
                                        );
                                        return true;
                                    } else if (itemId == R.id.hapus) {
                                        // Retrieve the IDbarang of the selected item
                                        String idBarang = barang.getIdbarang();
                                        String namaBarang = barang.getBarang();
                                        
                                        // Call MainActivity deleteData method with item name (it will show its own confirmation dialog)
                                        ((MainActivity) context).deleteData(idBarang, namaBarang);
                                        
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            });
                            
                            // Show the PopupMenu
                            popupMenu.show();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public int getItemCount() {
        return barangList != null ? barangList.size() : 0;
    }
    
    // Method to show update dialog
    private void showUpdateDialog(Barang barang) {
        try {
            // Create AlertDialog for update
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Update Data Barang");
            
            // Create LinearLayout for input fields
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(50, 40, 50, 10);
            
            // Create EditText fields for input
            final EditText etUpdateBarang = new EditText(context);
            etUpdateBarang.setHint("Nama Barang");
            etUpdateBarang.setText(barang.getBarang());
            layout.addView(etUpdateBarang);
            
            final EditText etUpdateStok = new EditText(context);
            etUpdateStok.setHint("Stok");
            etUpdateStok.setText(barang.getStock());
            etUpdateStok.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            layout.addView(etUpdateStok);
            
            final EditText etUpdateHarga = new EditText(context);
            etUpdateHarga.setHint("Harga");
            etUpdateHarga.setText(barang.getHarga());
            etUpdateHarga.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
            layout.addView(etUpdateHarga);
            
            builder.setView(layout);
            
            // Set positive button for update
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Get updated values from EditText fields
                    String newBarang = etUpdateBarang.getText().toString().trim();
                    String newStok = etUpdateStok.getText().toString().trim();
                    String newHarga = etUpdateHarga.getText().toString().trim();
                    
                    // Call updateData method from MainActivity
                    ((MainActivity) context).updateData(barang.getIdbarang(), newBarang, newStok, newHarga);
                }
            });
            
            // Set negative button for cancel
            builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            
            // Show the dialog
            builder.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error showing update dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    // ViewHolder inner class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBarang, tvStok, tvHarga, tvMenu;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBarang = itemView.findViewById(R.id.tvBarang);
            tvStok = itemView.findViewById(R.id.tvStock);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvMenu = itemView.findViewById(R.id.tvMenu);
        }
    }
}
