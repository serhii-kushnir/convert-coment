// src/main/java/org/cc/HomeController.java

package org.cc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/convert")
    public String convert(@RequestParam String text,
                          Model model) {

        String result = Arrays.stream(text.split("\\R"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));

        model.addAttribute("input", text);
        model.addAttribute("result", result);

        return "index";
    }
}