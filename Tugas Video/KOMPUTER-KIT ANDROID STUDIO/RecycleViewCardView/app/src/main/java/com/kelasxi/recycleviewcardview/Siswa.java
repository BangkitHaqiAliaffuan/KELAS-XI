package com.kelasxi.recycleviewcardview;

public class Siswa {
    private String nama;
    private String alamat;

    // Constructor
    public Siswa(String nama, String alamat) {
        this.nama = nama;
        this.alamat = alamat;
    }

    // Getter and Setter for nama
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    // Getter and Setter for alamat
    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
