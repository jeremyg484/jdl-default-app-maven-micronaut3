package tech.jhipster.sample.config;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class CorsController {

    @Post("/api/test-cors")
    public void testCorsOnApiPath() {}

    @Get("/test/test-cors")
    public void testCorsOnOtherPath() {}
}
