(ns go-visual.core
  (:gen-class)
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json])
  (:require [go-visual.string2number :as string2number])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [clojure.java.io :as io]))

(defn extract-pipeline-instance-history 
  [pipeline]
  {:name (:name pipeline) 
   :counter (:counter pipeline)
   :statges (map 
             (fn [stages-map] {:name (get stages-map :name) :counter (get stages-map :counter)}) 
             (get pipeline :stages))})

(defn fetch-pipeline-datas
  [url username password]
  (json/read-str (get (client/get url {:basic-auth [username password]}) :body)
                 :key-fn keyword))

(defn write-to-site-json
  [statis output-file-des]
  (spit output-file-des (json/write-str
                         {:title (:name (first statis))
                          :categories (map #(:counter %) statis)
                          :data (map #(- (:statges-run-times %) (:statges %)) statis)})))

(defn statistic-pipeline-instace
  [pipeline-instance]
  {:name (:name pipeline-instance)
   :counter (:counter pipeline-instance)
   :statges-run-times   (reduce + (map (comp string2number/to-number :counter) (:statges pipeline-instance)))
   :statges ((comp count :statges) pipeline-instance)})

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

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        {:keys [url username password target]} options]
    (write-to-site-json 
     (map (comp  statistic-pipeline-instace 
                 extract-pipeline-instance-history)
          (take 15 (:pipelines (fetch-pipeline-datas url username password)))) 
     target)))

