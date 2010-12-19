(ns gotbs.busdata-test
  (:use [gotbs.busdata] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]
            [gotbs [bustracker :as bustracker]]))

(test/deftest should-fetch-stop-pdist
  (expect (stop-pdist "56" "North Bound" "15847") => 19452
          (fake (bustracker/fetch-pattern-data-for-route "56" "North Bound") =>
                (list
                 {:pdist "19452.0",
                  :stpnm "Milwaukee & North Avenue/Damen",
                  :stpid "15847",
                  :typ "S",
                  :lon "-87.677861452103",
                  :lat "41.910716957663",
                  :seq "35"}
                 {:pdist "12345.0",
                  :stpnm "Some other stop",
                  :stpid "42",
                  :typ "S",
                  :lon "-23",
                  :lat "23"}))
          ))

(test/deftest should-get-vehicle-direction
  (let [vehicle
        {:pid "1970"}]
    (expect
     (vehicle-direction vehicle) => "North Bound"
     (fake (bustracker/fetch-pattern-data-by-id "1970") => {:rtdir "North Bound"}))))

(test/deftest should-get-inflight-vehicles
  (expect
   (in-flight-vehicles 56 "North Bound") => (list
                                             {:pdist "23449",
                                              :des "Jefferson Park Blue Line",
                                              :rt "56",
                                              :pid "1970",
                                              :hdg "321",
                                              :lon "-87.68906578650841",
                                              :lat "41.91789069542518",
                                              :tmstmp "20101126 11:37",
                                              :vid "1219"}
                                             {:pdist "37076",
                                              :des "Jefferson Park Blue Line",
                                              :rt "56",
                                              :pid "1970",
                                              :hdg "311",
                                              :lon "-87.72772837493379",
                                              :lat "41.94155812667588",
                                              :tmstmp "20101126 11:37",
                                              :vid "6789"})
   (fake ( bustracker/fetch-vehicles-on-route-data 56) => (list
                                                           {:pdist "23449",
                                                            :des "Jefferson Park Blue Line",
                                                            :rt "56",
                                                            :pid "1970",
                                                            :hdg "321",
                                                            :lon "-87.68906578650841",
                                                            :lat "41.91789069542518",
                                                            :tmstmp "20101126 11:37",
                                                            :vid "1219"}
                                                           {:pdist "37076",
                                                            :des "Jefferson Park Blue Line",
                                                            :rt "56",
                                                            :pid "1970",
                                                            :hdg "311",
                                                            :lon "-87.72772837493379",
                                                            :lat "41.94155812667588",
                                                            :tmstmp "20101126 11:37",
                                                            :vid "6789"}
                                                           {:pdist "7214",
                                                            :des "Madison/Wabash",
                                                            :rt "56",
                                                            :pid "1971",
                                                            :hdg "138",
                                                            :lon "-87.74778226216634",
                                                            :lat "41.95360596974691",
                                                            :tmstmp "20101126 11:37",
                                                            :vid "6584"}
                                                           {:pdist "23289",
                                                            :des "Madison/Wabash",
                                                            :rt "56",
                                                            :pid "1971",
                                                            :hdg "127",
                                                            :lon "-87.70233852580442",
                                                            :lat "41.925900475453524",
                                                            :tmstmp "20101126 11:37",
                                                            :vid "6749"}))
   (fake (bustracker/fetch-pattern-data-by-id "1970") => {:rtdir "North Bound",
                                                          :ln "51451.0"
                                                          :pid "1970"})
   (fake (bustracker/fetch-pattern-data-by-id "1971") => {:rtdir "South Bound",
                                                          :ln "51451.0"
                                                          :pid "1971"})))

(test/deftest should-fetch-vehicles-before-stop
  (expect (fetch-vehicles-before-stop "56" "North Bound" "15847") =>
          (list
           {:pdist "23449",
            :rt "56",
            :pid "1970",
            :vid "1219"}
           {:pdist "37076",
            :rt "56",
            :pid "1970",
            :vid "6789"})
          (fake (stop-pdist "56" "North Bound" "15847") => 40000)
          (fake (in-flight-vehicles "56" "North Bound") => (list
                                             {:pdist "23449",
                                              :rt "56",
                                              :pid "1970",
                                              :vid "1219"}
                                             {:pdist "37076",
                                              :rt "56",
                                              :pid "1970",
                                              :vid "6789"}))))

(test/deftest should-fetch-vehicles-past-stop
  (expect (fetch-vehicles-past-stop "56" "North Bound" "15847") =>
          (list
           {:pdist "37076"
            :rt "56"
            :pid "1970"
            :vid "6789"})
          (fake (stop-pdist "56" "North Bound" "15847") => 30000)
          (fake (in-flight-vehicles "56" "North Bound") => (list
                                                            {:pdist "23449",
                                                             :rt "56",
                                                             :pid "1970",
                                                             :vid "1219"}
                                                            {:pdist "37076",
                                                             :rt "56"
                                                             :pid "1970"
                                                             :vid "6789"}))))

(test/deftest should-fetch-vehicles-between-stops
  (expect (fetch-vehicles-between-stops "56" "North Bound" "stop-1" "stop-2") =>
          (list
           {:pdist "37067"
            :rt "56"
            :pid "1970"
            :vid "6789"}
           {:pdist "25427"
            :rt "56"
            :pid "1970"
            :vid "3425"})
          (fake (stop-pdist "56" "North Bound" "stop-1") => 25000)
          (fake (stop-pdist "56" "North Bound" "stop-2") => 38000)
          (fake (in-flight-vehicles "56" "North Bound") => (list
                                                            {:pdist "40000"
                                                             :rt "56"
                                                             :pid "1970"
                                                             :vid "6789"}
                                                            {:pdist "37067"
                                                             :rt "56"
                                                             :pid "1970"
                                                             :vid "6789"}
                                                            {:pdist "25427"
                                                             :rt "56"
                                                             :pid "1970"
                                                             :vid "3425"}
                                                            {:pdist "20000"
                                                             :rt "56"
                                                             :pid "1970"
                                                             :vid "9999"}))))
