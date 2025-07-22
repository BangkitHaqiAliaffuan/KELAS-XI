package com.kelasxi.sqlitedatabase;

public class Barang {
    private String idbarang;
    private String barang;
    private String stock;
    private String harga;
    
    // Constructor
    public Barang(String idbarang, String barang, String stock, String harga) {
        this.idbarang = idbarang;
        this.barang = barang;
        this.stock = stock;
        this.harga = harga;
    }
    
    // Default constructor
    public Barang() {
    }
    
    // Getter methods
    public String getIdbarang() {
        return idbarang;
    }
    
    public String getBarang() {
        return barang;
    }
    
    public String getStock() {
        return stock;
    }
    
    public String getHarga() {
        return harga;
    }
    
    // Setter methods
    public void setIdbarang(String idbarang) {
        this.idbarang = idbarang;
    }
    
    public void setBarang(String barang) {
        this.barang = barang;
    }
    
    public void setStock(String stock) {
        this.stock = stock;
    }
    
    public void setHarga(String harga) {
        this.harga = harga;
    }
}
