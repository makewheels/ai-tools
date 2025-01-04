package com.github.makewheels.aitools;

import com.github.makewheels.aitools.argue.ArgueService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class ArgueTest {
    @Resource
    private ArgueService argueService;

    @Test
    public void test() {
        argueService.argue();
    }

}
