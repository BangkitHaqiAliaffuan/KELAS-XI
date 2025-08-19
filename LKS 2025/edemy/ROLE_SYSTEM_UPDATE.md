# Updated Role-Based Authentication System

## 🏗️ Struktur Organization & Role

### Organization: "edemy"
- **Member Role "member"**: Hanya akses student portal
- **Member Role "educator"**: Akses educator portal  
- **Member Role "admin"**: Akses educator portal + management

### Role Hierarchy:
```
Non-member → Student Portal (/courses)
Organization Member (role: "member") → Student Portal (/courses) 
Organization Member (role: "educator") → Educator Portal (/educator/dashboard)
Organization Member (role: "admin") → Educator Portal (/educator/dashboard)
```

## 🔄 Authentication Flow

### 1. **Regular Student**
- Register/Login di homepage
- Tidak ada organization membership
- Role: `student`
- Access: Student portal only

### 2. **Organization Member (role: "member")**
- Member dari organization "edemy" dengan role "member"
- Bisa login di `/educator` tapi akan diberi pesan "Access Restricted"
- Role: `student` (meski organization member)
- Access: Student portal only

### 3. **Organization Member (role: "educator")**
- Member dari organization "edemy" dengan role "educator"
- Bisa login di `/educator` dan akses penuh
- Role: `educator`
- Access: Educator portal

### 4. **Organization Admin (role: "admin")**
- Member dari organization "edemy" dengan role "admin"
- Bisa login di `/educator` dan akses penuh
- Role: `educator`
- Access: Educator portal + organization management

## 💻 Implementation Code

### Updated Role Detection:
```javascript
const getUserRole = () => {
  if (userMemberships?.data) {
    const educatorMembership = userMemberships.data.find(
      membership => membership.organization.name.toLowerCase() === 'edemy'
    );
    
    if (educatorMembership) {
      // Check specific role within organization
      if (educatorMembership.role === 'educator' || educatorMembership.role === 'admin') {
        return 'educator';
      }
      // If just "member" role, still a student
      return 'student';
    }
  }
  
  return user.publicMetadata?.role || 'student';
};
```

### Educator Access Check:
```javascript
const isEducator = () => {
  if (userMemberships?.data) {
    const educatorMembership = userMemberships.data.find(
      membership => membership.organization.name.toLowerCase() === 'edemy'
    );
    
    if (educatorMembership) {
      return educatorMembership.role === 'educator' || educatorMembership.role === 'admin';
    }
  }
  return false;
};
```

## 📋 Clerk Dashboard Setup

### 1. Organization Roles Setup:
1. **Go to Organizations > edemy > Settings > Roles**
2. **Create custom roles**:
   - `member`: Basic organization member (student access)
   - `educator`: Can access educator portal
   - `admin`: Can access educator portal + manage organization

### 2. Assign Roles:
- **Regular Students**: No organization membership
- **Organization Students**: Add to "edemy" with role "member"
- **Educators**: Add to "edemy" with role "educator"
- **Admins**: Add to "edemy" with role "admin"

### 3. Member Management:
1. **Organizations > edemy > Members**
2. **Invite new member** with specific role
3. **Change role** of existing members as needed

## ✅ Testing Scenarios

### Test Case 1: Regular Student
- [ ] Can register/login on homepage
- [ ] Redirected to `/courses` after auth
- [ ] Cannot access `/educator/dashboard`

### Test Case 2: Organization Member (role: "member")
- [ ] Can login on homepage
- [ ] Redirected to `/courses` after auth
- [ ] Can access `/educator` but shows "Access Restricted" message
- [ ] Cannot access `/educator/dashboard`

### Test Case 3: Organization Educator (role: "educator")
- [ ] Can login at `/educator`
- [ ] Redirected to `/educator/dashboard` after auth
- [ ] Has full educator portal access
- [ ] Cannot access organization management

### Test Case 4: Organization Admin (role: "admin")  
- [ ] Can login at `/educator`
- [ ] Redirected to `/educator/dashboard` after auth
- [ ] Has full educator portal access
- [ ] Can manage organization members

## 🔒 Security Benefits

1. **Granular Role Control**: Organization membership ≠ educator access
2. **Flexible User Management**: Students can be org members without educator privileges
3. **Clear Access Hierarchy**: Member < Educator < Admin
4. **Real-time Role Updates**: Changes in Clerk reflect immediately
5. **No Self-Registration**: All educator access controlled via organization

## 📞 Support & Troubleshooting

### User Can't Access Educator Portal:
1. Check organization membership in Clerk Dashboard
2. Verify role is "educator" or "admin" (not just "member")
3. Ask user to logout/login to refresh membership data

### User Has Wrong Access Level:
1. Update role in Organizations > edemy > Members
2. User needs to refresh session (logout/login)
3. Changes should be immediate
