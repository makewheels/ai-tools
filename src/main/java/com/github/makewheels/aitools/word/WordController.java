package com.github.makewheels.aitools.word;

import com.github.makewheels.aitools.system.response.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("food")
public class WordController {
    @Resource
    private WordService wordService;
    @Resource
    private WordHelper wordHelper;

    @PostMapping
    public Result<Void> importWords() {
        return null;
    }
}
