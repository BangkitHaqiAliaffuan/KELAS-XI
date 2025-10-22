<?php

namespace App\Imports;

use App\Models\Subject;
use Illuminate\Support\Collection;
use Maatwebsite\Excel\Concerns\ToCollection;
use Maatwebsite\Excel\Concerns\WithHeadingRow;
use Maatwebsite\Excel\Concerns\WithValidation;

class SubjectImport implements ToCollection, WithHeadingRow, WithValidation
{
    /**
     * @param Collection $collection
     */
    public function collection(Collection $rows)
    {
        foreach ($rows as $row) {
            Subject::updateOrCreate(
                [
                    'kode' => $row['kode'],
                ],
                [
                    'nama' => $row['nama'] ?? '',
                    'kode' => $row['kode'] ?? '',
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
            'nama' => 'required|string|max:255',
            'kode' => 'required|string|max:255|unique:subjects,kode',
        ];
    }

    /**
     * @return array
     */
    public function customValidationMessages()
    {
        return [
            'nama.required' => 'Subject name is required',
            'kode.required' => 'Subject code is required',
            'kode.unique' => 'Subject code already exists',
        ];
    }
}