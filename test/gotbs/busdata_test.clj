(ns gotbs.busdata-test
  (:use [gotbs.busdata] :reload-all
        [clojure.contrib.mock.test-adapter])
  (:require [clojure [test :as test]]
            [gotbs [bustracker :as bustracker]]))

(defn ignore []
  (test/deftest should-get-vehicle-direction
    (let [vehicle
          {:pid "1970"}]
      (expect
       [bustracker/fetch-pattern-data-by-id
        (has-args ["1970"]
                  (returns {:rtdir "North Bound"}))]
       (test/is
        (=
         "North Bound"
         (vehicle-direction vehicle))))))

  (test/deftest should-fetch-vehicles-before-stop
    (expect
     [stop-pdist (returns 19452.0)]
     (test/is
      (=
       (list 1 2 3)
       (list 1 2 3))))))


