(ns gotbs.util.carousel-test
  (:use [gotbs.util.relational] :reload-all
        [midje.semi-sweet])
  (:require [clojure [test :as test]]))


(test/deftest should-extend-rel
  (expect
   (extend-rel
    :foo
    #(+ (:a %) (:b %))
    '({:a 1 :b 1}, {:a 2, :b 3})) =>
    '({:foo 2, :a 1, :b 1} {:foo 5, :a 2, :b 3})))