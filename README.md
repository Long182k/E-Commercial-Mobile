# E-Commerce Mobile App

A modern e-commerce mobile application built with Jetpack Compose for Android.

## Important Notices

### ⚠️ Email Registration Requirements
- **Real Email Required**: Please use a valid email address when registering
- Email verification is necessary for:
  - Password reset functionality
  - Order confirmation notifications
  - Account security features
- Invalid emails will prevent:
  - Order placement
  - Password reset
  - Email notifications

### 📸 Profile Image Upload
- Avatar images are handled by Cloudinary
- Supported formats: JPG, PNG
- Images are automatically optimized
- Secure cloud storage

## Key Features

### 🛍️ Shopping Experience
- **Best Sellers**: Discover trending products
- **Category Filtering**: Browse products by category
- **Search**: Find products quickly
- **Product Details**: View comprehensive product information

### 🛒 Cart Management
- Add/remove items
- Update quantities
- Real-time price calculations
- Cart persistence across sessions

### 📦 Order Processing
- Checkout summary
- Address management
- Order confirmation
- Email notifications for:
  - Order confirmation
  - Order status updates

### 👤 User Account
- **Profile Management**
  - Edit personal information
  - Update avatar (Cloudinary integration)
  - View order history
- **Security Features**
  - Secure login/registration
  - Password reset via email
  - Session management

### 🔍 Search & Filter
- Search by product name
- Filter by category
- Sort by price
- View best sellers

## Authentication Features

- **Registration**: Create account with email verification
- **Login**: Secure access to your account
- **Password Management**:
  - Change password
  - Forgot password recovery
  - Email-based reset process
- **Profile Updates**:
  - Edit personal details
  - Update profile picture
  - Manage shipping addresses

## Technical Integration

### Third-Party Services
- **MailerSend**: Email service for notifications
- **Cloudinary**: Media management for user avatars
- **Supabase**: Backend database hosting

### API Integration
- RESTful API communication
- Real-time updates
- Secure data transfer
- Token-based authentication

## Development Stack

- Kotlin
- Jetpack Compose
- Material Design 3
- Koin (Dependency Injection)
- Coil (Image Loading)
- Retrofit (API Client)
- Kotlin Coroutines
- Android Architecture Components

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Run on emulator or physical device
4. Register with a valid email address
5. Start exploring the app features

## Requirements

- Android 6.0 (API level 23) or higher
- Internet connection
- Valid email address for registration
