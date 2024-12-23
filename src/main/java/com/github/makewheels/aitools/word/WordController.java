package com.github.makewheels.aitools.word;

import com.github.makewheels.aitools.system.response.Result;
import com.github.makewheels.aitools.word.response.WordResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("word")
public class WordController {
    @Resource
    private WordService wordService;
    @Resource
    private WordHelper wordHelper;

    @PostMapping("import")
    public Result<Void> importWords(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        wordHelper.importWords(multipartFile);
        return Result.ok();
    }

    @GetMapping("randomPick")
    public Result<WordResponse> randomPick() {
        return Result.ok(wordService.randomPick());
    }
}
