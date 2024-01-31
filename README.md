
# 🚀 ExceptionCatcher Sample App 🌟

Welcome to the ExceptionCatcher Sample App repository! This project serves as a practical example of how the _ExceptionCatcher Library_ can be integrated and used in Android applications. Dive in to see how you can effortlessly track and manage exceptions in your apps!

## 🌐 About ExceptionCatcher Library:

ExceptionCatcher is a powerful tool designed to help developers track and handle exceptions in Android applications. This sample app demonstrates its implementation and usage in a real-world scenario.

## 🎯 Running the Sample App:

To get a feel for how ExceptionCatcher works, you can run this sample app which already includes the library:

1. **Clone the Repository** 📂
   - Clone this repository to your local machine to get started.
   - Open the project in your preferred Android development environment.

2. **Explore the Code** 🔍
   - Check out how ExceptionCatcher is initialized in the `MyApplication` class.
   - See how exceptions are caught and handled throughout the app.

## 🌐 How to Run the Server:

To see the full capabilities of ExceptionCatcher, you can run a server to receive and display exception reports:

1. **Install Node.js** 🌳
   - Ensure Node.js is installed on your machine. If not, download it from [Node.js official site](https://nodejs.org/).

2. **Setup the Server** 💻
   - Find the `server.js` file in the server directory of this project.
   - Open a terminal or command prompt.
   - Change directory (cd) into the server folder.

3. **Start the Server** 🚀
   - Run the command `node server.js` to start the server.
   - The server will listen for exception reports from the app.
   - It prints its IP address, useful for manual IP entry on physical devices.

## 📱 Using ExceptionCatcher in Your Own App:

Want to integrate ExceptionCatcher into your own projects? Here's how:

1. **Initialize in the Application Class**:
   - Add code to initialize ExceptionCatcher in your Application class:
     ```kotlin
     class YourApplication : Application() {
         override fun onCreate() {
             super.onCreate()
             ExceptionCatcher.initialize(this)
         }
     }
     ```

2. **Catch Exceptions Manually**:
   - Use the following snippet to manually catch exceptions:
     ```kotlin
     try {
         // Potentially problematic code
     } catch (e: Exception) {
         ExceptionCatcher.handleException(e, this)
     }
     ```

Enjoy exploring this sample app and happy coding! 🌈✨
