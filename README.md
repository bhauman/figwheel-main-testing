# figwheel-main-testing

Integration testing for figwheel-main

## Overview

Testing the round trip operation of figwheel and ensure proper file
load order and and heads up display.

To run tests look at `deps.edn` for the options

    clojure -A:fig:cljs-753:test

To clean all compiled files:

    rm -rf target/public


## Future work

* more tests!
* make it work in a headless environment (currently a browser is launched)
* get it working for node reloading as well

## License

Copyright © 2020 Bruce Hauman

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
