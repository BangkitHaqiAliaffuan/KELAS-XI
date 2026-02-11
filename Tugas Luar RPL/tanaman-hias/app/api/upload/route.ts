import { put } from "@vercel/blob"
import { type NextRequest, NextResponse } from "next/server"
import { writeFile } from "fs/promises"
import path from "path"

// Configure max body size (10MB)
export const config = {
  api: {
    bodyParser: false,
  },
}

// Toggle between local and Vercel Blob storage
const USE_LOCAL_STORAGE = process.env.USE_LOCAL_STORAGE === "true"

// Max file size: 10MB
const MAX_FILE_SIZE = 10 * 1024 * 1024

export async function POST(request: NextRequest) {
  try {
    const formData = await request.formData()
    const file = formData.get("file") as File
    const title = formData.get("title") as string

    if (!file) {
      return NextResponse.json({ error: "No file provided" }, { status: 400 })
    }

    // Validate file size
    if (file.size > MAX_FILE_SIZE) {
      return NextResponse.json(
        { error: `File terlalu besar. Maksimal 10MB. Ukuran file: ${(file.size / 1024 / 1024).toFixed(2)}MB` },
        { status: 413 }
      )
    }

    // Validate file type
    if (!file.type.startsWith("image/")) {
      return NextResponse.json({ error: "File harus berupa gambar" }, { status: 400 })
    }

    if (USE_LOCAL_STORAGE) {
      // Upload to local storage (for debugging)
      const bytes = await file.arrayBuffer()
      const buffer = Buffer.from(bytes)

      // Save to public/uploads directory
      const filename = `${Date.now()}-${file.name}`
      const filepath = path.join(process.cwd(), "public", "uploads", filename)
      
      await writeFile(filepath, buffer)
      console.log("[v0] File saved locally:", filepath)

      return NextResponse.json({
        id: filename,
        url: `/uploads/${filename}`,
        title: title || file.name,
        uploadedAt: new Date().toISOString(),
      })
    } else {
      // Upload to Vercel Blob (for production)
      const blob = await put(file.name, file, {
        access: "public",
      })

      return NextResponse.json({
        id: blob.pathname,
        url: blob.url,
        title: title || file.name,
        uploadedAt: new Date().toISOString(),
      })
    }
  } catch (error) {
    console.error("[v0] Upload error:", error)
    const errorMessage = error instanceof Error ? error.message : "Upload failed"
    return NextResponse.json(
      { error: errorMessage },
      { status: 500 }
    )
  }
}
