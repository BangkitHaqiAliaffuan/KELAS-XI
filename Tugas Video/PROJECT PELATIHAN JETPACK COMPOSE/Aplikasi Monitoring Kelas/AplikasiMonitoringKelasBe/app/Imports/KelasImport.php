<?php

namespace App\Imports;

use App\Models\Kelas;
use Illuminate\Support\Collection;
use Maatwebsite\Excel\Concerns\ToCollection;
use Maatwebsite\Excel\Concerns\WithHeadingRow;
use Maatwebsite\Excel\Concerns\WithValidation;

class KelasImport implements ToCollection, WithHeadingRow, WithValidation
{
    /**
     * @param Collection $collection
     */
    public function collection(Collection $rows)
    {
        foreach ($rows as $row) {
            Kelas::updateOrCreate(
                [
                    'kode_kelas' => $row['kode_kelas'],
                ],
                [
                    'nama_kelas' => $row['nama_kelas'] ?? '',
                    'kode_kelas' => $row['kode_kelas'] ?? '',
                ]
            );
        }
    }

    /**
     * @return array
     */
    public function rules(): array
    {
        return [
            'nama_kelas' => 'required|string|max:255',
            'kode_kelas' => 'required|string|max:255|unique:classes,kode_kelas',
        ];
    }

    /**
     * @return array
     */
    public function customValidationMessages()
    {
        return [
            'nama_kelas.required' => 'Class name is required',
            'kode_kelas.required' => 'Class code is required',
            'kode_kelas.unique' => 'Class code already exists',
        ];
    }
}