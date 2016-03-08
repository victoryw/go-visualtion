(ns go-visual.static_pipeline_instance
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json])
  (:require [go-visual.string2number :as string2number]))
  
  (defn fetch-pipeline-datas
    [url username password]
    (json/read-str (get (client/get url {:basic-auth [username password]}) :body)
                   :key-fn keyword))


  (defn extract-pipeline-instance-history 
    [pipeline]
    {:name (:name pipeline) 
     :counter (:counter pipeline)
     :statges (map 
               (fn [stages-map] {:name (get stages-map :name) :counter (get stages-map :counter)}) 
               (get pipeline :stages))})

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