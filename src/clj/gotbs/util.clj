(ns gotbs.util)

(defn chunk-func [chunk-size func coll]
  "Take a function an a collection. Apply that function to the collection in groups of chunk-size and return the concatenated result. For example, (chunk-func f 2 [1 2 3 4 5]) would return: (concat (f [1 2]), (f [3 4]), (f [5]))"
  (map #(apply func %) (partition-all chunk-size coll)))

(defn wrap-with-input [func]
  "Return a function. That function with then take a single item of input and return a 2-item seq with the first item being the input and the second being the result of applying func to that input"
  (fn [x] (list x (func x))))


