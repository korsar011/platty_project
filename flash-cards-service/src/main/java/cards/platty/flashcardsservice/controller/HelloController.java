package cards.platty.flashcardsservice.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("api/flash/hello")
    public String hello() {
        return "Hello from flash cards service";
    }
}
