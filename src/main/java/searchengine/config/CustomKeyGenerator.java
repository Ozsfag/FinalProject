package searchengine.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class CustomKeyGenerator implements KeyGenerator {

    /**
     * Generates a key based on the target class name, method name, and parameters.
     *
     * @param  target     the target object
     * @param  method     the method being called
     * @param  params     the parameters passed to the method
     * @return            the generated key
     */
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getSimpleName() + " " +  StringUtils.arrayToDelimitedString(params, "_");
    }
}
