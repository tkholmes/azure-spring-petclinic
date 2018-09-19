package org.springframework.samples.petclinic.system;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
public class ShowSystem {

    @GetMapping(value = "/showsystem", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Properties showSystem() {
        /*
        final Map<String, String> map = new HashMap<>();
        Properties p = System.getProperties();
        Enumeration keys = p.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)p.get(key);
            map.put(key, value);
        }

        return map;
        */
        return System.getProperties();
    }

}
