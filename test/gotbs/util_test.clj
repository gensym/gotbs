(ns gotbs.util-test
  (:use [gotbs.util] :reload-all)
  (:use [clojure.test]))

(deftest should-pass-through-chunk-of-one
  (is (= (list 2 4 6) (chunk-func 1 #(* % 2) [1 2 3]))))

(deftest should-chunk-size-2
  (is
   (= 
    (list 3 7 5)
    (chunk-func 2 + [1 2 3 4 5]))))


