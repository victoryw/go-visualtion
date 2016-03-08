(ns go-visual.command-line-paramters
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.java.io :as io]))


  (def cli-options 
    [["-l" "--url url" "server url" 
      :default "../go-visual-site/" 
      :parse-fn #(str %)]
     ["-u" "--username userrname" "username" 
      :default "" 
      :parse-fn #(str %)]
     ["-p" "--password password" "password" 
      :default ""  
      :parse-fn #(str %)]
     ["-t" "--target target" "the folder of flush result named as data.json"
      :default "../go-visual-site/data.json"
      :parse-fn #(str (io/file % "data.json"))]
     ["-h" "--help"]])
