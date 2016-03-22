(ns go-visual.static_pipeline_instance_test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [clj-http.fake :refer :all]
            [go-visual.static_pipeline_instance :refer :all]))

(deftest statis-pipeline-instance-history-last-stage-time
  (testing "should keep total start time" 
    (let [username "username"
          password "passwword"
          url      "http://example.com"
          startedTime "2016-03-22T00:00:00"
          endTime  "2016-03-22T15:00:00"
          json-body (json/write-str {:pipelines [{:counter 1 
                                                  :name "123" 
                                                  :stages [{:name "st-1" :counter "1" :scheduled true :result "Passed" :jobs [{:scheduled_date 1458268260408}]}
                                                           {:name "st-2" :counter "1" :scheduled true :result "Passed" :jobs [{:scheduled_date 1458648000000}]}]}]})]

      (with-fake-routes {{:address url :basic-auth {username password}} 
                         (fn [reqeust] {:status 200 :headers {} :body json-body})}
        (is (= (statistic-each-pipeline-stage-run-time url username password startedTime endTime)
               [{:name "123" 
                 :counter 1
                 :end-time 1458648000000
                 :pipeline-run-times 1, :status true, :success 1}]))))))

(deftest statis-pipeline-not-schduled-end-time
  (testing "should extract pipeline end time when with some job not scheduled"
    (let [username "username"
          password "passwword"
          startedTime "2016-03-22T00:00:00"
          endTime  "2016-03-22T15:00:00"
          url      "http://example.com"
          json-body (json/write-str {:pipelines [{:counter 1 
                                                  :name "123" 
                                                  :stages [{:name "st-1" :counter "1" :scheduled true :result "Failed" :jobs []}]}]})]

      (with-fake-routes {{:address url :basic-auth {username password}} 
                         (fn [reqeust] {:status 200 :headers {} :body json-body})}
        (is (= (statistic-each-pipeline-stage-run-time url username password startedTime endTime) '()))))))

(run-tests 'go-visual.static_pipeline_instance_test)
