(ns figwheel-main-testing.test-utils
  (:require
   [goog.net.XhrIo :as xhrio]
   [goog.string :as gstring]
   [goog.object :as gobj]
   [figwheel.core :as fc]
   [clojure.core.async :refer [<! put! take! go chan close!] :as asyn]
   [clojure.string :as string]))

(defn provide! [string-ns]
  (reduce
   (fn [accum part]
     (if-let [accum (gobj/get accum part)]
       accum
       (let [obj #js{}]
         (gobj/set accum part obj)
         obj)))
   goog/global
   (string/split string-ns ".")))

(defonce data-atom (atom {}))

(defn reset-data! []
  (reset! data-atom {}))

(defn reload-hook-ns [ns]
  (swap! data-atom update :reload-hook-ns (fnil conj []) ns))

(defn signal [data]
  (swap! data-atom assoc :signal data))

(defonce ns-receiver-chan-atom (atom (chan)))

(defn reset-receiver-chan! []
  (reset! ns-receiver-chan-atom (chan)))

(defn add-ns [ns]
  (put! @ns-receiver-chan-atom ns))

(defn drain-chan [ch]
  (let [res (asyn/into [] ch)]
    (close! ch)
    res))

(defn post [url data callback]
  (.send js/goog.net.XhrIo url
              (fn [e]
                (callback (.getResponseText (.-target e))))
              "POST"
              data))

(defn api-send! [data]
  (let [res (asyn/promise-chan)]
    (post "/test-an-action"
          (str "data=" (gstring/urlEncode (pr-str data)))
          (fn [_]
            (put! res 1)
            (close! res)))
    res))

(defn api-send-and-wait! [data]
  (let [request (asyn/promise-chan)
        state-change (asyn/promise-chan)]
    (post "/test-an-action"
          (str "data=" (gstring/urlEncode (pr-str data)))
          (fn [res]
            (put! request res)
            (close! request)))
    (add-watch fc/state ::api-send-and-wait-watch
               (fn [_ _ _ n]
                 (put! state-change n)
                 (close! state-change)
                 (remove-watch fc/state
                               ::api-send-and-wait-watch)))
    (go (<! request)
        (<! state-change))))

(defn touch-files! [paths]
  (api-send! {:action :touch
              :file-paths paths}))

(defn touch-file! [path]
  (touch-files! [path]))

(defn append-line! [path line]
  (api-send! {:action :append-line
              :file-path path
              :line line}))

(defn truncate-file! [path]
  (api-send! {:action :truncate-file
              :file-path path
              :contains "CHANGES AFTER THIS"}))
