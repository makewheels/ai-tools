package com.github.makewheels.aitools.extract;

import cn.hutool.core.thread.ThreadUtil;
import com.github.makewheels.aitools.system.response.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("extract")
public class ExtractController {
    @Resource
    private ExtractService extractService;

    @GetMapping("createTask")
    public Result<Extract> createTask(@RequestParam String extension) {
        return Result.ok(extractService.createTask(extension));
    }

    @GetMapping("startTask")
    public Result<Void> startTask(@RequestParam String taskId) {
        ThreadUtil.execAsync(() -> extractService.startTask(taskId));
        return Result.ok();
    }
}
