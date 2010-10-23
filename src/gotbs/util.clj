(ns gotbs.util)

(defn chunk-func [chunk-size func coll]
  "Take a function an a collection. Apply that function to the collection in groups of chunk-size and return the concatenated result. For example, (chunk-func f 2 [1 2 3 4 5]) would return: (concat (f [1 2]), (f [3 4]), (f [5]))"
  (map #(apply func %) (partition-all chunk-size coll)))

(defn chunk-fun)

