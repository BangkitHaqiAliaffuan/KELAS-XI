# PowerShell script untuk test upload image
# Ganti YOUR_TOKEN dengan token Bearer yang valid

Write-Host "Testing POST request with form-data..." -ForegroundColor Green

# Test 1: Create post without image
Write-Host "`n=== Test 1: Post without image ===" -ForegroundColor Yellow

$headers = @{
    "Accept" = "application/json"
    "Authorization" = "4|uOhly1QcFnVbiFLhEA2NNuAhWuAgmOeUbg051cOu5f51581f"
}

$body = @{
    title = "Test Post Without Image"
    news_content = "This is test content without image"
}

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8000/api/posts" -Method Post -Headers $headers -Form $body
    Write-Host "Success:" -ForegroundColor Green
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error:" -ForegroundColor Red
    $_.Exception.Message
    if ($_.Exception.Response) {
        $_.Exception.Response.Content
    }
}

Write-Host "`n`n=== Test 2: Post with image ===" -ForegroundColor Yellow

# Test 2: Create post with image (buat file test-image.jpg di folder yang sama)
$bodyWithFile = @{
    title = "Test Post With Image"
    news_content = "This is test content with image"
    file = Get-Item "test-image.jpg" -ErrorAction SilentlyContinue
}

if ($bodyWithFile.file) {
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8000/api/posts" -Method Post -Headers $headers -Form $bodyWithFile
        Write-Host "Success:" -ForegroundColor Green
        $response | ConvertTo-Json -Depth 3
    } catch {
        Write-Host "Error:" -ForegroundColor Red
        $_.Exception.Message
        if ($_.Exception.Response) {
            $_.Exception.Response.Content
        }
    }
} else {
    Write-Host "test-image.jpg not found. Skipping image upload test." -ForegroundColor Orange
}

Write-Host "`nDone!" -ForegroundColor Green
