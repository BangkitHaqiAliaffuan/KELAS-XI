"use client"

import type React from "react"

import { useState } from "react"
import { Upload, X, Check, AlertCircle } from "lucide-react"
import Link from "next/link"

// Max file size: 10MB
const MAX_FILE_SIZE = 10 * 1024 * 1024

// Compress image before upload
const compressImage = async (file: File): Promise<File> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = (event) => {
      const img = new Image()
      img.src = event.target?.result as string
      img.onload = () => {
        const canvas = document.createElement("canvas")
        let width = img.width
        let height = img.height

        // Resize if too large (max 1920px)
        const maxDimension = 1920
        if (width > maxDimension || height > maxDimension) {
          if (width > height) {
            height = (height / width) * maxDimension
            width = maxDimension
          } else {
            width = (width / height) * maxDimension
            height = maxDimension
          }
        }

        canvas.width = width
        canvas.height = height
        const ctx = canvas.getContext("2d")
        ctx?.drawImage(img, 0, 0, width, height)

        canvas.toBlob(
          (blob) => {
            if (blob) {
              const compressedFile = new File([blob], file.name, {
                type: "image/jpeg",
                lastModified: Date.now(),
              })
              resolve(compressedFile)
            } else {
              reject(new Error("Kompresi gagal"))
            }
          },
          "image/jpeg",
          0.8
        )
      }
      img.onerror = () => reject(new Error("Gagal memuat gambar"))
    }
    reader.onerror = () => reject(new Error("Gagal membaca file"))
  })
}

