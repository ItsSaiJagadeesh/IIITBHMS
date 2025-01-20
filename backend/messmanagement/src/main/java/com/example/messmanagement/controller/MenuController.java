package com.example.messmanagement.controller;

import com.example.messmanagement.entity.Menu;
import com.example.messmanagement.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/daily")
    public ResponseEntity<List<Menu>> getDailyMenu(
            @RequestParam String dayOfWeek,
            @RequestParam String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        List<Menu> menu = menuService.getDailyMenu(dayOfWeek, parsedDate);
        return ResponseEntity.ok(menu);
    }
}