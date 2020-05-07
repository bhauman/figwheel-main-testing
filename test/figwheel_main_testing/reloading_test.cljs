(ns  figwheel-main-testing.reloading-test
    (:require
     [cljs.test :refer-macros [deftest is testing async]]
     [goog.net.XhrIo :as xhrio]
     [goog.string :as gstring]
     [goog.object :as gobj]
     [clojure.core.async :refer [<! put! take! go chan close!] :as asyn]
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
              drain-chan]
      :as ut]
     #_[figwheel-main-testing.core :refer [multiply]]))

;; OK for now we will have to define all the files that
;; we want to be reloaded so that they exist

;; In the real test we will make this file a ^:figwheel-no-load file
;; so that it doesn't get rerun on every change

(provide! "figwheel_main_testing.core")

;; ----------------------------------------------------------------------
;; testing reloading
;; ----------------------------------------------------------------------

(deftest test-reload
  (async
   done
   (go
     ;; wait before we start
     (<! (asyn/timeout 2000))
     (reset-receiver-chan!)     
     (<! (touch-file! "src/figwheel_main_testing/core.cljs"))
     (is (= 'figwheel-main-testing.core (<! @ut/ns-receiver-chan-atom)))
     (is (empty? (<! (drain-chan @ut/ns-receiver-chan-atom))))
     (done)
     )))

;; ----------------------------------------------------------------------
;; testing dependents reloading
;; ----------------------------------------------------------------------


(deftest test-reload-dependents-aaa

  (async
   done
   (go
     (reset-receiver-chan!)
     (<! (touch-file! "src/figwheel_main_testing/aaa.cljs"))
     (is (= 'figwheel-main-testing.aaa
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.core
            (<! @ut/ns-receiver-chan-atom)))
     (<! (asyn/timeout 100))
     (is (empty? (<! (drain-chan @ut/ns-receiver-chan-atom))))
     (done))
   ))

(deftest test-reload-dependents-bbb
  (async
   done
   (go
     (reset-receiver-chan!)
     (<! (touch-file! "src/figwheel_main_testing/bbb.cljs"))
     (is (= 'figwheel-main-testing.bbb
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.aaa
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.core
            (<! @ut/ns-receiver-chan-atom)))
     (<! (asyn/timeout 100))
     (is (empty? (<! (drain-chan @ut/ns-receiver-chan-atom))))
     (done))))

(deftest test-reload-dependents-ccc
  (async
   done
   (go
     (reset-receiver-chan!)
     (<! (touch-file! "src/figwheel_main_testing/ccc.cljs"))
     (is (= 'figwheel-main-testing.ccc
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.bbb
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.aaa
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.core
            (<! @ut/ns-receiver-chan-atom)))
     (<! (asyn/timeout 100))
     (is (empty? (<! (drain-chan @ut/ns-receiver-chan-atom))))
     (done))))

(deftest test-reload-dependents-ddd
  (reset-receiver-chan!)
  (async
   done
   (go
     (<! (touch-file! "src/figwheel_main_testing/ddd.cljs"))
     (is (= 'figwheel-main-testing.ddd
            (<! @ut/ns-receiver-chan-atom)))                    
     (is (= 'figwheel-main-testing.ccc
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.bbb
            (<! @ut/ns-receiver-chan-atom)))                    
     (is (= 'figwheel-main-testing.aaa
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.core
            (<! @ut/ns-receiver-chan-atom)))
     (<! (asyn/timeout 100))
     (is (empty? (<! (drain-chan @ut/ns-receiver-chan-atom))))
     (done))))


(deftest test-reload-dependents-graph-g-some
  (reset-receiver-chan!)
  (async
   done
   (go
     (<! (touch-file! "src/figwheel_main_testing/g_some.cljs"))
     (is (= 'figwheel-main-testing.g-some
            (<! @ut/ns-receiver-chan-atom)))                    
     (is (= 'figwheel-main-testing.aa
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.aaa
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.core
            (<! @ut/ns-receiver-chan-atom)))
     (<! (asyn/timeout 100))
     (is (empty? (<! (drain-chan @ut/ns-receiver-chan-atom))))
     (done))))

;; ----------------------------------------------------------------------
;; testing changing multiple files
;; ----------------------------------------------------------------------

(deftest test-changing-multiple-files
  (reset-receiver-chan!)
  (async
   done
   (go
     (<! (touch-files! ["src/figwheel_main_testing/core.cljs"
                        "src/figwheel_main_testing/ddd.cljs"
                        "src/figwheel_main_testing/g_some.cljs"]))
     (is (= 'figwheel-main-testing.g-some
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.ddd
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.aa
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.ccc
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.bbb
            (<! @ut/ns-receiver-chan-atom)))     
     (is (= 'figwheel-main-testing.aaa
            (<! @ut/ns-receiver-chan-atom)))
     (is (= 'figwheel-main-testing.core
            (<! @ut/ns-receiver-chan-atom)))
     (<! (asyn/timeout 100))
     (is (empty? (<! (drain-chan @ut/ns-receiver-chan-atom))))
     (done)))
  )


;; TODO test exceptions and warnings

;; TODO test javascript reloading

;; TODO turn off dependents loading?

;; TODO need to figure out how to test different settings


#_(deftest multiply-test-2
  (is (= (* 75 10) (multiply 10 75))))
