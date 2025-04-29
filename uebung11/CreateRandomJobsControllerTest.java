package ch.zhaw.freelancer4u.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CreateRandomJobsControllerTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    @InjectMocks
    private CreateRandomJobsController createRandomJobsController;

    @Test
    public void testRandomJob_Success() {
        // Arrange
        var job = new Job("title", "desc1", JobType.IMPLEMENT, 50.0, "");

        when(chatClient.prompt("Create a random job")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.entity(Job.class)).thenReturn(job);

        // Act
        ResponseEntity<Job> response = createRandomJobsController.randomJob();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(job, response.getBody());
    }

    // You might want to add a test for exception handling if that's a possibility
    @Test
    public void testRandomJob_ExceptionThrown() {
        // Arrange
        when(chatClient.prompt("Create a random job")).thenThrow(new RuntimeException("Chat service unavailable"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> createRandomJobsController.randomJob());
    }

    @Test
    public void testRandomJobs_Success() {
        // Arrange
        List<Job> expectedJobs = List.of(
                new Job("Job 1", "Description 1", JobType.OTHER, 100.0, ""),
                new Job("Job 2", "Description 2", JobType.IMPLEMENT, 200.0, ""),
                new Job("Job 3", "Description 3", JobType.TEST, 150.0, ""));

        ParameterizedTypeReference<List<Job>> typeRef = new ParameterizedTypeReference<List<Job>>() {
        };

        when(chatClient.prompt("Create three random jobs")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.entity(typeRef)).thenReturn(expectedJobs);

        // Act
        ResponseEntity<List<Job>> response = createRandomJobsController.randomJobs();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
        assertEquals(expectedJobs, response.getBody());
    }

    @Test
    public void testRandomJobs_EmptyList() {
        // Arrange
        List<Job> emptyList = Collections.emptyList();
        ParameterizedTypeReference<List<Job>> typeRef = new ParameterizedTypeReference<List<Job>>() {
        };

        when(chatClient.prompt("Create three random jobs")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.entity(typeRef)).thenReturn(emptyList);

        // Act
        ResponseEntity<List<Job>> response = createRandomJobsController.randomJobs();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testRandomJobs_ExceptionThrown() {
        // Arrange
        when(chatClient.prompt("Create three random jobs")).thenThrow(new RuntimeException("Chat service unavailable"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> createRandomJobsController.randomJobs());
    }
}