#Nest showcase project

##Description
Web page that allows you to view and modify the thermostat target temperature, and view status (online/offline) of the camera.

I want to draw your attention to the fact that this is prototype. There are no comments in code, there are no tests. Exceptional cases not processed properly, just logged.

##How to run project
```bash
$ git clone https://github.com/vratushnyi/nest-scalajs-play.git
$ cd nest-scalajs-play
$ sbt run
```
Open the following url [http://localhost:9000/](http://localhost:9000) in your browser, after the following message appears in console:
```
--- (Running the application, auto-reloading is enabled) ---

[info] p.c.s.NettyServer - Listening for HTTP on /0:0:0:0:0:0:0:0:9000

(Server started, use Ctrl+D to stop and go back to the console...)
```

##Libraries/frameworks used
* **Client**: Scala.js, React, Diode,  Bootstrap, log4javascript, ScalaCSS
* **Server**: Play Framework, Akka, WebSocket
* **Shared**: upickle

<img src="/example.png" alt="Example" width="500px">
