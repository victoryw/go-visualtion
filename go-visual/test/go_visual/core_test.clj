(ns go-visual.core-test
  (:require [clojure.test :refer :all]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [go-visual.core :refer :all]
            [clj-http.fake :refer :all]))

(deftest test-mock
  (testing "should success when with corret paramters"
    (let [username "username"
          password "passwword"
          startedTime "2016-03-22T00:00:00"
          endTime  "2016-03-22T15:00:00"
          url      "http://example.com"
          json-body (json/write-str {:pipelines [{:counter 1 :name "123" :stages [{:name "st-1" :counter "1" :scheduled true :result "Passed" :jobs [{:scheduled_date 1458648000000}]}]}]})
          result (atom nil)]
      (with-fake-routes {{:address url :basic-auth {username password}} 
                         (fn [reqeust] {:status 200 :headers {} :body json-body})}
        (with-redefs-fn 
          {#'spit (fn [output-file-des statis]  (reset! result statis))}
          (fn [] 
            (-main "--url"  url  "--username"  username "--password" password "--startedTime" startedTime "--endTime" endTime)
            (is (= "{\"title\":\"123\",\"categories\":[1],\"pipelineRunTimes\":[1],\"countinueSuccessCount\":[1],\"status\":[true],\"endTime\":[null]}" @result)))))))

  (testing "should success when no start or end date"
    (let [username "username"
          password "passwword"
          url      "http://example.com"
          json-body (json/write-str {:pipelines [{:counter 1 :name "123" :stages [{:name "st-1" :counter "1" :scheduled true :result "Passed" :jobs [{:scheduled_date 100}]}]}]})
          result (atom nil)]
      (with-fake-routes {{:address url :basic-auth {username password}} 
                         (fn [reqeust] {:status 200 :headers {} :body json-body})}
        (with-redefs-fn 
          {#'spit (fn [output-file-des statis]  (reset! result statis))}
          (fn [] 
            (-main "--url"  url  "--username"  username "--password" password)
            (is (= "{\"title\":\"123\",\"categories\":[1],\"pipelineRunTimes\":[1],\"countinueSuccessCount\":[1],\"status\":[true],\"endTime\":[null]}" @result))))))))

(run-tests 'go-visual.core-test)
