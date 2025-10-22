<?php

namespace App\Utils;

use Exception;
use Illuminate\Support\Arr;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Str;

class CsvReader
{
    /**
     * Parse a CSV file and return an array of rows
     *
     * @param string $filePath
     * @param array $rules
     * @return array
     */
    public static function parse(string $filePath, array $rules = []): array
    {
        if (!file_exists($filePath)) {
            throw new Exception('File not found: ' . $filePath);
        }

        $file = fopen($filePath, 'r');
        if (!$file) {
            throw new Exception('Unable to open file: ' . $filePath);
        }

        // Read the header row
        $header = fgetcsv($file);
        if (!$header) {
            fclose($file);
            throw new Exception('Empty CSV file or unable to read header');
        }

        // Normalize header names to match Laravel conventions
        $normalizedHeader = array_map(function ($column) {
            return Str::snake(Str::lower(str_replace([' ', '-', '.', '_'], '_', $column)));
        }, $header);

        $rows = [];
        $lineNumber = 1; // Header is line 1

        while (($data = fgetcsv($file)) !== false) {
            $lineNumber++;
            
            // Skip empty rows
            if (count(array_filter($data)) === 0) {
                continue;
            }

            // Ensure we have enough data columns
            if (count($data) < count($normalizedHeader)) {
                // Pad the data array with empty values
                $data = array_pad($data, count($normalizedHeader), '');
            }

            // Create associative array with header as keys
            $row = array_combine($normalizedHeader, array_slice($data, 0, count($normalizedHeader)));

            // Validate if rules are provided
            if (!empty($rules)) {
                $validator = Validator::make($row, $rules);
                
                if ($validator->fails()) {
                    // Add validation errors to the row
                    $row['_errors'] = $validator->errors()->toArray();
                    $row['_line_number'] = $lineNumber;
                }
            }

            $rows[] = $row;
        }

        fclose($file);

        return $rows;
    }

    /**
     * Convert associative array keys to match model attributes
     *
     * @param array $data
     * @param array $mapping
     * @return array
     */
    public static function mapColumns(array $data, array $mapping): array
    {
        $mappedData = [];

        foreach ($mapping as $csvColumn => $modelAttribute) {
            // Check if the CSV column exists in data
            if (array_key_exists($csvColumn, $data)) {
                $mappedData[$modelAttribute] = $data[$csvColumn];
            } else {
                // Handle case where CSV column doesn't exist
                $mappedData[$modelAttribute] = null;
            }
        }

        return $mappedData;
    }
}