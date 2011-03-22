(ns gotbs.route-data-test
  (:use [gotbs.route-data] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]
            [gotbs [bustracker :as bustracker]]))

;; get a route from a pretty name
(test/deftest should-get-route-from-pretty-name
  (expect (route-descriptor "56 - Milwaukee") => {:rt "56"
                                                  :rtnm "Milwaukee"}
          (fake (bustracker/fetch-routes) =>
                (list
                 {:rt "50",
                  :rtnm "Damen" }
                 {:rt "56"
                  :rtnm "Milwaukee"}))))

;; get a route direction from a pretty name

;; get a list of route pretty names (move from routes.clj)
(test/deftest should-get-display-names
  (expect (display-names) => ["50 - Damen" "56 - Milwaukee"]
          (fake (bustracker/fetch-routes) =>
                (list
                 {:rt "50",
                  :rtnm "Damen" }
                 {:rt "56"
                  :rtnm "Milwaukee"}))))
