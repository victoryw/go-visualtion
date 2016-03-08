(defproject go-visual "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"] 
                  [clj-http "2.1.0"]
                  [org.clojure/data.json "0.2.6"]
                  [org.clojure/tools.cli "0.3.3"]]
  :main ^:skip-aot go-visual.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
