package com.github.makewheels.aitools.wordbook;

import com.github.makewheels.aitools.system.response.Result;
import com.github.makewheels.aitools.word.response.WordResponse;
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

    @GetMapping("listMyWordBook")
    public Result<List<WordBookResponse>> listMyWordBook() {
        return Result.ok(wordBookService.listMyWordBook());
    }

    @GetMapping("randomPick")
    public Result<WordResponse> randomPick() {
        return Result.ok(wordBookService.randomPick());
    }
}
