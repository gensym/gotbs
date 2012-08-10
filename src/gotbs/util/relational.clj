(ns gotbs.util.relational)

(defn extend-rel [name f xrel]
  (map #(assoc % name (f %)) xrel))

(defn project-rel [names xrel]
  (map #(select-keys % names) xrel))

(defn rename-rel [from-name to-name xrel]
  (if (= from-name to-name)
    xrel
    (->> xrel
         (map #(assoc % to-name (get % from-name)))
         (map #(dissoc % from-name)))))

(defn add-seq [name xrel]
  (map-indexed (fn [idx itm] (assoc itm name idx)) xrel))