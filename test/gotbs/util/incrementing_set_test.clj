(ns gotbs.util.incrementing-set-test
  (:use [gotbs.util.incrementing-set] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]))

(test/deftest should-contain-incremented-items
  (let [set (is-inc (is-make) 4)]
    (expect (is-items set) => '(4))
    (expect (contains? set 4) => true)
    (expect (contains? set 5) => false)))

(test/deftest should-not-contain-items-decremented-to-zero
  (let [s1 (is-make)
        s2 (is-inc s1 5)
        s3 (is-inc s2 4)
        s4 (is-inc s3 4)]
    (expect (is-items (is-dec s3 4)) => '(5))
    (expect (is-items (is-dec s4 4)) => '(4 5))
    (expect (is-items (is-dec (is-dec s4 4) 4)) => '(5))))
