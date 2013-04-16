(ns gotbs.js.timeline
  (:require [goog.net.XhrIo :as gxhr]))

(.write js/document "<p>WTF - get. it. on!</p>")
(.log js/console "FFFuuu")

(comment (def canvas-id "timelines"))

(defn draw-path [context path]
  (if (not (empty? path))
    (let [[x y] (first path)]
      (.beginPath context)
      (.moveTo context x y)
      (doseq [[x y] (rest path)]
        (.lineTo context x y))
      (.stroke context))))

(defn scale-points [x0 x1 width y0 y1 height path]
  (let [xrange (- x1 x0)
        yrange (- y1 y0)]
    (map (fn [[x y]]
           [(/ (- x x0) (* xrange width))
            (/ (- y y0) (* yrange height))])
         path)))

(defn find-range [pointsets]
  (let [[x y] (ffirst pointsets)]
    (reduce
     (fn [[xn xx yn yx] [x y]]
       [(min xn x)
        (max xx x)
        (min yn y)
        (max yx y)])
     [x x y y]
     (apply concat pointsets))))

(defn ^:export draw-canvas [canvas-id paths] 

  (let [elem  (.getElementById js/document canvas-id)
        context (.getContext elem "2d")
        width (.-width elem)
        height (.-height elem)
        [x0 x1 y0 y1] (find-range paths)
        points (map (partial scale-points x0 x1 width y0 y1 height) paths)]

    (.clearRect context 0 0 width height)
    (.save context)
    (set! (.-lineWidth context) 1.5)
    (doseq [path points]
      (draw-path context path))
    (.restore context)))

;; Everything below here should be moved out of this namespace

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
               (let [runset  (.getResponseText (.-target message))]
                 (.log js/console runset)
                 (set! (.-runs js/document) runset)
                 (comment (draw-canvas "timelines" (.-runs runset)))))))

(defn ^:export get-data [start-date end-date]
  (get-runs-json start-date end-date))

(def start-date #inst "2012-03-07T21:00:00.000-00:00")
(def end-date #inst "2012-03-08T00:00:00.000-00:00")


