# Gym Management System - JavaFX Frontend

A comprehensive JavaFX application for managing gym operations including equipment, members, packages, rooms, workout sessions, and feedback.

## Features

- **Authentication**: User registration and login with JWT token management
- **Dashboard**: Overview of gym statistics and key metrics
- **Equipment Management**: CRUD operations for gym equipment
- **Member Management**: Manage gym members and their information
- **Package Management**: Handle membership packages and pricing
- **Room Management**: Manage gym rooms and facilities
- **Workout Session Management**: Track and manage workout sessions
- **Feedback Management**: Collect and manage customer feedback
- **Complete CRUD Operations**: All modules support Create, Read, Update, Delete operations
- **Real-time Data**: Automatic refresh and real-time updates
- **Form Validation**: Client-side validation with user-friendly error messages
- **Responsive UI**: Clean, modern interface with progress indicators
- **Session Management**: Secure JWT-based authentication with automatic token handling
- **Error Handling**: Comprehensive error handling with user notifications

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- JavaFX 19 (included in dependencies)

## Project Structure

\`\`\`
src/main/java/com/gymapp/
├── api/                    # HTTP client and API configuration
│   ├── ApiClient.java      # HTTP client wrapper
│   └── ApiConfig.java      # API endpoints configuration
├── controller/             # FXML controllers
│   ├── LoginController.java
│   ├── RegisterController.java
│   ├── MainController.java
│   ├── DashboardController.java
│   ├── EquipmentController.java
│   └── EquipmentFormController.java
├── model/                  # Data models/DTOs
│   ├── User.java
│   ├── Equipment.java
│   └── Member.java
├── util/                   # Utility classes
│   └── SessionManager.java # Authentication session management
└── GymManagementApp.java   # Main application class

src/main/resources/
└── fxml/                   # FXML layout files
    ├── Login.fxml
    ├── Register.fxml
    ├── MainWindow.fxml
    ├── Dashboard.fxml
    ├── Equipment.fxml
    └── EquipmentForm.fxml
\`\`\`

## Setup Instructions

### 1. Clone the Repository

\`\`\`bash
git clone <repository-url>
cd gym-management-javafx
\`\`\`

### 2. Configure API Base URL

Edit `src/main/java/com/gymapp/api/ApiConfig.java` and update the `BASE_URL`:

\`\`\`java
public static final String BASE_URL = "https://your-api-domain.com";
\`\`\`

### 3. Build and Run the Project

#### Using Maven:

\`\`\`bash
# Clean and compile
mvn clean compile

# Run the application
mvn javafx:run

# Or build and run JAR
mvn clean package
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar target/gym-management-javafx-1.0.0.jar
\`\`\`

#### Using Gradle (alternative):

If you prefer Gradle, create a `build.gradle` file:

\`\`\`gradle
plugins {
    id 'java'
    id 'application'
    id 'org.openjfx.javafxplugin' version '0.0.13'
}

group = 'com.gymapp'
version = '1.0.0'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.openjfx:javafx-controls:19'
    implementation 'org.openjfx:javafx-fxml:19'
    implementation 'com.fasterxml.jackson.core:jackson-core:2.15.2'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.15.2'
    implementation 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2'
}

javafx {
    version = '19'
    modules = ['javafx.controls', 'javafx.fxml']
}

application {
    mainClass = 'com.gymapp.GymManagementApp'
}
\`\`\`

Then run with:

\`\`\`bash
./gradlew run
\`\`\`

## API Integration

The application integrates with a REST API backend with the following endpoints:

### Authentication
- `POST /auth/register` - User registration
- `POST /auth/login` - User login (returns JWT token)

### Equipment
- `GET /equipment` - List all equipment
- `GET /equipment/{id}` - Get equipment details
- `POST /equipment` - Create new equipment
- `PUT /equipment/{id}` - Update equipment
- `DELETE /equipment/{id}` - Delete equipment

### Members
- `GET /member` - List all members
- `GET /member/{id}` - Get member details
- `POST /member` - Create new member
- `PUT /member/{id}` - Update member
- `DELETE /member/{id}` - Delete member

### Packages
- `GET /package` - List all packages
- `GET /package/{id}` - Get package details
- `POST /package` - Create new package
- `PUT /package/{id}` - Update package
- `DELETE /package/{id}` - Delete package

### Rooms
- `GET /room` - List all rooms
- `GET /room/{id}` - Get room details
- `POST /room` - Create new room
- `PUT /room/{id}` - Update room
- `DELETE /room/{id}` - Delete room

### Workout Sessions
- `GET /workout-session` - List all sessions
- `GET /workout-session/{id}` - Get session details
- `POST /workout-session` - Create new session
- `PUT /workout-session/{id}` - Update session
- `DELETE /workout-session/{id}` - Delete session

### Feedback
- `GET /feedback` - List all feedback
- `POST /feedback` - Submit new feedback

## Authentication

The application uses JWT (JSON Web Token) for authentication. After successful login, the token is automatically included in all API requests via the `Authorization: Bearer <token>` header.

## Key Components

### SessionManager
Singleton class that manages user authentication state and JWT token storage.

### ApiClient
HTTP client wrapper that handles all API communications with automatic JWT token attachment.

### Controllers
FXML controllers that handle UI interactions and business logic for each view.

### Models
Data transfer objects (DTOs) that represent the data structures used by the API.

## Building for Distribution

To create a distributable JAR file:

\`\`\`bash
mvn clean package
\`\`\`

To create a native installer (requires additional setup):

\`\`\`bash
mvn clean javafx:jpackage
\`\`\`

## Module Overview

### 1. Authentication Module
- User registration and login
- JWT token management
- Automatic session handling

### 2. Dashboard Module
- Real-time statistics display
- Quick overview of all gym metrics
- Automatic data refresh

### 3. Equipment Management
- Add, edit, delete gym equipment
- Track equipment status and maintenance
- Equipment categorization and search

### 4. Member Management
- Complete member profile management
- Membership type tracking
- Member status monitoring

### 5. Package Management
- Membership package creation and management
- Pricing and duration configuration
- Feature list management

### 6. Room Management
- Gym room and facility management
- Capacity and availability tracking
- Room type categorization

### 7. Workout Session Management
- Session scheduling and tracking
- Member and trainer assignment
- Session status monitoring

### 8. Feedback Management
- Customer feedback collection
- Rating system
- Feedback status tracking

## Troubleshooting

### Common Issues

1. **JavaFX Runtime Components Missing**
   - Ensure you're using Java 17+ with JavaFX modules
   - The Maven plugin should handle JavaFX dependencies automatically

2. **API Connection Issues**
   - Verify the API base URL in `ApiConfig.java`
   - Check that the backend API is running and accessible
   - Ensure proper CORS configuration on the backend

3. **Authentication Issues**
   - Check that JWT tokens are being properly stored and sent
   - Verify token expiration and refresh logic

4. **Build Issues**
   - Ensure Maven 3.6+ is installed
   - Check Java version compatibility (Java 17+)
   - Clear Maven cache: `mvn clean`

### Development Tips

- Use Scene Builder for visual FXML editing
- Enable JavaFX CSS styling for better UI appearance
- Implement proper error handling and user feedback
- Consider adding logging for debugging purposes

## API Integration Details

The application automatically handles:
- JWT token attachment to all requests
- Request/response serialization with Jackson
- Error handling and user notifications
- Loading states during API calls

All API calls are made asynchronously to prevent UI blocking.

## Dependencies

- **JavaFX Controls & FXML**: UI framework and layout
- **Jackson**: JSON serialization/deserialization
- **Java HTTP Client**: Built-in HTTP client for API communication

## License

This project is licensed under the MIT License - see the LICENSE file for details.
