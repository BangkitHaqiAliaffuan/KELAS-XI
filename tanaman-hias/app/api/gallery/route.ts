import { list } from "@vercel/blob"
import { NextResponse } from "next/server"
import { readdir, stat } from "fs/promises"
import path from "path"

// Toggle between local and Vercel Blob storage
const USE_LOCAL_STORAGE = process.env.USE_LOCAL_STORAGE === "true"

export async function GET() {
  try {
    if (USE_LOCAL_STORAGE) {
      // Read from local storage (for debugging)
      const uploadsDir = path.join(process.cwd(), "public", "uploads")
      
      try {
        const files = await readdir(uploadsDir)
        
        // Filter only image files
        const imageFiles = files.filter(file => 
          /\.(jpg|jpeg|png|gif|webp|svg)$/i.test(file)
        )

        // Get file stats and create gallery items
        const galleryImages = await Promise.all(
          imageFiles.map(async (file) => {
            const filepath = path.join(uploadsDir, file)
            const stats = await stat(filepath)
            
            return {
              id: file,
              url: `/uploads/${file}`,
              title: file.replace(/^\d+-/, '').replace(/\.[^/.]+$/, "") || "Untitled",
              uploadedAt: stats.mtime.toISOString(),
            }
          })
        )

        // Sort by upload date (newest first)
        const sortedImages = galleryImages.sort(
          (a, b) => new Date(b.uploadedAt).getTime() - new Date(a.uploadedAt).getTime()
        )

        console.log("[v0] Local gallery images:", sortedImages.length)
        return NextResponse.json(sortedImages)
      } catch (error) {
        console.log("[v0] No local uploads found or directory doesn't exist")
        return NextResponse.json([])
      }
    } else {
      // Read from Vercel Blob (for production)
      const { blobs } = await list()

      const galleryImages = blobs
        .map((blob) => ({
          id: blob.pathname,
          url: blob.url,
          title:
            blob.pathname
              .split("/")
              .pop()
              ?.replace(/\.[^/.]+$/, "") || "Untitled",
          uploadedAt: blob.uploadedAt.toISOString(),
        }))
        .sort((a, b) => new Date(b.uploadedAt).getTime() - new Date(a.uploadedAt).getTime())

      return NextResponse.json(galleryImages)
    }
  } catch (error) {
    console.error("[v0] Gallery error:", error)
    return NextResponse.json([], { status: 200 })
  }
}
