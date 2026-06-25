package be.profacile.savefunds.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO standard pour les réponses d'erreur API
 *
 * Utilisé par GlobalExceptionHandler
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private int status;
    private LocalDateTime timestamp;
    private List<String> errors;

    /**
     * Constructeur pour erreur simple
     */
    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructeur pour erreurs de validation
     */
    public ErrorResponse(String message, int status, List<String> errors) {
        this.message = message;
        this.status = status;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}