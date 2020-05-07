(ns figwheel-main-testing.core-test
    (:require
     [cljs.test :refer-macros [deftest is testing async]]
     [goog.net.XhrIo :as xhrio]
     [goog.string :as gstring]
     [goog.object :as gobj]
     [goog.dom :as gdom]
     [clojure.core.async :refer [<! put! take! go go-loop chan close!] :as asyn]
     [clojure.string :as string]
     #_[figwheel-main-testing.core]
     [figwheel-main-testing.test-utils
      :refer [provide!
              reset-receiver-chan!
              reset-data!
              touch-file!
              touch-files!
              append-line!
              truncate-file!
              api-send-and-wait!
              drain-chan]
      :as ut]
     #_[figwheel-main-testing.core :refer [multiply]]))


;; OK for now we will have to define all the files that
;; we want to be reloaded so that they exist

;; In the real test we will make this file a ^:figwheel-no-load file
;; so that it doesn't get rerun on every change

(provide! "figwheel_main_testing.core")

;; ----------------------------------------------------------------------
;; testing no-load-meta-data
;; ----------------------------------------------------------------------
;; the namespace aaaa has figwheel-no-load on it
;; dependency chain: bbbb > aaaa > core

(deftest test-no-reload-metadata-bbbb
  "This should not reload the aaaa namespace even though it is a
  dependant of bbbb, however the core namespace should be loaded as it
  is a dependent of bbbb"
  (reset-receiver-chan!)
  (reset-data!)
  (async
   done
   (go
     (<! (touch-file! "src/figwheel_main_testing/bbbb.cljs"))
     (is (= 'figwheel-main-testing.bbbb
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.core
            (<! @ut/ns-receiver-chan-atom)))
     (<! (asyn/timeout 100))
     (is (empty? (<! (drain-chan @ut/ns-receiver-chan-atom))))
     (done))))


(deftest test-no-reload-metadata-aaaa
  (reset-receiver-chan!)
  (reset-data!)
  (async
   done
   (go
     (<! (touch-file! "src/figwheel_main_testing/aaaa.cljs"))
     (is (= 'figwheel-main-testing.core
            (<! @ut/ns-receiver-chan-atom)))
     (<! (asyn/timeout 100))
     (is (empty? (<! (drain-chan @ut/ns-receiver-chan-atom))))
     (done))))

;; ----------------------------------------------------------------------
;; testing actual change in code
;; ----------------------------------------------------------------------
;; the tests above all just touch the files so we don't know if new
;; behavior is getting reloaded


(deftest test-actual-code-change
  (reset-receiver-chan!)
  (reset-data!)
  (let [i (rand-int 1000000)]
    (async
     done
     (go
       (<! (append-line! "src/figwheel_main_testing/changer.cljs"
                         (str "(util/signal " i ")" )))
       (is (= 'figwheel-main-testing.changer
              (<! @ut/ns-receiver-chan-atom)))
       (is (= 'figwheel-main-testing.core
              (<! @ut/ns-receiver-chan-atom)))
       (<! (asyn/timeout 100))
       (is (= i (:signal @ut/data-atom)))
       (<! (truncate-file! "src/figwheel_main_testing/changer.cljs"))
       (done)))))

;; ----------------------------------------------------------------------
;; testing warnings
;; ----------------------------------------------------------------------

(defn heads-up-text-content []
  (when-let [el (gdom/getElement "figwheel-heads-up-content-area")]
    (gdom/getRawTextContent el)))

(defn heads-up-includes? [s]
  (when-let [content (heads-up-text-content)]
    (.includes content s)))

(defn head-up-showing? []
  (not (string/blank? (heads-up-text-content))))

(deftest test-warning
  (let [i (rand-int 1000000)]
    (async
     done
     (go
       (<! (asyn/timeout 2000))
       (reset-receiver-chan!)
       (reset-data!)
       (<!
        (api-send-and-wait! {:action :append-line
                             :file-path "src/figwheel_main_testing/changer.cljs"
                             :line (str "(list ddd" i ")" )}))
       
       (<! (asyn/timeout 100))
       (is (heads-up-includes? "Compile Warning"))
       (is (heads-up-includes? (str i)))
       (<! (asyn/timeout 1000))
       (<! (truncate-file! "src/figwheel_main_testing/changer.cljs"))
       (done)))))

(deftest test-could-not-analyze
  (async
   done
   (go
     (<! (asyn/timeout 2000))
     (reset-receiver-chan!)
     (reset-data!)
     #_(<! (truncate-file! "src/figwheel_main_testing/changer.cljs"))
     (<!
      (api-send-and-wait! {:action :append-line
                           :file-path "src/figwheel_main_testing/changer.cljs"
                           :line "(defn)"}))
     
     (<! (asyn/timeout 100))
     (is (head-up-showing?))
     (is (heads-up-includes? "Could not Analyze"))
     (is (heads-up-includes? (str "(defn)")))
     (<! (asyn/timeout 1000))
     (<! (truncate-file! "src/figwheel_main_testing/changer.cljs"))
     (done))))

(deftest test-compile-exception
  (let [i (rand-int 1000000)]
    (async
     done
     (go
       (<! (asyn/timeout 2000))
       (reset-receiver-chan!)
       (reset-data!)
       (<!
        (api-send-and-wait! {:action :append-line
                             :file-path "src/figwheel_main_testing/changer.cljs"
                             :line (str "(defn ddd" i ")")}))
       
       (<! (asyn/timeout 100))
       (is (head-up-showing?))
       (is (heads-up-includes? "Could not" #_Compile))
       #_(is (heads-up-includes? test-str))
       (<! (asyn/timeout 1000))
       (<! (truncate-file! "src/figwheel_main_testing/changer.cljs"))
       (done)))))




;; TODO test javascript reloading

;; TODO turn off dependents loading?

;; TODO need to figure out how to test different settings


#_(deftest multiply-test-2
  (is (= (* 75 10) (multiply 10 75))))
