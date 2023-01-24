package net.lpo.CountryRouter.controller;

import net.lpo.CountryRouter.dto.Route;
import net.lpo.CountryRouter.tools.CountryService;
import net.lpo.CountryRouter.tools.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class RoutingController {

    private static final Logger log = LoggerFactory.getLogger(RoutingController.class);

    @GetMapping("/routing/{origin}/{destination}")
    public ResponseEntity<Route> routing(@PathVariable String origin, @PathVariable String destination) {
        log.info("origin: {}, destination: {}, depth level: {}", origin, destination, Router.MAX_DEEP);
        // TODO: handle cash if needed to speed up
        Map<String, Set<String>> countriesMap = CountryService.loadCountryMap();
        if (origin.equals(destination) || !countriesMap.containsKey(origin) || !countriesMap.containsKey(destination)) {
            log.error("origin/destination does not exists or are identical");
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }

        List<String> route = Router.searchRoute(origin, destination, countriesMap);
        if (route == null)  {
            log.error("route was not one or both countries has no land borders");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else if (route.size() < 2) {
            log.error("route was not found with given deepness level");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        log.info("route: {}", route);
        return ResponseEntity.ok(new Route(route));
    }
}
