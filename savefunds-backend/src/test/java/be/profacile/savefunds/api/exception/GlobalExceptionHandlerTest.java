/*
package be.profacile.savefunds.api.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    // Controller interne — expose les exceptions à tester
    @RestController
    static class TestController {

        @GetMapping("/test/not-found")
        public void notFound() {
            throw new ResourceNotFoundException("Resource non trouvée");
        }

        @GetMapping("/test/illegal-argument")
        public void illegalArgument() {
            throw new IllegalArgumentException("Argument invalide");
        }

        @GetMapping("/test/illegal-state")
        public void illegalState() {
            throw new IllegalStateException("État invalide");
        }
    }

    @Test
    @DisplayName("ResourceNotFoundException → 404")
    void shouldReturn404OnResourceNotFound() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Resource non trouvée"));
    }

    @Test
    @DisplayName("IllegalArgumentException → 400")
    void shouldReturn400OnIllegalArgument() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Argument invalide"));
    }

    @Test
    @DisplayName("IllegalStateException → 400")
    void shouldReturn400OnIllegalState() throws Exception {
        mockMvc.perform(get("/test/illegal-state"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("État invalide"));
    }
}*/
