(ns gotbs.cta.bustracker-data-parser
  (:require [clojure.xml :as xml])
  (:import (java.io StringBufferInputStream)))

(defn- parse-xml [s] (xml/parse (StringBufferInputStream. s)))

(defn- content-xml-to-map [parsed-xml]
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
    (reduce xml-to-map {} parsed-xml)))

(defn- filter-tag [tag-name s]
  (filter #(= (:tag %) tag-name) s))

(defn- remove-tag [tag-name s]
  (remove #(= (:tag %) tag-name) s))

(defn as-stop-data [xml]
  (map
   content-xml-to-map
   (map
    :content 
    (filter-tag
     :stop
     (:content (parse-xml xml))))))

(defn as-vehicle-data [xml]
  	(map
	 content-xml-to-map
	 (map
	  :content
	  (filter-tag
	   :vehicle
	   (:content (parse-xml xml))))))

(defn as-pattern-data [xml]
  (content-xml-to-map
   (remove-tag
    :pt
    (first
     (map
      :content
      (filter-tag
       :ptr
       (:content (parse-xml xml))))))))

(defn as-waypoint-data [xml direction]
  (map content-xml-to-map
       (map
        :content
        (filter-tag
         :pt
         (first
          (filter
           #(= direction (first (:content (first (filter-tag :rtdir %)))))
           (map
            :content
            (filter-tag
             :ptr
             (:content (parse-xml xml))))))))))

(defn as-routes [xml]
  (map content-xml-to-map
       (map
        :content
        (filter-tag
         :route
         (:content (parse-xml xml))))))

(defn as-route-direction [xml]
  (flatten
   (map
    :content
    (filter-tag
     :dir
     (:content (parse-xml xml))))))

(defn as-prediction [xml]
  (map content-xml-to-map
   (map
    :content
    (filter-tag
     :prd
     (:content (parse-xml xml))))))
