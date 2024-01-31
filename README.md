
# 🚀 ExceptionCatcher Integration Guide 🌟

## 🌐 How to Run the Server:

1. **Install Node.js** 🌳
   - Ensure that Node.js is installed on your machine.
   - If not, download and install it from [Node.js official site](https://nodejs.org/).

2. **Setup the Server** 💻
   - Copy the `server.js` file to a folder on your machine.
   - Open a terminal or command prompt.
   - Change directory (cd) into the folder where `server.js` is located.

3. **Start the Server** 🚀
   - Run the command `node server.js`. This will start the server on your local machine.
   - The server will be listening for exception reports sent from your app.
   - The server also prints its IP address - handy if you're using a physical device and need to enter the IP address manually.
   - Keep the terminal open to maintain the server running.

## 🎯 How to Use ExceptionCatcher in Your App:

1. **Initialize in the Application Class** 📱
   - Add the following code in your Application class to initialize ExceptionCatcher:
     ```kotlin
     class MyApplication : Application() {
         override fun onCreate() {
             super.onCreate()
             ExceptionCatcher.initialize(this)
         }
     }
     ```

2. **Catch Exceptions Manually** 🕵️‍♂️
   - To manually catch exceptions and send them to the server, use this code snippet:
     ```kotlin
     try {
         // Your code that might throw an exception
     } catch (e: Exception) {
         ExceptionCatcher.handleException(e, this) // 'this' is your context
     }
     ```

Happy coding and may your apps be ever exception-free! 🌈✨
