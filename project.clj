(defproject gotbs "0.0.1"
  :description "CTA Bus Tracker predictions for multi-leg commutes"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [midje "0.8.0"]
		 [ring/ring-core "0.2.5"]
		 [ring/ring-devel "0.2.5"]
		 [ring/ring-jetty-adapter "0.2.5"]
		 [enlive "1.0.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.2.1"]
		     [lein-run "1.0.0-SNAPSHOT"]])
