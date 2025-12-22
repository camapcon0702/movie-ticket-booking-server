package qnt.moviebooking.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import qnt.moviebooking.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleUsernameNotFound(UsernameNotFoundException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));

        }

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }

        @ExceptionHandler(ExistException.class)
        public ResponseEntity<ApiResponse<Void>> handleExist(ExistException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), e.getMessage(), null));
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException e) {
                return ResponseEntity.badRequest()
                                .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                "Lỗi hệ thống: " + e.getMessage(), null));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message("Dữ liệu không hợp lệ")
                                .data(errors)
                                .build();

                return ResponseEntity.badRequest().body(response);
        }

        
}
