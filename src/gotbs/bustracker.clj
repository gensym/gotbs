(ns gotbs.bustracker
  (:use
   gotbs.cta.bustracker-api
   [clojure.tools.logging :as log]
   clojure.java.io (input-stream))
  (:require
            [gotbs.util.http-utils :as http]))

;; BEGIN HARDCODING

(def http-api (make-bustracker (System/getenv "CTA_BUSTRACKER_API_KEY")))

(def west-bound "West Bound")

(def north-bound "North Bound")

(def milwaukee-and-north-avenue-damen-56 "15847")

(def milwaukee-and-talman-56 "5552")

(def madison-and-clinton-56 "455")

(def destination "Jefferson Park Blue Line")
;; END HARDCODING

(defn content-xml-to-map [xml]
  "Takes a seq in the form of [{:tag :a :content [\"something\"]} {:tag b :content [\"else\"]}] and turns it into a map, with the value of the :tag as the keys and the values of :content as values. If the value of :content is a list, takes the first item off of that list."
  (let [
	xml-to-map
	(fn [coll tag-attrs]
	  (assoc 
	      coll 
	    (:tag tag-attrs) 
	    (let [content (:content tag-attrs)]
	      (if (> (count content) 1)
		content
		(first content)))))]
    (reduce xml-to-map {} xml)))


(defn filter-tag [tag-name s]
  (filter #(= (:tag %) tag-name) s))

(defn remove-tag [tag-name s]
  (remove #(= (:tag %) tag-name) s))

(defn fetch-stop-data [route direction]
  (map
   content-xml-to-map
   (map
    :content 
    (filter-tag
     :stop
     (:content
      (stops http-api route direction))))))

(defn stop-id [route direction stop-name]
  (first (map :stpid (filter #(= (:stpnm %1) stop-name) (fetch-stop-data route direction)))))

(defn stop-name [route direction stop-id]
     (first (map :stpnm (filter #(= (:stpid %) stop-id) (fetch-stop-data route direction)))))

(let [construct-vehicle-data
      (fn [vehicle-data-xml]
	(map
	 content-xml-to-map
	 (map
	  :content
	  (filter-tag
	   :vehicle
	   (:content
            vehicle-data-xml)))))]
  
  (defn fetch-vehicles-on-route-data [route]
    (construct-vehicle-data (vehicles-on-route http-api route)))

  (defn fetch-vehicles-data [& vehicle_ids]
    (let [vids  (reduce #(str %1 "," %2) vehicle_ids)]
      (construct-vehicle-data (vehicle-by-id http-api vids)))))

(defn fetch-pattern-data-by-id [pattern-id]
  "Fetch data about the pattern by the pattern ID. Excludes waypoints"
  (content-xml-to-map
   (remove-tag
    :pt
    (first
     (map
      :content
      (filter-tag
       :ptr
       (:content
        (pattern-by-id http-api pattern-id))))))))

(defn fetch-pattern-data-for-route [route dir]
  "Fetch the pattern data (including waypoints) by the route and direction"
  (map
   content-xml-to-map
   (map
    :content
    (filter-tag
     :pt
     (first
      (filter
       #(= dir (first (:content (first (filter-tag :rtdir %)))))
       (map
	:content
	(filter-tag
	 :ptr
	 (:content
          (patterns-for-route http-api route))))))))))

(defn fetch-routes []
  (map
   content-xml-to-map
   (map
    :content
    (filter-tag
     :route
     (:content
      (all-routes http-api))))))

(defn fetch-route-direction [route]
  (flatten
   (map
    :content
    (filter-tag
     :dir
     (:content
      (directions http-api route))))))

(defn destination [route dir]
  (:stpnm (last (fetch-pattern-data-for-route route dir))))

(defn fetch-prediction-data [route stop-id]
  (map
   content-xml-to-map
   (map
    :content
    (filter-tag
     :prd
     (:content
      (predictions http-api route stop-id))))))

(defstruct prediction :route :direction :eta)

(defn make-prediction [bustracker-prediction]
  (struct-map
      prediction
      :route (:rt bustracker-prediction)
      :direction (:rtdir bustracker-prediction)
      :eta (last (re-seq #"\S+" (:prdtm bustracker-prediction)))))

(defn make-predictions [route stop-id]
  (map
   make-prediction
   (fetch-prediction-data route stop-id)))
