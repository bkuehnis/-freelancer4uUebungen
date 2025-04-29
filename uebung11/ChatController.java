package ch.zhaw.freelancer4u.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.freelancer4u.repository.JobRepository;
import ch.zhaw.freelancer4u.service.CompanyService;
import ch.zhaw.freelancer4u.service.UserService;
import ch.zhaw.freelancer4u.tools.FreelancerTools;

@RestController
@RequestMapping("/chatbot")
public class ChatController {
    @Autowired
    JobRepository jobRepository;

    @Autowired
    CompanyService companyService;

    @Autowired
    UserService userService;

    @Autowired
    OpenAiChatModel chatModel;

    @Autowired
    ChatClient chatClient;

    ChatMemory chatMemory;

    @GetMapping("/api")
    public ResponseEntity<String> chat(@RequestParam(required = true) String message) {
        if (!userService.userHasRole("admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        String content = chatClient.prompt(
                "Du bist ein Chatbot. Der den Benutzer Fragen beantwortet über Bestehende Jobs, deren Preis, Beschreibung, Titel und Unternehmen.")
                .tools(new FreelancerTools(jobRepository, companyService)).user(message)
                .call()
                .content();

        return ResponseEntity.status(HttpStatus.OK).body(content);
    }
}
