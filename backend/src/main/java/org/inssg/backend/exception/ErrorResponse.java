package org.inssg.backend.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ErrorResponse {

    private int status;
    private String message;
    private List<FieldError> fieldErrors;
    private List<ConstraintViolationError> violationErrors;

    @Builder(builderMethodName = "businessExceptionBuilder")
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @Builder
    private ErrorResponse(List<FieldError> fieldErrors, List<ConstraintViolationError> violationErrors) {
        this.fieldErrors = fieldErrors;
        this.violationErrors = violationErrors;
    }

    public static ErrorResponse of(BindingResult bindingResult) {
        return ErrorResponse.builder()
                .fieldErrors(FieldError.of(bindingResult))
                .build();
    }

    public static ErrorResponse of(Set<ConstraintViolation<?>> violations) {
        return ErrorResponse.builder()
                .violationErrors(ConstraintViolationError.of(violations))
                .build();
    }

    public static ErrorResponse of(ExceptionCode exceptionCode) {
        return ErrorResponse.businessExceptionBuilder()
                .status(exceptionCode.getStatus())
                .message(exceptionCode.getMessage())
                .build();
    }

    public static ErrorResponse of(HttpStatus httpStatus, String errorMessage) {
        return ErrorResponse.businessExceptionBuilder()
                .status(httpStatus.value())
                .message(errorMessage)
                .build();
    }
    public static ErrorResponse of(HttpStatus httpStatus) {
        return new ErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase());
    }


    @Getter
    public static class FieldError{
        private String field;
        private Object rejectedValue;
        private String reason;

        @Builder
        private FieldError(String field, Object rejectedValue, String reason) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<FieldError> of(BindingResult bindingResult) {
            List<org.springframework.validation.FieldError> fieldErrorList = bindingResult.getFieldErrors();

            return fieldErrorList.stream()
                    .map(error -> FieldError.builder()
                            .field(error.getField())
                            .rejectedValue(error.getRejectedValue() == null ? "" : error.getRejectedValue().toString())
                            .reason(error.getDefaultMessage())
                            .build())
                    .collect(Collectors.toList());
        }

    }
    @Getter
    public static class ConstraintViolationError {
        private String propertyPath;
        private Object rejectedValue;
        private String reason;

        @Builder
        private ConstraintViolationError(String propertyPath, Object rejectedValue, String reason) {
            this.propertyPath = propertyPath;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<ConstraintViolationError> of(Set<ConstraintViolation<?>> constraintViolations) {
            return constraintViolations.stream()
                    .map(constraintViolation -> ConstraintViolationError.builder()
                            .propertyPath(constraintViolation.getPropertyPath().toString())
                            .rejectedValue(constraintViolation.getInvalidValue().toString())
                            .reason(constraintViolation.getMessage())
                            .build())
                    .collect(Collectors.toList());
        }
    }
}
