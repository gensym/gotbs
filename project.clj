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
                 [domina "1.0.2-SNAPSHOT"]
                 [org.clojure/google-closure-library-third-party "0.0-2029"]
                 [com.cemerick/piggieback "0.0.4"]]
  :source-paths ["src/clj"]
  :plugins [[lein-cljsbuild "0.3.0"]]
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :cljsbuild { :builds
              {:dev {
                     :source-paths ["src/brepl" "src/cljs"]
                     :compiler {:output-to "resources/web/cljs/gotbs_client.dbg.js"
                                :optimizations :whitespace
                                :pretty-print true}}
               :prod {
                      :source-paths ["src/cljs"]
                      :compiler {:output-to "resources/web/cljs/gotbs_client.js"
                                 :externs ["resources/web/js/moment_externs.js"]}}
               :pre-prod {
                          :source-paths ["src/brepl" "src/cljs"]
                          :compiler { :output-to "resources/web/cljs/gotbs_client_pre.js"
                                     :optimizations :simple}}}}
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
