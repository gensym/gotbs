(ns gotbs.bustracker
  (:import
   (java.net URL)
   (java.io BufferedReader InputStreamReader StringBufferInputStream))
  (:use
   clojure.xml (parse)
   clojure.java.io (input-stream)))

;; BEGIN HARDCODING
(def api-key "kv6yHNkUrkZJkjA8u7V5sxNTq")

(def route "72")

(def direction "West+Bound")

(def stop-name  "North Ave & Milwaukee/Damen (Blue Line)")
;; END HARDCODING

(defn fetch-url
  "Returns the contents at a URL as a string"
  [address]
  (let [url (URL. address)]
    (with-open [stream (. url openStream)]
      (let [buf (BufferedReader. (InputStreamReader. stream))]
	(apply str (line-seq buf))))))

(defn fetch-location-data-xml []
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getvehicles?key=" api-key "&rt=" route)))

(defn fetch-prediction-data-xml []
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getpredictions?key=" api-key "&rt=" route)))

(defn fetch-stop-data-xml []
  (fetch-url
   (str "http://www.ctabustracker.com/bustime/api/v1/getstops?key=" api-key "&rt=" route "&dir=" direction)))

(defn content-xml-to-map [xml]
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
	(map content-xml-to-map (map :content (filter #(= (:tag %) :vehicle) (xml-seq xml)))))

(defn make-predictions [xml]
  (map content-xml-to-map (map :content (filter #(= (:tag %) :vehicle) (xml-seq xml)))))

(defn fetch-prediction-data []
     (fetch-prediction-data-xml))

(defn fetch-location-data []
  (make-vehicles (parse (StringBufferInputStream. (fetch-location-data-xml)))))

(defn stop-id []
  (first (map :stpid (filter #(= (:stpnm %1) stop-name) (fetch-stop-data)))))

(defn fetch-stop-data []
  (map
   content-xml-to-map
   (map
    :content 
    (filter
     #(= (:tag %) :stop)
     (xml-seq 
      (parse
       (StringBufferInputStream.
	(fetch-stop-data-xml))))))))


