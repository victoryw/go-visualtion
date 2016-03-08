(ns go-visual.core
  (:gen-class)
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json])
  (:require [go-visual.string2number :as string2number])
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [go-visual.command-line-paramters :as paramters]))

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

(defn statistic-pipeline-instace
  [pipeline-instance]
  {:name (:name pipeline-instance)
   :counter (:counter pipeline-instance)
   :statges-run-times   (reduce + (map (comp string2number/to-number :counter) (:statges pipeline-instance)))
   :statges ((comp count :statges) pipeline-instance)})

(defn statistic-each-pipeline-stage-run-time
  [url username password]
  (map (comp  statistic-pipeline-instace 
              extract-pipeline-instance-history)
       (:pipelines (fetch-pipeline-datas url username password))))

(defn write-to-site-json
  [statis output-file-des]
  (spit output-file-des (json/write-str
                         {:title (:name (first statis))
                          :categories (map #(:counter %) statis)
                          :data (map #(- (:statges-run-times %) (:statges %)) statis)})))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (let [{:keys [options arguments errors summary]} (parse-opts args (paramters/cli-options))
        {:keys [url username password target]} options]
    (write-to-site-json (statistic-each-pipeline-stage-run-time url username password) target)))

