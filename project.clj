(defproject gotbs "0.0.1"
  :description "CTA Bus Tracker data analysis and visualization"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/tools.nrepl "0.2.0-beta10"]
                 [org.slf4j/slf4j-log4j12 "1.6.4"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/data.json "0.1.2" :exclusions [org.clojure/clojure]]
                 [org.clojure/core.memoize "0.5.1" :exclusions [org.clojure/clojure]]
                 [org.webbitserver/webbit "0.2.0" :exclusions [org.clojure/clojure]]
                 [org.eclipse.jetty/jetty-servlet "7.4.2.v20110526" :exclusions [org.clojure/clojure]]
                 [ring/ring-core "1.0.1" :exclusions [org.clojure/clojure]]
                 [ring/ring-jetty-adapter "1.0.1" :exclusions [org.clojure/clojure]]
                 [ring/ring-devel "1.0.1" :exclusions [org.clojure/clojure]]
                 [ring.middleware.logger "0.4.0" :exclusions [org.clojure/clojure]]
                 [clj-time/clj-time "0.4.5" :exclusions [org.clojure/clojure]]
                 [enlive "1.0.0-SNAPSHOT" :exclusions [org.clojure/clojure]]
                 [com.datomic/datomic-free "0.8.3862"
                  :exclusions [org.slf4j/slf4j-nop org.slf4j/log4j-over-slf4j]]
                 [jayq "2.3.0"]]
  :source-paths ["src/clj"]
  :plugins [[lein-cljsbuild "0.3.0"]]

  :cljsbuild {:builds
              [{:source-paths ["src/cljs"]
                :compiler {
                           :output-dir "resources/web/js/"
                           :output-to "resources/web/js/gotbs_client.js"
                           :optimizations :simple
                           :pretty-print true}}]}
 
   :profiles {:dev
             {:dependencies
              [[midje "1.3.2-SNAPSHOT" :exclusions [org.clojure/clojure]]
               [org.gensym/tools.trace "0.1" :exclusions [org.clojure/clojure]]
               ]}}
   :main gotbs.run
   :aliases {
             "createdb" ["run" "-m" "gotbs.data-import.create-db" "datomic:free://localhost:4334/gotbs"]
             "deletedb" ["run" "-m" "gotbs.data-import.delete-db" "datomic:free://localhost:4334/gotbs"]
             "import-data" ["run" "-m" "gotbs.data-import.directories" "datomic:free://localhost:4334/gotbs"]
             }
   :jvm-opts ["-Xmx2g"]
   :min-lein-version "2.0.0")
