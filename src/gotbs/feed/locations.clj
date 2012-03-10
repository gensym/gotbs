(ns gotbs.feed.locations
  (:require [lamina.core :as l]
            [aleph.http :as http]
            [aleph.formats :as formats])
  (:import
   (java.io  BufferedReader InputStreamReader StringBufferInputStream)
   (org.jboss.netty.buffer BigEndianHeapChannelBuffer ChannelBufferInputStream)))




(def api-key "kv6yHNkUrkZJkjA8u7V5sxNTq")

(def url "http://www.ctabustracker.com/bustime/api/v1/getvehicles")


(def client (http/http-client {:url "http://www.ctabustracker.com/bustime/api/v1/getvehicles"}))

;; returns a result channel
(defn doit [route]
  (client {:method :get
           :query-params {:key api-key
                          :rt route}}))

;; Returns the body as an input stream
(defn to-is [res-channel]
  (ChannelBufferInputStream. (:body @res-channel)))

;; Next step - figure out how to put the result channel into a pipeline



