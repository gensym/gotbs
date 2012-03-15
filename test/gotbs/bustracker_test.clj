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
            (fake (bt/bustracker-url "routes" anything {}) => fake-url)
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


(test/deftest should-fetch-vehicles-on-route
  (let [fake-url (Object.)]
    (expect (fetch-vehicles-on-route-data "56")  => [
                                                     {:pdist "0", :des "Jefferson Park Blue Line", :rt "56", :pid "1970", :hdg "269", :lon "-87.62567138671875", :lat "41.882389068603516", :tmstmp "20101023 16:20", :vid "6583"}
                                                     {:pdist "8507", :des "Jefferson Park Blue Line", :rt "56", :pid "1970", :hdg "312", :lon "-87.64712356939549", :lat "41.89094803972942", :tmstmp "20101023 16:20", :vid "1192"}
                                                     {:pdist "17380", :des "Jefferson Park Blue Line", :rt "56", :pid "1970", :hdg "310", :lon "-87.67201232910156", :lat "41.90691724030868", :tmstmp "20101023 16:20", :vid "1220"}
                                                     {:pdist "27267", :des "Jefferson Park Blue Line", :rt "56", :pid "1970", :hdg "313", :lon "-87.69990354968655", :lat "41.92452209226547", :tmstmp "20101023 16:20", :vid "6818"}
                                                     {:pdist "30889", :des "Jefferson Park Blue Line", :rt "56", :pid "1970", :hdg "309", :lon "-87.7103894990066", :lat "41.930946350097656", :tmstmp "20101023 16:20", :vid "6576"}
                                                     {:pdist "41770", :des "Jefferson Park Blue Line", :rt "56", :pid "1970", :hdg "310", :lon "-87.74128446907832", :lat "41.949793125021046", :tmstmp "20101023 16:20", :vid "6788"}
                                                     {:pdist "48132", :des "Jefferson Park Blue Line", :rt "56", :pid "1970", :hdg "328", :lon "-87.75634190807604", :lat "41.96281412203018", :tmstmp "20101023 16:20", :vid "6808"}
                                                     {:pdist "2045", :des "Madison/Wabash", :rt "56", :pid "1971", :hdg "146", :lon "-87.7587793284449", :lat "41.96516589460702", :tmstmp "20101023 16:20", :vid "1207"}
                                                     {:pdist "8651", :des "Madison/Wabash", :rt "56", :pid "1971", :hdg "128", :lon "-87.7436500787735", :lat "41.95113521814346", :tmstmp "20101023 16:20", :vid "6812"}
                                                     {:pdist "18186", :des "Madison/Wabash", :rt "56", :pid "1971", :hdg "130", :lon "-87.71670935288915", :lat "41.934690583427", :tmstmp "20101023 16:20", :vid "6804"}
                                                     {:pdist "28437", :des "Madison/Wabash", :rt "56", :pid "1971", :hdg "133", :lon "-87.68761510434358", :lat "41.91691456670346", :tmstmp "20101023 16:20", :vid "6766"}
                                                     {:pdist "49928", :des "Madison/Wabash", :rt "56", :pid "1971", :hdg "89", :lon "-87.62958908081055", :lat "41.88336181640625", :tmstmp "20101023 16:20", :vid "6570"}]
            (fake
             (bt/bustracker-url "vehicles" anything {"rt" "56"}) =>
             fake-url)
            (fake (bt/fetch-url fake-url) =>
                  (slurp "resources/test-data/vehicles-on-route-56.xml")))))
