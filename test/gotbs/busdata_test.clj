(ns gotbs.busdata-test
  (:use [gotbs.busdata] :reload-all
        [clojure.contrib.mock.test-adapter])
  (:require [clojure [test :as test]]
            [gotbs [bustracker :as bustracker]]))

(test/deftest mocktest-should-work
  (expect [bustracker/stop-pdist (returns 123)]
          (test/is (= 123 (mock-test)))))

(test/deftest should-fetch-vehicles-before-stop
  (expect
   [bustracker/stop-pdist (returns 19452.0)]
   (test/is
    (=
     (list 1 2 3)
     (list 1 2 3)))))

(test/deftest should-work
  (test/is (= 2 2)))

