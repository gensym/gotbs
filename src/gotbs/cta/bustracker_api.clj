(ns gotbs.cta.bustracker-api
  (:require [clojure.string :as s]
            [clojure.tools.logging :as log])
  (:use
   clojure.xml (parse)
   clojure.java.io (input-stream))
  (:require  [clojure.string :as string])
  (:import
   (java.net URL)
   (java.io BufferedReader InputStreamReader StringBufferInputStream)))

(defn to-url-params [params]
  (let [h (zipmap (keys params) (map #(string/replace % #"\s" "+" ) (vals params)))]
    (s/join "&" (map (partial s/join "=") (seq h)))))

(defn- apply-all [fns val] ((apply comp (reverse fns)) val))

(defn- parse-xml [s] (parse (StringBufferInputStream. s)))

(defn fetch-url
  "Returns the contents at a URL as a string"
  [address]
  (let [url (URL. address)]
    (log/info (str "Requesting " url))
    (with-open [stream (. url openStream)]
      (let [buf (BufferedReader. (InputStreamReader. stream))]
        (log/spy
         (apply str (line-seq buf)))))))

(defn bustracker-url
  ([rpc-name api-key]
     (bustracker-url rpc-name api-key {}))
  ([rpc-name api-key params]
     (let [merged-params  (merge {"key" api-key} params)
           url-params (to-url-params merged-params)]
       (str "http://www.ctabustracker.com/bustime/api/v1/get" rpc-name "?" url-params))))

(defprotocol BusTrackerProtocol
  (all-routes [_])
  (directions [_ route])
  (vehicles-on-route [_ route])
  (vehicle-by-id [_ vehicle-id])
  (patterns-for-route [_ route])
  (pattern-by-id [_ pattern-id])
  (predictions [_ route stop-id])
  (stops [_ route direction]))

(defn url-handler [url]
  (parse-xml (fetch-url url)))

(deftype BustrackerApi [api-key]
  BusTrackerProtocol
  (all-routes [_] (url-handler (bustracker-url "routes" api-key)))
  (directions [_ route] (url-handler (bustracker-url "directions" api-key {"rt" route})))
  (vehicles-on-route [_ route] (url-handler (bustracker-url "vehicles" api-key {"rt" route})))
  (vehicle-by-id [_ vehicle-id] (url-handler (bustracker-url "vehicles" api-key {"vid" vehicle-id})))
  (patterns-for-route [_ route] (url-handler (bustracker-url "patterns" api-key {"rt" route})))
  (pattern-by-id [_ pattern-id] (url-handler (bustracker-url "patterns" api-key {"pid" pattern-id})))
  (predictions [_ route stop-id] (url-handler (bustracker-url "predictions" api-key {"rt" route "stpid" stop-id})))
  (stops [_ route direction] (url-handler (bustracker-url "stops" api-key {"rt" route "dir" direction})))
  )

(defn make-bustracker [api-key]
  (BustrackerApi. api-key) )
