package com.github.makewheels.aitools.word;

import com.github.makewheels.aitools.system.response.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
