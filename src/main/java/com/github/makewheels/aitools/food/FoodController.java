package com.github.makewheels.aitools.food;

import com.github.makewheels.aitools.system.response.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("food")
public class FoodController {
    @Resource
    private FoodService foodService;

    @GetMapping("createTask")
    public Result<Food> createTask(@RequestParam String extension) {
        return Result.ok(foodService.createTask(extension));
    }

    @GetMapping("startTask")
    public Result<Food> startTask(@RequestParam String extension) {
        // TODO
        return Result.ok(new Food());
    }
}
