(ns gotbs.bustracker
  (:import
   (java.net URL)
   (java.io BufferedReader InputStreamReader StringBufferInputStream))
  (:use
   clojure.xml (parse)
   clojure.java.io (input-stream)))

(defn fetch-url
  "Returns the contents at a URL as a string"
  [address]
  (let [url (URL. address)]
    (with-open [stream (. url openStream)]
      (let [buf (BufferedReader. (InputStreamReader. stream))]
	(apply str (line-seq buf))))))

(defn fetch-location-data-xml []
  (fetch-url 
   "http://www.ctabustracker.com/bustime/api/v1/getvehicles?key=kv6yHNkUrkZJkjA8u7V5sxNTq&rt=72,50"))

(defn vehicle-content-xml-to-map
	[xml]
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
		


(defn make-vehicles
	[xml]
	(map vehicle-content-xml-to-map (map :content (filter #(= (:tag %) :vehicle) (xml-seq xml)))))
	
(defn fetch-location-data []
  (make-vehicles (parse (StringBufferInputStream. (fetch-location-data-xml)))))
