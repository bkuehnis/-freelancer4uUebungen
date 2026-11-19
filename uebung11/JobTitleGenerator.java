package ch.zhaw.freelancer4u.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class JobTitleGenerator {
    private static final Logger log = LoggerFactory.getLogger(JobTitleGenerator.class);
    private final ChatClient chatClient;

    public JobTitleGenerator(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generate(String title, String description) {
        try {
            String generated = chatClient.prompt()
                    .system("Write a concise job title based on the supplied title and description. Return only the title.")
                    .user(title + "\n" + description)
                    .call()
                    .content();
            if (generated != null && !generated.isBlank()) {
                return generated.strip();
            }
        } catch (RuntimeException exception) {
            log.warn("AI job title generation failed ({})", exception.getClass().getSimpleName());
        }
        return title;
    }
}
