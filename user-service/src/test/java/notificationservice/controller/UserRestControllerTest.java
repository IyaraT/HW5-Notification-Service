package notificationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import notificationservice.dto.UserRequest;
import notificationservice.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import notificationservice.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserRestController.class)
@ContextConfiguration(classes = app.App.class)

class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_returnsList() throws Exception {
        List<UserResponse> users = List.of(
                new UserResponse(1L, "Test", "test@gmail.com", 33, "2024"),
                new UserResponse(2L, "Alice", "alice@gmail.com", 25, "2024")
        );

        Mockito.when(userService.getAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test")))
                .andExpect(jsonPath("$[1].email", is("alice@gmail.com")));
    }

    @Test
    void getById_found() throws Exception {
        UserResponse user = new UserResponse(1L, "Test", "test@gmail.com", 33, "2024");
        Mockito.when(userService.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test")));
    }

    @Test
    void getById_notFound() throws Exception {
        Mockito.when(userService.getById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_returnsCreated() throws Exception {
        UserRequest request = new UserRequest("Test", "test@gmail.com", 33);
        UserResponse response = new UserResponse(1L, "Test", "test@gmail.com", 33, "2024");

        Mockito.when(userService.create(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test")));
    }

    @Test
    void update_returnsOkWhenExists() throws Exception {
        UserRequest request = new UserRequest("NewName", "new@gmail.com", 30);
        UserResponse response = new UserResponse(1L, "NewName", "new@gmail.com", 30, "2024");

        Mockito.when(userService.update(eq(1L), any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("NewName")));
    }

    @Test
    void update_returnsNotFoundWhenMissing() throws Exception {
        UserRequest request = new UserRequest("NewName", "new@gmail.com", 30);

        Mockito.when(userService.update(eq(1L), any(UserRequest.class))).thenReturn(null);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}