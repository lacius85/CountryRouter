package net.lpo.CountryRouter.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lpo.CountryRouter.dto.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component
@EnableCaching
public class CountryService {

    private static final Logger log = LoggerFactory.getLogger(CountryService.class);

    private static final String DATA_URL = "https://raw.githubusercontent.com/mledoze/countries/master/countries.json";

    @Cacheable("countries")
    public static Map<String, Set<String>> loadCountryMap() {
        Map<String, Set<String>> countriesMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode json = getJson();
            for (Iterator<JsonNode> it = json.elements(); it.hasNext(); ) {
                JsonNode currentNode = it.next();
                Country current = mapper.treeToValue(currentNode, Country.class);
                countriesMap.put(current.getCca3(), current.getBorders());
            }
        } catch (IOException ioe) {
            log.error("IOException " + ioe.getMessage());
        }
        return countriesMap;
    }

    public static JsonNode getJson() throws IOException {
        URL url = new URL(DATA_URL);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(url);
    }
}
