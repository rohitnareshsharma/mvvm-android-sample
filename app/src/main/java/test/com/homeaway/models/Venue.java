package test.com.homeaway.models;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

/**
 *
 * Venue represents one point of interest .
 * It is mapped according to following json format.
 *
 *
 *  {
 *             "id":"52d456c811d24128cdd7bc8b",
 *             "name":"Storyville Coffee Company",
 *             "contact":{
 *                "phone":"2067805777",
 *                "formattedPhone":"(206) 780-5777",
 *                "twitter":"storyville"
 *             },
 *             "location":{
 *                "address":"1001 1st Ave",
 *                "crossStreet":"at Madison St",
 *                "lat":47.60475923205166,
 *                "lng":-122.33636210125788,
 *                "labeledLatLngs":[
 *                   {
 *                      "label":"display",
 *                      "lat":47.60475923205166,
 *                      "lng":-122.33636210125788
 *                   }
 *                ],
 *                "postalCode":"98104",
 *                "cc":"US",
 *                "city":"Seattle",
 *                "state":"WA",
 *                "country":"United States",
 *                "formattedAddress":[
 *                   "1001 1st Ave (at Madison St)",
 *                   "Seattle, WA 98104",
 *                   "United States"
 *                ]
 *             },
 *             "categories":[
 *                {
 *                   "id":"4bf58dd8d48988d1e0931735",
 *                   "name":"Coffee Shop",
 *                   "pluralName":"Coffee Shops",
 *                   "shortName":"Coffee Shop",
 *                   "icon":{
 *                      "prefix":"https:\/\/ss3.4sqi.net\/img\/categories_v2\/food\/coffeeshop_",
 *                      "suffix":".png"
 *                   },
 *                   "primary":true
 *                }
 *             ],
 *             "verified":true,
 *             "stats":{
 *                "tipCount":43,
 *                "usersCount":1432,
 *                "checkinsCount":2443
 *             },
 *             "url":"https:\/\/www.facebook.com\/StoryvilleCoffee",
 *             "allowMenuUrlEdit":true,
 *             "beenHere":{
 *                "lastCheckinExpiredAt":0
 *             },
 *             "specials":{
 *                "count":0,
 *                "items":[
 *
 *                ]
 *             },
 *             "storeId":"",
 *             "hereNow":{
 *                "count":0,
 *                "summary":"Nobody here",
 *                "groups":[
 *
 *                ]
 *             },
 *             "referralId":"v-1523913911",
 *             "venueChains":[
 *                {
 *                   "id":"556e5ca0bd6a82902e297f0f"
 *                }
 *             ],
 *             "hasPerk":false
 *   }
 */
@Entity(tableName = "venues")
public class Venue {

    @PrimaryKey
    @NonNull public String id;

    public String name;

    @Ignore
    public Location location;

    @Ignore
    public List<Category> categories;

    @Ignore
    public boolean verified;

    @Ignore
    public Stat stats;

    @Ignore
    public String url;

    @Ignore
    public boolean allowMenuUrlEdit;

    @Ignore
    public BeenHere beenHere;

    @Ignore
    public Specials specials;

    @Ignore
    public String storeId;

    @Ignore
    public HereNow hereNow;

    @Ignore
    public String referralId;

    @Ignore
    public List<VenueChain> venueChains;

    @Ignore
    public boolean hasPerk;

    public boolean favourite;

    public class Location {

        public String address;

        public String crossStreet;

        public double lat;

        public double lng;

        public List<LabeledLatLngs> labeledLatLngs;

        public float distance;

        public String postalCode;

        public String cc;

        public String city;

        public String state;

        public String country;

        public String [] formattedAddress;

        public float getDistanceInKm() {
            return distance/1000;
        }
    }


    public class Category {
        public String id;

        public String name;

        public String pluralName;

        public String shortName;

        public Icon icon;

        public boolean primary;
    }


    public class Icon {

        public static final int DEFAULT_ICON_SIZE = 88;

        public String prefix;
        public String suffix;

        public String getUrl() {
            return prefix + DEFAULT_ICON_SIZE +suffix;
        }
    }


    public class LabeledLatLngs {
        public String label;
        public String lat;
        public String lng;
    }


    public class Stat {
        public String tipCount;
        public String usersCount;
        public String checkinsCount;
    }


    public class BeenHere {
        public String lastCheckinExpiredAt;
    }


    public class Specials {
        public String count;
        public String [] items;
    }


    public class HereNow {
         public String count;
         public String summary;
         public List<Group> groups;
    }

    public class Group {
        public String type;
        public String name;
        public String count;
    }

    public class VenueChain {
        public String id;
    }
}
