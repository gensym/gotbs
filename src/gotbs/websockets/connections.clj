(ns gotbs.websockets.connections)

(defn connection-set []
  {:topics-by-connection (ref {})
   :connections-by-topic (ref {})})

(defn subscribe [connection-set connection topic]
  (dosync
   (let [topics (get @(:topics-by-connection connection-set) connection  [])
         connections (get @(:connections-by-topic connection-set)  topic [])]
     (alter (:topics-by-connection connection-set)
            (fn [tbc] (assoc tbc connection (cons topic topics))))
     (alter (:connections-by-topic connection-set)
            (fn [cbt] (assoc cbt topic (cons connection connections)))))))

(defn close [connection-set connection]
  {})

(defn broadcast [connection-set topic data]
  "Return the number of messages sent"
  (let [connections (get @(:connections-by-topic connection-set) topic)]
    (count (map (fn [conn] (send-data conn data)) connections))))

(defn send-data [connection data]
  (io!
   (.send connection data)))
