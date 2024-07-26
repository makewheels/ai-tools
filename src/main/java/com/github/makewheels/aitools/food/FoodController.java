package com.github.makewheels.aitools.food;

import cn.hutool.core.thread.ThreadUtil;
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

    @GetMapping("startTask2")
    public Result<Void> startTask(@RequestParam String taskId) {
        ThreadUtil.execAsync(() -> foodService.startTask(taskId));
        return Result.ok();
    }

    @GetMapping("getById")
    public Result<Food> getById(@RequestParam String taskId) {
        Food food = foodService.getById(taskId);
        food.setPrompt(null);
        return Result.ok(food);
    }
}
