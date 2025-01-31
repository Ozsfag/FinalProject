package searchengine.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import searchengine.validators.URIValidator;

@Constraint(validatedBy = URIValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ComprehensiveUrlValidator {
  String message() default "Invalid host";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
