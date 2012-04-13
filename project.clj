(defproject gotbs "0.0.1" 
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/data.json "0.1.2"]
                 [org.clojure/core.memoize "0.5.1"]
                 [org.webbitserver/webbit "0.2.0"]
                 [org.eclipse.jetty/jetty-servlet "7.4.2.v20110526"]
                 [ring/ring-core "1.0.1"]
                 [ring/ring-jetty-adapter "1.0.1"]
                 [ring/ring-devel "1.0.1"]
                 [clj-time/clj-time "0.3.7"]
                 [enlive "1.0.0-SNAPSHOT"]
                 [com.datomic/datomic "0.1.2753"]]
  :profiles {:dev
             {:dependencies
              [[midje "1.3.2-SNAPSHOT"]
               [org.gensym/tools.trace "0.1"]]}}
  :main gotbs.run
  :min-lein-version "2.0.0"
  :plugins [[lein-swank "1.4.1"]]
  :description "CTA Bus Tracker predictions for multi-leg commutes")
