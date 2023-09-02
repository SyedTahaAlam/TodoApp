Design:
Todo Application
Running this application require Android Studio Dolphin | 2021.3.1 Patch 1

Design:
Design of the application is straightforward it has a list that displays the todos in three
different colors
● EAA3DDBA for the lowest priority todo.
● FFE6B06E for medium level priority todo.
● FFC45959 for the highest level priority todo.
Todo displays title , description and date of creation along with the checkbox to mark it as completed. Users can delete a todo by dragging the todo from right to left which displays the delete button.
Todo list can be sorted in 4 different sorting priority high to low, low to high, and recent to oldest or oldest to recent todo by clicking on menu of topbar.
Todo can be added by clicking on floating action button and add todo screen consist of 4 text field two enabled, one opens the datepicker and last for the priority selection.
Title and priority are compulsory to be added and date can be changed but by default current date is selected.
Architecture:
The app consists of MVVM pattern and for user interface it is using compose.
MVVM along with a repository is used as it creates a separation between the ui and data. And makes code more testable.
Application is also using dependency injection (HILT) which also separate the creational logic where the variable is being used. It also helps in creating a clean and well organized code.
Improvements:
Improvements that can be made in this application could be:
● Time should also be taken as input. Which will increase the productivity of the todo
● After taking time as input for todo a notification could be shown to the particular time.
● Location can also taken as input that where this action is intended to be performed. So that if the user is passing that location app can notify the user that a todo is remains undone on that place
