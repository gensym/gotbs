(ns gotbs.bustracker-test
  (:use [gotbs.bustracker] :reload-all
        [clojure.contrib.mock.test-adapter])
  (:require [clojure [test :as test]]))

(test/deftest should-fetch-stop-data-for-route
  (expect
   [fetch-stop-data-xml
    (has-args ["56" "North Bound"]
              (returns (slurp "resources/test-data/stop-data-56-nb.xml")))]
   (test/is
    (=
     {:stpid "5520"
      :stpnm "Fulton & Desplaines"
      :lat "41.886871516554"
      :lon "-87.644065618515"}
     (first (fetch-stop-data "56" "North Bound"))))))

(test/deftest should-fetch-all-routes
  (expect
   [fetch-routes-data-xml
    (has-args []
              (returns (slurp "resources/test-data/routes.xml")))]
   (test/is
    (=
     {:rtnm "Indiana/Hyde Park"
      :rt "1"})
    (first (fetch-routes)))))

(test/deftest should-fetch-route-direction
  (expect
   [fetch-route-direction-xml
    (has-args ["56"]
              (returns (slurp "resources/test-data/route-direction-56.xml")))]
   (test/is
    (= ["North Bound" "South Bound"]
         (fetch-route-direction "56")))))

(test/deftest should-fetch-pattern-data-for-route
  (expect
   [fetch-pattern-data-by-id-xml
    (has-args ["1970"]
              (returns (slurp "resources/test-data/pattern-data-by-id-1970.xml")))]
   (test/is
    (= 
     {:rtdir "North Bound",
      :ln "51451.0"
      :pid "1970"}
     (fetch-pattern-data-by-id "1970")))))
