(ns go-visual.core
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:require [go-visual.command-line-paramters :as paramters])
  (:require [clojure.data.json :as json])
  (:require [go-visual.static_pipeline_instance :as statis]))

(defn write-to-site-json
  [statis output-file-des]
  (spit output-file-des (json/write-str
                         {:title (:name (first statis))
                          :categories (map #(:counter %) statis)
                          :pipelineRunTimes (map #(:pipeline-run-times %) statis)
                          :countinueSuccessCount (map #(:success %) statis)
                          :status (map #(:status %) statis)
                          :endTime (map #(:endTime %) statis)})))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (let [{:keys [options arguments errors summary]} (parse-opts args (paramters/cli-options))
        {:keys [url username password target startedTime endTime]} options]
    (write-to-site-json (statis/statistic-each-pipeline-stage-run-time url username password startedTime endTime) target)))

