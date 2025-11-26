import { put } from "@vercel/blob"
import { type NextRequest, NextResponse } from "next/server"
import { writeFile } from "fs/promises"
import path from "path"

// Toggle between local and Vercel Blob storage
const USE_LOCAL_STORAGE = process.env.USE_LOCAL_STORAGE === "true"

export async function POST(request: NextRequest) {
  try {
    const formData = await request.formData()
    const file = formData.get("file") as File
    const title = formData.get("title") as string

    if (!file) {
      return NextResponse.json({ error: "No file provided" }, { status: 400 })
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
    return NextResponse.json({ error: "Upload failed" }, { status: 500 })
  }
}
