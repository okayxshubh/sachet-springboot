package in.gov.cybercrime.sachet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponse<T> {

    // Format: DD-MM-YYYY 24hr format
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    private String status;   // OK / ERROR
    private String message;  // Msg..
    private T data; // Data Array

    public static <T> GenericResponse<T> ok(T data) {
        return GenericResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status("OK")
                .message("SUCCESS")
                .data(data)
                .build();
    }

    public static <T> GenericResponse<T> ok(String msg, T data) {
        return GenericResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status("OK")
                .message(msg)
                .data(data)
                .build();
    }

    public static <T> GenericResponse<T> fail(String msg) {
        return GenericResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status("ERROR")
                .message(msg)
                .data(null)
                .build();
    }
}
