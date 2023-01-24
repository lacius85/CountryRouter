package net.lpo.CountryRouter.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Router {

    private static final Logger log = LoggerFactory.getLogger(Router.class);

    private static final int MIN_DEEP = 2;
    public static final int MAX_DEEP = 20; // no longer road than 15 was found in rough test, 20 should be enough

    public static List<String> searchRoute(String origin, String destination, Map<String, Set<String>> borders ) {
        // check solitary states
        if (borders.get(origin).isEmpty() || borders.get(destination).isEmpty()) {
            return null;
        }
        List<String> route = new ArrayList<>(List.of(origin));
        // check if direct neighbour
        if (borders.get(route.get(lastIndexOf(route))).contains(destination)) {
            route.add(destination);
            return route;
        }
        // check if common neighbour exists
        Set<String> destinationBorders = borders.get(destination);
        String commonNeighbour = borders.get(route.get(lastIndexOf(route))).stream().filter(destinationBorders::contains).findFirst().orElse(null);
        if (commonNeighbour != null) {
            route.add(commonNeighbour);
            route.add(destination);
            return route;
        }
        // find longer route
        for (int depthLevel = MIN_DEEP; depthLevel <= MAX_DEEP; depthLevel++) {
            log.debug("searching in depth level: {}", depthLevel);
            List<String> result = findNewRoute(route, destination, borders, new HashSet<>(), depthLevel);
            if (!result.isEmpty()) {
                log.info("Result found in level {}", depthLevel);
                return result;
            }
        }
        // no suitable route found for given conditions
        return new ArrayList<>();
    }

    public static List<String> findNewRoute(List<String> route, String destination, Map<String, Set<String>> borders, Set<String> deadEnd, int maxLevel) {
        if (route.size() >= maxLevel) {
            //deadEnd.addAll(borders.get(route.get(lastIndexOf(route))));
            return new ArrayList<>();
        }
        // search neighbours
        for (String borderCountry : borders.get(route.get(lastIndexOf(route)))) {
            // route found
            if (borders.get(borderCountry).contains(destination)) {
                route.add(borderCountry);
                route.add(destination);
                return route;
            }
        }
        // search deeper
        for (String borderCountry : borders.get(route.get(lastIndexOf(route)))) {
            if (route.contains(borderCountry) || deadEnd.contains(borderCountry)) {
                continue;
            }
            List<String> newRoute = new ArrayList<>(route);
            Set<String> roadEnds = new HashSet<>(deadEnd);
            newRoute.add(borderCountry);
            List<String> result = findNewRoute(newRoute, destination, borders, deadEnd, maxLevel);
            if (result.isEmpty()) {
                roadEnds.add(borderCountry);
                return findNewRoute(route, destination, borders, roadEnds, maxLevel);
            } else if (result.contains(destination)) {
                return result; // result was found!
            }
        }
        // no suitable route found for given conditions
        return new ArrayList<>();
    }

    private static int lastIndexOf(List<String> list) {
        return list.size() - 1;
    }
}
