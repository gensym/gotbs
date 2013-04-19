(ns gotbs.js.run-plots
  (:require [gotbs.js.timeline :as timeline]
            [cljs.reader :as reader]
            [goog.net.XhrIo :as gxhr]))


(defn to-xy [{x :time y :dist}] [x y])

(defn get-uri [start-date end-date]
  (let [uri
        (goog.Uri. "/")
        qr (. uri (getQueryData))]
    (.add qr "from" (.toJSON start-date))
    (.add qr "to" (.toJSON end-date))
    (.setPath uri "/runs/for_route.edn")))

(defn  get-runs-json [start-date end-date]
  (gxhr/send (get-uri start-date end-date)
             (fn [message]
               (let [runset (-> message
                                (.-target)
                                (.getResponseText)
                                (reader/read-string))]
                 
                 (set! (.-runs js/document) runset)
                 (timeline/draw-canvas "timelines" (map (partial map to-xy) (:runs runset)))))))

(defn ^:export get-data [start-date end-date]
  (get-runs-json start-date end-date))
