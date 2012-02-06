(ns gotbs.bustracker-test
  (:use [gotbs.bustracker] :reload-all
        [midje.semi-sweet])
  (:require [clojure.test :as test]
            [gotbs.cta.bustracker-api :as bt]))

(test/deftest should-fetch-stop-data-for-route
  (let [fake-url (Object.)]
    (expect
     (first (fetch-stop-data "56" "North Bound")) =>   {:stpid "5520"
                                                        :stpnm "Fulton & Desplaines"
                                                        :lat "41.886871516554"
                                                        :lon "-87.644065618515"}
     (fake
      (bt/bustracker-url "stops" anything {"rt" "56", "dir" "North Bound"}) => fake-url)
     (fake
      (bt/fetch-url fake-url) =>  (slurp "resources/test-data/stop-data-56-nb.xml")))))

(test/deftest should-fetch-all-routes
  (let [fake-url (Object.)]
    (expect (first (fetch-routes)) => {:rtnm "Indiana/Hyde Park"
                                       :rt "1"}
            (fake (bt/bustracker-url "routes" anything) => fake-url)
            (fake (bt/fetch-url fake-url) =>  (slurp "resources/test-data/routes.xml")))))

(test/deftest should-fetch-route-direction
  (let [fake-url (Object.)]
    (expect (fetch-route-direction "56") =>  ["North Bound" "South Bound"]
            (fake (bt/bustracker-url "directions" anything {"rt" "56"}) => fake-url)
            (fake (bt/fetch-url fake-url) =>
                  (slurp "resources/test-data/route-direction-56.xml")))))

(test/deftest should-fetch-pattern-data-by-route
  (let [fake-url (Object.)]
    (expect (take 3
                  (fetch-pattern-data-for-route "56" "North Bound")) =>
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
                    :typ "W"})
                  (fake
                   (bt/bustracker-url "patterns" anything {"rt" "56"}) =>
                   fake-url)
                  (fake
                   (bt/fetch-url fake-url) =>
                   (slurp "resources/test-data/pattern-data-for-route-56.xml")))))

(test/deftest should-fetch-pattern-data-by-id
  (let [fake-url (Object.)]
    (expect (fetch-pattern-data-by-id "1970") => {:rtdir "North Bound",
                                                  :ln "51451.0"
                                                  :pid "1970"}
            (fake
             (bt/bustracker-url "patterns" anything {"pid" "1970"}) =>
             fake-url)
            (fake
             (bt/fetch-url fake-url) =>
              (slurp "resources/test-data/pattern-data-by-id-1970.xml")))))

