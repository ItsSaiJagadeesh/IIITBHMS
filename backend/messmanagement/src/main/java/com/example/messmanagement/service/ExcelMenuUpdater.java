package com.example.messmanagement.service;

import com.example.messmanagement.entity.Menu;
import com.example.messmanagement.repository.MenuRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ExcelMenuUpdater {

    @Autowired
    private MenuRepository menuRepository;

    @Scheduled(fixedRate = 86400000) // Runs daily
    public void updateMenuFromExcel() throws IOException {
        File file = new File("src/main/resources/20-Jan_to_2-Feb_menu.xlsx");
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        // Get days of the week from Row 1
        Row dayRow = sheet.getRow(0);
        int columns = dayRow.getLastCellNum(); // Total number of days (columns)

        for (int col = 0; col < columns; col++) {
            String currentDay = dayRow.getCell(col).getStringCellValue(); // Monday, Tuesday, etc.

            // Get dates from Rows 2 and 3
            LocalDate firstDate = LocalDate.parse(sheet.getRow(1).getCell(col).getStringCellValue(), DateTimeFormatter.ofPattern("dd/MMM/yy"));
            LocalDate secondDate = LocalDate.parse(sheet.getRow(2).getCell(col).getStringCellValue(), DateTimeFormatter.ofPattern("dd/MMM/yy"));

            // Parse meals dynamically for this day
            int rowIndex = 3; // Start from the 4th row
            while (rowIndex < sheet.getLastRowNum()) {
                Row mealRow = sheet.getRow(rowIndex);

                if (mealRow == null || mealRow.getCell(0) == null) {
                    rowIndex++; // Skip empty rows
                    continue;
                }

                String cellValue = mealRow.getCell(0).getStringCellValue().trim();

                // Check if the row is a meal type (e.g., "BREAKFAST," "LUNCH")
                if (isMealType(cellValue)) {
                    String mealType = cellValue;
                    rowIndex++; // Move to the next row for menu items
                    StringBuilder menuItems = new StringBuilder();

                    // Collect menu items under this meal type
                    while (rowIndex < sheet.getLastRowNum()) {
                        Row itemRow = sheet.getRow(rowIndex);
                        if (itemRow == null || itemRow.getCell(0) == null || isMealType(itemRow.getCell(0).getStringCellValue().trim())) {
                            break; // Stop if we reach a new meal type or empty row
                        }

                        for (int colIndex = 0; colIndex < itemRow.getLastCellNum(); colIndex++) {
                            Cell itemCell = itemRow.getCell(col);
                            if (itemCell != null) {
                                menuItems.append(itemCell.getStringCellValue()).append(", ");
                            }
                        }
                        rowIndex++;
                    }

                    // Save menu for both dates
                    saveMenu(menuRepository, currentDay, firstDate, mealType, menuItems.toString());
                    saveMenu(menuRepository, currentDay, secondDate, mealType, menuItems.toString());
                } else {
                    rowIndex++; // Not a meal type, move to the next row
                }
            }
        }

        workbook.close();
    }

    private boolean isMealType(String cellValue) {
        return cellValue.equalsIgnoreCase("BREAKFAST") ||
               cellValue.equalsIgnoreCase("LUNCH") ||
               cellValue.equalsIgnoreCase("SNACKS") ||
               cellValue.equalsIgnoreCase("DINNER");
    }

    private void saveMenu(MenuRepository repository, String day, LocalDate date, String mealType, String menuItems) {
        Menu menu = new Menu();
        menu.setDayOfWeek(day);
        menu.setDate(date);
        menu.setMealType(mealType);
        menu.setMenuItems(menuItems);
        repository.save(menu);
    }
}