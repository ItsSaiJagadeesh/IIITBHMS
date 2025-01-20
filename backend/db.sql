CREATE DATABASE iiitbhms;
USE iiitbhms;
CREATE TABLE menu (
    id INT AUTO_INCREMENT PRIMARY KEY,
    day_of_week VARCHAR(10), -- Example: 'Monday'
    meal_type VARCHAR(20), -- Example: 'Breakfast', 'Lunch', 'Snacks', 'Dinner'
    menu_items TEXT, -- Stores the menu items as a string
    date DATE -- Date corresponding to the menu
);

SELECT * FROM menu;