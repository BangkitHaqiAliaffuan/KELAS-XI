# Clerk Organization Setup untuk Edemy

## 1. Organization Configuration di Clerk Dashboard

### A. Buat Organization
1. **Buka [dashboard.clerk.com](https://dashboard.clerk.com)**
2. **Pilih project Anda (My Application)**
3. **Pergi ke Organizations**
4. **Klik "Create Organization"**
5. **Name: `edemy`**
6. **Slug: `edemy`**

### B. Configure Organization Settings
1. **Organizations > Settings**
2. **Enable "Allow users to create organizations": OFF**
3. **Enable "Allow users to join organizations": OFF**
4. **Enable "Show organization switcher": ON**

### C. Invite Educators
1. **Pergi ke Organizations > edemy > Members**
2. **Klik "Add user"**
3. **Masukkan email educator**
4. **Set role: `admin` atau `basic_member`**
5. **Send invitation**

## 2. API Keys Configuration

### A. Environment Variables (.env)
```bash
# Clerk Configuration
VITE_CLERK_PUBLISHABLE_KEY=pk_test_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
CLERK_SECRET_KEY=sk_test_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

# Organization (opsional untuk webhook)
CLERK_ORGANIZATION_ID=org_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

### B. Dashboard Settings
1. **Configure > Paths**
   - After sign-up URL: `/courses`
   - After sign-in URL: `/courses`
   - Home URL: `/`

2. **Settings > Advanced**
   - ✅ Enable "Allow public metadata"
   - ✅ Enable "Allow unsafe metadata"

## 3. Authentication Flow

### Student Authentication (Member biasa):
- **Halaman**: `/` (Homepage dengan Header)
- **Components**: `StudentSignInButton`, `StudentSignUpButton`
- **Redirect**: `/courses`
- **Role**: `student` (default)

### Educator Authentication (Organization member):
- **Halaman**: `/educator` atau `/educator/auth`
- **Components**: `SignInButton` (no register)
- **Redirect**: `/educator/dashboard`
- **Role**: `educator` (auto dari organization membership)
- **Registration**: Invite-only via Clerk Dashboard

## 4. Role Management Logic

```javascript
// Organization member = educator
if (membership) {
  return 'educator';
}

// Fallback to metadata or default student
return user?.publicMetadata?.role || 'student';
```

## 5. Protection Strategy

### Student Routes:
- `/my-courses` - Requires `student` role
- Redirect educators → `/educator/dashboard`

### Educator Routes:
- `/educator/dashboard` - Requires organization membership
- `/educator/add-course` - Requires organization membership
- `/educator/my-courses` - Requires organization membership
- `/educator/students` - Requires organization membership
- Redirect students → `/courses`

## 6. Testing

### Test Student Flow:
1. Register di homepage (bukan di /educator)
2. Verifikasi role = 'student'
3. Access `/my-courses` ✅
4. Try access `/educator/dashboard` → redirect ke `/courses`

### Test Educator Flow:
1. Invite educator via Clerk Dashboard
2. Educator login di `/educator`
3. Verifikasi organization membership
4. Access `/educator/dashboard` ✅
5. Try access `/my-courses` → redirect ke `/educator/dashboard`

## 7. Organization Roles

### Clerk Organization Roles:
- **`admin`**: Full educator access + admin privileges
- **`basic_member`**: Standard educator access

### Application Mapping:
- Organization member (any role) = `educator`
- Non-organization member = `student`

## 8. Important Notes

- **NO EDUCATOR REGISTRATION** di aplikasi - invite only via dashboard
- **Organization membership** takes precedence over metadata
- **Automatic role detection** berdasarkan organization membership
- **Separate auth flows** untuk student vs educator
