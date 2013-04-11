(ns gotbs.util.relational
  (:use [clojure.set :only [join]]))

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

(defn extract-rel [names xrel]
  (into #{} (project-rel names xrel)))

(defn extend-with-id-of-normalized [left left-id right right-id]
  "Returns the xrel on the left annotated with left-id, whose value is the corresponding value to right-if in the right xrel"
  (let [attrs (into #{} (mapcat keys left))]
    (->>
     (join left right)
     (project-rel (conj attrs right-id))
     (rename-rel right-id left-id))))

