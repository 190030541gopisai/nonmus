# Authentication Flow Improvements

## Changes Made

### Backend (Auth Service)

**Problem**: During login, if email was not verified, the backend was automatically resending OTP. This caused:
- Rate limit errors shown on the login page instead of the OTP verification page
- Poor UX - user couldn't see the cooldown timer
- Unnecessary API calls

**Solution**:
1. **Removed auto-resend OTP during login** (`AuthService.java`)
   - No longer calls `resendOtp()` when email is unverified
   - User must manually request OTP on the verification page

2. **Added userId to 403 response** (`EmailNotVerifiedResponse.java`)
   - Frontend can now navigate to OTP page with proper context
   - Response includes both `userId` and `email`

3. **Updated login endpoint return type** (`AuthController.java`)
   - Changed from `ApiResponse<LoginResponse>` to `ApiResponse<?>` to support different response types

### Frontend

**1. Registration - Better Error Handling** (`RegisterForm.tsx`)
- Detects "user already exists" error (409 status)
- Shows friendly message: "This email is already registered"
- Provides link to login page directly

**2. Login Flow Improvements** (`LoginForm.tsx`)
- When user is unverified (403), immediately navigates to OTP verification
- No error message shown on login page for unverified emails
- Extracts userId from response to pass to verification page

**3. OTP Verification Enhancements** (`OtpVerification.tsx`)
- **Manual OTP Request**: User clicks "Send verification code" button
- **Rate Limit Display**: Cooldown timer shows on OTP page, not login
- **Better UX Messages**:
  - "Verification code sent to your email" on success
  - "Too many requests. Please wait X seconds" on rate limit
  - Shows attempts remaining when OTP is incorrect
- **Handles navigation from login**: Accepts userId/email from location state

## User Experience Flow

### Registration Flow
```
1. User fills registration form
2. Click "Create account"
3. If email exists → Show error + "Go to login" link
4. If success → Redirect to OTP verification
```

### Login Flow (Unverified Email)
```
1. User enters email/password
2. Click "Sign in"
3. If unverified → Navigate to OTP page (NO auto-resend)
4. User sees "Send verification code" button
5. Click button → OTP sent to email
6. Enter OTP → Verified and logged in
```

### Login Flow (Verified Email)
```
1. User enters email/password
2. Click "Sign in"
3. Success → Redirect to dashboard
```

### OTP Verification Flow
```
1. User on OTP page (from registration or login)
2. Option A: Already have code → Enter and verify
3. Option B: Need code → Click "Send verification code"
4. If rate limited → See countdown timer (e.g., "Resend available in 45s")
5. After cooldown → Can request new code
6. Enter code → Verified
```

## Files Modified

### Backend
- `auth-service/src/main/java/com/nonmus/service/AuthService.java`
- `auth-service/src/main/java/com/nonmus/controller/AuthController.java`
- `auth-service/src/main/java/com/nonmus/dto/EmailNotVerifiedResponse.java` (NEW)

### Frontend
- `src/components/auth/LoginForm.tsx`
- `src/components/auth/RegisterForm.tsx`
- `src/components/auth/OtpVerification.tsx`

## Testing

To test the improved flow:

1. **Start Backend**:
   ```bash
   # Start PostgreSQL + Redis
   docker-compose up -d
   
   # Run each service
   cd auth-service && mvn spring-boot:run
   cd user-service && mvn spring-boot:run
   cd email-service && mvn spring-boot:run
   ```

2. **Start Frontend**:
   ```bash
   cd frontend/nonmus && npm run dev
   ```

3. **Test Scenarios**:
   - Register new user → OTP page → Verify
   - Register existing user → See "already registered" message
   - Login with unverified email → OTP page → Request code → Verify
   - Spam resend button → See rate limit with countdown
   - Login with verified email → Dashboard

## Benefits

✅ **No more confusing errors on login page**  
✅ **Rate limits shown where user can act on them**  
✅ **Clear "already registered" messaging**  
✅ **User has control over when OTP is sent**  
✅ **Better countdown/cooldown visibility**  
✅ **Consistent error handling across all forms**
