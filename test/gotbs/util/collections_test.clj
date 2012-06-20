(ns gotbs.util.collections-test
  (:use [gotbs.util.collections] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]))

(test/deftest flex-partition-should-partition-by-arbitrary-function
  (expect
   (flex-partition
    (fn [x] (or (= 0 (mod x 3))
                (= 0 (mod x 5)))) (range 11)) => '((0 1 2) (3 4) (5) (6 7 8) (9) (10))))