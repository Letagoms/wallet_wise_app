README
WalletWise-Personal Budgeting App

1.	Overview
WalletWise is a personal budgeting mobile application designed to help users track expenses, manage budgets, and make smarter financial decisions. The app combines financial tracking, automation, and interactive features to create a user-friendly and engaging experience. 
2.	Purpose
Many individuals struggle with:
•	Tracking daily expenses
•	Staying within budget
•	Maintaining accurate financial records
WalletWise solves this by providing a centralized, easy-to-use platform for managing finances efficiently.
3.	The key features for WalletWise
•	User authentication(Login & Register with username, email, and password)
•	Expense tracking
•	Category management
•	Receipt upload and storage
•	Budget monitoring
•	Graph and data visualization
•	Financial forecast(scheduled/recurring transcations)
•	Receipt scanner
•	Gamification(badges and streaks)

4.	Data Management
Uses a local SQLite databse
Stores:
•	User accounts
•	Expenses
•	Categories
•	Receipt images
Data is linked per user for privacy and personalization
5.	User Interface & App Flow
Login and Registration
•	Users can securely log in using email and passwors
•	New users can register with username, email, and password
•	Incorrect login attempts display error feedback
Home Dashboard
Display current balance
Quick access to:
•	Add expenses
•	View transactions
•	Show achievement pop-ups(badges & streaks)
Navigation System
The app separates features into:
Core Freatures:
•	Home
•	Add expenses
•	Transactions 
•	Graph
•	Budget goals
•	Category spending
•	Progress dashboard
Special Features:
•	Financial forecast
•	Receipt scanner
•	Reward(gamification)
Add Expenses
Users can:
•	Enter amount, date, and time
•	Select category
•	Add description
•	Upload receipt image
Expenses List
Displays all expenses grouped by:
•	Date
•	Category
•	Amount
•	Users can view attached receipts
Graph
•	Visual representation of spending trends
•	Filter by date

Financial Forecast
•	Users can schedule income and expenses
•	Calculate projected savings
Receipt Scanner
•	Scan receipts using the camera
•	Automatically store and link to expenses
Rewards & Gamification
•	Earn badges for achievements (e.g, staying within budget, consistent logging)
•	Track streaks for consistency
•	Get notifications for milestones
How Github was used 
6.	Version 
GitHub is used for version control, collaboration, and automation of the WalletWise project. It ensures that all team members can work on the application simultaneously without overwriting each other’s work.
6.1 GitHub Repository Structure
The project is stored in a shared GitHub repository containing:
• Source code (Android/Kotlin files)
• XML layouts
• Database helper classes (SQLite)
• Assets (icons, images)
• README documentation
Recommended folder structure:
• /app → Main Android application code
• /database → SQLite helper classes
• /ui → Activities and UI components
• /utils → Helper classes
6.2 Branching Strategy (Important for Group Work)
Instead of everyone pushing directly to the main branch, the team should use branches:
• main → Stable, working version of the app
• dev → Integration branch for combining features
• feature/* → Individual features (e.g. feature/login, feature/gamification)
Workflow:
1.	Create a new branch for your feature
2.	Work and commit changes locally
3.	Push branch to GitHub
4.	Create a Pull Request (PR)
5.	Team reviews and merges into dev
6.	Once stable, dev is merged into main
This prevents breaking the main application.
6.3 Basic Git Commands
Common commands used in the project:
• git clone <repo_url> → Copy project to local machine
• git checkout -b feature/login → Create new branch
• git add . → Stage changes
• git commit -m "Added login feature" → Save changes
• git push origin feature/login → Upload changes
• git pull origin dev → Get latest updates
6.4 GitHub Actions (Automation)
GitHub Actions is used to automate tasks such as building and testing the app whenever code is pushed.
Purpose:
• Ensure code compiles successfully
• Detect errors early
• Maintain code quality
• Automate testing (optional)
6.5 Example GitHub Actions Workflow
A workflow file is stored in:
.github/workflows/android.yml
Example configuration:
name: Android CI
on:
push:
branches: [ "dev", "main" ]
pull_request:
branches: [ "dev", "main" ]
jobs:
build:
runs-on: ubuntu-latest
steps:
- name: Checkout code
  uses: actions/checkout@v3

- name: Set up JDK
  uses: actions/setup-java@v3
  with:
    distribution: 'temurin'
    java-version: '17'

- name: Grant permission
  run: chmod +x gradlew

- name: Build project
  run: ./gradlew build
6.6 How GitHub Actions Helps This Project
• Automatically builds the WalletWise app on every update
• Prevents broken code from being merged
• Helps identify issues in login, database, or UI logic early
• Supports team collaboration without constant manual testing
6.7 Key Considerations
• Always pull latest changes before starting work
• Avoid editing the same files simultaneously
• Write clear commit messages
• Test features locally before pushing
Conclusion (Extended)
By integrating GitHub and GitHub Actions, WalletWise ensures:
• Structured collaboration
• Code safety and version tracking
• Automated quality checks
This makes the development process more efficient, especially in a team environment.



Conclusion
WalletWise provides a complete financial management solution by combining:
•	Practical budgeting tools
•	Engaging user experiences
•	Secure local data storage
With the receipt scanner and financial forecasts features fully integrated, and the app running perfectly, it empowers users to take control of their finances and build better spending habits. 



tation of spending trends
•	Filter by date
