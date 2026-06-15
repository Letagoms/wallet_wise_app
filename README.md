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
