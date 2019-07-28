package test.com.homeaway.models;

import java.util.List;

public class SearchVenueResponseWrapper {

    public Response response;

    public class Response {
        public List<Venue> venues;
    }
}
