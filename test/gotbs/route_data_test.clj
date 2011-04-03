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

(test/deftest should-get-pattern-points-from-pretty-name
  (expect (route-pattern-points "56 - Milwaukee" "North Bound") =>
          [ {:lat  41.91 :lon  -87.67}
            {:lat  41.88 :lon  -87.62}]
          (fake (bustracker/fetch-routes) =>
                (list {:rt "56" :rtnm "Milwaukee"}))
          (fake (bustracker/fetch-pattern-data-for-route "56" "North Bound") =>
                (list
                 {:pdist "19452.0",
                  :stpnm "Milwaukee & North Avenue/Damen",
                  :stpid "15847",
                  :typ "S",
                  :lon "-87.67",
                  :lat "41.91",
                  :seq "35"}
                 {:pdist "12345.0",
                  :stpnm "Some other stop",
                  :stpid "42",
                  :typ "S",
                  :lon "-87.62",
                  :lat "41.88"}))))

(test/deftest should-get-route-direction-from-pretty-name)
(expect (route-direction "56 - Milwaukee") => ["North Bound" "South Bound"]
        (fake (bustracker/fetch-routes) =>
                (list
                 {:rt "50",
                  :rtnm "Damen" }
                 {:rt "56"
                  :rtnm "Milwaukee"}))
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
