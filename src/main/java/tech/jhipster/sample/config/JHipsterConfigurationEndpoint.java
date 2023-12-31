package tech.jhipster.sample.config;

import io.micronaut.context.env.PropertySourcePropertyResolver;
import io.micronaut.core.convert.format.MapFormat;
import io.micronaut.core.naming.conventions.StringConvention;
import io.micronaut.management.endpoint.annotation.Endpoint;
import io.micronaut.management.endpoint.annotation.Read;
import java.util.Map;
import java.util.regex.Pattern;

@Endpoint("configprops")
public class JHipsterConfigurationEndpoint {

    private final PropertySourcePropertyResolver propertyResolver;
    private final Pattern maskPattern;

    JHipsterConfigurationEndpoint(PropertySourcePropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
        this.maskPattern = Pattern.compile(".*(password|credential|certificate|key|secret|token).*", Pattern.CASE_INSENSITIVE);
    }

    @Read
    Map<String, Object> getProperties() {
        Map<String, Object> properties = propertyResolver.getAllProperties(StringConvention.RAW, MapFormat.MapTransformation.FLAT);
        properties
            .entrySet()
            .stream()
            .filter(entry -> maskPattern.matcher(entry.getKey()).matches())
            .forEach(entry -> entry.setValue("****"));
        return properties;
    }
}
