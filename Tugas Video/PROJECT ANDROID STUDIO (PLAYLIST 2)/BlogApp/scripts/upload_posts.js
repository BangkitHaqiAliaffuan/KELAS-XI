// Script to upload sample posts to Firestore using a service account key
// Usage: node upload_posts.js ../firestore_samples/posts_sample.json ../app/serviceAccountKey.json

const admin = require('firebase-admin')
const fs = require('fs')

const postsFile = process.argv[2] || '../firestore_samples/posts_sample.json'
const keyFile = process.argv[3] || '../app/serviceAccountKey.json'

if (!fs.existsSync(postsFile)) {
  console.error('Posts file not found:', postsFile)
  process.exit(1)
}

if (!fs.existsSync(keyFile)) {
  console.error('Service account key not found:', keyFile)
  process.exit(1)
}

const posts = JSON.parse(fs.readFileSync(postsFile, 'utf8'))

admin.initializeApp({
  credential: admin.credential.cert(require(keyFile))
})

const db = admin.firestore()

async function upload() {
  for (const p of posts) {
    const id = p.id || db.collection('posts').doc().id
    const docRef = db.collection('posts').doc(id)
    // remove id from payload (we use as doc id)
    const payload = Object.assign({}, p)
    delete payload.id
    await docRef.set(payload)
    console.log('Uploaded:', id)
  }
  console.log('All posts uploaded')
}

upload().catch(err => {
  console.error('Upload failed:', err)
  process.exit(1)
})
