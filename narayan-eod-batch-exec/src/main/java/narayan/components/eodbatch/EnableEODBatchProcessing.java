package narayan.components.eodbatch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enabler annotation to be applied / used in client modules.
 *
 * @author narayana
 *
 */
@Configuration
@ComponentScan
@Import({EODBatchContext.class, EODBatchScheduler.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableEODBatchProcessing {

}