export default function UploadPage() {
  const [files, setFiles] = useState<File[]>([])
  const [uploading, setUploading] = useState(false)
  const [uploadedFiles, setUploadedFiles] = useState<string[]>([])
  const [error, setError] = useState<string>("")

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
    e.currentTarget.classList.add("bg-accent-green/20")
  }

  const handleDragLeave = (e: React.DragEvent) => {
    e.currentTarget.classList.remove("bg-accent-green/20")
  }

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    e.currentTarget.classList.remove("bg-accent-green/20")
    const newFiles = Array.from(e.dataTransfer.files).filter((file) => {
      if (!file.type.startsWith("image/")) return false
      if (file.size > MAX_FILE_SIZE) {
        setError(`File ${file.name} terlalu besar. Maksimal 10MB`)
        return false
      }
      return true
    })
    setFiles((prev) => [...prev, ...newFiles])
    if (newFiles.length > 0) setError("")
  }

  const handleFileInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newFiles = Array.from(e.target.files || []).filter((file) => {
      if (file.size > MAX_FILE_SIZE) {
        setError(`File ${file.name} terlalu besar. Maksimal 10MB`)
        return false
      }
      return true
    })
    setFiles((prev) => [...prev, ...newFiles])
    if (newFiles.length > 0) setError("")
  }

  const removeFile = (index: number) => {
    setFiles((prev) => prev.filter((_, i) => i !== index))
  }

  const handleUpload = async () => {
    if (files.length === 0) {
      setError("Pilih minimal satu gambar")
      return
    }

    setUploading(true)
    setError("")

    try {
      for (const file of files) {
        console.log("[v0] Compressing file:", file.name, "Size:", (file.size / 1024 / 1024).toFixed(2), "MB")
        
        // Compress image before upload
        let fileToUpload = file
        if (file.size > 1024 * 1024) { // Compress if > 1MB
          try {
            fileToUpload = await compressImage(file)
            console.log("[v0] Compressed size:", (fileToUpload.size / 1024 / 1024).toFixed(2), "MB")
          } catch (compressError) {
            console.warn("[v0] Compression failed, uploading original:", compressError)
          }
        }

        const formData = new FormData()
        formData.append("file", fileToUpload)
        formData.append("title", file.name)

        console.log("[v0] Uploading file:", file.name)

        const response = await fetch("/api/upload", {
          method: "POST",
          body: formData,
        })

        if (!response.ok) {
          let errorMessage = "Upload gagal"
          try {
            const errorData = await response.json()
            errorMessage = errorData.error || errorMessage
          } catch {
            // If response is not JSON, use status text
            errorMessage = `${response.status}: ${response.statusText}`
          }
          throw new Error(errorMessage)
        }

        console.log("[v0] File uploaded successfully:", file.name)
        setUploadedFiles((prev) => [...prev, file.name])
      }

      // Reset after successful upload
      setTimeout(() => {
        setFiles([])
        setUploadedFiles([])
      }, 2000)
    } catch (err) {
      console.error("[v0] Upload error:", err)
      const errorMessage = err instanceof Error ? err.message : "Terjadi kesalahan saat upload"
      setError(errorMessage)
    } finally {
      setUploading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-b from-cream-bg to-white py-12 px-4">
      <div className="max-w-2xl mx-auto">
        {/* Header */}
        <div className="text-center mb-12">
          <Link href="/" className="inline-block text-primary-green hover:text-secondary-green font-semibold mb-6">
            ← Kembali
          </Link>
          <h1 className="text-5xl md:text-6xl font-bold gradient-text mb-4">Upload Foto Taman</h1>
          <p className="text-lg text-gray-600">Bagikan keindahan taman hias Anda dengan komunitas</p>
        </div>

        <div className="bg-white rounded-2xl shadow-lg p-8">
          {/* Drag and drop area */}
          <div
            onDragOver={handleDragOver}
            onDragLeave={handleDragLeave}
            onDrop={handleDrop}
            className="border-2 border-dashed border-secondary-green rounded-xl p-12 text-center cursor-pointer hover:bg-accent-green/10 transition-colors mb-8"
          >
            <Upload className="w-16 h-16 text-secondary-green mx-auto mb-4" />
            <p className="text-lg font-semibold text-primary-green mb-2">Drag and drop foto di sini</p>
            <p className="text-gray-600 mb-4">atau klik untuk memilih file</p>
            <input type="file" multiple accept="image/*" onChange={handleFileInput} className="hidden" id="fileInput" />
            <label htmlFor="fileInput" className="inline-block">
              <span className="bg-secondary-green hover:bg-primary-green text-white px-6 py-3 rounded-lg font-semibold transition-all cursor-pointer inline-block">
                Pilih Gambar
              </span>
            </label>
          </div>

          {/* Error message */}
          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg mb-6">{error}</div>
          )}

          {/* File list */}
          {files.length > 0 && (
            <div className="mb-8">
              <h3 className="font-bold text-lg text-primary-green mb-4">Gambar untuk diupload ({files.length})</h3>
              <div className="space-y-4">
                {files.map((file, index) => (
                  <div key={`${file.name}-${index}`} className="flex gap-4 items-start bg-cream-bg p-4 rounded-lg">
                    <img
                      src={URL.createObjectURL(file) || "/placeholder.svg"}
                      alt={file.name}
                      className="w-20 h-20 object-cover rounded-lg"
                    />
                    <div className="flex-1">
                      <p className="text-sm text-gray-700 font-medium">{file.name}</p>
                      <p className="text-xs text-gray-500 mt-1">{(file.size / 1024 / 1024).toFixed(2)} MB</p>
                    </div>
                    <button
                      onClick={() => removeFile(index)}
                      className="text-red-500 hover:text-red-700 flex-shrink-0"
                      aria-label="Hapus file"
                    >
                      <X className="w-6 h-6" />
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Success message */}
          {uploadedFiles.length > 0 && !uploading && (
            <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded-lg mb-6 flex items-center gap-2">
              <Check className="w-5 h-5" />
              <div>
                <p className="font-semibold">Upload berhasil!</p>
                <p className="text-sm">{uploadedFiles.length} gambar telah ditambahkan ke galeri</p>
              </div>
            </div>
          )}

          {/* Upload button */}
          {files.length > 0 && (
            <button
              onClick={handleUpload}
              disabled={uploading}
              className="w-full bg-secondary-green hover:bg-primary-green disabled:bg-gray-400 text-white px-6 py-3 rounded-lg font-semibold transition-all disabled:cursor-not-allowed"
            >
              {uploading ? "Mengupload..." : `Upload ${files.length} Gambar`}
            </button>
          )}

          {/* Empty state */}
          {files.length === 0 && uploadedFiles.length === 0 && (
            <div className="text-center py-8 text-gray-600">
              <p>Belum ada gambar dipilih</p>
            </div>
          )}
        </div>

        {/* Info box */}
        <div className="mt-8 bg-primary-green/10 border border-primary-green rounded-lg p-6">
          <h3 className="font-bold text-primary-green mb-3">Tips Upload</h3>
          <ul className="text-gray-700 space-y-2 text-sm">
            <li>✓ Format: JPG, PNG, WebP (max 5MB per gambar)</li>
            <li>✓ Resolusi optimal: 1080x1080px atau lebih</li>
            <li>✓ Foto akan ditampilkan di galeri utama</li>
          </ul>
        </div>
      </div>
    </div>
  )
}
