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
