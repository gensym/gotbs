(ns gotbs.websockets.connections)

(defn connection-set []
  {:topics-by-connection (ref {})
   :connections-by-topic (ref {})})

(defn- add-topic [connection topic topics-by-connection]
  (let [topics (get topics-by-connection connection #{})]
    (assoc topics-by-connection connection (conj topics topic))))

(defn- add-connection [connection topic connections-by-topic]
  (let [connections (get connections-by-topic topic #{})]
    (assoc connections-by-topic topic (conj connections connection))))

(defn subscribe [connection-set connection topic]
  (dosync
   (alter (:topics-by-connection connection-set) (partial add-topic connection topic))
   (alter (:connections-by-topic connection-set) (partial add-connection connection topic))))

(defn- remove-connection [connection topic connections-by-topic]
  (let [connections (get connections-by-topic topic #{})]
    (assoc connections-by-topic topic (disj connections connection))))

(defn close [connection-set connection]
  (dosync
   (dorun
    (map
     (fn [topic] (alter
                 (:connections-by-topic connection-set)
                 (partial remove-connection connection topic)))
     (get @(:topics-by-connection connection-set) connection)))
   (alter (:topics-by-connection connection-set) (fn [m] (dissoc m connection)))
   ))

(defn send-data [connection data]
  (io!
   (.send connection data)))

(defn broadcast [connection-set topic data]
  "Return the number of messages sent"
  (let [connections (get @(:connections-by-topic connection-set) topic)]
    (count (map (fn [conn] (send-data conn data)) connections))))
