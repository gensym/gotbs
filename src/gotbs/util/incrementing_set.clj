(ns gotbs.util.incrementing-set)

(defn is-inc [counts-by-item item]
  (assoc counts-by-item item
         (inc (or (get counts-by-item item) 0))))

(defn is-dec [counts-by-item item]
  (let [newval (dec (get counts-by-item item))]
    (if (> newval 0) (assoc counts-by-item item newval) (dissoc counts-by-item item))))

(defn is-items [counts-by-item]
  (keys counts-by-item))

(defn is-make [] {})
