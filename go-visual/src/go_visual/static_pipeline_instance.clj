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
             (:stages pipeline))})

(defn statistic-pipeline-instace
  [pipeline-instance]
  {:name (:name pipeline-instance)
   :counter (:counter pipeline-instance)
   :pipeline-run-times   (+ 1 (- 
                               (reduce + (map (comp string2number/to-number :counter) (:statges pipeline-instance))) 
                               ((comp count :statges) pipeline-instance)))})

(defn statis-pipeline-success-status
  [pipelines]
  (sort-by :counter < (map (fn [pipeline] 
         {:name (:name pipeline) 
          :counter (:counter pipeline) 
          :status  (every? #(and (true? (:scheduled %)) (= (str (:result %)) "Passed")) (:stages pipeline))})
       pipelines)))

(defn statis-pipeline-continue-failure-counter
  [last-result-failure-counter pipeline-instance]
  (if (true? (:status pipeline-instance)) 
    (assoc pipeline-instance :fail 0)
    (assoc pipeline-instance :fail (+ 1 last-result-failure-counter))))

(defn extract-last-pipeline-continue-failure-counter 
  [last-result]
  (if (empty? last-result) 0
      (if (list? last-result) (:fail (last last-result)) (:fail last-result))))

(defn statis-accumulate-pipeline-failure-counter
  [pipelines]
  (flatten (reduce #(list % (statis-pipeline-continue-failure-counter (extract-last-pipeline-continue-failure-counter %) %2)) '() pipelines)))

(defn statistic-each-pipeline-stage-run-time
  [url username password]
  (def pipelines (:pipelines (fetch-pipeline-datas url username password)))
  ; (def statis (sort-by :counter < (map (comp  statistic-pipeline-instace extract-pipeline-instance-history) pipelines)))
  (statis-accumulate-pipeline-failure-counter 
   (map  
    (fn [statistic-pipeline status-pipeline] (assoc statistic-pipeline :status (:status status-pipeline))) 
    (sort-by :counter < (map (comp  statistic-pipeline-instace extract-pipeline-instance-history) pipelines))
    (statis-pipeline-success-status pipelines))))

