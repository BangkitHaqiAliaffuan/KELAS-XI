const mongoose = require('mongoose');
require('dotenv').config();

// Import data files
const coursesData = require('./courses.json');
const usersData = require('./users.json');
const testimonialsData = require('./testimonials.json');
const enrollmentsData = require('./enrollments.json');

// MongoDB connection with retry logic
const connectDB = async (retries = 3) => {
  for (let i = 0; i < retries; i++) {
    try {
      console.log(`🔄 Attempting MongoDB connection (${i + 1}/${retries})...`);
      
      await mongoose.connect(process.env.MONGODB_URL, {
        useNewUrlParser: true,
        useUnifiedTopology: true,
        serverSelectionTimeoutMS: 10000,
        socketTimeoutMS: 45000,
      });
      
      console.log('✅ MongoDB connected successfully');
      console.log(`📍 Connected to: ${mongoose.connection.name}`);
      console.log(`🌐 Host: ${mongoose.connection.host}`);
      return;
      
    } catch (error) {
      console.error(`❌ MongoDB connection attempt ${i + 1} failed:`, error.message);
      
      if (error.message.includes('IP')) {
        console.error('💡 SOLUTION: Add your current IP to MongoDB Atlas whitelist');
        console.error('🔗 Go to: https://cloud.mongodb.com → Network Access → Add IP Address');
        console.error('📍 Add: 0.0.0.0/0 (allow from anywhere) for development');
      }
      
      if (i === retries - 1) {
        console.error('💥 All connection attempts failed. Exiting...');
        process.exit(1);
      }
      
      console.log('⏳ Waiting 5 seconds before retry...');
      await new Promise(resolve => setTimeout(resolve, 5000));
    }
  }
};

// Define Mongoose Schemas (simple schemas for import)
const CourseSchema = new mongoose.Schema({}, { strict: false, collection: 'courses' });
const UserSchema = new mongoose.Schema({}, { strict: false, collection: 'users' });
const TestimonialSchema = new mongoose.Schema({}, { strict: false, collection: 'testimonials' });
const EnrollmentSchema = new mongoose.Schema({}, { strict: false, collection: 'enrollments' });

// Create Models
const Course = mongoose.model('Course', CourseSchema);
const User = mongoose.model('User', UserSchema);
const Testimonial = mongoose.model('Testimonial', TestimonialSchema);
const Enrollment = mongoose.model('Enrollment', EnrollmentSchema);

// Import functions
const importCourses = async () => {
  try {
    console.log('🔄 Importing courses...');
    
    // Clear existing courses
    await Course.deleteMany({});
    console.log('🗑️ Cleared existing courses');
    
    // Insert new courses
    const insertedCourses = await Course.insertMany(coursesData);
    console.log(`✅ Imported ${insertedCourses.length} courses`);
    
    return insertedCourses;
  } catch (error) {
    console.error('❌ Error importing courses:', error.message);
    throw error;
  }
};

const importUsers = async () => {
  try {
    console.log('🔄 Importing users...');
    
    // Clear existing users
    await User.deleteMany({});
    console.log('🗑️ Cleared existing users');
    
    // Insert new users
    const insertedUsers = await User.insertMany(usersData);
    console.log(`✅ Imported ${insertedUsers.length} users`);
    
    return insertedUsers;
  } catch (error) {
    console.error('❌ Error importing users:', error.message);
    throw error;
  }
};

const importTestimonials = async () => {
  try {
    console.log('🔄 Importing testimonials...');
    
    // Clear existing testimonials
    await Testimonial.deleteMany({});
    console.log('🗑️ Cleared existing testimonials');
    
    // Insert new testimonials
    const insertedTestimonials = await Testimonial.insertMany(testimonialsData);
    console.log(`✅ Imported ${insertedTestimonials.length} testimonials`);
    
    return insertedTestimonials;
  } catch (error) {
    console.error('❌ Error importing testimonials:', error.message);
    throw error;
  }
};

const importEnrollments = async () => {
  try {
    console.log('🔄 Importing enrollments...');
    
    // Clear existing enrollments
    await Enrollment.deleteMany({});
    console.log('🗑️ Cleared existing enrollments');
    
    // Insert new enrollments
    const insertedEnrollments = await Enrollment.insertMany(enrollmentsData);
    console.log(`✅ Imported ${insertedEnrollments.length} enrollments`);
    
    return insertedEnrollments;
  } catch (error) {
    console.error('❌ Error importing enrollments:', error.message);
    throw error;
  }
};

// Main import function
const importAllData = async () => {
  try {
    console.log('🚀 Starting data import to MongoDB Atlas...\n');
    
    // Connect to database
    await connectDB();
    
    // Import all collections
    const courses = await importCourses();
    const users = await importUsers();
    const testimonials = await importTestimonials();
    const enrollments = await importEnrollments();
    
    console.log('\n🎉 ================================');
    console.log('   DATA IMPORT COMPLETED!');
    console.log('🎉 ================================');
    console.log(`📚 Courses: ${courses.length}`);
    console.log(`👥 Users: ${users.length}`);
    console.log(`💬 Testimonials: ${testimonials.length}`);
    console.log(`📝 Enrollments: ${enrollments.length}`);
    console.log('================================\n');
    
    // Verify data
    console.log('🔍 Verifying imported data...');
    const courseCount = await Course.countDocuments();
    const userCount = await User.countDocuments();
    const testimonialCount = await Testimonial.countDocuments();
    const enrollmentCount = await Enrollment.countDocuments();
    
    console.log(`✅ Database verification:`);
    console.log(`   - Courses in DB: ${courseCount}`);
    console.log(`   - Users in DB: ${userCount}`);
    console.log(`   - Testimonials in DB: ${testimonialCount}`);
    console.log(`   - Enrollments in DB: ${enrollmentCount}`);
    
    // Sample data check
    console.log('\n📋 Sample course data:');
    const sampleCourse = await Course.findOne();
    if (sampleCourse) {
      console.log(`   - Title: ${sampleCourse.courseTitle}`);
      console.log(`   - Price: $${sampleCourse.coursePrice}`);
      console.log(`   - Enrolled Students: ${sampleCourse.enrolledStudents?.length || 0}`);
    }
    
  } catch (error) {
    console.error('💥 Import failed:', error.message);
    process.exit(1);
  } finally {
    // Close database connection
    await mongoose.connection.close();
    console.log('🔌 Database connection closed');
    console.log('✨ Import process completed!');
  }
};

// Run import if script is executed directly
if (require.main === module) {
  importAllData();
}

module.exports = {
  importAllData,
  importCourses,
  importUsers,
  importTestimonials,
  importEnrollments
};
