<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Http\JsonResponse;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Facades\Log;

class WasteClassificationController extends Controller
{
    /**
     * Classify waste as organic or non-organic using AI
     *
     * @param Request $request
     * @return JsonResponse
     */
    public function classifyWaste(Request $request): JsonResponse
    {
        try {
            // Validate the request
            $validator = Validator::make($request->all(), [
                'image' => 'required|image|mimes:jpeg,png,jpg,gif|max:10240', // max 10MB
            ]);

            if ($validator->fails()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation failed',
                    'errors' => $validator->errors()
                ], 422);
            }

            // Store the uploaded image temporarily
            $image = $request->file('image');
            $imagePath = $image->store('waste-classification', 'public');
            $fullImagePath = Storage::disk('public')->path($imagePath);

            // Process image with AI model
            $result = $this->processWithAIModel($fullImagePath);

            // Clean up the temporary file
            unlink($fullImagePath);

            if (isset($result['error'])) {
                return response()->json([
                    'success' => false,
                    'message' => 'Error processing image',
                    'error' => $result['error']
                ], 500);
            }

            return response()->json([
                'success' => true,
                'data' => [
                    'classification' => $result['classification'],
                    'confidence' => $result['confidence'],
                    'probability' => $result['probability']
                ]
            ], 200);

        } catch (\Exception $e) {
            Log::error('Waste classification error: ' . $e->getMessage());
            
            return response()->json([
                'success' => false,
                'message' => 'An error occurred during classification',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Process image with AI model
     *
     * @param string $imagePath
     * @return array
     */
    private function processWithAIModel(string $imagePath): array
    {
        try {
            // Define the path to the Python script
            $pythonScript = base_path('ai_model/classifier_api.py');
            
            // Ensure the Python script exists
            if (!file_exists($pythonScript)) {
                Log::error('AI model script not found: ' . $pythonScript);
                return ['error' => 'AI model script not found'];
            }

            // Determine the correct Python executable
            $pythonCommand = 'python';
            if (!exec('which python')) {
                // Try alternative command for Windows
                $pythonCommand = 'python';
            }

            // Execute the Python script to classify the image
            $command = $pythonCommand . ' ' . escapeshellarg($pythonScript) . ' ' . escapeshellarg($imagePath);
            Log::info('Executing command: ' . $command);
            
            $output = shell_exec($command . ' 2>&1'); // Capture both stdout and stderr

            Log::info('Python script output: ' . $output);

            if ($output === null) {
                Log::error('Failed to execute AI model command');
                return ['error' => 'Failed to execute AI model'];
            }

            // Parse the JSON response
            $result = json_decode(trim($output), true);

            if (json_last_error() !== JSON_ERROR_NONE) {
                Log::error('Invalid JSON response from AI model: ' . $output);
                return ['error' => 'Invalid JSON response from AI model: ' . json_last_error_msg()];
            }

            return $result;
        } catch (\Exception $e) {
            Log::error('AI Model Processing Error: ' . $e->getMessage());
            return ['error' => 'Processing failed: ' . $e->getMessage()];
        }
    }
}