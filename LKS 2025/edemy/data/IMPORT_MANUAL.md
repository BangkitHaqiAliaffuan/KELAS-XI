# ================================================
# MANUAL IMPORT COMMANDS FOR MONGODB ATLAS
# ================================================
# Jika script Node.js tidak bisa connect, gunakan mongoimport CLI

# 1. Download MongoDB Database Tools:
# https://www.mongodb.com/try/download/database-tools

# 2. Extract dan add ke PATH atau gunakan full path

# 3. Import commands (ganti dengan connection string Anda):

# Import Courses
mongoimport --uri "mongodb+srv://uwawuwuz:5QiqAMR7N2O6Pyhs@cluster0.qulq6ya.mongodb.net/edemy_lms" --collection courses --file courses.json --jsonArray --drop

# Import Users  
mongoimport --uri "mongodb+srv://uwawuwuz:5QiqAMR7N2O6Pyhs@cluster0.qulq6ya.mongodb.net/edemy_lms" --collection users --file users.json --jsonArray --drop

# Import Testimonials
mongoimport --uri "mongodb+srv://uwawuwuz:5QiqAMR7N2O6Pyhs@cluster0.qulq6ya.mongodb.net/edemy_lms" --collection testimonials --file testimonials.json --jsonArray --drop

# Import Enrollments
mongoimport --uri "mongodb+srv://uwawuwuz:5QiqAMR7N2O6Pyhs@cluster0.qulq6ya.mongodb.net/edemy_lms" --collection enrollments --file enrollments.json --jsonArray --drop

# ================================================
# ALTERNATIVE: MONGODB COMPASS IMPORT
# ================================================
# 1. Download MongoDB Compass: https://www.mongodb.com/products/compass
# 2. Connect using connection string
# 3. Select database: edemy_lms
# 4. For each collection:
#    - Create collection (courses, users, testimonials, enrollments)
#    - Click "ADD DATA" → "Import File"
#    - Select corresponding JSON file
#    - Click "Import"

# ================================================
# VERIFY DATA AFTER IMPORT
# ================================================
# Run these commands in MongoDB Compass or mongosh:

# db.courses.countDocuments()
# db.users.countDocuments()  
# db.testimonials.countDocuments()
# db.enrollments.countDocuments()

# Sample queries:
# db.courses.find().limit(1)
# db.users.find().limit(1)
