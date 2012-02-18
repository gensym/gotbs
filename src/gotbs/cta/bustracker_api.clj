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

(defn- to-url-params [params]
  (let [h (zipmap (keys params) (map #(string/replace % #"\s" "+" ) (vals params)))]
    (s/join "&" (map (partial s/join "=") (seq h)))))

(defn parse-xml [s] (parse (StringBufferInputStream. s)))

(defn fetch-url
  "Returns the contents at a URL as a string"
  [address]
  (let [url (URL. address)]
    (log/info (str "Requesting " url))
    (with-open [stream (. url openStream)]
      (let [buf (BufferedReader. (InputStreamReader. stream))]
        (log/spy
         (apply str (line-seq buf)))))))

(defn bustracker-url [rpc-name api-key params]
  (let [merged-params  (merge {"key" api-key} params)
        url-params (to-url-params merged-params)]
    (str "http://www.ctabustracker.com/bustime/api/v1/get" rpc-name "?" url-params)))

(defn- cta-bustracker-get
  ([cache-ttl api-key rpc-name]
     (cta-bustracker-get cache-ttl api-key rpc-name {}))
  ([cache-ttl api-key rpc-name rpc-args]
     (-> (bustracker-url rpc-name api-key rpc-args)
         (fetch-url)
         (parse-xml))))

(defprotocol BusTrackerProtocol
  (all-routes [_])
  (directions [_ route])
  (vehicles-on-route [_ route])
  (vehicle-by-id [_ vehicle-id])
  (patterns-for-route [_ route])
  (pattern-by-id [_ pattern-id])
  (predictions [_ route stop-id])
  (stops [_ route direction]))

(deftype BustrackerApi [api-key]
  BusTrackerProtocol
  (all-routes [_] (cta-bustracker-get :day api-key "routes"))
  (directions [_ route] (cta-bustracker-get :day api-key "directions" {"rt" route}))
  (vehicles-on-route [_ route] (cta-bustracker-get :minute api-key "vehicles"  {"rt" route}))
  (vehicle-by-id [_ vehicle-id] (cta-bustracker-get :minute api-key "vehicles" {"vid" vehicle-id}))
  (patterns-for-route [_ route] (cta-bustracker-get :day api-key  "patterns"  {"rt" route}))
  (pattern-by-id [_ pattern-id] (cta-bustracker-get :day api-key "patterns" {"pid" pattern-id}))
  (predictions [_ route stop-id] (cta-bustracker-get :minute api-key  "predictions" {"rt" route "stpid" stop-id}))
  (stops [_ route direction] (cta-bustracker-get :day api-key "stops" {"rt" route "dir" direction})))

(defn make-bustracker [api-key]
  (BustrackerApi. api-key) )
