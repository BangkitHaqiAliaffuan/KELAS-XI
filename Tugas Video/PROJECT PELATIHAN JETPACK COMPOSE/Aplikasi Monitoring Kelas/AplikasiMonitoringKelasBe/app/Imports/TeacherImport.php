<?php

namespace App\Imports;

use App\Models\Teacher;
use Illuminate\Support\Collection;
use Maatwebsite\Excel\Concerns\ToCollection;
use Maatwebsite\Excel\Concerns\WithHeadingRow;
use Maatwebsite\Excel\Concerns\WithValidation;

class TeacherImport implements ToCollection, WithHeadingRow, WithValidation
{
    /**
     * @param Collection $collection
     */
    public function collection(Collection $rows)
    {
        foreach ($rows as $row) {
            Teacher::updateOrCreate(
                [
                    'email' => $row['email'],
                ],
                [
                    'name' => $row['name'] ?? '',
                    'email' => $row['email'] ?? '',
                    'password' => bcrypt($row['password'] ?? 'password'),
                    'mata_pelajaran' => $row['mata_pelajaran'] ?? null,
                    'is_banned' => $row['is_banned'] ?? false,
                    'email_verified_at' => $row['email_verified_at'] ?? null,
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
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:teachers,email',
            'password' => 'nullable|string|min:8',
            'mata_pelajaran' => 'nullable|string|max:255',
            'is_banned' => 'nullable|boolean',
        ];
    }

    /**
     * @return array
     */
    public function customValidationMessages()
    {
        return [
            'name.required' => 'Name is required',
            'email.required' => 'Email is required',
            'email.email' => 'Email must be a valid email address',
            'email.unique' => 'Email already exists',
            'password.min' => 'Password must be at least 8 characters',
        ];
    }
}