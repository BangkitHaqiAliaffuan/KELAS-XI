# API Testing Guide for Edemy LMS

## Prerequisites
Ensure both servers are running:
- Backend API: http://localhost:5000
- Frontend React: http://localhost:5173

## Test API Endpoints

### 1. Health Check
```bash
curl http://localhost:5000/health
```

### 2. Get All Courses
```bash
curl "http://localhost:5000/api/courses"
```

### 3. Get Featured Courses
```bash
curl "http://localhost:5000/api/courses/featured"
```

### 4. Get Courses with Filters
```bash
# With pagination
curl "http://localhost:5000/api/courses?page=1&limit=6"

# With category filter
curl "http://localhost:5000/api/courses?category=Programming"

# With search
curl "http://localhost:5000/api/courses?search=React"

# With price range
curl "http://localhost:5000/api/courses?minPrice=5&maxPrice=20"

# With sorting
curl "http://localhost:5000/api/courses?sortBy=price&sortOrder=asc"
```

### 5. Get Single Course
```bash
# Replace {courseId} with actual course ID from database
curl "http://localhost:5000/api/courses/{courseId}"
```

### 6. Get Course Categories
```bash
curl "http://localhost:5000/api/courses/categories"
```

### 7. Get Testimonials
```bash
curl "http://localhost:5000/api/testimonials"
```

### 8. Get Featured Testimonials
```bash
curl "http://localhost:5000/api/testimonials/featured"
```

### 9. Get Users
```bash
curl "http://localhost:5000/api/users"
```

### 10. Get Enrollments
```bash
curl "http://localhost:5000/api/enrollments"
```

## Frontend Integration Points

### 1. Homepage (http://localhost:5173/)
- ✅ Displays featured courses from API
- ✅ Shows testimonials from database
- ✅ Loading states and error handling

### 2. Courses Page (http://localhost:5173/courses)
- ✅ Shows all courses with pagination
- ✅ Filter by category, level, price range
- ✅ Search functionality
- ✅ Sorting options

### 3. Course Detail Page (http://localhost:5173/course/{id})
- ✅ Displays individual course details
- ✅ Course description, instructor info
- ✅ Pricing and enrollment information

## Sample Course IDs for Testing
After running the data import, you should have courses in your database.
You can get course IDs by calling the courses API or checking MongoDB directly.

## Database Status
✅ MongoDB Atlas connected
✅ Data imported successfully:
   - 📚 8 Courses
   - 👥 3 Users  
   - 💬 3 Testimonials
   - 📝 4 Enrollments

## Next Steps for Full Integration
1. 🔐 Integrate Clerk Authentication
2. 💳 Complete Stripe Payment Flow
3. 📊 Build Dashboard for Educators
4. 🎓 Create Student Progress Tracking
5. 📱 Add Responsive Mobile Design
6. 🔍 Implement Advanced Search
7. 📧 Email Notifications
8. 📈 Analytics and Reporting
