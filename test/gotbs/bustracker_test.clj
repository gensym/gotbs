(ns gotbs.bustracker-test
  (:use [gotbs.bustracker] :reload-all
        [clojure.contrib.mock.test-adapter])
  (:require [clojure [test :as test]]))

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
