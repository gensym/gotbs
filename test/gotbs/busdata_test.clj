(ns gotbs.busdata-test
  (:use [gotbs.busdata] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]
            [gotbs [bustracker :as bustracker]]))

(declare  another-fake)

(defn function-under-test-3 []
  (+ (bustracker/fetch-pattern-data-by-id 1) (bustracker/fetch-pattern-data-by-id 2 2) (another-fake)))

(test/deftest example-of-multiple-faked-functions
  (expect (function-under-test-3) => 111
     (fake (bustracker/fetch-pattern-data-by-id 1) => 1)
     (fake (bustracker/fetch-pattern-data-by-id 2 2) => 10)
     (fake (another-fake) => 100)))

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

;; (defn ignore []
;;      (test/deftest should-fetch-vehicles-before-stop
;;        (expect
;;         [stop-pdist (returns 19452.0)]
;;         (test/is
;;          (=
;;           (list 1 2 3)
;;           (list 1 2 3))))))


