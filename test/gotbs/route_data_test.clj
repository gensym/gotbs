(ns gotbs.route-data-test
  (:use [gotbs.route-data] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]
            [gotbs [bustracker :as bustracker]]))

(test/deftest should-get-route-from-pretty-name
  (expect (route-descriptor "56 - Milwaukee") => {:rt "56"
                                                  :rtnm "Milwaukee"}
          (fake (bustracker/fetch-routes) =>
                (list
                 {:rt "50",
                  :rtnm "Damen" }
                 {:rt "56"
                  :rtnm "Milwaukee"}))))

(test/deftest should-get-route-direction-from-pretty-name)
(expect (route-direction "56") => ["North Bound" "South Bound"]
        (fake (bustracker/fetch-route-direction "56") =>
              ["North Bound" "South Bound"]))

(test/deftest should-get-display-names
  (expect (display-names) => ["50 - Damen" "56 - Milwaukee"]
          (fake (bustracker/fetch-routes) =>
                (list
                 {:rt "50",
                  :rtnm "Damen" }
                 {:rt "56"
                  :rtnm "Milwaukee"}))))

(test/deftest should-prettify-routes-data
  (expect (matching-routes "5") => ["5 - South Shore Night Bus" "50 - Damen" "56 - Milwaukee"]
          (fake (bustracker/fetch-routes) =>
                (list
                 {:rtnm "Indiana/Hyde Park"
                  :rt "1"}
                 {:rtnm "South Shore Night Bus"
                  :rt "5"}
                 {:rtnm "Jackson Park Express"
                  :rt "6"}
                 {:rtnm "Damen"
                  :rt "50"}
                 {:rtnm "Milwaukee"
                  :rt "56"}))))

(test/deftest should-ignore-case-when-matching-routes
  (expect (matching-routes "d") => ["1 - Indiana/Hyde Park" "50 - Damen"]
          (fake (bustracker/fetch-routes) =>
                (list
                 {:rtnm "Indiana/Hyde Park"
                  :rt "1"}
                 {:rtnm "South Shore Night Bus"
                  :rt "5"}
                 {:rtnm "Jackson Park Express"
                  :rt "6"}
                 {:rtnm "Damen"
                  :rt "50"}
                 {:rtnm "Milwaukee"
                  :rt "56"}))))

(test/deftest should-fetch-waypoints-for-route
  (expect (waypoints "56" "North Bound") => (list
                                             {:lat 41.882130914862 :lon -87.625998258591}
                                             {:lat 41.882118933114 :lon -87.62768805027}
                                             {:lat 41.882063018264 :lon -87.629334926605})
  
          (fake (bustracker/fetch-pattern-data-for-route "56" "North Bound") =>
                     (list
                      {:seq "1"
                       :lat "41.882130914862"
                       :lon "-87.625998258591"
                       :typ "S"
                       :stpid "450"
                       :stpnm "Madison & Wabash"
                       :pdist "0.0"
                       },
                      {:seq "2"
                       :lat "41.882118933114",
                       :lon "-87.62768805027"
                       :typ "S"
                       :stpid "4729",
                       :stpnm "Madison & State"
                       :pdist "654.0"},
                      {:seq "3"
                       :lat "41.882063018264",
                       :lon "-87.629334926605",
                       :typ "W"}))))
