# MediaLab Assistant

MediaLab Assistant is a **JavaFX-based task management system** designed to help users efficiently manage tasks, categories, priorities, and reminders. The project follows **MVC architecture** and stores data in a JSON format.

## Table of Contents
- [Features](#features)
- [Installation & Setup](#installation--setup)
- [Usage](#usage)
- [Technologies Used](#technologies-used)
- [JavaDoc Documentation](#javadoc-documentation)
- [CSS Styling](#css-styling)
- [Contributing](#contributing)
- [License](#license)

## Features
✅ Task creation, editing, and deletion  
✅ Dynamic category and priority management  
✅ Task filtering and searching  
✅ JSON-based persistent storage  
✅ JavaFX GUI with intuitive design  
✅ JavaDoc documentation for project maintainability  


## Installation & Setup
### Prerequisites
- Java 17 or later
- Maven (for dependency management)
- IntelliJ IDEA or another Java IDE

### Steps
1. **Clone the repository:**
   ```sh
   git clone https://github.com/your-username/TaskManagementSystem.git
   cd TaskManagementSystem
   ```
2. **Open the project in IntelliJ IDEA**
   - Ensure Maven is imported
   - Set the Java SDK (17+)
3. **Run the project**
   - Locate `Main.java` in `org.example`
   - Click **Run** in your IDE

## Usage
- **Add, Edit, and Delete Tasks** through the GUI
- **Filter Tasks** by category or priority
- **Use JSON storage** for persistent data
- **View Statistics** (Total, Completed, Delayed, Upcoming tasks)

## Technologies Used
- **Java 17**
- **JavaFX** for UI
- **Jackson** for JSON handling
- **JUnit** for testing
- **Maven** for dependency management

## JavaDoc Documentation
To generate and view JavaDoc documentation:
```sh
javadoc -d docs -sourcepath src/main/java -subpackages org.example
```
Or, open `docs/index.html` in your browser.

## CSS Styling
The frontend is customized with CSS. Key stylesheets:
- `dialogstyles.css`: Styles for popups and modals
- `tabstyles.css`: Styles for tabs and task displays

## Contributing
Contributions are welcome! To contribute:
1. Fork the repository
2. Create a new branch (`git checkout -b feature-name`)
3. Commit your changes (`git commit -m 'Add feature'`)
4. Push to your branch (`git push origin feature-name`)
5. Submit a pull request

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
