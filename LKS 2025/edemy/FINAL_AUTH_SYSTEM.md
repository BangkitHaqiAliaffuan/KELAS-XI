# Sistem Authentication Edemy - Final Version

## 🎯 Konsep Sederhana

### **Student (Regular User)**
- **Signup/Login**: Di halaman utama (/)
- **Organization**: TIDAK perlu masuk organization
- **Access**: Student portal (/courses)
- **Role**: `student`

### **Educator**
- **Signup**: TIDAK BISA self-register
- **Login**: Hanya di /educator 
- **Organization**: HARUS member of "edemy" dengan role "educator" atau "admin"
- **Access**: Educator portal (/educator/dashboard)
- **Role**: `educator`

## 🔧 Flow Authentication

### **Student Flow:**
```
1. User → Homepage (/)
2. Klik Register/Login
3. Setelah auth → Redirect ke /courses
4. Role: student (automatic)
5. Organization membership: TIDAK diperlukan
```

### **Educator Flow:**
```
1. Admin invite user ke organization "edemy" dengan role "educator"
2. User terima invitation
3. User → /educator
4. Klik Login (NO register button)
5. Sistem check: member of "edemy" + role "educator"?
6. Jika YES → Redirect ke /educator/dashboard
7. Jika NO → Redirect ke /courses (student portal)
```

## 💻 Technical Implementation

### **Role Detection Logic:**
```javascript
const getUserRole = () => {
  // Check organization membership dengan role "educator"
  if (userMemberships?.data) {
    const educatorMembership = userMemberships.data.find(
      membership => membership.organization.name.toLowerCase() === 'edemy'
    );
    
    if (educatorMembership?.role === 'educator' || educatorMembership?.role === 'admin') {
      return 'educator';
    }
  }
  
  // Semua yang lain = student
  return 'student';
};
```

### **Access Control:**
```javascript
const isEducator = () => {
  // HANYA cek organization membership
  if (userMemberships?.data) {
    const educatorMembership = userMemberships.data.find(
      membership => membership.organization.name.toLowerCase() === 'edemy'
    );
    
    return educatorMembership?.role === 'educator' || educatorMembership?.role === 'admin';
  }
  
  return false; // No organization = not educator
};
```

## 📋 Clerk Dashboard Setup

### **Organization Setup:**
1. **Buat organization "edemy"**
2. **Setup roles di organization:**
   - `educator`: Access ke educator portal
   - `admin`: Access ke educator portal + manage organization

### **User Management:**
1. **Regular Students**: 
   - Biarkan signup sendiri di homepage
   - JANGAN add ke organization

2. **Educators**:
   - Admin invite ke organization "edemy"
   - Set role sebagai "educator"
   - Educator login di /educator

## ✅ Testing Scenarios

### **Test 1: Regular Student**
- [ ] Register di homepage → Success
- [ ] Login di homepage → Success  
- [ ] Redirect ke /courses → Success
- [ ] Access /educator/dashboard → BLOCKED (redirect ke /courses)

### **Test 2: User coba signup di /educator**
- [ ] Akses /educator → Lihat login form
- [ ] Klik "Sign up" (jika ada) → Redirect ke /courses
- [ ] NOT educator access → Success

### **Test 3: Educator (organization member)**
- [ ] Admin invite ke organization → Success
- [ ] User accept invitation → Success
- [ ] Login di /educator → Success
- [ ] Redirect ke /educator/dashboard → Success
- [ ] Access educator features → Success

### **Test 4: Non-educator tries educator access**
- [ ] Regular student coba akses /educator → Redirect ke /courses
- [ ] Protection working → Success

## 🔒 Security Features

1. **No Self-Registration untuk Educator**: Hanya admin yang bisa invite
2. **Organization-Based Access**: Educator HARUS member organization
3. **Clear Separation**: Student tidak perlu organization, Educator harus
4. **Real-time Checking**: Membership check setiap kali akses protected route
5. **Automatic Redirect**: Wrong role = automatic redirect ke portal yang sesuai

## 📞 Management Workflow

### **Add New Student:**
- Student register sendiri di homepage
- No additional action needed

### **Add New Educator:**
1. Admin → Clerk Dashboard
2. Organizations → edemy → Members  
3. Invite member dengan email
4. Set role: "educator"
5. Send invitation
6. Educator accept invitation
7. Educator bisa login di /educator

### **Remove Educator:**
1. Admin → Organizations → edemy → Members
2. Remove member dari organization
3. Ex-educator otomatis jadi student pada next login

Sistem ini sederhana dan jelas: **No organization = Student, Organization member with educator role = Educator**.
