package test.com.homeaway.models;

import java.util.List;

public class SearchVenueResponseWrapper {

    public Meta meta;

    public Response response;

    public class Meta {
        public int code;
        public String requestId;
    }

    public class Response {
        public List<Venue> venues;
    }
}
