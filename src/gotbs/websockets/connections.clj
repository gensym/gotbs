(ns gotbs.websockets.connections)

(defn connection-set []
  {:connection-agents (ref {})
   :topics-by-connection (ref {})
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

(defn open [connection-set connection]
  (println "Opened " connection)
  (dosync
   (alter (:connection-agents connection-set) (fn [cba] (assoc cba connection (agent nil))))))

(defn close [connection-set connection]
  (dosync
   (dorun
    (map
     (fn [topic] (alter
                 (:connections-by-topic connection-set)
                 (partial remove-connection connection topic)))
     (get @(:topics-by-connection connection-set) connection)))
   (alter (:connection-agents connection-set) (fn [m] (dissoc m connection)))
   (alter (:topics-by-connection connection-set) (fn [m] (dissoc m connection)))))

(defn send-data [connection-set connection data]
  (io!
   (if-let [conn-agent (get @(:connection-agents connection-set) connection)]
     (do
       (println "Sending off to " connection)
       (send-off conn-agent (fn [_]
                              (do
                                (println "Sending '" data "' to connection " connection)
                                (.send connection data))))))))

(defn broadcast [connection-set topic data]
  "Return the number of messages sent"
  (let [connections (get @(:connections-by-topic connection-set) topic)]
    (count (map (fn [conn] (send-data connection-set conn data)) connections))))
