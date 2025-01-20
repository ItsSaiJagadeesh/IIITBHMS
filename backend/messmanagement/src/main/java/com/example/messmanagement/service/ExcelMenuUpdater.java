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
        File file = new File("src/main/resources/menu.xlsx");
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        String currentDay = null;
        LocalDate currentDate = null;

        for (Row row : sheet) {
            int rowIndex = row.getRowNum();

            if (rowIndex == 0) {
                currentDay = row.getCell(0).getStringCellValue();
            } else if (rowIndex == 1) {
                currentDate = LocalDate.parse(row.getCell(0).getStringCellValue(), DateTimeFormatter.ofPattern("dd/MMM/yy"));
            } else {
                String mealType = row.getCell(0).getStringCellValue();
                StringBuilder menuItems = new StringBuilder();

                for (int col = 1; col < row.getLastCellNum(); col++) {
                    menuItems.append(row.getCell(col).getStringCellValue()).append(", ");
                }

                Menu menu = new Menu();
                menu.setDayOfWeek(currentDay);
                menu.setDate(currentDate);
                menu.setMealType(mealType);
                menu.setMenuItems(menuItems.toString());
                menuRepository.save(menu);
            }
        }
        workbook.close();
    }
}