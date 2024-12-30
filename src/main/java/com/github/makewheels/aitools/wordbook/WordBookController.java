package com.github.makewheels.aitools.wordbook;

import com.github.makewheels.aitools.system.response.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("wordBook")
public class WordBookController {
    @Resource
    private WordBookService wordBookService;

    @GetMapping("list")
    public Result<List<WordBookResponse>> list() {
        return Result.ok(wordBookService.list());
    }
}
