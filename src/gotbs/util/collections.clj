(ns gotbs.util.collections)

(defn flex-partition
  "Returns a lazy sequence of lists. Takes the items in coll
and applies f to them. If f returns true, starts a new group of items.
Otherwise, the item will be added to the last group"
  ([f coll]
     (lazy-seq
      (when-let [s (first coll)]
        (let [fst (first coll)
              run (cons fst (take-while (complement f) (next coll)))]
          (cons run (flex-partition f (seq (drop (count run) coll)))))))))

