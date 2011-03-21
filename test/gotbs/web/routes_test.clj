(ns gotbs.web.routes-test
  (:use [gotbs.web.routes] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]
            [gotbs [bustracker :as bustracker]]))

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
