(defproject gotbs "0.0.1" 
  :dependencies [
                 [org.clojure/clojure "1.4.0-beta4"]
                 [org.clojure/tools.logging "0.2.3" :exclusions [org.clojure/clojure]]
                 [org.clojure/data.json "0.1.2" :exclusions [org.clojure/clojure]]
                 [org.clojure/core.memoize "0.5.1" :exclusions [org.clojure/clojure]]
                 [org.webbitserver/webbit "0.2.0" :exclusions [org.clojure/clojure]]
                 [org.eclipse.jetty/jetty-servlet "7.4.2.v20110526" :exclusions [org.clojure/clojure]]
                 [ring/ring-core "1.0.1" :exclusions [org.clojure/clojure]]
                 [ring/ring-jetty-adapter "1.0.1" :exclusions [org.clojure/clojure]]
                 [ring/ring-devel "1.0.1" :exclusions [org.clojure/clojure]]
                 [clj-time/clj-time "0.3.7" :exclusions [org.clojure/clojure]]
                 [enlive "1.0.0-SNAPSHOT" :exclusions [org.clojure/clojure]]
                 [com.datomic/datomic "0.1.2753"]]
 
   :profiles {:dev
             {:dependencies
              [[midje "1.3.2-SNAPSHOT" :exclusions [org.clojure/clojure]]
               [org.gensym/tools.trace "0.1" :exclusions [org.clojure/clojure]]
               ]}}
  :main gotbs.run
  :min-lein-version "2.0.0"
  :plugins [[lein-swank "1.4.1"]]
  :description "CTA Bus Tracker predictions for multi-leg commutes")
