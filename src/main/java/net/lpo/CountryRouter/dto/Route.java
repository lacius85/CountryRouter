package net.lpo.CountryRouter.dto;

import java.util.List;
import java.util.Objects;

public class Route {
    List<String> route;

    public Route() {

    }

    public Route(List<String> route) {
        this.route = route;
    }

    public List<String> getRoute() {
        return route;
    }

    public void setRoute(List<String> route) {
        this.route = route;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route1 = (Route) o;

        return Objects.equals(route, route1.route);
    }

    @Override
    public int hashCode() {
        return route != null ? route.hashCode() : 0;
    }
}
